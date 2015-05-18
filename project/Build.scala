/*
 *    Copyright 2015 Dmitriy Yefremov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import sbt._
import Keys._
import sbt.IO

import twirl.sbt.TwirlPlugin._
import de.johoop.jacoco4sbt.JacocoPlugin._

object Sleipnir extends Build {

  val sharedSettings = jacoco.settings ++ Seq(
    organization := "net.yefremov.sleipnir",
    version := "0.1.0",
    scalaVersion := "2.10.4"
  )

  /**
   * A dummy aggregator project. It does nothing by itself, but lets us see the root folder in Intellij.
   */
  lazy val sleipnir = project.in(file("."))
    .aggregate(sleipnirGenerator, sleipnirSbtPlugin, sampleData)
    .settings(sharedSettings: _*)
    .settings(publishArtifact := false)

  /**
   * The generator project. For now it includes both the code to generate Scala classes and the runtime code needed to
   * use the generated classes.
   */
  lazy val sleipnirGenerator = project.in(file("generator"))
    .settings(sharedSettings: _*)
    .settings(name := "sleipnir-generator")
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

  /**
   * The generator in the form of an SBT plugin.
   */
  lazy val sleipnirSbtPlugin = project.in(file("sbt-plugin"))
    .dependsOn(sleipnirGenerator)
    .aggregate(sleipnirGenerator)
    .settings(sharedSettings: _*)
    .settings(sbtPlugin := true)
    .settings(name := "sleipnir-sbt-plugin")

  /**
   * Some sample PDSC files to test the generator. All the craziness below is needed to build the generator and actually
   * use it withing the same SBT project.
   */
  lazy val sampleData = project.in(file("sample-data"))
    .dependsOn(sleipnirGenerator)
    .settings(sharedSettings: _*)
    .settings(publishArtifact := false)
    .settings(forkedVmSleipnirGeneratorSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2-core" % "2.4.14" % "test"
      )
    )

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
    val mainClass = "net.yefremov.sleipnir.Sleipnir"
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
