/*
 *    Copyright 2015 Dmitriy Yefremov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.yefremov.sleipnir.data

import com.linkedin.data.DataMap
import com.linkedin.data.schema._
import com.linkedin.data.template._

/**
 * An abstract class that is extended by all Scala record template classes. This class is designed to take care of all heavy
 * lifting and make extending classes really lightweight. It in turn simplifies the logic to generate those classes.
 * @author Dmitriy Yefremov
 */
abstract class ScalaRecordTemplate protected(data: DataMap, schema: RecordDataSchema) extends RecordTemplate(data, schema) {

  def getRequired[T](field: RecordDataSchema.Field, value: Option[T]): T =
    value.getOrElse(throw new RequiredFieldNotPresentException(field.getName))

  def getDirect[T](field: RecordDataSchema.Field, valueClass: Class[T]): Option[T] =
    Option(obtainDirect(field, valueClass, GetMode.DEFAULT))

  def getWrapped[T <: DataTemplate[_]](field: RecordDataSchema.Field, valueClass: Class[T]): Option[T] =
    Option(obtainWrapped(field, valueClass, GetMode.DEFAULT))

  def getCustom[T](field: RecordDataSchema.Field, valueClass: Class[T]): Option[T] =
    Option(obtainCustomType(field, valueClass, GetMode.DEFAULT))

}
