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

import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import net.yefremov.sleipnir.data.{ScalaArrayTemplate, TypeCoercer}
import ScalaArrayTemplate._
import net.yefremov.sleipnir.data.{ScalaArrayTemplate, TypeCoercer}
import TypeCoercer._
import BooleanArray._

/**
 * Custom wrapper for Seq[Boolean].
 */
class BooleanArray protected(override val items: Seq[Boolean], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[Boolean]) = this(items, unwrapAll(items, PrimitiveInputCoercer))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object BooleanArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"boolean\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, Boolean] = { case x: Boolean => x }

  def apply(items: Seq[Boolean]): BooleanArray = new BooleanArray(items)

}
