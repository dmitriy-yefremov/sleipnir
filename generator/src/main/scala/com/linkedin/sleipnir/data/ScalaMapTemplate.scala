package com.linkedin.sleipnir.data

import com.linkedin.data.DataMap
import com.linkedin.data.schema.{MapDataSchema, DataSchema}
import com.linkedin.data.template.DataTemplate

import scala.collection.JavaConverters._

/**
 * A super type for all map types
 * @author Anirudh Padmarao
 */
abstract class ScalaMapTemplate(mapData: DataMap, dataSchema: MapDataSchema) extends DataTemplate[DataMap] {

  override def schema: DataSchema = dataSchema

  override def data: DataMap = mapData

  // everything is immutable, so no need to copy
  override def copy: DataTemplate[DataMap] = this

  def map: Map[String, Any]

}

object ScalaMapTemplate {

  /**
   * Converts a Scala [[Map]] into a Pegasus [[DataMap]]. Values in the map are unwrapped. Unwrapping is only applicable
   * to complex types. It converts instances of Scala classes (wrappers) into underlying data objects. Simple types are
   * returned as is.
   *
   * This method is needed to serialize a Scala [[Map]] to Pegasus JSON.
   */
  def unwrapAll[T](map: Map[String, T], coercer: PartialFunction[T, AnyRef]): DataMap = {
    val dataMap = new DataMap(map.mapValues(unwrap(coercer)).asJava)
    dataMap.setReadOnly()
    dataMap
  }

  def unwrap[T](coercer: PartialFunction[T, AnyRef])(value: T): AnyRef = {
    coercer(value)
  }

  def emptyPartialFunction[T]: PartialFunction[Any, T] = {
    case x: T => x
  }

  /**
   * Converts a Pegasus [[DataMap]] into a Scala [[Map]]. Values in the map are wrapped. Wrapping is only applicable
   * to complex types. It converts Pegasus data objects into corresponding Scala classes (wrappers). Simple types are
   * returned as is.
   *
   * This method is needed to deserialize a Scala [[Map]] from Pegasus JSON.
   */
  def wrapAll[T](mapData: DataMap, coercer: PartialFunction[Any, T]) : Map[String, T] = {
    mapData.asScala.mapValues(wrap(coercer)).toMap
  }

  private def wrap[T](coercer: PartialFunction[Any, T])(raw: Any): T  = {
    coercer(raw)
  }

}
