package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.{DataSchema, MapDataSchema}
import com.linkedin.sleipnir.data.custom._
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.MapTemplate

/**
 * Common functionality for [[MapDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait MapTypeGenerator extends AbstractTypeGenerator {

  override def schema: MapDataSchema

  def valuesGenerator: TypeGenerator = nestedGenerator(schema.getValues)

  protected def externalClassName: String = s"Map[String, ${valuesGenerator.name.externalClassName}]"

}

/**
 * A generator for maps of complex types (e.g. a map of records).
 */
class ComplexMapTypeGenerator(override val schema: MapDataSchema,
                              override val parentGenerator: Option[TypeGenerator],
                              override val namespacePrefix: Option[String]) extends MapTypeGenerator {

  override val name: TypeName = {
    val valuesName = valuesGenerator.name
    TypeName(valuesName.shortClassName + "Map", namespace(valuesName.packageName), externalClassName)
  }

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
                                override val namespacePrefix: Option[String]) extends MapTypeGenerator {

  private val PrimitiveWrapperClasses = Map(
    DataSchema.Type.BOOLEAN -> TypeName(classOf[BooleanMap], externalClassName),
    DataSchema.Type.INT -> TypeName(classOf[IntMap], externalClassName),
    DataSchema.Type.LONG -> TypeName(classOf[LongMap], externalClassName),
    DataSchema.Type.FLOAT -> TypeName(classOf[FloatMap], externalClassName),
    DataSchema.Type.DOUBLE -> TypeName(classOf[DoubleMap], externalClassName),
    DataSchema.Type.BYTES -> TypeName(classOf[BytesMap], externalClassName),
    DataSchema.Type.STRING -> TypeName(classOf[StringMap], externalClassName)
  )

  override val name: TypeName = PrimitiveWrapperClasses(schema.getValues.getType)

  override val referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None
}
