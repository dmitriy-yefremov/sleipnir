package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.NamedDataSchema

/**
 * Common functionality for [[NamedDataSchema]] type generators.
 * @author Dmitriy Yefremov
 */
trait NamedTypeGenerator extends TypeGenerator {

  override def schema: NamedDataSchema

  override def shortClassName: String = schema.getName

  override def packageName: String = schema.getNamespace

  override def fullClassName: String = schema.getFullName

  override def externalClassName: String = schema.getFullName


}
