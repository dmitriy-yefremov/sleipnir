package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.FixedDataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.FixedTemplate

/**
 * A generator for [[FixedDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class FixedTypeGenerator(override val schema: FixedDataSchema,
                         override val parentGenerator: Option[TypeGenerator],
                         override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  override val name: TypeName = TypeName(schema.getName, namespace(schema.getNamespace))

  override val referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = FixedTemplate(this).toString()
    generatedClass(source)
  }

}
