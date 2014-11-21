package com.linkedin.sleipnir.sbt

import com.linkedin.sleipnir.Sleipnir

import sbt._
import Keys._

object SleipnirPlugin extends Plugin {

  val sleipnirGenerator = taskKey[Seq[File]]("Sleipnir generator")

  lazy val sleipnirSettings: Seq[Def.Setting[_]] = Seq(
    sleipnirGenerator in Compile := {
      val src = sourceDirectory.value / "main" / "pegasus"
      val dst = sourceManaged.value
      runSleipnirGenerator(src, dst)
    },
    sourceGenerators in Compile <+= (sleipnirGenerator in Compile)
  )

  def runSleipnirGenerator(src: File, dst: File): Seq[File] = {
    Sleipnir.run(src.toString, src, dst)
  }
}
