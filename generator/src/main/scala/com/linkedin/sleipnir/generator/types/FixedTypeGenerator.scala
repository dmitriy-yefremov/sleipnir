package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.FixedDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.FixedTemplate

/**
 * A generator for [[FixedDataSchema]] types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
class FixedTypeGenerator(override val schema: FixedDataSchema, override val parentGenerator: Option[TypeGenerator]) extends AbstractTypeGenerator {

  override val name: TypeName = TypeName(schema.getName, schema.getNamespace)

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = FixedTemplate(this).toString()
    generatedClass(source)
  }

}
