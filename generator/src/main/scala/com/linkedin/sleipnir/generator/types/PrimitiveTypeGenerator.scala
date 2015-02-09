package com.linkedin.sleipnir.generator.types


import com.linkedin.data.ByteString
import com.linkedin.data.schema._
import com.linkedin.data.schema.DataSchema.Type


/**
 * A universal generator for all primitive data types.
 * @author Dmitriy Yefremov
 */
class PrimitiveTypeGenerator(override val schema: PrimitiveDataSchema,
                             override val parentGenerator: Option[TypeGenerator],
                             override val namespacePrefix: Option[String],
                             override val filename: String) extends AbstractTypeGenerator with PredefinedTypeGenerator {

  override val typeNames = Map(
    Type.BOOLEAN -> TypeName(classOf[java.lang.Boolean], "Boolean"),
    Type.NULL -> TypeName(classOf[java.lang.Object], "Null"),
    Type.FLOAT -> TypeName(classOf[java.lang.Float], "Float"),
    Type.STRING -> TypeName(classOf[java.lang.String], "String"),
    Type.DOUBLE -> TypeName(classOf[java.lang.Double], "Double"),
    Type.LONG -> TypeName(classOf[java.lang.Long], "Long"),
    Type.INT -> TypeName(classOf[java.lang.Integer], "Int"),
    Type.BYTES -> TypeName(classOf[ByteString])
  )

  override val typeSchema: DataSchema = schema
}
