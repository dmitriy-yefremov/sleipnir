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

import com.linkedin.data.schema.{ArrayDataSchema, DataSchema}

import net.yefremov.sleipnir.data.custom._
import net.yefremov.sleipnir.generator.txt.ArrayTemplate

import com.typesafe.scalalogging.slf4j.StrictLogging
import net.yefremov.sleipnir.data.custom._
import net.yefremov.sleipnir.generator.GeneratedClass

/**
 * Common functionality for [[ArrayDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait ArrayTypeGenerator extends AbstractTypeGenerator {

  override def schema: ArrayDataSchema

  def itemsGenerator: TypeGenerator = nestedGenerator(schema.getItems)

  protected def externalClassName: String = s"scala.Seq[${itemsGenerator.name.externalClassName}]"

}

/**
 * A generator for arrays of complex types (e.g. an array of records).
 */
class ComplexArrayTypeGenerator(override val schema: ArrayDataSchema,
                                override val parentGenerator: Option[TypeGenerator],
                                override val namespacePrefix: Option[String],
                                override val filename: String) extends ArrayTypeGenerator with StrictLogging {

  override val name: TypeName = alias.getOrElse {
    val itemsName: TypeName = itemsGenerator.name
    TypeName(itemsName.shortClassName + "Array", itemsName.packageName, externalClassName)
  }

  override def referencedGenerators: Seq[TypeGenerator] = Seq(itemsGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = ArrayTemplate(this).toString()
    generatedClass(source)
  }

  val itemsClassName: String = itemsGenerator.name.externalClassName

}

/**
 * A generator for arrays of primitive types (e.g. an array of integers).
 */
class PrimitiveArrayTypeGenerator(override val schema: ArrayDataSchema,
                                  override val parentGenerator: Option[TypeGenerator],
                                  override val namespacePrefix: Option[String],
                                  override val filename: String) extends ArrayTypeGenerator with PredefinedTypeGenerator {

  override val typeNames = Map(
    DataSchema.Type.BOOLEAN -> TypeName(classOf[BooleanArray], externalClassName),
    DataSchema.Type.INT -> TypeName(classOf[IntArray], externalClassName),
    DataSchema.Type.LONG -> TypeName(classOf[LongArray], externalClassName),
    DataSchema.Type.FLOAT -> TypeName(classOf[FloatArray], externalClassName),
    DataSchema.Type.DOUBLE -> TypeName(classOf[DoubleArray], externalClassName),
    DataSchema.Type.BYTES -> TypeName(classOf[BytesArray], externalClassName),
    DataSchema.Type.STRING -> TypeName(classOf[StringArray], externalClassName)
  )

  override val typeSchema: DataSchema = schema.getItems
}
