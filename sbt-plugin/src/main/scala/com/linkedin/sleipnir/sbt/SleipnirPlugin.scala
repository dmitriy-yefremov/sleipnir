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
      //TODO: make namespace prefix configurable
      val namespacePrefix = Some("scala")
      Sleipnir.run(resolverPath, src, dst, namespacePrefix)
    },
    sourceGenerators in Compile <+= (sleipnirGenerator in Compile),
    libraryDependencies  += {
      val version = SleipnirPlugin.getClass.getPackage.getImplementationVersion
      "com.linkedin.sleipnir" % s"sleipnirgenerator_2.10" % version
    }
  )

}
