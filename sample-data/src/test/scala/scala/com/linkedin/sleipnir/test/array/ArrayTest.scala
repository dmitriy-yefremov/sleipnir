package scala.com.linkedin.sleipnir.test.array

import com.linkedin.data.ByteString
import scala.com.linkedin.sleipnir.test.{SimpleEnum,SleipnirSpec}
import com.linkedin.sleipnir.test.CustomPoint

class ArrayTest extends SleipnirSpec {

  "Array types" should {

    "support primitive values" in {
      val array = Seq(StringValue)
      val record = ArrayPrimitiveRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["string value"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support complex values" in {
      val array = Seq(SimpleRecordValue)
      val record = ArrayComplexRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":[{"field":"string value"}]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support enum values" in {
      val array = Seq(SimpleEnum.Foo)
      val record = ArrayEnumRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["Foo"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support unknown enum values" in {
      val arrayEnumRecordSchema = ArrayEnumRecord(Seq()).schema()
      val recordFromJson = fromJson[ArrayEnumRecord]("""{"arrayField":["Baz"]}""", arrayEnumRecordSchema)
      recordFromJson.arrayField must beEqualTo(Seq(SimpleEnum.$Unknown))
    }

    "support bytes values" in {
      val array = Seq(ByteString.copy(Array[Byte](100)))
      val record = ArrayBytesRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["d"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support custom java bindings" in {
      val customPoint = new CustomPoint(1, 2)
      val customPoints = Seq(customPoint)
      val record = CustomPointArrayRecord(customPoints)
      record.field must beEqualTo(customPoints)
      val recordFromJson = checkSerialization(record, """{"field":["1,2"]}""")
      recordFromJson.field must beEqualTo(customPoints)
    }

  }

}
