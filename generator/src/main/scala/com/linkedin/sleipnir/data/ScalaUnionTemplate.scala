package com.linkedin.sleipnir.data

import scala.reflect.runtime.universe._

import com.linkedin.data.schema.{DataSchema, UnionDataSchema}
import com.linkedin.data.template.{DataTemplate, RecordTemplate, UnionTemplate}

/**
 * A super type for all union types. This class is designed to take care of all heavy lifting and make extending classes
 * really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaUnionTemplate(data: AnyRef, schema: UnionDataSchema) extends UnionTemplate(data, schema) {

  private val mirror: Mirror = runtimeMirror(getClass.getClassLoader)

  private def runtimeClass[T](tpe: Type): Class[T] = {
    mirror.runtimeClass(tpe).asInstanceOf[Class[T]]
  }

  protected def set[T: TypeTag](memberSchema: DataSchema, value: T): Unit = {
    typeOf[T] match {
      case recordType if recordType <:< typeOf[RecordTemplate] =>
        val record = value.asInstanceOf[RecordTemplate]
        selectWrapped(memberSchema, runtimeClass[RecordTemplate](recordType), memberSchema.getUnionMemberKey, record)
      case directType =>
        selectDirect(memberSchema, runtimeClass[Any](directType), memberSchema.getUnionMemberKey, value)
    }
  }

  protected def get[T: TypeTag](memberSchema: DataSchema): T = {
    typeOf[T] match {
      case dataType if dataType <:< typeOf[DataTemplate[_]] =>
        obtainWrapped(memberSchema, runtimeClass[DataTemplate[_]](dataType), memberSchema.getUnionMemberKey).asInstanceOf[T]
      case directType =>
        obtainDirect(memberSchema, runtimeClass[Any](directType), memberSchema.getUnionMemberKey).asInstanceOf[T]
    }
  }

  protected def maybeGet[T: TypeTag](memberSchema: DataSchema): Option[T] = {
    if (memberIs(memberSchema.getUnionMemberKey)) {
      Option(get(memberSchema))
    } else {
      None
    }
  }

}
