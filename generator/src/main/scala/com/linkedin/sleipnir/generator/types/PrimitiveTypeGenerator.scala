package com.linkedin.sleipnir.generator.types

import com.linkedin.data.ByteString
import com.linkedin.data.schema._
import com.linkedin.sleipnir.generator.GeneratedClass
import com.typesafe.scalalogging.slf4j.StrictLogging

/**
 * A universal generator for all primitive data types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
case class PrimitiveTypeGenerator(override val schema: PrimitiveDataSchema) extends TypeGenerator with StrictLogging {

  override def shortClassName: String = {
    schema match {
      case b: BytesDataSchema => classOf[ByteString].getSimpleName
      case _ => primitiveClassName
    }
  }

  override def fullClassName: String = {
    schema match {
      case b: BytesDataSchema => classOf[ByteString].getName
      case _ => primitiveClassName
    }
  }

  override def externalClassName: String = fullClassName

  private def primitiveClassName: String = {
    schema match {
      case b: BooleanDataSchema => "Boolean"
      case n: NullDataSchema => "Null"
      case f: FloatDataSchema => "Float"
      case s: StringDataSchema => "String"
      case d: DoubleDataSchema => "Double"
      case l: LongDataSchema => "Long"
      case i: IntegerDataSchema => "Int"
    }
  }

  override def packageName: String = throw new NotImplementedError("This method should never be called for primitive types")

  override def generateClasses: Seq[GeneratedClass] = Seq.empty

}
