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

package net.yefremov.sleipnir.generator.types

import org.specs2.mutable.Specification


class ReservedWordsEscapingTest extends Specification with ReservedWordsEscaping {

  "escapeScalaReserved(String)" should {

    "return an escaped string for reserved words" in {
      val word = "class"
      escapeScalaReserved(word) must beEqualTo("`class`")
    }

    "return the string as is if it's not a reserved word" in {
      val word = "something"
      escapeScalaReserved(word) must beTheSameAs(word)
    }

    "return the string as is if it's not a full match" in {
      val word = "some.package.name.class"
      escapeScalaReserved(word) must beTheSameAs(word)
    }

  }

  "escapeScalaReserved(TypeName)" should {

    "return an escaped string for reserved words" in {
      val name = TypeName("trait", "com.linkedin.object", "com.linkedin.object.trait", "com.linkedin.object.trait.type")
      val escaped = TypeName("`trait`", "com.linkedin.`object`", "com.linkedin.`object`.`trait`", "com.linkedin.`object`.`trait`.`type`")
      escapeScalaReserved(name) must beEqualTo(escaped)
    }

  }

  "escapePegasusReserved" should {

    "return an escaped string for reserved methods" in {
      val word = "data"
      escapePegasusReserved(word) must beEqualTo("`data_`")
    }

    "return the string as is if it's not a reserved word" in {
      val word = "something"
      escapePegasusReserved(word) must beTheSameAs(word)
    }

    "return the string as is if it's not a full match" in {
      val word = "some.package.name.data"
      escapePegasusReserved(word) must beTheSameAs(word)
    }

  }

}
