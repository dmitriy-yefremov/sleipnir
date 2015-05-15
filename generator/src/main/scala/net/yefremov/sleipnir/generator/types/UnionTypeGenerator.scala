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

import com.linkedin.data.schema.{RecordDataSchema, UnionDataSchema}
import net.yefremov.sleipnir.generator.txt.UnionTemplate
import net.yefremov.sleipnir.generator.GeneratedClass

import scala.collection.JavaConverters._


/**
 * A generator for [[UnionDataSchema]] types.
 * @author Dmitriy Yefremov
 */
class UnionTypeGenerator(override val schema: UnionDataSchema,
                         override val parentGenerator: Option[TypeGenerator],
                         override val namespacePrefix: Option[String],
                         override val filename: String) extends AbstractTypeGenerator {

  override val name: TypeName = escapeScalaReserved(alias.getOrElse {
    val referencesToParentRecord = findMatchingParent(_.isInstanceOf[RecordTypeGenerator])
    if (referencesToParentRecord.isEmpty) {
      throw new IllegalArgumentException(s"Can't determine type's name: $schema")
    }
    val parentRecordSchema = referencesToParentRecord(0).schema.asInstanceOf[RecordDataSchema]
    // try to find the field of the parent record that refers to the current schema
    val firstReference = referencesToParentRecord(1)
    val fieldOption = parentRecordSchema.getFields.asScala.find(_.getType == firstReference.schema)
    val field = fieldOption.getOrElse {
      throw new IllegalArgumentException(s"${parentRecordSchema.getFullName} doesn't have a field of type ${schema.getType()}")
    }
    // build the name out of the parent's name and the field name
    val shortClassName = parentRecordSchema.getName + field.getName.capitalize + "Union"
    val packageName = parentRecordSchema.getNamespace
    TypeName(shortClassName, namespace(packageName))
  })

  def memberValName(generator: TypeGenerator): String = s"Member${generator.name.shortClassName}"

  override def referencedGenerators: Seq[TypeGenerator] = schema.getTypes.asScala.map(nestedGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = UnionTemplate(this).toString()
    generatedClass(source)
  }

}
