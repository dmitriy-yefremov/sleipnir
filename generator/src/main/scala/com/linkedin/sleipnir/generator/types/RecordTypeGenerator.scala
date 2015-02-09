package com.linkedin.sleipnir.generator.types

import scala.collection.JavaConverters._

import com.linkedin.data.schema.RecordDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.RecordTemplate

/**
 * A generator for [[RecordDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class RecordTypeGenerator(override val schema: RecordDataSchema,
                          override val parentGenerator: Option[TypeGenerator],
                          override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  def orderedFields: Seq[RecordDataSchema.Field] = schema.getFields.asScala.sortBy(field => field.getOptional || fieldHasDefault(field))

  def escapedFieldName(field: RecordDataSchema.Field): String = {
    escapePegasusReserved(escapeScalaReserved(field.getName))
  }

  def fieldValName(field: RecordDataSchema.Field): String = {
    s"Field${field.getName.capitalize}"
  }

  def fieldHasDefault(field: RecordDataSchema.Field): Boolean =
    Option(field.getDefault).isDefined

  def constructorArg(field: RecordDataSchema.Field): String = {
    val baseType = setterFieldTypeOf(field)
    val baseArg = s"${escapedFieldName(field)}: $baseType"
    if (field.getOptional || fieldHasDefault(field)) {
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

  def getterFieldTypeOf(field: RecordDataSchema.Field): String = {
    val base = fieldGenerator(field).name.externalClassName
    if(field.getOptional) {
      s"Option[$base]"
    } else {
      base
    }
  }

  def setterFieldTypeOf(field: RecordDataSchema.Field): String = {
    val base = fieldGenerator(field).name.externalClassName
    if (fieldHasDefault(field)) {
      s"Option[$base]"
    } else {
      getterFieldTypeOf(field)
    }
  }

  def fieldGenerator(field: RecordDataSchema.Field): TypeGenerator = nestedGenerator(field.getType)

  override val name: TypeName = escapeScalaReserved(alias.getOrElse {
    TypeName(schema.getName, namespace(schema.getNamespace))
  })

  override def referencedGenerators: Seq[TypeGenerator] = {
    schema.getFields.asScala.map(field => fieldGenerator(field))
  }

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = RecordTemplate(this).toString()
    generatedClass(source)
  }

}
