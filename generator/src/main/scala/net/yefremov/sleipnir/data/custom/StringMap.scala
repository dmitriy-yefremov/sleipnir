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

package net.yefremov.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import net.yefremov.sleipnir.data.{TypeCoercer, ScalaMapTemplate}
import ScalaMapTemplate._
import TypeCoercer._
import StringMap._

/**
 * Custom wrapper for Map[String, String].
 * @author Dmitriy Yefremov
 */
class StringMap protected(override val map: Map[String, String], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, String]) = this(map, ScalaMapTemplate.unwrapAll(map, PrimitiveInputCoercer))

  def this(data: DataMap) = this(wrapAll(data, Coercer), data)

}

object StringMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"string\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: OutputCoercer[String] = { case x: String => x }

  def apply(map: Map[String, String]): StringMap = new StringMap(map)

}
