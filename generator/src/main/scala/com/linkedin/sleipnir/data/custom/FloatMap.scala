package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate
import com.linkedin.sleipnir.data.ScalaMapTemplate._
import com.linkedin.sleipnir.data.TypeCoercer._
import com.linkedin.sleipnir.data.custom.FloatMap._

/**
 * Custom wrapper for Map[String, Float].
 * @author Dmitriy Yefremov
 */
class FloatMap protected(override val map: Map[String, Float], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, Float]) = this(map, unwrapAll(map, PrimitiveInputCoercer))

  def this(data: DataMap) = this(wrapAll(data, Coercer), data)

}

object FloatMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"float\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: OutputCoercer[Float] = { case x: Float => x }

  def apply(map: Map[String, Float]): FloatMap = new FloatMap(map)

}
