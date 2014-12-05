package com.linkedin.sleipnir.data

import com.linkedin.data.DataMap
import com.linkedin.data.schema._
import com.linkedin.data.template._

/**
 * An abstract class that is extended by all Scala record template classes. This class is designed to take care of all heavy
 * lifting and make extending classes really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaRecordTemplate(data: DataMap, schema: RecordDataSchema) extends RecordTemplate(data, schema) {

  def getRequired[T](field: RecordDataSchema.Field, value: Option[T]) =
    value.getOrElse(throw new RequiredFieldNotPresentException(field.getName))

  def getDirect[T](field: RecordDataSchema.Field, valueClass: Class[T]): Option[T] =
    Option(obtainDirect(field, valueClass, GetMode.DEFAULT))

  def getWrapped[T <: DataTemplate[_]](field: RecordDataSchema.Field, valueClass: Class[T]): Option[T] =
    Option(obtainWrapped(field, valueClass, GetMode.DEFAULT))

}
