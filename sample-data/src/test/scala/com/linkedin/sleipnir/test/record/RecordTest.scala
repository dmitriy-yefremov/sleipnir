package com.linkedin.sleipnir.test.record

import com.linkedin.sleipnir.test.SleipnirSpec

class RecordTest extends SleipnirSpec {

  "Enum fields" should {

    "be supported" in {
      val record = EnumRecord(TestEnum.Foo)
      record.enum must beEqualTo(TestEnum.Foo)
      val recordFromJson = checkSerialization(record, """{"enum":"Foo"}""")
      recordFromJson.enum must beEqualTo(TestEnum.Foo)
    }

  }

  "Array fields" should {

    "support primitive types" in {
      val array = Seq(StringValue)
      val record = ArrayPrimitiveRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["string value"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support complex types" in {
      val array = Seq(SimpleRecordValue)
      val record = ArrayComplexRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":[{"field":"string value"}]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

  }

  "Map fields" should {

    "support primitive types" in {
      val map = Map("key" -> StringValue)
      val record = MapPrimitiveRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":"string value"}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

    "support complex types" in {
      val map = Map("key" -> SimpleRecordValue)
      val record = MapComplexRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":{"field":"string value"}}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

  }

  "Record fields" should {

    "be supported" in {
      val record = RecordRecord(SimpleRecordValue)
      record.recordField must beEqualTo(SimpleRecordValue)
      val recordFromJson = checkSerialization(record, """{"recordField":{"field":"string value"}}""")
      recordFromJson.recordField must beEqualTo(SimpleRecordValue)
    }

  }

  "Fixed fields" should {

    "be supported" in {
      val fixed = Fixed1("a")
      val record = FixedRecord(fixed)
      record.fixedField must beEqualTo(fixed)
      val recordFromJson = checkSerialization(record, """{"fixedField":"a"}""")
      recordFromJson.fixedField must beEqualTo(fixed)
    }

  }

  "Optional fields" should {

    "support primitive types" in {
      val record = OptionalPrimitiveRecord(Some(StringValue))
      record.stringOption must beEqualTo(Some(StringValue))
      val recordFromJson = checkSerialization(record, """{"stringOption":"string value"}""")
      recordFromJson.stringOption must beEqualTo(Some(StringValue))
    }

    "support primitive types not set" in {
      val record = OptionalPrimitiveRecord(None)
      record.stringOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.stringOption must beEqualTo(None)
    }

    "support complex types" in {
      val record = OptionalComplexRecord(Some(SimpleRecordValue))
      record.recordOption must beEqualTo(Some(SimpleRecordValue))
      val recordFromJson = checkSerialization(record, """{"recordOption":{"field":"string value"}}""")
      recordFromJson.recordOption must beEqualTo(Some(SimpleRecordValue))
    }

    "support complex types not set" in {
      val record = OptionalComplexRecord(None)
      record.recordOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.recordOption must beEqualTo(None)
    }

    "support array types" in {
      val arrayOption = Some(Seq(StringValue))
      val record = OptionalArrayPrimitiveRecord(arrayOption)
      record.arrayOption must beEqualTo(arrayOption)
      val recordFromJson = checkSerialization(record, """{"arrayOption":["string value"]}""")
      recordFromJson.arrayOption must beEqualTo(arrayOption)
    }

    "support array types not set" in {
      val record = OptionalArrayPrimitiveRecord(None)
      record.arrayOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.arrayOption must beEqualTo(None)
    }

  }

  "Recursive references" should {

    "be supported" in {
      val tailRecord = RecursiveRecord(1, None)
      val record = RecursiveRecord(2, Some(tailRecord))
      record.tail must beEqualTo(Some(tailRecord))
      val recordFromJson = checkSerialization(record, """{"head":2,"tail":{"head":1}}""")
      recordFromJson.tail must beEqualTo(Some(tailRecord))
    }

  }

}
