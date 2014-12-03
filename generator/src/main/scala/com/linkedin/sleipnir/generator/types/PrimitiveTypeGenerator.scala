package com.linkedin.sleipnir.generator.types

import com.linkedin.data.ByteString
import com.linkedin.data.schema._
import com.linkedin.data.schema.DataSchema.Type
import com.linkedin.sleipnir.generator.GeneratedClass

/**
 * A universal generator for all primitive data types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
class PrimitiveTypeGenerator(override val schema: PrimitiveDataSchema, override val parentGenerator: Option[TypeGenerator]) extends AbstractTypeGenerator {

  private val PrimitiveClassNames = Map(
    Type.BOOLEAN -> "Boolean",
    Type.NULL -> "Null",
    Type.FLOAT -> "Float",
    Type.STRING -> "String",
    Type.DOUBLE -> "Double",
    Type.LONG -> "Long",
    Type.INT -> "Int"
  )

  override def shortClassName: String = PrimitiveClassNames(schema.getType)

  override def packageName: String = "scala"

  override def externalClassName: String = shortClassName

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None

}

/**
 * Even though [[BytesDataSchema]] represents primitive data types it needs to be handles separately.
 * @param schema the type schema
 */
class BytesTypeGenerator(override val schema: BytesDataSchema, override val parentGenerator: Option[TypeGenerator]) extends AbstractTypeGenerator {

  override def shortClassName: String = classOf[ByteString].getSimpleName

  override def packageName: String = classOf[ByteString].getPackage.getName

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None

}
