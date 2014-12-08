package scala.com.linkedin.sleipnir.test.enumeration

import scala.com.linkedin.sleipnir.test.SleipnirSpec

class EnumTest extends SleipnirSpec {

  "Enum types" should {

    "escape reserved symbol names" in {
      val enum = ReservedWordEnum.`if`
      enum.toString must beEqualTo("if")
    }

  }

}
