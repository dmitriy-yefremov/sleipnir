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
import net.yefremov.sleipnir.generator.GeneratedClass

/**
 * Base interface for type generators. A type generator is responsible for generation of classes of the specific type.
 * It may also have some type specific helper methods.
 * @author Dmitriy Yefremov
 */
trait TypeGenerator {

  /**
   * Data schema representing the type.
   */
  def schema: DataSchema

  /**
   * Name of the type.
   */
  def name: TypeName

  /**
   * The TypeGenerators referenced by the schema of this generator (e.g. the generator for a record schema references generators for the fields' schemas).
   */
  def referencedGenerators: Seq[TypeGenerator]

  /**
   * A reference to the parent generator. It is [[None]] for top level schemas.
   */
  def parentGenerator: Option[TypeGenerator]


  /**
   * Generates class for this schema. Some types do not require a new class to be generated.
   */
  def generateClass: Option[GeneratedClass]

}
