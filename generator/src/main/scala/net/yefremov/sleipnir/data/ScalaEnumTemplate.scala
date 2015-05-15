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
 * A super type for all enum types
 * @author Anirudh Padmarao
 */
trait ScalaEnumTemplate extends Enumeration {

  /**
   * Type that represents values of this enumeration.
   * This type is unique for every concrete type as a workaround for type erasure of enum values.
   */
  type Type <: Value

  /**
   * The [[Type]] from this [[Enumeration]] to use if the string representing the value
   * during deserialization from Pegasus JSON is not recognized.
   */
  val $Unknown: Type

  def withNameOrUnknown(s: String): Type =
    values.find(_.toString == s).getOrElse($Unknown).asInstanceOf[Type]

}
