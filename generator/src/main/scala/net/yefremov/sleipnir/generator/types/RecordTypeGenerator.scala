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

package net.yefremov.sleipnir.generator.types

import com.linkedin.data.schema.RecordDataSchema
import net.yefremov.sleipnir.generator.txt.RecordTemplate
import net.yefremov.sleipnir.generator.GeneratedClass

import scala.collection.JavaConverters._


/**
 * A generator for [[RecordDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class RecordTypeGenerator(override val schema: RecordDataSchema,
                          override val parentGenerator: Option[TypeGenerator],
                          override val namespacePrefix: Option[String],
                          override val filename: String) extends AbstractTypeGenerator {

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

  def matchPattern(param: String): String = {
    val escapedFields = orderedFields.map(escapedFieldName)
    val fieldGetters = escapedFields.map(field => s"""$param.$field""")
    val params = fieldGetters.mkString(", ")
    if (escapedFields.length > 1) {
      s"""($params)"""
    } else {
      params
    }
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
