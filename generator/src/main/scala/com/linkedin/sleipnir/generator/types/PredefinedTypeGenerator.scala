package com.linkedin.sleipnir.generator.types

import com.linkedin.data.schema.DataSchema.Type
import com.linkedin.sleipnir.generator.GeneratedClass

/**
 * This trait should be used for all type generators that do not actually generate any code, but just return references
 * to existing predefined classes.
 * @author Dmitriy Yefremov
 */
trait PredefinedTypeGenerator {

  self: TypeGenerator =>

  override def referencedGenerators: Seq[TypeGenerator] = Seq.empty

  override def generateClass: Option[GeneratedClass] = None

  def typeNames: Map[Type, TypeName]

}
