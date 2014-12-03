package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.DataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import org.apache.commons.lang3.StringEscapeUtils

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
   * Short class name (e.g. "TypeGenerator").
   */
  def shortClassName: String

  /**
   * Package name (e.g. "com.linkedin.sleipnir").
   */
  def packageName: String

  /**
   * Full class name including the package (e.g. "com.linkedin.sleipnir.generator.types.TypeGenerator").
   */
  def fullClassName: String

  /**
   * Full class name to be used for external references to this type (e.g. for arrays we will use
   * "Seq[com.linkedin.sleipnir.generator.types.TypeGenerator]" instead of the actual
   * "com.linkedin.sleipnir.generator.types.TypeGeneratorArray").
   */
  def externalClassName: String

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
