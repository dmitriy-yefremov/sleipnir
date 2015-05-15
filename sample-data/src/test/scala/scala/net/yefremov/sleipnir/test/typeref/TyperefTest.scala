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

package scala.net.yefremov.sleipnir.test.typeref

import net.yefremov.sleipnir.test.CustomString

import scala.net.yefremov.sleipnir.test.SleipnirSpec

class TyperefTest extends SleipnirSpec {

  "Typeref types" should {

    "not override names for predefined types" in {
      val record = PrimitiveTyperefRecord(StringValue)
      record.field must beEqualTo(StringValue)
      val recordFromJson = checkSerialization(record, """{"field":"string value"}""")
      recordFromJson.field must beEqualTo(StringValue)

    }

    "support custom bindings with custom coercers" in {
      val customString = new CustomString(StringValue)
      val record = CustomStringTyperefRecord(customString)
      record.field must beEqualTo(customString)
      val recordFromJson = checkSerialization(record, """{"field":"string value"}""")
      recordFromJson.field must beEqualTo(customString)
    }

  }

}
