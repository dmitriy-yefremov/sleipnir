package com.linkedin.sleipnir.data

import com.linkedin.data.DataMap

import scala.reflect.ClassTag

import com.linkedin.data.schema._
import com.linkedin.data.template.{DataTemplate, RecordTemplate, UnionTemplate}

/**
 * A super type for all union types. This class is designed to take care of all heavy lifting and make extending classes
 * really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaUnionTemplate(data: AnyRef, schema: UnionDataSchema) extends UnionTemplate(data, schema) {

  protected def set[T: ClassTag](memberSchema: DataSchema, value: T): Unit = {
    value match {
      case record: RecordTemplate =>
        selectWrapped(memberSchema, record.getClass.asInstanceOf[Class[DataTemplate[DataMap]]], memberSchema.getUnionMemberKey, record)
      case directType =>
        selectDirect(memberSchema, directType.getClass.asInstanceOf[Class[T]], memberSchema.getUnionMemberKey, value)
    }
  }

  protected def get[T: ClassTag](memberSchema: DataSchema): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    memberSchema.getDereferencedDataSchema match {
      case primitive: PrimitiveDataSchema =>
        obtainDirect(memberSchema, clazz, memberSchema.getUnionMemberKey).asInstanceOf[T]
      case _: RecordDataSchema | _: FixedDataSchema | _: UnionDataSchema =>
        obtainWrapped(memberSchema, clazz.asInstanceOf[Class[DataTemplate[DataMap]]], memberSchema.getUnionMemberKey).asInstanceOf[T]
    }
  }

  protected def maybeGet[T: ClassTag](memberSchema: DataSchema): Option[T] = {
    if (memberIs(memberSchema.getUnionMemberKey)) {
      Option(get[T](memberSchema))
    } else {
      None
    }
  }

}
