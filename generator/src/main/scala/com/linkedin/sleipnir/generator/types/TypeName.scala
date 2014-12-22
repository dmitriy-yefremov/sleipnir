package com.linkedin.sleipnir.generator.types

/**
 * Represents all naming related information of a type.
 * @param shortClassName short class name (e.g. "TypeGenerator")
 * @param packageName package name (e.g. "com.linkedin.sleipnir")
 * @param fullClassName fully qualified class name including the package (e.g. "com.linkedin.sleipnir.generator.types.TypeGenerator")
 * @param externalClassName fully qualified class name to be used for external references to this type (e.g. for arrays
 *                          it will be "Seq[com.linkedin.sleipnir.generator.types.TypeGenerator]" instead of the actual
 *                          "com.linkedin.sleipnir.generator.types.TypeGeneratorArray")
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