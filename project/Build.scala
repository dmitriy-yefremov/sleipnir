import sbt._
import Keys._
import sbt.IO

import twirl.sbt.TwirlPlugin._
import de.johoop.jacoco4sbt.JacocoPlugin._

object Sleipnir extends Build {

  /**
   * A dummy aggregator project. It does nothing by itself, but lets us see the root folder in Intellij.
   */
  lazy val sleipnir = project.in(file("."))
    .aggregate(sleipnirGenerator, sleipnirSbtPlugin, sampleData)
    .settings(jacoco.settings: _*)

  /**
   * The generator project. For now it includes both the code to generate Scala classes and the runtime code needed to
   * use the generated classes.
   */
  lazy val sleipnirGenerator = project.in(file("generator"))
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
        "ch.qos.logback" % "logback-classic" % "1.1.2",
        "com.linkedin.pegasus" % "data" % "1.20.0",
        "org.scalariform" %% "scalariform" % "0.1.4",
        "org.apache.commons" % "commons-lang3" % "3.4",
        "org.specs2" %% "specs2-core" % "2.4.14" % "test"
      )
    )
    .settings(Twirl.settings: _*)
    .settings(jacoco.settings: _*)

  /**
   * The generator in the form of an SBT plugin.
   */
  lazy val sleipnirSbtPlugin = project.in(file("sbt-plugin"))
    .dependsOn(sleipnirGenerator)
    .aggregate(sleipnirGenerator)
    .settings(
      sbtPlugin := true
    )
    .settings(jacoco.settings: _*)

  /**
   * Some sample PDSC files to test the generator. All the craziness below is needed to build the generator and actually
   * use it withing the same SBT project.
   */
  lazy val sampleData = project.in(file("sample-data"))
    .dependsOn(sleipnirGenerator)
    .settings(forkedVmSleipnirGeneratorSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2-core" % "2.4.14" % "test"
      )
    )
    .settings(jacoco.settings: _*)

  lazy val forkedVmSleipnirGenerator = taskKey[Seq[File]]("Sleipnir generator executed in a forked VM")

  val forkedVmSleipnirGeneratorSettings = Seq(
    forkedVmSleipnirGenerator in Compile := {
      val src = sourceDirectory.value / "main" / "pegasus"
      val dst = sourceDirectory.value / "main" / "codegen"
      val classpath = (dependencyClasspath in Runtime in sleipnirGenerator).value.files
      streams.value.log.info("Generating PDSC bindings...")
      val files = runForkedGenerator(src, dst, classpath)
      streams.value.log.info(s"There are ${files.size} classes generated from PDSC")
      files
    },
    sourceGenerators in Compile <+= (forkedVmSleipnirGenerator in Compile),
    unmanagedSourceDirectories in Compile += sourceDirectory.value / "main" / "codegen",
    managedSourceDirectories in Compile += sourceDirectory.value / "main" / "codegen",
    cleanFiles += sourceDirectory.value / "main" / "codegen"
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
