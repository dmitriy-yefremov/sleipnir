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

    "support primitive types" in {
      val array = Seq(StringValue)
      val record = ArrayPrimitiveRecord(array)
      toJson(record) must beEqualTo("""{"arrayField":["string value"]}""")
      record.arrayField must beEqualTo(array)
    }

    "support complex types" in {
      val array = Seq(SimpleRecordValue)
      val record = ArrayComplexRecord(array)
      toJson(record) must beEqualTo("""{"arrayField":[{"field":"string value"}]}""")
      record.arrayField must beEqualTo(array)
    }

  }

  "Map fields" should {

    "support primitive types" in {
      val map = Map("key" -> StringValue)
      val record = MapPrimitiveRecord(map)
      toJson(record) must beEqualTo("""{"mapField":{"key":"string value"}}""")
      record.mapField must beEqualTo(map)
    }

    "support complex types" in {
      val map = Map("key" -> SimpleRecordValue)
      val record = MapComplexRecord(map)
      toJson(record) must beEqualTo("""{"mapField":{"key":{"field":"string value"}}}""")
      record.mapField must beEqualTo(map)
    }

  }

  "Record fields" should {

    "be supported" in {
      val record = RecordRecord(SimpleRecordValue)
      toJson(record) must beEqualTo("""{"recordField":{"field":"string value"}}""")
      record.recordField must beEqualTo(SimpleRecordValue)
    }

  }

  "Optional fields" should {

    "support primitive types" in {
      val record = OptionalPrimitiveRecord(Some(StringValue))
      val json = toJson(record)
      json must beEqualTo("""{"stringOption":"string value"}""")
      val recordFromJson = fromJson[OptionalPrimitiveRecord](json)
      recordFromJson.stringOption must beEqualTo(Some(StringValue))
    }

    "support primitive types not set" in {
      val record = OptionalPrimitiveRecord(None)
      val json = toJson(record)
      json must beEqualTo("{}")
      val recordFromJson = fromJson[OptionalPrimitiveRecord](json)
      recordFromJson.stringOption must beEqualTo(None)
    }

    "support complex types" in {
      val record = OptionalComplexRecord(Some(SimpleRecordValue))
      val json = toJson(record)
      json must beEqualTo("""{"recordOption":{"field":"string value"}}""")
      val recordFromJson = fromJson[OptionalComplexRecord](json)
      recordFromJson.recordOption must beEqualTo(Some(SimpleRecordValue))
    }

    "support complex types not set" in {
      val record = OptionalComplexRecord(None)
      val json = toJson(record)
      json must beEqualTo("{}")
      val recordFromJson = fromJson[OptionalComplexRecord](json)
      recordFromJson.recordOption must beEqualTo(None)
    }

  }

}
