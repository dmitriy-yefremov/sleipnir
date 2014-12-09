package com.linkedin.sleipnir.data

/**
 * Contains types and functions for the conversion between scala and pegasus types.
 */
trait TypeCoercer {

  /**
   * Converts Scala types into the Pegasus types.
   */
  type InputCoercer[T] = PartialFunction[T, AnyRef]

  /**
   * Converts Pegasus types into Scala types.
   */
  type OutputCoercer[T] = PartialFunction[Any, T]

  /**
   * A pass through input coercer that converts Scala primitive types into their Java counterparts.
   */
  val PrimitiveInputCoercer: InputCoercer[Any] = {
    case x: AnyRef => x
  }

}
