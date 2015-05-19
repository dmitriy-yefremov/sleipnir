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

package net.yefremov.sleipnir.sbt

import net.yefremov.sleipnir.Sleipnir

import sbt._
import Keys._
import java.io.File.pathSeparator

object SleipnirPlugin extends Plugin {

  val sleipnirGenerator = taskKey[Seq[File]]("Generates Scala bindings for PDSC files")

  val sleipnirSourceDirectory = settingKey[File]("Folder with PDSC files used to generate Scala bindings")

  val sleipnirDestinationDirectory = settingKey[File]("Folder with the generated bindings")

  val sleipnirPrefix = settingKey[Option[String]]("Namespace prefix used for generated Scala classes")

  val sleipnirCacheSources = taskKey[File]("Caches .pdsc sources")

  /**
   * Settings that need to be added to the project to enable generation of Scala bindings for PDSC files located in the project.
   */
  val sleipnirSettings: Seq[Def.Setting[_]] = Seq(

    sleipnirPrefix := Some("scala"),

    sleipnirSourceDirectory := sourceDirectory.value / "main" / "pegasus",

    sleipnirDestinationDirectory := sourceDirectory.value / "main" / "codegen",

    sleipnirCacheSources := streams.value.cacheDirectory / "pdsc.sources",

    sleipnirGenerator in Compile := {
      val log = streams.value.log
      val src = sleipnirSourceDirectory.value
      val dst = sleipnirDestinationDirectory.value
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
        val generatedFiles = Sleipnir.run(resolverPath, src, dst, namespacePrefix)

        // NOTE: deleting stale files does not work properly with sleipnir activated on two different projects
        //val staleFiles = previousScalaFiles.sorted.diff(generatedFiles.sorted)
        //log.info("Not deleting stale files " + staleFiles.mkString(", "))
        //IO.delete(staleFiles)
        cacheSourceFiles()
        generatedFiles
      } else {
        previousScalaFiles
      }
    },

    sourceGenerators in Compile <+= (sleipnirGenerator in Compile),

    unmanagedSourceDirectories in Compile += sleipnirDestinationDirectory.value,

    managedSourceDirectories in Compile += sleipnirDestinationDirectory.value,

    cleanFiles += sleipnirDestinationDirectory.value,

    libraryDependencies += {
      val version = SleipnirPlugin.getClass.getPackage.getImplementationVersion
      "net.yefremov.sleipnir" % s"sleipnir-generator_2.10" % version
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
    artifact.name.endsWith("-data-template") || artifact.configurations.toSeq.exists(_.name == "dataTemplate")
  }

  /**
   * Settings that need to be added to the project to enable generation of Scala bindings for PDSC files coming from downstream services.
   */
  val sleipnirDownstreamSettings: Seq[Def.Setting[_]] = sleipnirSettings ++ Seq(

    dataTemplatesDependenciesFilter := DefaultDataTemplatesDependenciesFilter,

    extractDataTemplatesTarget := target.value / "pegasus-temp",

    sleipnirSourceDirectory := extractDataTemplatesTarget.value / "pegasus",

    cleanFiles ++= Seq(extractDataTemplatesTarget.value, sleipnirDestinationDirectory.value),

    dataTemplatesDependencies := update.value.matching(dataTemplatesDependenciesFilter.value),

    extractDataTemplates := {
      val target = extractDataTemplatesTarget.value
      IO.delete(target)
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

  /**
   * Generates settings that place the artifact generated by `packagingTaskKey` in the specified `ivyConfig`,
   * while also suffixing the artifact name with "-" and the `ivyConfig`.
   */
  def restliArtifactSettings(packagingTaskKey : TaskKey[File])(ivyConfig : String): Seq[Def.Setting[_]] = {
    val config = Configurations.config(ivyConfig)

    Seq(
      (artifact in packagingTaskKey) <<= (artifact in packagingTaskKey) { artifact =>
        artifact.copy(name = artifact.name + "-" + ivyConfig,
          configurations = artifact.configurations ++ Seq(config))
      },
      ivyConfigurations += config
    )
  }

  /**
   * Finds descendants of `dir` matching `globExpr` and maps them to paths relative to `dir`.
   */
  def mappings(dir : File, globExpr : String): Seq[(File, String)] = {
    val filter = GlobFilter(globExpr)
    Seq(dir).flatMap(d => Path.allSubpaths(d).filter{ case (f, id) => filter.accept(f) } )
  }

  //package
  val packageDataModel = taskKey[File]("Produces a data model jar containing only pdsc files")

  // Returns settings that can be applied to a project to cause it to package the Pegasus artifacts.
  def pegasusArtifacts: Seq[Def.Setting[_]] = {
    def packageDataModelMappings: Def.Initialize[Task[Seq[(File, String)]]] = sleipnirSourceDirectory.map{ (dir) =>
      mappings(dir, "*.pdsc")
    }

    // The resulting settings create the two packaging tasks, put their artifacts in specific Ivy configs,
    // and add their artifacts to the project.

    val defaultConfig = config("default").extend(Runtime).describedAs("Configuration for default artifacts.")

    val dataTemplateConfig = new Configuration("dataTemplate", "pegasus data templates",
      isPublic=true,
      extendsConfigs=List(Compile),
      transitive=true)

    Defaults.packageTaskSettings(packageDataModel, packageDataModelMappings) ++
      restliArtifactSettings(packageDataModel)("dataModel") ++
      Seq(
        packagedArtifacts <++= Classpaths.packaged(Seq(packageDataModel)),
        artifacts <++= Classpaths.artifactDefs(Seq(packageDataModel)),
        ivyConfigurations ++= List(dataTemplateConfig, defaultConfig),
        artifact in (Compile, packageBin) ~= { (art: Artifact) =>
          art.copy(configurations = art.configurations ++ List(dataTemplateConfig))
        }
      )
  }

}
