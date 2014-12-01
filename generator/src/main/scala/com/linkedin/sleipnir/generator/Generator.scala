package com.linkedin.sleipnir.generator

import java.io.{File, FileWriter, PrintWriter}

import com.linkedin.data.schema._
import com.linkedin.sleipnir.generator.types._
import grizzled.slf4j.Logging
import scala.util.control.NonFatal
import scalariform.formatter.ScalaFormatter

trait Generator extends Logging {

  def processSchemas(schemas: Seq[DataSchema], targetDir: File) = {
    val uniqueGenerators = schemas.foldLeft(Set[TypeGenerator]()) { (acc, schema) =>
      TypeGeneratorFactory.instance(schema).referencedGenerators ++ acc
    }
    val generatedClasses = uniqueGenerators.flatMap { generator =>
      generator.generateClass
    }
    val files = generatedClasses.map { generatedClass =>
      writeToFile(generatedClass, targetDir)
    }
    files.toList
  }

  protected def writeToFile(generatedClass: GeneratedClass, targetDir: File): File = {
    val fullName = generatedClass.name.replace('.', File.separatorChar) + ".scala"
    val targetFile = new File(targetDir, fullName)
    val content = formatSourceCode(generatedClass.source)
    // format source code before writing
    writeToFile(targetFile, content)
    targetFile
  }

  protected def writeToFile(file: File, content: String) {
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

  protected def formatSourceCode(content: String): String = {
    try {
      ScalaFormatter.format(content)
    } catch {
      case NonFatal(e) =>
        logger.error(s"Can't format the generated code: ${e.getMessage}")
        content
    }
  }

}




