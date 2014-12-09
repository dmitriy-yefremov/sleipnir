package com.linkedin.sleipnir.data.custom

import com.linkedin.data.ByteString
import com.linkedin.data.DataList
import com.linkedin.data.schema.ArrayDataSchema
import com.linkedin.data.template.DataTemplateUtil
import com.linkedin.sleipnir.data.ScalaArrayTemplate
import com.linkedin.sleipnir.data.ScalaArrayTemplate._
import com.linkedin.sleipnir.data.custom.BytesArray._

/**
 * Custom wrapper for Seq[ByteString]
 */
class BytesArray protected(override val items: Seq[ByteString], itemsData: DataList) extends ScalaArrayTemplate(itemsData, Schema) {

  def this(items: Seq[ByteString]) = this(items, unwrapAll(items, PrimitiveInputCoercer))

  def this(data: DataList) = this(wrapAll(data, Coercer), data)

}

object BytesArray {

  private val SchemaJson: String = "{ \"type\" : \"array\", \"items\" : \"bytes\" }"

  private val Schema: ArrayDataSchema = DataTemplateUtil.parseSchema(SchemaJson).asInstanceOf[ArrayDataSchema]

  private val Coercer: PartialFunction[Any, ByteString] = { case x: ByteString => x }

  def apply(items: Seq[ByteString]) = new BytesArray(items)

}
