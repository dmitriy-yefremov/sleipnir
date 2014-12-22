package com.linkedin.sleipnir.generator.types

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
