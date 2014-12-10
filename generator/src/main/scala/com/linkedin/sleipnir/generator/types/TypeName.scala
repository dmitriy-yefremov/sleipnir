package com.linkedin.sleipnir.generator.types

/**
 * Represents all naming related information of a type.
 * @param shortClassName short class name (e.g. "TypeGenerator")
 * @param packageName package name (e.g. "com.linkedin.sleipnir")
 * @param fullClassName fully qualified class name including the package (e.g. "com.linkedin.sleipnir.generator.types.TypeGenerator")
 * @param externalClassName fully qualified class name to be used for external references to this type (e.g. for arrays
 *                          it will be "Seq[com.linkedin.sleipnir.generator.types.TypeGenerator]" instead of the actual
 *                          "com.linkedin.sleipnir.generator.types.TypeGeneratorArray")
 * @param internalClassName fully qualified class name to be used for internal references to this type in restli
 *                          (e.g. for Booleans it will be java.lang.Boolean instead of Boolean)
 * @author Dmitriy Yefremov
 */
case class TypeName(shortClassName: String, packageName: String, fullClassName: String, externalClassName: String, internalClassName: String)

object TypeName {

  private def fullClassName(shortClassName: String, packageName: String): String = {
    packageName + "." + shortClassName
  }

  def apply(shortClassName: String, packageName: String): TypeName = {
    val fullName = fullClassName(shortClassName, packageName)
    TypeName(shortClassName, packageName, fullName, fullName, fullName)
  }

  def apply(shortClassName: String, packageName: String, externalClassName: String): TypeName = {
    val fullName = fullClassName(shortClassName, packageName)
    TypeName(shortClassName, packageName, fullName, externalClassName, externalClassName)
  }

  def apply(shortClassName: String, packageName: String, externalClassName: String, internalClassName: String): TypeName = {
    val fullName = fullClassName(shortClassName, packageName)
    TypeName(shortClassName, packageName, fullName, externalClassName, internalClassName)
  }

  def apply(clazz: Class[_]): TypeName = {
    TypeName(clazz.getSimpleName, clazz.getPackage.getName)
  }

  def apply(clazz: Class[_], externalClassName: String): TypeName = {
    TypeName(clazz.getSimpleName, clazz.getPackage.getName, externalClassName)
  }

}