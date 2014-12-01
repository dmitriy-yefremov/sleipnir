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
  def fullClassName: String = packageName + "." + shortClassName

  /**
   * Full class name to be used for external references to this type (e.g. for arrays we will use
   * "Seq[com.linkedin.sleipnir.generator.types.TypeGenerator]" instead of the actual
   * "com.linkedin.sleipnir.generator.types.TypeGeneratorArray").
   */
  def externalClassName: String = fullClassName

  /**
   * Type's schema in JSON format with Java escaping.
   */
  def schemaJson: String = {
    val json = schema.toString
    StringEscapeUtils.escapeJava(json)
  }

  /**
   * The TypeGenerators of this schema, as well as any referenced schemas.
   * The acc holds the already visited TypeGenerators.
   */
  def referencedGeneratorsAcc(acc: Set[TypeGenerator]): Set[TypeGenerator]

  /**
   * The TypeGenerators of this schema, as well as any referenced schemas.
   */
  def referencedGenerators: Set[TypeGenerator] = referencedGeneratorsAcc(Set[TypeGenerator]())

  /**
   * Generates class for this schema
   */
  def generateClass: Option[GeneratedClass]

  /**
   * Creates an instance of the generator of the specified type. Current schema is used as parent when an instance is created.
   */
  protected def nestedGenerator(nestedSchema: DataSchema) = TypeGeneratorFactory.instance(nestedSchema, schema)


}
