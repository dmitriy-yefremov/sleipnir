package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.{DataSchema, ArrayDataSchema}
import com.linkedin.data.template._
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.ArrayTemplate
import grizzled.slf4j.Logging

/**
 * Common functionality for [[ArrayDataSchema]] generators.
 * @author Dmitriy Yefremov
 */
sealed trait ArrayTypeGenerator extends TypeGenerator {

  override def schema: ArrayDataSchema

  protected def itemsGenerator: TypeGenerator = nestedGenerator(schema.getItems)

  override def externalClassName: String = s"Seq[${itemsGenerator.externalClassName}]"

}

/**
 * A generator for arrays of primitive types (e.g. an array of integers).
 */
case class ComplexArrayTypeGenerator(override val schema: ArrayDataSchema) extends ArrayTypeGenerator with Logging {

  override def shortClassName: String = itemsGenerator.shortClassName + "Array"

  override def packageName: String = itemsGenerator.packageName

  override def referencedGenerators: Seq[TypeGenerator] = Seq(itemsGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = ArrayTemplate(this).toString()
    Some(GeneratedClass(fullClassName, source))
  }

  def itemsClassName: String = itemsGenerator.shortClassName

}

/**
 * A generator for arrays of complex types (e.g. an array of records).
 */
case class PrimitiveArrayTypeGenerator(override val schema: ArrayDataSchema) extends ArrayTypeGenerator {

  private val PrimitiveWrapperClasses = Map(
    DataSchema.Type.BOOLEAN -> classOf[BooleanArray],
    DataSchema.Type.INT -> classOf[IntegerArray],
    DataSchema.Type.LONG -> classOf[LongArray],
    DataSchema.Type.FLOAT -> classOf[FloatArray],
    DataSchema.Type.DOUBLE -> classOf[DoubleArray],
    DataSchema.Type.BYTES -> classOf[BytesArray],
    DataSchema.Type.STRING -> classOf[StringArray]
  )

  override def shortClassName: String = PrimitiveWrapperClasses(schema.getItems.getType).getSimpleName

  override def packageName: String = PrimitiveWrapperClasses(schema.getItems.getType).getPackage.getName

  override def referencedGenerators: Seq[TypeGenerator] = Seq()

  override def generateClass: Option[GeneratedClass] = None

}
