package com.linkedin.sleipnir.data

import scala.reflect.ClassTag

import com.linkedin.data.DataMap
import com.linkedin.data.schema._
import com.linkedin.data.template.{DataTemplate, UnionTemplate}

/**
 * A super type for all union types. This class is designed to take care of all heavy lifting and make extending classes
 * really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaUnionTemplate protected(data: AnyRef, schema: UnionDataSchema) extends UnionTemplate(data, schema) {

  protected def set[T: ClassTag](memberSchema: DataSchema, value: T): Unit = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    memberSchema.getDereferencedDataSchema match {
      case _: PrimitiveDataSchema =>
        selectDirect(memberSchema, clazz.asInstanceOf[Class[T]], memberSchema.getUnionMemberKey, value)
      case _: RecordDataSchema | _: FixedDataSchema | _: UnionDataSchema =>
        selectWrapped(memberSchema, clazz.asInstanceOf[Class[DataTemplate[DataMap]]], memberSchema.getUnionMemberKey, value.asInstanceOf[DataTemplate[DataMap]])
    }
  }

  protected def get[T: ClassTag](memberSchema: DataSchema): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    memberSchema.getDereferencedDataSchema match {
      case _: PrimitiveDataSchema =>
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
