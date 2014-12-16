package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.DataSchema
import com.linkedin.sleipnir.generator.GeneratedClass
import grizzled.slf4j.Logging
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Some code shared between most of type generators.
 * @author Dmitriy Yefremov
 */
trait AbstractTypeGenerator extends TypeGenerator with Logging {

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
   * Constructs a full name space given the expected package name.
   */
  protected def namespace(packageName: String): String = {
    namespacePrefix match {
      case None => packageName
      case Some(prefix) => s"$prefix.$packageName"
    }
  }

  /**
   * Escapes the given string if it is a reserved word.
   */
  protected def escapeReserved(word: String) = {
    if (AbstractTypeGenerator.ScalaReservedWords(word)) {
      s"`$word`"
    } else if (AbstractTypeGenerator.PegasusReservedWords(word)) {
      s"`${word}_`"
    } else {
      word
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
  protected def nestedGenerator(nestedSchema: DataSchema) = TypeGeneratorFactory.instance(nestedSchema, this, namespacePrefix)

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
   * The set shows the reserved words in Scala. These reserved words may not be used as constant or variable or any
   * other identifier names.
   *
   * See the spec for details: http://www.scala-lang.org/files/archive/spec/2.11/01-lexical-syntax.html
   */
  val ScalaReservedWords: Set[String] = Set(
    "abstract", "case", "catch", "class", "def", "do", "else", "extends", "false", "final", "finally", "for", "forSome",
    "if", "implicit", "import", "lazy", "match", "new", "null", "object", "override", "package", "private", "protected",
    "return", "sealed", "super", "this", "throw", "trait", "try", "true", "type", "val", "var", "while", "with", "yield"
  )

  /**
   * Words that are reserved because they happen to be used in Pegasus base classes.
   */
  val PegasusReservedWords: Set[String] = Set("data", "schema", "clone", "copy", "hashCode", "toString")

  /**
   * Set of words that should be escaped.
   */
  //val ReservedWords: Set[String] = ScalaReservedWords ++ PegasusReservedWords

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
