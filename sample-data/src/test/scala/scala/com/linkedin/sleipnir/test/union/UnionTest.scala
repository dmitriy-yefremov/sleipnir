package scala.com.linkedin.sleipnir.test.union

import scala.com.linkedin.sleipnir.test.{SimpleRecord, SleipnirSpec}

class UnionTest extends SleipnirSpec {

  "Union types" should {

    "support instantiating from all underlying types" in {
      val unionFromString = UnionRecordFieldUnion(StringValue)
      checkSerialization(unionFromString, """{"string":"string value"}""")
      val unionFromRecord = UnionRecordFieldUnion(SimpleRecordValue)
      checkSerialization(unionFromRecord, """{"com.linkedin.sleipnir.test.SimpleRecord":{"field":"string value"}}""")
      success
    }

    "support unboxing of the underlying type" in {

      def unbox(union: UnionRecordFieldUnion): Any = {
        union match {
          case UnionRecordFieldUnion(string: String) => string
          case UnionRecordFieldUnion(record: SimpleRecord) => record
        }
      }

      unbox(UnionRecordFieldUnion(StringValue)) must beEqualTo(StringValue)
      unbox(UnionRecordFieldUnion(SimpleRecordValue)) must beEqualTo(SimpleRecordValue)
    }

    "support indirect references" in {
      val union = UnionInArrayRecordFieldUnion(StringValue)
      val array = Seq(union)
      val record = UnionInArrayRecord(array)
      val recordFromJson = checkSerialization(record, """{"field":[{"string":"string value"}]}""")
      recordFromJson.field must beEqualTo(array)
    }

    "work as a record field" in {
      val union = UnionRecordFieldUnion(StringValue)
      val record = UnionRecord(union)
      val json = toJson(record)
      json must beEqualTo("""{"field":{"string":"string value"}}""")
      val recordFromJson = fromJson[UnionRecord](json)
      recordFromJson.field must beEqualTo(union)
    }

  }

}
