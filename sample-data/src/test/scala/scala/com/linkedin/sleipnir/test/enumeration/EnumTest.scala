package scala.com.linkedin.sleipnir.test.enumeration

import scala.com.linkedin.sleipnir.test.SleipnirSpec

class EnumTest extends SleipnirSpec {

  "Enum types" should {

    "escape reserved symbol names" in {
      val enum = ReservedWordsEnum.`if`
      enum.toString must beEqualTo("if")
    }

    "support types with no symbols" in {
      NoSymbolsEnum.values.toSeq must beEqualTo(Seq(NoSymbolsEnum.$Unknown))
    }

  }

}
