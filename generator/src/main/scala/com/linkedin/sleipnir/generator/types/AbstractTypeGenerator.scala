package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.{NullDataSchema, DataSchema}
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Some code shared between most of type generators.
 * @author Dmitriy Yefremov
 */
trait AbstractTypeGenerator extends TypeGenerator {

  override def fullClassName: String = packageName + "." + shortClassName

  override def externalClassName: String = fullClassName

  /**
   * Type's schema in JSON format with Java escaping.
   */
  def schemaJson: String = {
    val json = schema.toString
    StringEscapeUtils.escapeJava(json)
  }

  /**
   * Creates an instance of the generator of the specified type. Current generator is used as the parent when the instance is created.
   */
  protected def nestedGenerator(nestedSchema: DataSchema) = TypeGeneratorFactory.instance(nestedSchema, this)

  /**
   * Finds a parent of this generator that matches the given predicate.
   * See [[AbstractTypeGenerator.findMatchingParent(TypeGenerator, TypeGenerator => Boolean)]] for details.
   */
  protected def findMatchingParent(predicate: TypeGenerator => Boolean): List[TypeGenerator] = {
    AbstractTypeGenerator.findMatchingParent(this, predicate)
  }

  /**
   * We need to have a custom implementation of equality comparison for type deduping purposes (e.g. a schema can
   * recursively refer to itself, but should only be processed once).
   */
  override def equals(other: Any): Boolean = other match {
    case that: AbstractTypeGenerator =>
        getClass == that.getClass &&
        schema == that.schema &&
        fullClassName == that.fullClassName
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(schema, fullClassName)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object AbstractTypeGenerator {

  /**
   * Finds a parent of the given generator that matches the given predicate.
   * @return a list of references from the given generator to the first parent matching the predicate, the resulting
   *         list always has the matching parent in the head and the given generator in the tail
   * @throws IllegalArgumentException if no matching parent is found
   */
  def findMatchingParent(generator: TypeGenerator, predicate: TypeGenerator => Boolean): List[TypeGenerator] = {

    def find(acc: List[TypeGenerator]): List[TypeGenerator] = {
      acc.head.parentGenerator match {
        case Some(parent) if predicate(parent) => parent::acc
        case Some(parent) => find(parent::acc)
        case None => throw new IllegalArgumentException("Could not find a matching parent")
      }
    }

    find(List(generator))
  }

}
