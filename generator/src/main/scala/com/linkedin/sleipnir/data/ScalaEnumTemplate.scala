package com.linkedin.sleipnir.data

/**
 * A super type for all enum types
 * @author Anirudh Padmarao
 */
trait ScalaEnumTemplate extends Enumeration {

  /**
   * The [[Value]] from this [[Enumeration]] to use if the string representing the value
   * during deserialization from Pegasus JSON is not recognized.
   */
  val $Unknown = Value

  def withNameOrUnknown(s: String): Value =
    values.find(_.toString == s).getOrElse($Unknown)

}
