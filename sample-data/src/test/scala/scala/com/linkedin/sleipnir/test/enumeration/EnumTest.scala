package scala.com.linkedin.sleipnir.test.enumeration

import scala.com.linkedin.sleipnir.test.{SimpleEnum, SleipnirSpec}

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
