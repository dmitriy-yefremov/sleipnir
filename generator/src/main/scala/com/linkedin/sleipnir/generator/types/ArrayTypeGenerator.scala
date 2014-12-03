package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.{ArrayDataSchema, DataSchema}
import com.linkedin.data.template._
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.ArrayTemplate
import grizzled.slf4j.Logging

/**
 * Common functionality for [[ArrayDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait ArrayTypeGenerator extends AbstractTypeGenerator {

  override def schema: ArrayDataSchema

  protected def itemsGenerator: TypeGenerator = nestedGenerator(schema.getItems)

  protected def externalClassName: String = s"Seq[${itemsGenerator.name.externalClassName}]"

}

/**
 * A generator for arrays of primitive types (e.g. an array of integers).
 */
class ComplexArrayTypeGenerator(override val schema: ArrayDataSchema, override val parentGenerator: Option[TypeGenerator]) extends ArrayTypeGenerator with Logging {

  override def name: TypeName = {
    val itemsName: TypeName = itemsGenerator.name
    TypeName(itemsName.shortClassName + "Array", itemsName.packageName, externalClassName)
  }

  override def referencedGenerators: Seq[TypeGenerator] = Seq(itemsGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = ArrayTemplate(this).toString()
    generatedClass(source)
  }

  def itemsClassName: String = itemsGenerator.name.shortClassName

}

/**
 * A generator for arrays of complex types (e.g. an array of records).
 */
class PrimitiveArrayTypeGenerator(override val schema: ArrayDataSchema, override val parentGenerator: Option[TypeGenerator]) extends ArrayTypeGenerator {

  private val PrimitiveWrapperClasses = Map(
    DataSchema.Type.BOOLEAN -> TypeName(classOf[BooleanArray], externalClassName),
    DataSchema.Type.INT -> TypeName(classOf[IntegerArray], externalClassName),
    DataSchema.Type.LONG -> TypeName(classOf[LongArray], externalClassName),
    DataSchema.Type.FLOAT -> TypeName(classOf[FloatArray], externalClassName),
    DataSchema.Type.DOUBLE -> TypeName(classOf[DoubleArray], externalClassName),
    DataSchema.Type.BYTES -> TypeName(classOf[BytesArray], externalClassName),
    DataSchema.Type.STRING -> TypeName(classOf[StringArray], externalClassName)
  )

  override def name: TypeName = PrimitiveWrapperClasses(schema.getItems.getType)

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None

}
