package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.DataSchema
import com.linkedin.sleipnir.generator.GeneratedClass

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
