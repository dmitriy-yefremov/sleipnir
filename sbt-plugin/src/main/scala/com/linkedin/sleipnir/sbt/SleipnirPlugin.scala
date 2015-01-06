package com.linkedin.sleipnir.sbt

import com.linkedin.sleipnir.Sleipnir

import sbt._
import Keys._
import java.io.File.pathSeparator

object SleipnirPlugin extends Plugin {

  val sleipnirGenerator = taskKey[Seq[File]]("Generates Scala bindings for PDSC files")

  val sleipnirSourceDirectory = settingKey[File]("Folder with PDSC files used to generate Scala bindings")

  val sleipnirPrefix = settingKey[Option[String]]("Namespace prefix used for generated Scala classes")

  val sleipnirCacheSources = taskKey[File]("Caches .pdsc sources")

  /**
   * Settings that need to be added to the project to enable generation of Scala bindings for PDSC files located in the project.
   */
  val sleipnirSettings: Seq[Def.Setting[_]] = Seq(

    sleipnirPrefix := Some("scala"),

    sleipnirSourceDirectory := sourceDirectory.value / "main" / "pegasus",

    sleipnirCacheSources := streams.value.cacheDirectory / "pdsc.sources",

    sleipnirGenerator in Compile := {
      val log = streams.value.log
      val src = sleipnirSourceDirectory.value
      val dst = sourceManaged.value
      val namespacePrefix = sleipnirPrefix.value
      val resolverPathFiles = Seq(src.getAbsolutePath) ++
        (managedClasspath in Compile).value.map(_.data.getAbsolutePath) ++
        (internalDependencyClasspath in Compile).value.map(_.data.getAbsolutePath) // adds in .pdscs from projects that this project .dependsOn
      val resolverPath = resolverPathFiles.mkString(pathSeparator)

      val cacheFileSources = sleipnirCacheSources.value
      val sourceFiles = (src ** "*.pdsc").get
      val previousScalaFiles = (dst ** "*.scala").get
      val s = streams.value

      val (anyFilesChanged, cacheSourceFiles) = {
        prepareCacheUpdate(cacheFileSources, sourceFiles, s)
      }

      log.debug("Detected changed files: " + anyFilesChanged)
      if (anyFilesChanged) {
        log.info("Generating Scala bindings for PDSC...")
        //TODO: remove this logging after the codebase is stabilized and not much debugging is needed
        log.info("Sleipnir resolver path: " + resolverPath)
        log.info("Sleipnir source path: " + src)
        log.info("Sleipnir destination path: " + dst)
        val generated = Sleipnir.run(resolverPath, src, dst, namespacePrefix)
        cacheSourceFiles()
        generated
      } else {
        previousScalaFiles
      }
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
   * Default filter to get the list of data template artifacts.
   */
  val DefaultDataTemplatesDependenciesFilter = DependencyFilter.fnToArtifactFilter { artifact =>
    artifact.configurations.toSeq.exists(_.name == "dataTemplate")
  }

  /**
   * Settings that need to be added to the project to enable generation of Scala bindings for PDSC files coming from downstream services.
   */
  val sleipnirDownstreamSettings: Seq[Def.Setting[_]] = sleipnirSettings ++ Seq(

    dataTemplatesDependenciesFilter := DefaultDataTemplatesDependenciesFilter,

    extractDataTemplatesTarget := target.value / "pegasus-temp",

    sleipnirSourceDirectory := extractDataTemplatesTarget.value / "pegasus",

    cleanFiles += extractDataTemplatesTarget.value,

    dataTemplatesDependencies := update.value.matching(dataTemplatesDependenciesFilter.value),

    extractDataTemplates := {
      val target = extractDataTemplatesTarget.value
      dataTemplatesDependencies.value.foreach { jar =>
        streams.value.log.info(s"Extracting data templates from ${jar.getName} to $target")
        IO.unzip(jar, target, new SimpleFilter(_.toLowerCase.endsWith(".pdsc")))
      }
    },

    compile in Compile <<= (compile in Compile).dependsOn(extractDataTemplates)

  )

  /**
   * Returns an indication of whether `sourceFiles` and their modify dates differ from what is recorded in `cacheFile`,
   * plus a function that can be called to write `sourceFiles` and their modify dates to `cacheFile`.
   */
  def prepareCacheUpdate(cacheFile: File, sourceFiles: Seq[File],
                         streams: std.TaskStreams[_]): (Boolean, () => Unit) = {
    val fileToModifiedMap = sourceFiles.map(f => f -> FileInfo.lastModified(f)).toMap

    val (_, previousFileToModifiedMap) = Sync.readInfo(cacheFile)(FileInfo.lastModified.format)
    val relation = Seq.fill(sourceFiles.size)(file(".")) zip sourceFiles //we only care about the source files here

    streams.log.debug(fileToModifiedMap.size + " <- current VS previous ->" + previousFileToModifiedMap.size)
    val anyFilesChanged = !cacheFile.exists || (previousFileToModifiedMap != fileToModifiedMap)
    def updateCache() {
      Sync.writeInfo(cacheFile, Relation.empty[File, File] ++ relation.toMap,
        sourceFiles.map(f => f -> FileInfo.lastModified(f)).toMap)(FileInfo.lastModified.format)
    }
    (anyFilesChanged, updateCache)
  }

}
