package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.FixedDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.FixedTemplate
import grizzled.slf4j.Logging

/**
 * A generator for [[FixedDataSchema]] types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
class FixedTypeGenerator(override val schema: FixedDataSchema, override val parentGenerator: Option[TypeGenerator]) extends NamedTypeGenerator with Logging {

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = FixedTemplate(this).toString()
    Some(GeneratedClass(fullClassName, source))
  }
}
