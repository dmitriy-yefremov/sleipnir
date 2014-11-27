package com.linkedin.sleipnir.test.union

import com.linkedin.sleipnir.test.{SimpleRecord, SleipnirSpec}

class UnionTest extends SleipnirSpec {

  "Union types" should {

    "should support instantiating from all underlying types" in {

      val unionFromString = UnionRecordUnionUnion(StringValue)
      toJson(unionFromString) must beEqualTo("""{"string":"string value"}""")
      val unionFromRecord = UnionRecordUnionUnion(SimpleRecordValue)
      toJson(unionFromRecord) must beEqualTo("""{"com.linkedin.sleipnir.test.SimpleRecord":{"field":"string value"}}""")
    }

    "should support unboxing of the underlying type" in {

      def unbox(union: UnionRecordUnionUnion): Any = {
        union match {
          case UnionRecordUnionUnion(string: String) => string
          case UnionRecordUnionUnion(record: SimpleRecord) => record
        }
      }

      unbox(UnionRecordUnionUnion(StringValue)) must beEqualTo(StringValue)
      unbox(UnionRecordUnionUnion(SimpleRecordValue)) must beEqualTo(SimpleRecordValue)
    }

    "should work as a record field" in {
      val union = UnionRecordUnionUnion(StringValue)
      val record = UnionRecord(union)
      val json = toJson(record)
      json must beEqualTo("""{"union":{"string":"string value"}}""")
      val recordFromJson = fromJson[UnionRecord](json)
      recordFromJson.union must beEqualTo(union)
    }

  }

}
