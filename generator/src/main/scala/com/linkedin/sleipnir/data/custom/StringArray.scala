package com.linkedin.sleipnir.data.custom

import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaArrayTemplate
import com.linkedin.sleipnir.data.ScalaArrayTemplate._
import com.linkedin.sleipnir.data.custom.StringArray._

/**
 * Custom wrapper for Seq[String]
 */
class StringArray protected(override val items: Seq[String], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[String]) = this(items, unwrapAll(items, PrimitiveInputCoercer))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object StringArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"string\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, String] = { case x: String => x }

  def apply(items: Seq[String]) = new StringArray(items)

}
