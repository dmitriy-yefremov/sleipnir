import java.io.{IOException, FileOutputStream, OutputStream, BufferedOutputStream}

import org.apache.commons.io.FileUtils
import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.tools.ExecFileLoader
import sbt._
import sbt.Keys._
import de.johoop.jacoco4sbt._
import de.johoop.jacoco4sbt.JacocoPlugin._

/**
 * To get test coverage for your multi-module project, add the following to each submodule with unit & integration tests:
 *
 *   .settings(testCoverageSubmoduleSettings: _*)
 *
 * Then, add the following to the root module which aggregates all submodules:
 *
 *   .settings(testCoverageRootModuleSettings:_*)
 *
 * In product-spec.json, add the following command to generate a report that will appear in the Product Dashboard:
 *
 *   "coverage": "play jacoco:cover it-jacoco:cover mergeJacocoReports"
 */
object TestCoverageSettings {

  val jacocoReportFormats = Seq(XMLReport("utf-8"), ScalaHTMLReport("utf-8"))

  val submoduleSettingsTask = Def.task {
    ((sourceDirectory in Compile).value, (jacoco.classesToCover in jacoco.Config).value, (jacoco.outputDirectory in jacoco.Config).value)
  }

  val mergeJacocoReports = taskKey[Unit]("Merges all Jacoco coverage reports from all submodules into one report")

  /**
   * For multi-module Play projects, adding this task to the root project will allow the creation of a single merged
   * Jacoco report(s) from the individual coverage reports generated by each submodule. This task should be executed
   * after jacoco:cover and it-jacoco:cover.
   */
  val mergeJacocoReportsTask = mergeJacocoReports := {

    // Create the output directory where everything will go
    val mergeOutputDir = baseDirectory.value / "build" / "reports" / "coverage"
    if(mergeOutputDir.exists) {
      mergeOutputDir.delete
    }
    FileUtils.forceMkdir(mergeOutputDir)

    // Find source directory, Jacoco class directory and Jacoco output directory for all submodules
    val submoduleSettings = submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject))).value
    val sourceDirectories = submoduleSettings.map(_._1)
    val classDirectories = submoduleSettings.flatMap(_._2)
    val jacocoOutputDirectories = submoduleSettings.map(_._3)

    // Merge all the Jacoco exec files together
    val loader = new ExecFileLoader
    val execFiles = jacocoOutputDirectories.map( _ / "jacoco.exec")
    execFiles.filter(_.isFile).foreach(loader.load)

    val mergedFile = mergeOutputDir / "jacoco-all-submodules-merged.exec"

    writeToFile(mergedFile) { outputStream =>
      val dataWriter = new ExecutionDataWriter(outputStream)
      loader.getSessionInfoStore accept dataWriter
      loader.getExecutionDataStore accept dataWriter
    }

    // Lastly, generate the merged report
    val report = new Report(
      reportDirectory = mergeOutputDir,
      executionDataFile = mergedFile,
      reportFormats = jacocoReportFormats,
      reportTitle = (jacoco.reportTitle in jacoco.Config).value,
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      tabWidth = (jacoco.sourceTabWidth in jacoco.Config).value,
      sourceEncoding = (jacoco.sourceEncoding in jacoco.Config).value,
      thresholds = (jacoco.thresholds in jacoco.Config).value,
      streams = streams.value)

    report.generate
  }

  // Copied from de.johoop.jacoco4sbt.Merging
  private def writeToFile(f: File)(writeFn: OutputStream => Unit) = {
    try {
      val out = new BufferedOutputStream(new FileOutputStream(f))
      try writeFn(out)
      catch {
        case e: IOException => throw new ResourcesException("Error merging Jacoco files: %s" format e.getMessage)
      } finally out.close()
    } catch {
      case e: IOException =>
        throw new ResourcesException("Unable to write out Jacoco file during merge: %s" format e.getMessage)
    }
  }

  // Settings that need to be added to every submodule, including the root module
  val testCoverageSubmoduleSettings = jacoco.settings ++ Seq(
    jacoco.reportFormats in jacoco.Config := jacocoReportFormats
  )

  // Settings that need to be added to the root module only
  val testCoverageAggregatorSettings = Seq(mergeJacocoReportsTask)

}
