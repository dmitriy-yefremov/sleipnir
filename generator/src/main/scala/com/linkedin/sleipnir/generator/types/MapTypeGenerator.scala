package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.{DataSchema, MapDataSchema}
import com.linkedin.data.template._
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.MapTemplate
import com.typesafe.scalalogging.slf4j.StrictLogging


/**
 * Common functionality for [[MapDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait MapTypeGenerator extends TypeGenerator {

  override def schema: MapDataSchema

  protected def valuesGenerator: TypeGenerator = nestedGenerator(schema.getValues)

  override def externalClassName: String = s"Map[String, ${valuesGenerator.fullClassName}]"

}

case class ComplexMapTypeGenerator(override val schema: MapDataSchema) extends MapTypeGenerator with StrictLogging {

  override def shortClassName: String = valuesGenerator.shortClassName + "Map"

  override def packageName: String = valuesGenerator.packageName

  override def generateClasses: Seq[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = MapTemplate(this).toString()
    val generated = GeneratedClass(fullClassName, source)
    generated +: valuesGenerator.generateClasses
  }

  def valuesClassName: String = valuesGenerator.shortClassName

}

case class PrimitiveMapTypeGenerator(override val schema: MapDataSchema) extends MapTypeGenerator {

  private val PrimitiveWrapperClasses = Map(
    DataSchema.Type.BOOLEAN -> classOf[BooleanMap],
    DataSchema.Type.INT -> classOf[IntegerMap],
    DataSchema.Type.LONG -> classOf[LongMap],
    DataSchema.Type.FLOAT -> classOf[FloatMap],
    DataSchema.Type.DOUBLE -> classOf[DoubleMap],
    DataSchema.Type.BYTES -> classOf[BytesMap],
    DataSchema.Type.STRING -> classOf[StringMap]
  )

  override def shortClassName: String = PrimitiveWrapperClasses(schema.getValues.getType).getSimpleName

  override def packageName: String = PrimitiveWrapperClasses(schema.getValues.getType).getPackage.getName

  override def generateClasses: Seq[GeneratedClass] = Seq.empty
}
