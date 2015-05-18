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
 * Represents all naming related information of a type.
 * @param shortClassName short class name (e.g. "TypeGenerator")
 * @param packageName package name (e.g. "net.yefremov.sleipnir")
 * @param fullClassName fully qualified class name including the package (e.g. "net.yefremov.sleipnir.generator.types.TypeGenerator")
 * @param externalClassName fully qualified class name to be used for external references to this type (e.g. for arrays
 *                          it will be "Seq[net.yefremov.sleipnir.generator.types.TypeGenerator]" instead of the actual
 *                          "net.yefremov.sleipnir.generator.types.TypeGeneratorArray")
 * @author Dmitriy Yefremov
 */
case class TypeName(shortClassName: String, packageName: String, fullClassName: String, externalClassName: String)

object TypeName {

  private def fullClassName(shortClassName: String, packageName: String): String = {
    packageName + "." + shortClassName
  }

  def apply(fullClassName: String): TypeName = {
    val index = fullClassName.lastIndexOf('.')
    val (start, end) = fullClassName.splitAt(index)
    TypeName(end.tail, start)
  }

  def apply(shortClassName: String, packageName: String): TypeName = {
    val fullName = fullClassName(shortClassName, packageName)
    TypeName(shortClassName, packageName, fullName, fullName)
  }

  def apply(shortClassName: String, packageName: String, externalClassName: String): TypeName = {
    val fullName = fullClassName(shortClassName, packageName)
    TypeName(shortClassName, packageName, fullName, externalClassName)
  }

  def apply(clazz: Class[_]): TypeName = {
    TypeName(clazz.getSimpleName, clazz.getPackage.getName)
  }

  def apply(clazz: Class[_], externalClassName: String): TypeName = {
    TypeName(clazz.getSimpleName, clazz.getPackage.getName, externalClassName)
  }

}