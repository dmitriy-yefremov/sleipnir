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

import com.linkedin.data.schema.{DataSchema, MapDataSchema}

import net.yefremov.sleipnir.data.custom._
import net.yefremov.sleipnir.generator.txt.MapTemplate
import net.yefremov.sleipnir.data.custom._
import net.yefremov.sleipnir.generator.GeneratedClass


/**
 * Common functionality for [[MapDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait MapTypeGenerator extends AbstractTypeGenerator {

  override def schema: MapDataSchema

  def valuesGenerator: TypeGenerator = nestedGenerator(schema.getValues)

  protected def externalClassName: String = s"scala.Predef.Map[String, ${valuesGenerator.name.externalClassName}]"

}

/**
 * A generator for maps of complex types (e.g. a map of records).
 */
class ComplexMapTypeGenerator(override val schema: MapDataSchema,
                              override val parentGenerator: Option[TypeGenerator],
                              override val namespacePrefix: Option[String],
                              override val filename: String) extends MapTypeGenerator {

  override val name: TypeName = escapeScalaReserved(alias.getOrElse {
    val valuesName = valuesGenerator.name
    TypeName(valuesName.shortClassName + "Map", valuesName.packageName, externalClassName)
  })

  override def referencedGenerators: Seq[TypeGenerator] = Seq(valuesGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = MapTemplate(this).toString()
    generatedClass(source)
  }

  val valuesClassName: String = valuesGenerator.name.externalClassName

}

/**
 * A generator for maps of primitive types (e.g. a map of integers).
 */
class PrimitiveMapTypeGenerator(override val schema: MapDataSchema,
                                override val parentGenerator: Option[TypeGenerator],
                                override val namespacePrefix: Option[String],
                                override val filename: String) extends MapTypeGenerator with PredefinedTypeGenerator {

  override val typeNames = Map(
    DataSchema.Type.BOOLEAN -> TypeName(classOf[BooleanMap], externalClassName),
    DataSchema.Type.INT -> TypeName(classOf[IntMap], externalClassName),
    DataSchema.Type.LONG -> TypeName(classOf[LongMap], externalClassName),
    DataSchema.Type.FLOAT -> TypeName(classOf[FloatMap], externalClassName),
    DataSchema.Type.DOUBLE -> TypeName(classOf[DoubleMap], externalClassName),
    DataSchema.Type.BYTES -> TypeName(classOf[BytesMap], externalClassName),
    DataSchema.Type.STRING -> TypeName(classOf[StringMap], externalClassName)
  )

  override val typeSchema: DataSchema = schema.getValues
}
