package scala.com.linkedin.sleipnir.test.union

import scala.com.linkedin.sleipnir.test.{SimpleEnum, SimpleRecord, SleipnirSpec}

class UnionTest extends SleipnirSpec {

  "Union types" should {

    "support primitive types" in {
      val union = StringOrSimpleRecord(StringValue)
      val unionFromJson = checkSerialization(union, """{"string":"string value"}""")
      unionFromJson.asString must beEqualTo(Some(StringValue))
    }

    "support record types" in {
      val union = StringOrSimpleRecord(SimpleRecordValue)
      val unionFromJson = checkSerialization(union, """{"com.linkedin.sleipnir.test.SimpleRecord":{"field":"string value"}}""")
      unionFromJson.asSimpleRecord must beEqualTo(Some(SimpleRecordValue))
    }

    "support enum types" in {
      val union = EnumUnion(SimpleEnum.Foo)
      val unionFromJson = checkSerialization(union, """{"com.linkedin.sleipnir.test.SimpleEnum":"Foo"}""")
      unionFromJson.asSimpleEnum must beEqualTo(Some(SimpleEnum.Foo))
    }

    "support array types" in {
      val array = Seq(StringValue)
      val union = ArrayUnion(array)
      val unionFromJson = checkSerialization(union, """{"array":["string value"]}""")
      unionFromJson.asStringArray must beEqualTo(Some(array))
    }

    "support map types" in {
      val map = Map("key" -> StringValue)
      val union = MapUnion(map)
      val unionFromJson = checkSerialization(union, """{"map":{"key":"string value"}}""")
      unionFromJson.asStringMap must beEqualTo(Some(map))
    }

    "support unboxing of the underlying type" in {

      def unbox(union: StringOrSimpleRecord): Any = {
        // an example of how to unbox union types
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

    "support null" in {
      val union = UnionWithNull.NullInstance
      toJson(union) must beEqualTo("""null""")
      union.isNull must beTrue
      union match {
        case UnionWithNull(string: String) => failure
        case nullUnion if nullUnion.isNull => success
      }
    }

  }

}
