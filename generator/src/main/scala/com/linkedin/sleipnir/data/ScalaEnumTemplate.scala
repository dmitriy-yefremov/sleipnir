package com.linkedin.sleipnir.data

/**
 * A super type for all enum types
 * @author Anirudh Padmarao
 */
trait ScalaEnumTemplate extends Enumeration {

  /**
   * Type that represents values of this enumeration.
   * This type is unique for every concrete type as a workaround for type erasure of enum values.
   */
  type CustomValue <: Value

  /**
   * The [[CustomValue]] from this [[Enumeration]] to use if the string representing the value
   * during deserialization from Pegasus JSON is not recognized.
   */
  val $Unknown: CustomValue

  def withNameOrUnknown(s: String): CustomValue =
    values.find(_.toString == s).getOrElse($Unknown).asInstanceOf[CustomValue]

}
