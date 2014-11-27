package com.linkedin.sleipnir.parser

import grizzled.slf4j.Logging
import java.io.{FileInputStream, FileNotFoundException, File}
import com.linkedin.util.FileUtil
import com.linkedin.data.schema.resolver.{FileDataSchemaLocation, FileDataSchemaResolver}
import com.linkedin.data.schema._
import scala.collection.JavaConverters._

/**
 * This mix-in is responsible for data schema files parsing functionality.
 * @author Dmitriy Yefremov
 */
trait Parser extends Logging {

  /**
   * Returns all schema files in the given path. 
   * @param path either a file or a directory
   * @return the file itself if the path represents a file or all schema under the given directory if the path represents a directory
   */
  def expandSource(path: File): Seq[File] = {
    if (path.isFile) {
      Seq(path)
    } else if (path.isDirectory) {
      val filter = new FileUtil.FileExtensionFilter(FileDataSchemaResolver.DEFAULT_EXTENSION)
      FileUtil.listFiles(path, filter).asScala
    } else {
      throw new FileNotFoundException(path.getName)
    }
  }

  /**
   * Parses the given files and returns the top level schemas found.
   * @param sources source files to parse
   * @param resolverPath where to look for referenced data types
   * @return top level data schemas (not all types defined in the source files are returned)
   */
  def parseSources(sources: Seq[File], resolverPath: String): Seq[DataSchema] = {
    val resolver = new FileDataSchemaResolver(SchemaParserFactory.instance, resolverPath)
    sources.flatMap { source =>
      val schemaLocation = new FileDataSchemaLocation(source)
      if (resolver.locationResolved(schemaLocation)) {
        logger.info(s"Skipping $source, already resolved")
        Seq.empty
      } else {
        logger.info(s"Parsing $source")
        parseSchema(schemaLocation, resolver)
      }
    }
  }

  private def parseSchema(schemaLocation: DataSchemaLocation, resolver: DataSchemaResolver): Seq[DataSchema] = {
    val parser = new SchemaParser(resolver)
    val schemaStream = new FileInputStream(schemaLocation.getSourceFile)
    try {
      parser.setLocation(schemaLocation)
      parser.parse(schemaStream)
      if (parser.hasError) {
        throw new IllegalArgumentException(parser.errorMessage)
      } else {
        val schemas = parser.topLevelDataSchemas.asScala
        schemas.foreach { schema =>
          validateSchemaWithFilepath(schemaLocation.getSourceFile, schema)
        }
        schemas
      }
    }
    finally {
      schemaStream.close()
    }
  }

  private def validateSchemaWithFilepath(sourceFile: File, schema: DataSchema): Unit = {
    schema match {
      case namedSchema: NamedDataSchema =>
        // check name
        val expectedName = sourceFile.getName.dropRight(FileDataSchemaResolver.DEFAULT_EXTENSION.length)
        if(namedSchema.getName != expectedName) {
          throw new IllegalArgumentException(s"Schema name '${namedSchema.getName}' does not match file name '${sourceFile.getAbsolutePath}'")
        }
        // check namespace
        val expectedPath = namedSchema.getNamespace.replace('.', File.separatorChar)
        val path = sourceFile.getParentFile.getAbsolutePath
        if (!path.endsWith(expectedPath)) {
          throw new IllegalArgumentException(s"Schema namespace '${namedSchema.getNamespace}' does not match file name '${sourceFile.getAbsolutePath}'")
        }
    }
  }

}
