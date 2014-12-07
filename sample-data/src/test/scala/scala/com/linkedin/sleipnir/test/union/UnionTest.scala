package scala.com.linkedin.sleipnir.test.union

import scala.com.linkedin.sleipnir.test.{SimpleRecord, SleipnirSpec}

class UnionTest extends SleipnirSpec {

  "Union types" should {

    "support instantiating from all underlying types" in {
      val unionFromString = StringOrSimpleRecord(StringValue)
      checkSerialization(unionFromString, """{"string":"string value"}""")
      val unionFromRecord = StringOrSimpleRecord(SimpleRecordValue)
      checkSerialization(unionFromRecord, """{"com.linkedin.sleipnir.test.SimpleRecord":{"field":"string value"}}""")
      success
    }

    "support unboxing of the underlying type" in {

      def unbox(union: StringOrSimpleRecord): Any = {
        union match {
          case StringOrSimpleRecord(string: String) => string
          case StringOrSimpleRecord(record: SimpleRecord) => record
        }
      }

      unbox(StringOrSimpleRecord(StringValue)) must beEqualTo(StringValue)
      unbox(StringOrSimpleRecord(SimpleRecordValue)) must beEqualTo(SimpleRecordValue)
    }

    "support indirect references" in {
      val union = UnionInArrayRecordFieldUnion(StringValue)
      val array = Seq(union)
      val record = UnionInArrayRecord(array)
      val recordFromJson = checkSerialization(record, """{"field":[{"string":"string value"}]}""")
      recordFromJson.field must beEqualTo(array)
    }

  }

}
