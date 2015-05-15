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

package net.yefremov.sleipnir.generator

import com.linkedin.data.schema._
import net.yefremov.sleipnir.generator.types._

import com.typesafe.scalalogging.slf4j.StrictLogging

import java.io.{File, FileWriter, PrintWriter}

import net.yefremov.sleipnir.generator.types.{TypeGenerator, TypeGeneratorFactory}

import scala.util.control.NonFatal

import scalariform.formatter.ScalaFormatter


/**
 * This mix-in is responsible for generation of Scala source code.
 * @author Dmitriy Yefremov
 */
trait Generator extends StrictLogging {

  /**
   * Generates Scala bindings for the given data schemas.
   * @param schemaFileMap a map of top level schemas to the file that the schema originated from
   * @param targetDir output directory
   * @param namespacePrefix optional prefix that is added to name space of the generated types
   * @return files that were generated
   */
  def processSchemas(schemaFileMap: Map[DataSchema, File], targetDir: File, namespacePrefix: Option[String]): Seq[File] = {
    val generators = uniqueGenerators(schemaFileMap, namespacePrefix)
    val generatedClasses = generators.flatMap { generator =>
      generator.generateClass
    }
    validateUniqueClasses(generatedClasses)
    generatedClasses.map { generatedClass =>
      writeToFile(generatedClass, targetDir)
    }
  }

  private def uniqueGenerators(schemaFileMap: Map[DataSchema, File], namespacePrefix: Option[String]): Seq[TypeGenerator] = {

    def loop(generators: Seq[TypeGenerator], acc: Set[TypeGenerator]): Set[TypeGenerator] = {
      generators match {
        case Nil => acc
        case h :: t if acc.contains(h) => loop(t, acc)
        case h :: t => loop(t ++ h.referencedGenerators, acc + h)
      }
    }

    val generators = schemaFileMap.foldLeft(Set[TypeGenerator]()) { (acc, schema) =>
      loop(Seq(TypeGeneratorFactory.instance(schema._1, namespacePrefix, schema._2.getAbsolutePath)), acc)
    }

    generators.toList
  }

  private def validateUniqueClasses(generatedClasses: Seq[GeneratedClass]): Unit = {
    val names = generatedClasses.map(_.name)
    val uniqueNames = names.distinct
    if (names.size != uniqueNames.size) {
      val duplicates = names.diff(uniqueNames).distinct.mkString(", ")
      logger.warn(s"Same class(es) generated multiple times: $duplicates. That may be caused by multiple versions of the same schema.")
    }
  }

  private def writeToFile(generatedClass: GeneratedClass, targetDir: File): File = {
    val fullName = generatedClass.name.replace('.', File.separatorChar) + ".scala"
    val targetFile = new File(targetDir, fullName)
    val content = formatSourceCode(generatedClass.source)
    // format source code before writing
    writeToFile(targetFile, content)
    targetFile
  }

  private def writeToFile(file: File, content: String) {
    logger.info(s"Writing $file")
    // ensure directories exist
    file.getParentFile.mkdirs()
    // write content
    val writer = new FileWriter(file)
    val printWriter = new PrintWriter(writer)
    try {
      printWriter.print(content)
    } finally {
      printWriter.close()
    }
  }

  private def formatSourceCode(content: String): String = {
    try {
      ScalaFormatter.format(content)
    } catch {
      case NonFatal(e) =>
        logger.error(s"Can't format the generated code: ${e.getMessage}")
        content
    }
  }

}




