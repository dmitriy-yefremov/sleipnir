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

package scala.net.yefremov.sleipnir.test.enumeration

import scala.net.yefremov.sleipnir.test.{SimpleEnum, SleipnirSpec}

class EnumTest extends SleipnirSpec {

  "Enum types" should {

    "provide the list of all values" in {
      SimpleEnum.values.toSeq === Seq(SimpleEnum.$Unknown, SimpleEnum.Foo, SimpleEnum.Bar)
    }

    "have numerical ids for all values" in {
      SimpleEnum.$Unknown.id === 0
      SimpleEnum.Foo.id === 1
      SimpleEnum.Bar.id === 2
    }

    "support instantiation by name" in {
      val enum = SimpleEnum.withName("Foo")
      enum === SimpleEnum.Foo
    }

    "support fall back to $Unknown" in {
      val enum = SimpleEnum.withNameOrUnknown("unexpected")
      enum === SimpleEnum.$Unknown
    }

    "escape reserved symbol names" in {
      val enum = ReservedWordsEnum.`sealed`
      enum.toString === "sealed"
    }

    "support types with no symbols" in {
      NoSymbolsEnum.values.toSeq === Seq(NoSymbolsEnum.$Unknown)
      NoSymbolsEnum.$Unknown.toString === "$Unknown"
    }

  }

}
