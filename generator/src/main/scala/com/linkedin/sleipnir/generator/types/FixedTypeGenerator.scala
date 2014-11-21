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
case class FixedTypeGenerator(override val schema: FixedDataSchema) extends NamedTypeGenerator with Logging {

  override def generateClasses: Seq[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = FixedTemplate(this).toString()
    val generated = GeneratedClass(fullClassName, source)
    Seq(generated)
  }
}
