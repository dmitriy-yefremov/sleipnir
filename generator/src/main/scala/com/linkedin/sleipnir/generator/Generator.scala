package com.linkedin.sleipnir.generator

import java.io.{File, FileWriter, PrintWriter}

import scala.util.control.NonFatal

import com.linkedin.data.schema._
import com.linkedin.sleipnir.generator.types._
import grizzled.slf4j.Logging
import scalariform.formatter.ScalaFormatter

/**
 * This mix-in is responsible for generation of Scala source code.
 * @author Dmitriy Yefremov
 */
trait Generator extends Logging {

  /**
   * Generates Scala bindings for the given data schemas.
   * @param schemas top level schemas to process
   * @param targetDir output directory
   * @param namespacePrefix optional prefix that is added to name space of the generated types
   * @return files that were generated
   */
  def processSchemas(schemas: Seq[DataSchema], targetDir: File, namespacePrefix: Option[String] = None): Seq[File] = {
    val generators = uniqueGenerators(schemas)
    val generatedClasses = generators.flatMap { generator =>
      generator.generateClass
    }
    generatedClasses.map { generatedClass =>
      writeToFile(generatedClass, targetDir)
    }
  }

  private def uniqueGenerators(schemas: Seq[DataSchema], namespacePrefix: Option[String] = None): Seq[TypeGenerator] = {

    def loop(generators: Seq[TypeGenerator], acc: Set[TypeGenerator]): Set[TypeGenerator] = {
      generators match {
        case Nil => acc
        case h :: t =>
          if (acc contains h) loop(t, acc)
          else loop(t ++ h.referencedGenerators, acc + h)
      }
    }

    val generators = schemas.foldLeft(Set[TypeGenerator]()) { (acc, schema) =>
      loop(Seq(TypeGeneratorFactory.instance(schema, namespacePrefix)), acc)
    }

    generators.toList
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




