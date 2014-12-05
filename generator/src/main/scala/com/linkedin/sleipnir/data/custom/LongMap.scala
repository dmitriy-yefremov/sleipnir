package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataMap
import com.linkedin.data.schema.MapDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaMapTemplate
import com.linkedin.sleipnir.data.ScalaMapTemplate._
import com.linkedin.sleipnir.data.custom.LongMap._

/**
 * Custom wrapper for Map[String, Long].
 * @author Dmitriy Yefremov
 */
class LongMap(override val map: Map[String, Long], mapData: DataMap) extends ScalaMapTemplate(mapData, Schema) {

   def this(map: Map[String, Long]) = this(map, ScalaMapTemplate.unwrapAll(map, PrimitiveInputCoercer))

   def this(data: DataMap) = this(wrapAll(data, Coercer), data)

 }

object LongMap {

   private val SchemaJson: String = "{ \"type\" : \"map\", \"values\" : \"long\" }"

   private val Schema: MapDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[MapDataSchema]

   private val Coercer: OutputCoercer[Long] = { case x: Long => x }

   def apply(map: Map[String, Long]) = new LongMap(map)

 }
