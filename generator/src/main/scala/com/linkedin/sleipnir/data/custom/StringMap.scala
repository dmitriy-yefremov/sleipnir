package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate

import StringMap._

/**
 * Custom wrapper for Map[String, String].
 * @author Dmitriy Yefremov
 */
class StringMap(override val map: Map[String, String], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, String]) = this(map, ScalaMapTemplate.unwrapAll(map))

  def this(data: DataMap) = this(ScalaMapTemplate.wrapAll(data, Coercer), data)

}

object StringMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"string\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: PartialFunction[Any, String] = { case x: String => x }

  def apply(map: Map[String, String]) = new StringMap(map)

}
