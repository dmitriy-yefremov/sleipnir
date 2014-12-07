package scala.com.linkedin.sleipnir.test

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
  def toJson(template: DataTemplate[_]): String = dataTemplateCodec.dataTemplateToString(template)

  /**
   * Deserializes a data template from the given JSON.
   * @return the deserialized instance
   */
  def fromJson[T <: DataTemplate[_] : ClassTag](json: String): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
    val dataMap = dataTemplateCodec.stringToMap(json)
    val constructor = DataTemplateUtil.templateConstructor(clazz)
    constructor.newInstance(dataMap)
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
    val templateFromJson = fromJson[T](json)
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


