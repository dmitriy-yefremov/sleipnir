package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaArrayTemplate
import com.linkedin.sleipnir.data.ScalaArrayTemplate._
import com.linkedin.sleipnir.data.custom.BooleanArray._

/**
 * Custom wrapper for Seq[Boolean].
 */
class BooleanArray(override val items: Seq[Boolean], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[Boolean]) = this(items, unwrapAll(items))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object BooleanArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"boolean\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, Boolean] = { case x: Boolean => x }

  def apply(items: Seq[Boolean]) = new BooleanArray(items)

}
