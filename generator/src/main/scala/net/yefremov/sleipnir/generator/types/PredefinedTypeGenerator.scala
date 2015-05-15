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

import com.linkedin.data.schema.DataSchema
import com.linkedin.data.schema.DataSchema.Type
import net.yefremov.sleipnir.generator.GeneratedClass

/**
 * This trait should be used for all type generators that do not actually generate any code, but just return references
 * to existing predefined classes.
 * @author Dmitriy Yefremov
 */
trait PredefinedTypeGenerator {

  self: TypeGenerator =>

  def typeNames: Map[Type, TypeName]

  def typeSchema: DataSchema

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None

  override def name: TypeName = typeNames(typeSchema.getDereferencedDataSchema.getType)

}
