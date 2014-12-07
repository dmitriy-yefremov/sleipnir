package com.linkedin.sleipnir.generator.types

import scala.collection.JavaConverters._

import com.linkedin.data.schema.EnumDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.EnumTemplate

/**
 * A generator for [[EnumDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class EnumTypeGenerator(override val schema: EnumDataSchema,
                        override val parentGenerator: Option[TypeGenerator],
                        override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  override val name: TypeName = TypeName(
    schema.getName,
    namespace(schema.getNamespace),
    namespace(schema.getFullName),
    namespace(schema.getFullName + ".Value")
  )

  val symbols: String = {
    schema.getSymbols.asScala.mkString(", ")
  }

  override val referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = EnumTemplate(this).toString()
    generatedClass(source)
  }
}
