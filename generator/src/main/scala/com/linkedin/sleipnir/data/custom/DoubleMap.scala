package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate

import DoubleMap._

/**
 * Custom wrapper for Map[String, Double].
 * @author Dmitriy Yefremov
 */
class DoubleMap(override val map: Map[String, Double], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, Double]) = this(map, ScalaMapTemplate.unwrapAll(map))

  def this(data: DataMap) = this(ScalaMapTemplate.wrapAll(data, Coercer), data)

}

object DoubleMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"double\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: PartialFunction[Any, Double] = { case x: Double => x }

  def apply(map: Map[String, Double]) = new DoubleMap(map)

}
