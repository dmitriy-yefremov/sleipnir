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

import com.linkedin.data.DataList
import com.linkedin.data.schema.{ArrayDataSchema, DataSchema}
import com.linkedin.data.template.DataTemplate
import TypeCoercer._

/**
 * A super type for all array types.
 * @author Dmitriy Yefremov
 */
abstract class ScalaArrayTemplate protected(itemsData: DataList, dataSchema: ArrayDataSchema) extends DataTemplate[DataList] {

  override def schema: DataSchema = dataSchema

  override def data: DataList = itemsData

  // everything is immutable, so no need to copy
  override def copy: DataTemplate[DataList] = this

  def items: Seq[Any]

  override def hashCode(): Int = data.values().hashCode()

  override def equals(other: Any): Boolean = {
    other match {
      case otherTemplate: ScalaArrayTemplate => data.equals(otherTemplate.data)
    }
  }

  override def toString: String = data.values().toString

}

object ScalaArrayTemplate {

  /**
   * Converts a Scala [[Seq]] into a Pegasus [[DataList]]. Values in the seq are unwrapped with the coercer.
   *
   * This method is needed to serialize a Scala [[Seq]] to Pegasus JSON.
   */
  def unwrapAll[T](items: Seq[T], coercer: InputCoercer[T]): DataList = {
    val dataList = new DataList(items.map(coercer).asJava)
    dataList.setReadOnly()
    dataList
  }

  /**
   * Converts a Pegasus [[DataList]] into a Scala [[Seq]]. Values in the list are wrapped with the coercer.
   *
   * This method is needed to deserialize a Scala [[Seq]] from Pegasus JSON.
   */
  def wrapAll[T](itemsData: DataList, coercer: OutputCoercer[T]): Seq[T] = {
    itemsData.asScala.map(coercer).toVector
  }

}
