package com.linkedin.sleipnir.parser

import com.typesafe.scalalogging.slf4j.StrictLogging
import java.io.{FileInputStream, FileNotFoundException, File}
import com.linkedin.util.FileUtil
import com.linkedin.data.schema.resolver.{FileDataSchemaLocation, FileDataSchemaResolver}
import com.linkedin.data.schema._
import scala.collection.JavaConverters._

trait Parser extends StrictLogging {

  def expandSource(sourceFile: File): Seq[File] = {
    if (sourceFile.isFile) {
      Seq(sourceFile)
    } else if (sourceFile.isDirectory) {
      val filter = new FileUtil.FileExtensionFilter(FileDataSchemaResolver.DEFAULT_EXTENSION)
      FileUtil.listFiles(sourceFile, filter).asScala
    } else {
      throw new FileNotFoundException(sourceFile.getName)
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
        parser.topLevelDataSchemas.asScala
      }
    }
    finally {
      schemaStream.close()
    }
  }

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

}
