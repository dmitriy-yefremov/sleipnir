package com.linkedin.sleipnir.data

import scala.collection.JavaConverters._

import com.linkedin.data.DataList
import com.linkedin.data.schema.{ArrayDataSchema, DataSchema}
import com.linkedin.data.template.DataTemplate
import com.linkedin.sleipnir.data.TypeCoercer._

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
