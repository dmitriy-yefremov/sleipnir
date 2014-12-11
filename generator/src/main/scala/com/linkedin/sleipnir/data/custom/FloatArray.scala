package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaArrayTemplate
import com.linkedin.sleipnir.data.ScalaArrayTemplate._
import com.linkedin.sleipnir.data.custom.FloatArray._

/**
 * Custom wrapper for Seq[Float]
 */
class FloatArray protected(override val items: Seq[Float], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[Float]) = this(items, unwrapAll(items, PrimitiveInputCoercer))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object FloatArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"float\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, Float] = { case x: Float => x }

  def apply(items: Seq[Float]): FloatArray = new FloatArray(items)

}
