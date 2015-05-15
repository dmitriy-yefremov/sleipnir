/*
 *    Copyright 2015 Dmitriy Yefremov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.yefremov.sleipnir.data

/**
 * Contains types and functions for the conversion between scala and pegasus types.
 */
object TypeCoercer {

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
