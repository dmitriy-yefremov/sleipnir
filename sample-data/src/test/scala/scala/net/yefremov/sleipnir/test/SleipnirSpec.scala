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

package scala.net.yefremov.sleipnir.test

import com.linkedin.data.schema.DataSchema

import scala.reflect.ClassTag

import com.linkedin.data.schema.validation.{RequiredMode, ValidateDataAgainstSchema, ValidationOptions}
import com.linkedin.data.template.{DataTemplate, DataTemplateUtil, JacksonDataTemplateCodec}
import org.specs2.mutable._

/**
 * Base class for all specifications of generated classes.
 * @author Dmitriy Yefremov
 */
trait SleipnirSpec extends Specification {

  val StringValue = "string value"

  val SimpleRecordValue = SimpleRecord(StringValue)

  val dataTemplateCodec = new JacksonDataTemplateCodec()

  /**
   * Serializes the given data template to JSON.
   * @return JSON string
   */
  def toJson(template: DataTemplate[_]): String = dataTemplateCodec.dataTemplateToString(template, true)

  /**
   * Deserializes a data template from the given JSON.
   * @return the deserialized instance
   */
  def fromJson[T <: DataTemplate[_] : ClassTag](json: String, schema: DataSchema): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
    val dataMap = dataTemplateCodec.stringToMap(json)
    DataTemplateUtil.wrap(dataMap, schema, clazz)
  }

  /**
   * Performs (de)serialization tests:
   * 1) JSON representation of the given template should match the expected value
   * 2) Deserialized copy of the template should be equal to the given one
   * @return the deserialized copy
   */
  def checkSerialization[T <: DataTemplate[_] : ClassTag](template: T, expectedJson: String): T = {
    val json = toJson(template)
    json must beEqualTo(expectedJson)
    val templateFromJson = fromJson[T](json, template.schema())
    fixUpData(templateFromJson)
    templateFromJson must beEqualTo(template)
    templateFromJson
  }

  /**
   * Validates the given data template against it's schema. That may cause modifications of internal data map.
   * This method is needed to make .hashCode and .equals to work.
   */
  def fixUpData[T <: DataTemplate[_]](template: T) {
    val validationOptions = new ValidationOptions(RequiredMode.CAN_BE_ABSENT_IF_HAS_DEFAULT)
    ValidateDataAgainstSchema.validate(template.data, template.schema, validationOptions)
  }

}


