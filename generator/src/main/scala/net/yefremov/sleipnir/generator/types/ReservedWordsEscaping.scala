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

/**
 * Contains functions to escape reserved words.
 * @author Dmitriy Yefremov
 */
trait ReservedWordsEscaping {

  /**
   * Escapes the given string if it is a Scala reserved word.
   */
  protected def escapeScalaReserved(word: String): String = {
    if (ReservedWordsEscaping.ScalaReservedWords(word)) {
      s"`$word`"
    } else {
      word
    }
  }

  /**
   * Escapes all fields of [[TypeName]]. If any of package segments is a reserved Scala words this segment is escaped.
   */
  protected def escapeScalaReserved(name: TypeName): TypeName = {

    def escapeNamespace(namespace: String): String = {
      val parts = namespace.split('.')
      val escapedParts = parts.map(escapeScalaReserved)
      escapedParts.mkString(".")
    }

    val shortClassName = escapeScalaReserved(name.shortClassName)
    val packageName = escapeNamespace(name.packageName)
    val fullClassName = escapeNamespace(name.fullClassName)
    val externalClassName = escapeNamespace(name.externalClassName)
    TypeName(shortClassName, packageName, fullClassName, externalClassName)
  }

  /**
   * Escapes the given string if it is a Pegasus method.
   */
  protected def escapePegasusReserved(word: String): String = {
    if (ReservedWordsEscaping.PegasusReservedWords(word)) {
      s"`${word}_`"
    } else {
      word
    }
  }

}

object ReservedWordsEscaping {

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

}
