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
      val log = streams.value.log
      log.info("Generating Scala bindings for PDSC...")
      //TODO: remove this logging after the codebase is stabilized and not much debugging is needed
      log.info("Sleipnir resolver path: " + resolverPath)
      log.info("Sleipnir source path: " + src)
      log.info("Sleipnir destination path: " + dst)
      Sleipnir.run(resolverPath, src, dst, namespacePrefix)
    },

    sourceGenerators in Compile <+= (sleipnirGenerator in Compile),

    libraryDependencies  += {
      val version = SleipnirPlugin.getClass.getPackage.getImplementationVersion
      "com.linkedin.sleipnir" % s"sleipnirgenerator_2.10" % version
    }
  )

}
