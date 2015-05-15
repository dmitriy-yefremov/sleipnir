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

import scala.collection.JavaConverters._

import com.linkedin.data.DataMap
import com.linkedin.data.schema.{DataSchema, MapDataSchema}
import com.linkedin.data.template.DataTemplate
import TypeCoercer._

/**
 * A super type for all map types
 * @author Anirudh Padmarao
 */
abstract class ScalaMapTemplate protected(mapData: DataMap, dataSchema: MapDataSchema) extends DataTemplate[DataMap] {

  override def schema: DataSchema = dataSchema

  override def data: DataMap = mapData

  // everything is immutable, so no need to copy
  override def copy: DataTemplate[DataMap] = this

  def map: Map[String, Any]

  override def hashCode(): Int = data.entrySet().hashCode()

  override def equals(other: Any): Boolean = {
    other match {
      case otherTemplate: ScalaMapTemplate => data.equals(otherTemplate.data)
    }
  }

  override def toString: String = data.entrySet().toString
}

object ScalaMapTemplate {

  /**
   * Converts a Scala [[Map]] into a Pegasus [[DataMap]]. Values in the map are unwrapped. Unwrapping is only applicable
   * to complex types. It converts instances of Scala classes (wrappers) into underlying data objects. Simple types are
   * returned as is.
   *
   * This method is needed to serialize a Scala [[Map]] to Pegasus JSON.
   */
  def unwrapAll[T](map: Map[String, T], coercer: InputCoercer[T]): DataMap = {
    val dataMap = new DataMap(map.mapValues(coercer).asJava)
    dataMap.setReadOnly()
    dataMap
  }

  /**
   * Converts a Pegasus [[DataMap]] into a Scala [[Map]]. Values in the map are wrapped. Wrapping is only applicable
   * to complex types. It converts Pegasus data objects into corresponding Scala classes (wrappers). Simple types are
   * returned as is.
   *
   * This method is needed to deserialize a Scala [[Map]] from Pegasus JSON.
   */
  def wrapAll[T](mapData: DataMap, coercer: OutputCoercer[T]) : Map[String, T] = {
    mapData.asScala.mapValues(coercer).toMap
  }

}
