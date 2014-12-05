package com.linkedin.sleipnir.data.custom

import com.linkedin.data.{ByteString, DataMap}
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate

import BytesMap._

/**
 * Custom wrapper for Map[String, ByteString].
 * @author Dmitriy Yefremov
 */
class BytesMap(override val map: Map[String, ByteString], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, ByteString]) = this(map, ScalaMapTemplate.unwrapAll(map))

  def this(data: DataMap) = this(ScalaMapTemplate.wrapAll(data, Coercer), data)

}

object BytesMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"bytes\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: PartialFunction[Any, ByteString] = { case x: ByteString => x }

  def apply(map: Map[String, ByteString]) = new BytesMap(map)

}
