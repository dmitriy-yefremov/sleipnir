package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaArrayTemplate
import com.linkedin.sleipnir.data.ScalaArrayTemplate._
import com.linkedin.sleipnir.data.custom.DoubleArray._

/**
 * Custom wrapper for Seq[Double]
 */
class DoubleArray protected(override val items: Seq[Double], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[Double]) = this(items, unwrapAll(items, PrimitiveInputCoercer))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object DoubleArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"double\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, Double] = { case x: Double => x }

  def apply(items: Seq[Double]) = new DoubleArray(items)

}
