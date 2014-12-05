package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate

import FloatMap._

/**
 * Custom wrapper for Map[String, Float].
 * @author Dmitriy Yefremov
 */
class FloatMap(override val map: Map[String, Float], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, Float]) = this(map, ScalaMapTemplate.unwrapAll(map))

  def this(data: DataMap) = this(ScalaMapTemplate.wrapAll(data, Coercer), data)

}

object FloatMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"float\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: PartialFunction[Any, Float] = { case x: Float => x }

  def apply(map: Map[String, Float]) = new FloatMap(map)

}
