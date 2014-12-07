package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.TyperefDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass

/**
 * A generator for [[TyperefDataSchema]] types. It does nothing except providing type meta information in the generators chain.
 * @author Dmitriy Yefremov
 */
class ReferenceTypeGenerator(override val schema: TyperefDataSchema,
                             override val parentGenerator: Option[TypeGenerator],
                             override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  override def name: TypeName = TypeName(schema.getName, namespace(schema.getNamespace))

  override def referencedGenerators: Seq[TypeGenerator] = Seq(nestedGenerator(schema.getRef))

  override val generateClass: Option[GeneratedClass] = None

}
