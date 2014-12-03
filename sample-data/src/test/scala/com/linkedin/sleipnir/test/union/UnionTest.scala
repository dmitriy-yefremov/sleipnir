package com.linkedin.sleipnir.test.union

import com.linkedin.sleipnir.test.{SimpleRecord, SleipnirSpec}

class UnionTest extends SleipnirSpec {

  "Union types" should {

    "should support instantiating from all underlying types" in {

      val unionFromString = UnionRecordFieldUnion(StringValue)
      toJson(unionFromString) must beEqualTo("""{"string":"string value"}""")
      val unionFromRecord = UnionRecordFieldUnion(SimpleRecordValue)
      toJson(unionFromRecord) must beEqualTo("""{"com.linkedin.sleipnir.test.SimpleRecord":{"field":"string value"}}""")
    }

    "should support unboxing of the underlying type" in {

      def unbox(union: UnionRecordFieldUnion): Any = {
        union match {
          case UnionRecordFieldUnion(string: String) => string
          case UnionRecordFieldUnion(record: SimpleRecord) => record
        }
      }

      unbox(UnionRecordFieldUnion(StringValue)) must beEqualTo(StringValue)
      unbox(UnionRecordFieldUnion(SimpleRecordValue)) must beEqualTo(SimpleRecordValue)
    }

    "should work as a record field" in {
      val union = UnionRecordFieldUnion(StringValue)
      val record = UnionRecord(union)
      val json = toJson(record)
      json must beEqualTo("""{"field":{"string":"string value"}}""")
      val recordFromJson = fromJson[UnionRecord](json)
      recordFromJson.field must beEqualTo(union)
    }

  }

}
