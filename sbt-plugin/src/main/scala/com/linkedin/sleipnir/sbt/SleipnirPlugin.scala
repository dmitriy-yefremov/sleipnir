package com.linkedin.sleipnir.sbt

import com.linkedin.sleipnir.Sleipnir

import sbt._
import Keys._
import java.io.File.pathSeparator

object SleipnirPlugin extends Plugin {

  val sleipnirGenerator = taskKey[Seq[File]]("Generates Scala bindings for PDSC files")

  val sleipnirSourceDirectory = settingKey[File]("Folder with PDSC files used to generate Scala bindings")

  val sleipnirPrefix = settingKey[Option[String]]("Namespace prefix used for generated Scala classes")

  /**
   * Settings that need to be added to the project to enable generation of Scala bindings for PDSC files located in the project.
   */
  val sleipnirSettings: Seq[Def.Setting[_]] = Seq(

    sleipnirPrefix := Some("scala"),

    sleipnirSourceDirectory := sourceDirectory.value / "main" / "pegasus",

    sleipnirGenerator in Compile := {
      val log = streams.value.log
      val src = sleipnirSourceDirectory.value
      val dst = sourceManaged.value
      val namespacePrefix = sleipnirPrefix.value
      val resolverPathFiles = Seq(src.getAbsolutePath) ++
        (managedClasspath in Compile).value.map(_.data.getAbsolutePath) ++
        (internalDependencyClasspath in Compile).value.map(_.data.getAbsolutePath) // adds in .pdscs from projects that this project .dependsOn
      val resolverPath = resolverPathFiles.mkString(pathSeparator)
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

  // The code below is temporary. Clients should not generate bindings for downstream services.

  val dataTemplatesDependencies = taskKey[Seq[File]]("Produces a list of dependencies with 'dataTemplate' configuration")

  val dataTemplatesDependenciesFilter = settingKey[DependencyFilter]("Only dependencies passing the filter will be included")

  val extractDataTemplatesTarget = settingKey[File]("Target directory for extracted data templates")

  val extractDataTemplates = taskKey[Unit]("Extracts data templates from JAR files")

  /**
   * Settings that need to be added to the project to enable generation of Scala bindings for PDSC files coming from downstream services.
   */
  val sleipnirDownstreamSettings: Seq[Def.Setting[_]] = sleipnirSettings ++ Seq(

    dataTemplatesDependenciesFilter := DependencyFilter.allPass,

    extractDataTemplatesTarget := target.value / "pegasus-temp",

    sleipnirSourceDirectory := extractDataTemplatesTarget.value / "pegasus",

    cleanFiles += extractDataTemplatesTarget.value,

    dataTemplatesDependencies := {
      val filter = dataTemplatesDependenciesFilter.value && DependencyFilter.fnToArtifactFilter { artifact =>
        artifact.configurations.toSeq.exists(_.name == "dataTemplate")
      }
      update.value.matching(filter)
    },

    extractDataTemplates := {
      val target = extractDataTemplatesTarget.value
      dataTemplatesDependencies.value.foreach { jar =>
        streams.value.log.info(s"Extracting data templates from ${jar.getName} to $target")
        IO.unzip(jar, target, new SimpleFilter(_.toLowerCase.endsWith(".pdsc")))
      }
    },

    compile in Compile <<= (compile in Compile).dependsOn(extractDataTemplates)

  )

}
