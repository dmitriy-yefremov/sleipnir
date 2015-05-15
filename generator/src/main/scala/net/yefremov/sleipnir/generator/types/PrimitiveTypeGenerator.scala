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
