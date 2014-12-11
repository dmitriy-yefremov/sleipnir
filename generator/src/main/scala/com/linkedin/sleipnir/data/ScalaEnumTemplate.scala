package com.linkedin.sleipnir.data

/**
 * A super type for all enum types
 * @author Anirudh Padmarao
 */
trait ScalaEnumTemplate extends Enumeration {

  val $Unknown = Value

  def withNameOrUnknown(s: String) = {
    try {
      withName(s)
    } catch {
      case _: NoSuchElementException => $Unknown
    }
  }

}
