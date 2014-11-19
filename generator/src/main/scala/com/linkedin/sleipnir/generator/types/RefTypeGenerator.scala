package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.TyperefDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.typesafe.scalalogging.slf4j.StrictLogging

/**
 * A generator for [[TyperefDataSchema]] types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
case class RefTypeGenerator(override val schema: TyperefDataSchema) extends NamedTypeGenerator with StrictLogging {

  override def generateClasses: Seq[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    ???
  }

}
