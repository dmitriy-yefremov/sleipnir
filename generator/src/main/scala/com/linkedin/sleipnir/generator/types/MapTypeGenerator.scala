package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.{DataSchema, MapDataSchema}
import com.linkedin.data.template._
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.MapTemplate

/**
 * Common functionality for [[MapDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait MapTypeGenerator extends AbstractTypeGenerator {

  override def schema: MapDataSchema

  protected def valuesGenerator: TypeGenerator = nestedGenerator(schema.getValues)

  protected def externalClassName: String = s"Map[String, ${valuesGenerator.name.externalClassName}]"

}

class ComplexMapTypeGenerator(override val schema: MapDataSchema, override val parentGenerator: Option[TypeGenerator]) extends MapTypeGenerator {

  override def name: TypeName = {
    val valuesName = valuesGenerator.name
    TypeName(valuesName.shortClassName + "Map", valuesName.packageName, externalClassName)
  }

  override def referencedGenerators: Seq[TypeGenerator] = Seq(valuesGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = MapTemplate(this).toString()
    generatedClass(source)
  }

  def valuesClassName: String = valuesGenerator.name.shortClassName

}

class PrimitiveMapTypeGenerator(override val schema: MapDataSchema, override val parentGenerator: Option[TypeGenerator]) extends MapTypeGenerator {

  private val PrimitiveWrapperClasses = Map(
    DataSchema.Type.BOOLEAN -> TypeName(classOf[BooleanMap], externalClassName),
    DataSchema.Type.INT -> TypeName(classOf[IntegerMap], externalClassName),
    DataSchema.Type.LONG -> TypeName(classOf[LongMap], externalClassName),
    DataSchema.Type.FLOAT -> TypeName(classOf[FloatMap], externalClassName),
    DataSchema.Type.DOUBLE -> TypeName(classOf[DoubleMap], externalClassName),
    DataSchema.Type.BYTES -> TypeName(classOf[BytesMap], externalClassName),
    DataSchema.Type.STRING -> TypeName(classOf[StringMap], externalClassName)
  )

  override def name: TypeName = PrimitiveWrapperClasses(schema.getValues.getType)

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None
}
