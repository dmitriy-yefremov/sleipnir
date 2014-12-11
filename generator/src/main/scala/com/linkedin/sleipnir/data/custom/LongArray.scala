package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaArrayTemplate
import com.linkedin.sleipnir.data.ScalaArrayTemplate._
import com.linkedin.sleipnir.data.custom.LongArray._

/**
 * Custom wrapper for Seq[Long].
 */
class LongArray protected(override val items: Seq[Long], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[Long]) = this(items, unwrapAll(items, PrimitiveInputCoercer))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object LongArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"long\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, Long] = { case x: Long => x }

  def apply(items: Seq[Long]): LongArray = new LongArray(items)

}
