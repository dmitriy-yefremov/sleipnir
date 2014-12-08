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

  def items: Seq[Any]

}

object ScalaArrayTemplate {

  /**
   * Converts a Scala [[Seq]] into a Pegasus [[DataList]]. Values in the seq are unwrapped.
   *
   * This method is needed to serialize a Scala [[Seq]] to Pegasus JSON.
   */
  def unwrapAll[T](items: Seq[T]): DataList = {
    val dataList = new DataList(items.map(unwrap).asJava)
    dataList.setReadOnly()
    dataList
  }

  private def unwrap[T](item: T): AnyRef = {
    item match {
      case dataTemplate: DataTemplate[AnyRef] => dataTemplate.data()
      case other: AnyRef => other
    }
  }

  /**
   * Converts a Pegasus [[DataList]] into a Scala [[Seq]]. Values in the list are wrapped.
   *
   * This method is needed to deserialize a Scala [[Seq]] from Pegasus JSON.
   */
  def wrapAll[T](itemsData: DataList, coercer: PartialFunction[Any, T]): Seq[T] = {
    itemsData.asScala.map(wrap(coercer)).toVector
  }

  private def wrap[T](coercer: PartialFunction[Any, T])(raw: Any): T = {
    coercer(raw)
  }

}
