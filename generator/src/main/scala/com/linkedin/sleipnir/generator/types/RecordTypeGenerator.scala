package com.linkedin.sleipnir.generator.types

import grizzled.slf4j.Logging

import scala.collection.JavaConverters._

import com.linkedin.data.schema.{DataSchema,RecordDataSchema}
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.RecordTemplate

/**
 * A generator for [[RecordDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class RecordTypeGenerator(override val schema: RecordDataSchema,
                          override val parentGenerator: Option[TypeGenerator],
                          override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  def orderedFields: Seq[RecordDataSchema.Field] = schema.getFields.asScala.sortBy(_.getOptional)

  def escapedFieldName(field: RecordDataSchema.Field): String = {
    escapeReserved(field.getName)
  }

  def fieldValName(field: RecordDataSchema.Field): String = {
    s"Field${field.getName.capitalize}"
  }

  def constructorArg(field: RecordDataSchema.Field): String = {
    val baseArg = s"${escapedFieldName(field)}: ${recordTypeOf(field)}"
    if (field.getOptional) {
      baseArg + " = None"
    } else {
      baseArg
    }
  }

  def constructorArgs: String = {
    orderedFields.map(constructorArg).mkString(", ")
  }

  def constructorParams: String = {
    val escaped = orderedFields.map(escapedFieldName)
    escaped.mkString(", ")
  }

  def recordTypeOf(field: RecordDataSchema.Field): String = {
    val base = fieldGenerator(field).name.externalClassName
    if(field.getOptional) s"Option[$base]" else base
  }

  def fieldGenerator(field: RecordDataSchema.Field): TypeGenerator = nestedGenerator(field.getType)

  override val name: TypeName = alias.getOrElse {
    TypeName(schema.getName, namespace(schema.getNamespace))
  }

  override def referencedGenerators: Seq[TypeGenerator] = {
    schema.getFields.asScala.map(field => fieldGenerator(field))
  }

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = RecordTemplate(this).toString()
    generatedClass(source)
  }

}
