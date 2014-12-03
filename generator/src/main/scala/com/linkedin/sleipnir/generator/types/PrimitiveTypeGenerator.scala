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
    Type.BOOLEAN -> TypeName("Boolean", "scala", "Boolean"),
    Type.NULL -> TypeName("Null", "scala", "Null"),
    Type.FLOAT -> TypeName("Float", "scala", "Float"),
    Type.STRING -> TypeName("String", "scala", "String"),
    Type.DOUBLE -> TypeName("Double", "scala", "Double"),
    Type.LONG -> TypeName("Long", "scala", "Long"),
    Type.INT -> TypeName("Int", "scala", "Int"),
    Type.BYTES -> TypeName(classOf[ByteString])
  )

  override val name: TypeName = PrimitiveClassNames(schema.getType)

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None

}
