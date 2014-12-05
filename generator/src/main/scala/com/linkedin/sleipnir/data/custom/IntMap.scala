package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate
import com.linkedin.sleipnir.data.ScalaMapTemplate._
import com.linkedin.sleipnir.data.custom.IntMap._

/**
 * Custom wrapper for Map[String, Int].
 * @author Dmitriy Yefremov
 */
class IntMap(override val map: Map[String, Int], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

  def this(map: Map[String, Int]) = this(map, unwrapAll(map, PrimitiveInputCoercer))

  def this(data: DataMap) = this(wrapAll(data, Coercer), data)

}

object IntMap {

  private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"int\" }"

  private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

  private val Coercer: OutputCoercer[Int] = { case x: Int => x }

  def apply(map: Map[String, Int]) = new IntMap(map)

}
