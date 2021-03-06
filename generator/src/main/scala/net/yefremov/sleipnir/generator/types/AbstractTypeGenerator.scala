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

import com.typesafe.scalalogging.slf4j.StrictLogging
import net.yefremov.sleipnir.generator.GeneratedClass

import org.apache.commons.lang3.StringEscapeUtils


/**
 * Some code shared between most of type generators.
 * @author Dmitriy Yefremov
 */
trait AbstractTypeGenerator extends TypeGenerator with ReservedWordsEscaping with StrictLogging {

  /**
   * Returns the alias of the current type if it is defined through a type reference.
   */
  def alias: Option[TypeName] = {
    parentGenerator.collect {
      case typeref: ReferenceTypeGenerator =>
        TypeName(typeref.schema.getName, namespace(typeref.schema.getNamespace))
    }
  }

  /**
   * Optional prefix that is added to name space of the generated types.
   */
  def namespacePrefix: Option[String]

  /**
   * The full path name of the PDSC file that the definition of this schema came from.
   */
  def filename: String

  /**
   * The full class name of the generator used to create this type.
   */
  def typeGeneratorName: String = getClass.getCanonicalName

  /**
   * Constructs a full name space given the expected package name.
   */
  protected def namespace(packageName: String): String = {
    namespacePrefix match {
      case None => packageName
      case Some(prefix) => s"$prefix.$packageName"
    }
  }

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
  protected def nestedGenerator(nestedSchema: DataSchema) =
    TypeGeneratorFactory.instance(nestedSchema, this, namespacePrefix, filename)

  /**
   * A shortcut to return a [[GeneratedClass]] instance given the source code.
   */
  protected def generatedClass(source: String) = Some(GeneratedClass(name.fullClassName, source))

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
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(schema, name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = s"${getClass.getSimpleName} [${name.fullClassName}]"
}

object AbstractTypeGenerator {

  /**
   * Finds a parent of the given generator that matches the given predicate.
   * @return a list of references from the given generator to the first parent matching the predicate (the matching parent
   *         is in the head and the given generator is in the tail), an empty list is returned if no matching parent found
   */
  def findMatchingParent(generator: TypeGenerator, predicate: TypeGenerator => Boolean): List[TypeGenerator] = {

    def find(acc: List[TypeGenerator]): List[TypeGenerator] = {
      acc.head.parentGenerator match {
        case Some(parent) if predicate(parent) => parent::acc
        case Some(parent) => find(parent::acc)
        case None => List.empty
      }
    }

    find(List(generator))
  }
}
