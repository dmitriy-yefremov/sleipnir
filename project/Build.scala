import sbt._
import Keys._
import sbt.IO

import twirl.sbt.TwirlPlugin._
import org.scalastyle.sbt.ScalastylePlugin

import com.linkedin.sbt.MintPlugin
import com.linkedin.sbt.core.ext.LiKeys._
import com.linkedin.sbt.core.ext.Predef._

object Sleipnir extends Build {

  /**
   * A dummy aggregator project. It does nothing by itself, but lets us see the root folder in Intellij.
   */
  lazy val sleipnir = project.in(file(".")).aggregate(sleipnirGenerator, sleipnirSbtPlugin, sampleData)

  /**
   * The generator project. For now it includes both the code to generate Scala classes and the runtime code needed to
   * use the generated classes.
   */
  lazy val sleipnirGenerator = project.in(file("generator"))
    .settings(
      productSpecDependencies ++= Seq(
        "external.grizzled-slf4j",
        "external.logback",
        "external.commons-lang",
        "external.scalariform",
        "external.specs2" in "test",
        "product.pegasus.data"
      )
    )
    .settings(Twirl.settings: _*)
    .settings(ScalastylePlugin.Settings: _*)
    .settings(commands ++= Seq(MintPlugin.buildCmd))

  /**
   * The generator in the form of an SBT plugin.
   */
  lazy val sleipnirSbtPlugin = project.in(file("sbt-plugin"))
    .dependsOn(sleipnirGenerator)
    .aggregate(sleipnirGenerator)
    .settings(
      sbtPlugin := true
    )
    .settings(ScalastylePlugin.Settings: _*)
    .settings(commands ++= Seq(MintPlugin.buildCmd))

  /**
   * Some sample PDSC files to test the generator. All the craziness below is needed to build the generator and actually
   * use it withing the same SBT project.
   */
  lazy val sampleData = project.in(file("sample-data"))
    .dependsOn(sleipnirGenerator)
    .settings(forkedVmSleipnirGeneratorSettings: _*)
    .settings(
      productSpecDependencies ++= Seq(
        "external.specs2" in "test"
      )
    )

  lazy val forkedVmSleipnirGenerator = taskKey[Seq[File]]("Sleipnir generator executed in a forked VM")

  val forkedVmSleipnirGeneratorSettings = Seq(
    forkedVmSleipnirGenerator in Compile := {
      val src = sourceDirectory.value / "main" / "pegasus"
      val dst = sourceManaged.value
      val classpath = (dependencyClasspath in Runtime in sleipnirGenerator).value.files
      streams.value.log.info("Generating PDSC bindings...")
      val files = runForkedGenerator(src, dst, classpath)
      streams.value.log.info(s"There are ${files.size} classes generated from PDSC")
      files
    },
    sourceGenerators in Compile <+= (forkedVmSleipnirGenerator in Compile)
  )

  def runForkedGenerator(src: File, dst: File, classpath: Seq[File]): Seq[File] = {
    val mainClass = "com.linkedin.sleipnir.Sleipnir"
    val args = Seq(src.toString, src.toString, dst.toString, "scala")
    val jvmOptions = Seq.empty //Seq("-Dorg.slf4j.simpleLogger.logFile=System.out")
    IO.withTemporaryFile("sleipnir", "output") { tmpFile =>
      val outStream = new java.io.FileOutputStream(tmpFile)
      try {
        val exitValue = new Fork.ForkScala(mainClass)(None, jvmOptions, classpath, args, None, CustomOutput(outStream))
        val outputLines = scala.io.Source.fromFile(tmpFile).getLines()
        if (exitValue != 0) {
          outputLines.foreach(println)
          sys.error(s"Troubles with code generator: $exitValue")
        }
      } finally {
        outStream.close()
      }
      val outputLines = scala.io.Source.fromFile(tmpFile).getLines()
      outputLines.flatMap { line =>
        val parts = line.split(" Writing ")
        if (parts.length == 2) {
          val outputFile = file(parts(1))
          Some(outputFile)
        } else {
          None
        }
      }.toSeq
    }
  }

}
