package com.linkedin.sleipnir.generator.types

import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.UnionTemplate
import com.typesafe.scalalogging.slf4j.StrictLogging

import scala.collection.JavaConverters._

import com.linkedin.data.schema.{RecordDataSchema, UnionDataSchema}

/**
 * A generator for [[UnionDataSchema]] types.
 * @param schema the type schema
 * @param parentSchema the parent type schema (e.g. a record that contains the union type as a field)
 * @author Dmitriy Yefremov
 */
case class UnionTypeGenerator(override val schema: UnionDataSchema, parentSchema: RecordDataSchema) extends TypeGenerator with StrictLogging {

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

  def typeGenerators: Seq[TypeGenerator] = schema.getTypes.asScala.map(TypeGeneratorFactory.instance)

  def memberValName(generator: TypeGenerator): String = s"Member${generator.shortClassName}"

  override def generateClasses: Seq[GeneratedClass] = {
    logger.info(s"Generating $fullClassName")
    val source = UnionTemplate(this).toString()
    val generated = GeneratedClass(fullClassName, source)
    generated +: schema.getTypes.asScala.flatMap { tpe =>
      val generator = TypeGeneratorFactory.instance(tpe, schema)
      generator.generateClasses
    }
  }

}
