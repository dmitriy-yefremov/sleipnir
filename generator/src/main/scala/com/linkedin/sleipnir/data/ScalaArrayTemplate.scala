package com.linkedin.sleipnir.data

import com.linkedin.data.{DataMap, DataList}
import com.linkedin.data.schema.{ArrayDataSchema, DataSchema}
import com.linkedin.data.template.DataTemplate

import scala.collection.JavaConverters._

/**
 * A super type for all array types.
 * @author Dmitriy Yefremov
 */
abstract class ScalaArrayTemplate protected(itemsData: DataList, dataSchema: ArrayDataSchema) extends DataTemplate[DataList] {

  override def schema: DataSchema = dataSchema

  override def data: DataList = itemsData

  // everything is immutable, so no need to copy
  override def copy: DataTemplate[DataList] = this

  def items: Seq[AnyRef]

}

object ScalaArrayTemplate {

  def unwrapAll[T <: AnyRef](items: Seq[T]): DataList = {
    val dataList = new DataList(items.map(unwrap).asJava)
    dataList.setReadOnly()
    dataList
  }

  def unwrap[T <: AnyRef](item: T): AnyRef = {
    item match {
      case dataTemplate: DataTemplate[AnyRef] => dataTemplate.data()
      case other => other
    }
  }

  def wrapAll[T <: AnyRef](itemsData: DataList, constructor: DataMap => T): Seq[T] = {
    itemsData.asScala.map(itemData => wrap(itemData, constructor)).toVector
  }

  def wrap[T <: AnyRef](raw: AnyRef, constructor: DataMap => T): T = {
    raw match {
      case data: DataMap => constructor(data)
    }
  }

}
