package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate
import com.linkedin.sleipnir.data.ScalaMapTemplate._
import com.linkedin.sleipnir.data.TypeCoercer._
import com.linkedin.sleipnir.data.custom.DoubleMap._

/**
 * Custom wrapper for Map[String, Double].
 * @author Dmitriy Yefremov
 */
class DoubleMap protected(override val map: Map[String, Double], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, Double]) = this(map, unwrapAll(map, PrimitiveInputCoercer))

  def this(data: DataMap) = this(wrapAll(data, Coercer), data)

}

object DoubleMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"double\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: OutputCoercer[Double] = { case x: Double => x }

  def apply(map: Map[String, Double]): DoubleMap = new DoubleMap(map)

}
