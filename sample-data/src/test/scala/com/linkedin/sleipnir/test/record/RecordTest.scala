package com.linkedin.sleipnir.test.record

import com.linkedin.sleipnir.test.SleipnirSpec

class RecordTest extends SleipnirSpec {

  "Enum fields" should {

    "be supported" in {
      val record = EnumRecord(TestEnum.Foo)
      toJson(record) must beEqualTo("""{"enum":"Foo"}""")
      record.enum must beEqualTo(TestEnum.Foo)
    }

  }

  "Array fields" should {

    "should support primitive types" in {
      val array = Seq(StringValue)
      val record = ArrayPrimitiveRecord(array)
      toJson(record) must beEqualTo("""{"arrayField":["string value"]}""")
      record.arrayField must beEqualTo(array)
    }

    "should support complex types" in {
      val array = Seq(SimpleRecordValue)
      val record = ArrayComplexRecord(array)
      toJson(record) must beEqualTo("""{"arrayField":[{"field":"string value"}]}""")
      record.arrayField must beEqualTo(array)
    }

  }

  "Optional fields" should {

    "should support primitive types" in {
      val record = OptionalPrimitiveRecord(Some(StringValue))
      val json = toJson(record)
      json must beEqualTo("""{"stringOption":"string value"}""")
      val recordFromJson = fromJson[OptionalPrimitiveRecord](json)
      recordFromJson.stringOption must beEqualTo(Some(StringValue))
    }

    "should support primitive types not set" in {
      val record = OptionalPrimitiveRecord(None)
      val json = toJson(record)
      json must beEqualTo("{}")
      val recordFromJson = fromJson[OptionalPrimitiveRecord](json)
      recordFromJson.stringOption must beEqualTo(None)
    }

    "should support complex types" in {
      val record = OptionalComplexRecord(Some(SimpleRecordValue))
      val json = toJson(record)
      json must beEqualTo("""{"recordOption":{"field":"string value"}}""")
      val recordFromJson = fromJson[OptionalComplexRecord](json)
      recordFromJson.recordOption must beEqualTo(Some(SimpleRecordValue))
    }

    "should support complex types not set" in {
      val record = OptionalComplexRecord(None)
      val json = toJson(record)
      json must beEqualTo("{}")
      val recordFromJson = fromJson[OptionalComplexRecord](json)
      recordFromJson.recordOption must beEqualTo(None)
    }

  }

}
