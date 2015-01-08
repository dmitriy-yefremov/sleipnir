package scala.com.linkedin.sleipnir.test.typeref

import scala.com.linkedin.sleipnir.test.SleipnirSpec
import com.linkedin.sleipnir.test.CustomString

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
