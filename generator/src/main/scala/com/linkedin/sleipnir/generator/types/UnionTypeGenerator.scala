package com.linkedin.sleipnir.generator.types

import scala.collection.JavaConverters._

import com.linkedin.data.schema.{RecordDataSchema, UnionDataSchema}
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.UnionTemplate
import grizzled.slf4j.Logging

/**
 * A generator for [[UnionDataSchema]] types.
 * @param schema the type schema
 * @param parentGenerator the parent generator
 * @author Dmitriy Yefremov
 */
class UnionTypeGenerator(override val schema: UnionDataSchema, override val parentGenerator: Option[TypeGenerator]) extends AbstractTypeGenerator with Logging {

  private val parentSchema = UnionTypeGenerator.findParentRecord(this)

  override def shortClassName: String = {
    // try to find the field of the parent that the current schema represents
    val fieldOption = parentSchema.getFields.asScala.find(_.getType == schema)
    val field = fieldOption.getOrElse {
      throw new IllegalArgumentException(s"${parentSchema.getFullName} doesn't have a field of type ${schema.getType()}")
    }
    // build the name out of the parent's name and the field name
    parentSchema.getName + field.getName.capitalize + "Union"
  }

  override def packageName: String = parentSchema.getNamespace

  override def fullClassName: String = packageName + "." + shortClassName

  override def externalClassName: String = fullClassName

  def typeGenerators: Seq[TypeGenerator] = schema.getTypes.asScala.map(nestedGenerator)

  def memberValName(generator: TypeGenerator): String = s"Member${generator.shortClassName}"

  override def referencedGenerators: Seq[TypeGenerator] = typeGenerators

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = UnionTemplate(this).toString
    Some(GeneratedClass(fullClassName, source))
  }

}

object UnionTypeGenerator {

  def findParentRecord(generator: TypeGenerator): RecordDataSchema = {
    generator.parentGenerator match {
      case Some(parent: RecordTypeGenerator) => parent.schema
      //case Some(parent) => findParentRecord(parent)
      case None => throw new IllegalArgumentException(s"Can't find a parent record")
    }
  }

}
