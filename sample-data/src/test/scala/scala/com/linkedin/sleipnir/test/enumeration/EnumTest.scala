package scala.com.linkedin.sleipnir.test.enumeration

import scala.com.linkedin.sleipnir.test.SleipnirSpec

class EnumTest extends SleipnirSpec {

  "Enum types" should {

    "escape reserved symbol names" in {
      val enum = ReservedWordsEnum.`sealed`
      enum.toString must beEqualTo("sealed")
    }

    "support types with no symbols" in {
      NoSymbolsEnum.values.toSeq must beEqualTo(Seq(NoSymbolsEnum.$Unknown))
      NoSymbolsEnum.$Unknown.toString must beEqualTo("$Unknown")
    }

  }

}
