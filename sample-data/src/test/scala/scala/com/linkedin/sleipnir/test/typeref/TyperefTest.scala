package scala.com.linkedin.sleipnir.test.typeref

import scala.com.linkedin.sleipnir.test.SleipnirSpec
import com.linkedin.sleipnir.test.typeref.CustomPoint

class TyperefTest extends SleipnirSpec {

  "Typeref types" should {

    "not override names for predefined types" in {
      val record = PrimitiveTyperefRecord(StringValue)
      record.field must beEqualTo(StringValue)
      val recordFromJson = checkSerialization(record, """{"field":"string value"}""")
      recordFromJson.field must beEqualTo(StringValue)

    }

    "support custom java class bindings" in {
      val customPoint = new CustomPoint(1, 2)
      val record = CustomPointRecord(customPoint)
      record.field must beEqualTo(customPoint)
      val recordFromJson = checkSerialization(record, """{"field":"1,2"}""")
      recordFromJson.field must beEqualTo(customPoint)

    }

  }

}
