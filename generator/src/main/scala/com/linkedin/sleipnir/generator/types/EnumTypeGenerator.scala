package com.linkedin.sleipnir.generator.types

import scala.collection.JavaConverters._

import com.linkedin.data.schema.EnumDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.EnumTemplate

/**
 * A generator for [[EnumDataSchema]] types.
 * @param schema the type schema
 * @author Dmitriy Yefremov
 */
class EnumTypeGenerator(override val schema: EnumDataSchema, override val parentGenerator: Option[TypeGenerator]) extends AbstractTypeGenerator {

  override def name: TypeName = TypeName(schema.getName, schema.getNamespace, schema.getFullName, schema.getFullName + ".Value")

  def symbols: String = {
    schema.getSymbols.asScala.mkString(", ")
  }

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = EnumTemplate(this).toString()
    generatedClass(source)
  }
}
