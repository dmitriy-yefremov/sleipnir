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

  def map: Map[String, AnyRef]

}

object ScalaMapTemplate {

  def unwrapAll[T <: AnyRef](map: Map[String, AnyRef]): DataMap = {
    val dataMap = new DataMap(map.mapValues(unwrap).asJava)
    dataMap.setReadOnly()
    dataMap
  }

  def unwrap[T <: AnyRef](value: T): AnyRef = {
    value match {
      case dataTemplate: DataTemplate[AnyRef] => dataTemplate.data()
      case other => other
    }
  }

  def wrapAll[T <: AnyRef](mapData: DataMap, constructor: DataMap => T) : Map[String, T] = {
    mapData.asScala.mapValues(wrap(constructor)).toMap
  }

  def wrap[T <: AnyRef](constructor: DataMap => T)(raw: AnyRef): T  = {
    raw match {
      case data: DataMap => constructor(data)
    }
  }

}
