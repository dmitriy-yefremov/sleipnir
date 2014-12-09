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

  // for predefined types we can not overwrite the name, so we always delegate the name decision to the referenced generator
  override def name: TypeName = referencedGenerator.name

  private def referencedGenerator = nestedGenerator(schema.getRef)

  override def referencedGenerators: Seq[TypeGenerator] = Seq(referencedGenerator)

  override val generateClass: Option[GeneratedClass] = None

}
