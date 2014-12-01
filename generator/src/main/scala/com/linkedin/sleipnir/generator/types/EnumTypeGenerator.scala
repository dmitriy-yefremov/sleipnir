package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.EnumDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.EnumTemplate
import grizzled.slf4j.Logging

import scala.collection.JavaConverters._

/**
 * A generator for [[EnumDataSchema]] types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
case class EnumTypeGenerator(override val schema: EnumDataSchema) extends NamedTypeGenerator with Logging {

  override def externalClassName: String = fullClassName + ".Value"

  def symbols: String = {
    schema.getSymbols.asScala.mkString(", ")
  }

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = EnumTemplate(this).toString()
    Some(GeneratedClass(fullClassName, source))
  }
}
