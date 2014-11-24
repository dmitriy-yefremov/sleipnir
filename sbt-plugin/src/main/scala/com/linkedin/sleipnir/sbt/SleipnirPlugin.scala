package com.linkedin.sleipnir.sbt

import com.linkedin.sleipnir.Sleipnir

import sbt._
import Keys._
import java.io.File.pathSeparator

object SleipnirPlugin extends Plugin {

  val sleipnirGenerator = taskKey[Seq[File]]("Sleipnir generator")

  lazy val sleipnirSettings: Seq[Def.Setting[_]] = Seq(
    sleipnirGenerator in Compile := {
      val src = sourceDirectory.value / "main" / "pegasus"
      val dst = sourceManaged.value
      val resolverPathFiles = Seq(src.getAbsolutePath) ++
        (managedClasspath in Compile).value.map(_.data.getAbsolutePath) ++
        (internalDependencyClasspath in Compile).value.map(_.data.getAbsolutePath) // adds in .pdscs from projects that this project .dependsOn
      val resolverPath = resolverPathFiles.mkString(pathSeparator)

      runSleipnirGenerator(resolverPath, src, dst)
    },
    sourceGenerators in Compile <+= (sleipnirGenerator in Compile)
  )

  def runSleipnirGenerator(resolverPath: String, src: File, dst: File): Seq[File] = {
    Sleipnir.run(resolverPath, src, dst)
  }
}
