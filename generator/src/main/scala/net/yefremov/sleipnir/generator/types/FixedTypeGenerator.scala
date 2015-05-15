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

import com.linkedin.data.schema.FixedDataSchema
import net.yefremov.sleipnir.generator.txt.FixedTemplate
import net.yefremov.sleipnir.generator.GeneratedClass


/**
 * A generator for [[FixedDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class FixedTypeGenerator(override val schema: FixedDataSchema,
                         override val parentGenerator: Option[TypeGenerator],
                         override val namespacePrefix: Option[String],
                         override val filename: String) extends AbstractTypeGenerator {

  override val name: TypeName = alias.getOrElse {
    TypeName(schema.getName, namespace(schema.getNamespace))
  }

  override val referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = FixedTemplate(this).toString()
    generatedClass(source)
  }
}
