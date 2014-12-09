package scala.com.linkedin.sleipnir.test.typeref

import scala.com.linkedin.sleipnir.test.SleipnirSpec

class TyperefTest extends SleipnirSpec {

  "Typeref types" should {

    "not override names for predefined types" in {
      val record = PrimitiveTyperefRecord(StringValue)
      record.field must beEqualTo(StringValue)
      val recordFromJson = checkSerialization(record, """{"field":"string value"}""")
      recordFromJson.field must beEqualTo(StringValue)

    }

  }

}
