package com.linkedin.sleipnir.generator.types

import grizzled.slf4j.Logging

import scala.collection.JavaConverters._

import com.linkedin.data.schema.{DataSchema,RecordDataSchema}
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.RecordTemplate

/**
 * A generator for [[RecordDataSchema]] types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
class RecordTypeGenerator(override val schema: RecordDataSchema, override val parentGenerator: Option[TypeGenerator]) extends AbstractTypeGenerator {

  def fieldValName(field: RecordDataSchema.Field): String = {
    s"Field${field.getName.capitalize}"
  }

  def constructorArg(field: RecordDataSchema.Field): String = {
    s"${field.getName}: ${recordTypeOf(field)}"
  }

  def constructorArgs: String = {
    schema.getFields.asScala.map(constructorArg).mkString(", ")
  }

  def constructorParams: String = {
    schema.getFields.asScala.map(_.getName).mkString(", ")
  }

  def recordTypeOf(field: RecordDataSchema.Field): String = {
    val base = fieldGenerator(field).name.externalClassName
    if(field.getOptional) s"Option[$base]" else base
  }

  def fieldGenerator(field: RecordDataSchema.Field) = nestedGenerator(field.getType)

  override val name: TypeName = TypeName(schema.getName, schema.getNamespace)

  override def referencedGenerators: Seq[TypeGenerator] = {
    schema.getFields.asScala.map(field => fieldGenerator(field))
  }

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = RecordTemplate(this).toString()
    generatedClass(source)
  }

}
