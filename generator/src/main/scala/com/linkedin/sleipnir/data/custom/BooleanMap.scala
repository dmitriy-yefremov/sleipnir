package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate
import com.linkedin.sleipnir.data.ScalaMapTemplate._
import com.linkedin.sleipnir.data.custom.BooleanMap._

/**
 * Custom wrapper for Map[String, Boolean].
 * @author Dmitriy Yefremov
 */
class BooleanMap protected(override val map: Map[String, Boolean], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, Boolean]) = this(map, unwrapAll(map, PrimitiveInputCoercer))

  def this(data: DataMap) = this(wrapAll(data, Coercer), data)

}

object BooleanMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"boolean\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: OutputCoercer[Boolean] = { case x: Boolean => x }

  def apply(map: Map[String, Boolean]): BooleanMap = new BooleanMap(map)

}
