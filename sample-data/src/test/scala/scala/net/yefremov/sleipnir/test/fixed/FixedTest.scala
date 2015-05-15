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

package scala.net.yefremov.sleipnir.test.fixed

import java.security.MessageDigest

import com.linkedin.data.ByteString

import scala.net.yefremov.sleipnir.test.SleipnirSpec

class FixedTest extends SleipnirSpec {

  private val Bytes = MessageDigest.getInstance("MD5").digest("Yo!".getBytes)

  private val BytesString = ByteString.copy(Bytes)

  private val AvroString = "yp\u001F\u00F5 \u001Bs$\n\u00AF\u00BF\"\u00B8i\u0014T"

  private val JsonString = "\"yp\\u001F\u00F5 \\u001Bs$\\n\u00AF\u00BF\\\"\u00B8i\\u0014T\""

  "Fixed types" should {

    "support instantiating from an array of bytes" in {
      val fixed = Fixed16(Bytes)
      fixed.bytes must beEqualTo(BytesString)
      toJson(fixed) must beEqualTo(JsonString)
    }

    "support instantiating from a ByteString object" in {
      val fixed = Fixed16(BytesString)
      fixed.bytes must beEqualTo(BytesString)
      toJson(fixed) must beEqualTo(JsonString)
    }

    "support instantiating from a string" in {
      val fixed = Fixed16(AvroString)
      fixed.bytes must beEqualTo(BytesString)
      toJson(fixed) must beEqualTo(JsonString)
    }

  }

}
