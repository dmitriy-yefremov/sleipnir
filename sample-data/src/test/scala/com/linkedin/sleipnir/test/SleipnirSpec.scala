package com.linkedin.sleipnir.test

import java.io.StringReader

import com.linkedin.data.DataMap
import com.linkedin.data.template.{DataTemplate, JacksonDataTemplateCodec}
import org.specs2.mutable._

import scala.reflect.ClassTag

/**
 * Base class for all specifications of generated classes.
 * @author Dmitriy Yefremov
 */
trait SleipnirSpec extends Specification {

  val dataTemplateCodec = new JacksonDataTemplateCodec()

  def toJson(template: DataTemplate[_]): String = dataTemplateCodec.dataTemplateToString(template)

  def fromJson[T : ClassTag](json: String): T = {
    val clazz = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
    val dataMap = dataTemplateCodec.readMap(new StringReader(json))
    clazz.getConstructor(classOf[DataMap]).newInstance(dataMap)
  }


 }


