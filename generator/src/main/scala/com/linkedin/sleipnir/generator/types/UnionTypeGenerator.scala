package com.linkedin.sleipnir.generator.types

import scala.collection.JavaConverters._

import com.linkedin.data.schema.{RecordDataSchema, UnionDataSchema}
import com.linkedin.sleipnir.generator.GeneratedClass
import com.linkedin.sleipnir.generator.txt.UnionTemplate

/**
 * A generator for [[UnionDataSchema]] types.
 * @param schema the type schema
 * @param parentGenerator the parent generator
 * @author Dmitriy Yefremov
 */
class UnionTypeGenerator(override val schema: UnionDataSchema,
                         override val parentGenerator: Option[TypeGenerator],
                         override val namespacePrefix: Option[String]) extends AbstractTypeGenerator {

  override val name: TypeName = {
    val referencesToParentRecord = findMatchingParent(_.isInstanceOf[RecordTypeGenerator])
    val parentRecordSchema = referencesToParentRecord.head.schema.asInstanceOf[RecordDataSchema]
    // try to find the field of the parent record that refers to the current schema
    val firstReference = referencesToParentRecord(1)
    val fieldOption = parentRecordSchema.getFields.asScala.find(_.getType == firstReference.schema)
    val field = fieldOption.getOrElse {
      throw new IllegalArgumentException(s"${parentRecordSchema.getFullName} doesn't have a field of type ${schema.getType()}")
    }
    // build the name out of the parent's name and the field name
    val shortClassName = parentRecordSchema.getName + field.getName.capitalize + "Union"
    val packageName: String = parentRecordSchema.getNamespace
    TypeName(shortClassName, namespace(packageName))
  }

  def memberValName(generator: TypeGenerator): String = s"Member${generator.name.shortClassName}"

  override def referencedGenerators: Seq[TypeGenerator] = schema.getTypes.asScala.map(nestedGenerator)

  override def generateClass: Option[GeneratedClass] = {
    logger.info(s"Generating ${name.fullClassName}")
    val source = UnionTemplate(this).toString()
    generatedClass(source)
  }

}
