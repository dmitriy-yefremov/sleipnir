/*
 *    Copyright 2015 Dmitriy Yefremov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package scala.net.yefremov.sleipnir.test.record

import net.yefremov.sleipnir.test.CustomPoint

import scala.net.yefremov.sleipnir.test.{SimpleEnum, SimpleRecord, SleipnirSpec}

class RecordTest extends SleipnirSpec {

  "Primitive fields" should {

    "support boolean types" in {
      val field = false
      val record = BooleanRecord(field)
      record.field must beEqualTo(field)
      val recordFromJson = checkSerialization(record, """{"field":false}""")
      recordFromJson.field must beEqualTo(field)
    }

  }

  "Enum fields" should {

    "be supported" in {
      val record = EnumRecord(SimpleEnum.Foo)
      record.enum must beEqualTo(SimpleEnum.Foo)
      val recordFromJson = checkSerialization(record, """{"enum":"Foo"}""")
      recordFromJson.enum must beEqualTo(SimpleEnum.Foo)
    }

    "support unknown values" in {
      val enumRecordSchema = EnumRecord(SimpleEnum.Foo).schema()
      val recordFromJson = fromJson[EnumRecord]("""{"enum":"Baz"}""", enumRecordSchema)
      recordFromJson.enum must beEqualTo(SimpleEnum.$Unknown)
    }

  }

  "Array fields" should {

    "be supported" in {
      val array = Seq(SimpleRecordValue)
      val record = ArrayRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":[{"field":"string value"}]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

  }

  "Map fields" should {

    "be supported" in {
      val map = Map("key" -> SimpleRecordValue)
      val record = MapRecord(map)
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

    "default to None" in {
      val record = OptionalPrimitiveRecord()
      record.stringOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.stringOption must beEqualTo(None)
    }

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

    "support primitive array types" in {
      val arrayOption = Some(Seq(StringValue))
      val record = OptionalArrayPrimitiveRecord(arrayOption)
      record.arrayOption must beEqualTo(arrayOption)
      val recordFromJson = checkSerialization(record, """{"arrayOption":["string value"]}""")
      recordFromJson.arrayOption must beEqualTo(arrayOption)
    }

    "support primitive array types not set" in {
      val record = OptionalArrayPrimitiveRecord(None)
      record.arrayOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.arrayOption must beEqualTo(None)
    }

    "support complex array types" in {
      val arrayOption = Some(Seq(SimpleRecordValue))
      val record = OptionalArrayComplexRecord(arrayOption)
      record.arrayOption must beEqualTo(arrayOption)
      val recordFromJson = checkSerialization(record, """{"arrayOption":[{"field":"string value"}]}""")
      recordFromJson.arrayOption must beEqualTo(arrayOption)
    }

    "support complex array types not set" in {
      val record = OptionalArrayComplexRecord(None)
      record.arrayOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.arrayOption must beEqualTo(None)
    }

    "support primitive map types" in {
      val map = Some(Map("key" -> StringValue))
      val record = OptionalMapPrimitiveRecord(map)
      record.mapOption must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapOption":{"key":"string value"}}""")
      recordFromJson.mapOption must beEqualTo(map)
    }

    "support primitive map types not set" in {
      val record = OptionalMapPrimitiveRecord(None)
      record.mapOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.mapOption must beEqualTo(None)
    }

    "support complex map types" in {
      val map = Some(Map("key" -> SimpleRecordValue))
      val record = OptionalMapComplexRecord(map)
      record.mapOption must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapOption":{"key":{"field":"string value"}}}""")
      recordFromJson.mapOption must beEqualTo(map)
    }

    "support complex map types not set" in {
      val record = OptionalMapComplexRecord(None)
      record.mapOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.mapOption must beEqualTo(None)
    }

    "support enum types" in {
      val enum = Some(SimpleEnum.Foo)
      val record = OptionalEnumRecord(enum)
      record.enumOption must beEqualTo(enum)
      val recordFromJson = checkSerialization(record, """{"enumOption":"Foo"}""")
      recordFromJson.enumOption must beEqualTo(enum)
    }

    "support enum types not set" in {
      val record = OptionalEnumRecord(None)
      record.enumOption must beEqualTo(None)
      val recordFromJson = checkSerialization(record, "{}")
      recordFromJson.enumOption must beEqualTo(None)
    }

  }

  "Union fields" should {

    "be supported" in {
      val union = UnionRecordFieldUnion(StringValue)
      val record = UnionRecord(union)
      record.field must beEqualTo(union)
      val recordFromJson = checkSerialization(record, """{"field":{"string":"string value"}}""")
      recordFromJson.field must beEqualTo(union)
    }

  }

  "Typeref fields" should {

    "support custom java bindings" in {
      val customPoint = new CustomPoint(1, 2)
      val record = CustomPointRecord(customPoint)
      record.field must beEqualTo(customPoint)
      val recordFromJson = checkSerialization(record, """{"field":"1,2"}""")
      recordFromJson.field must beEqualTo(customPoint)
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

  "Scala reserved words" should {

    "be escaped" in {
      val record = ReservedWordsRecord(StringValue, `type`.$Unknown)
      record.`object` must beEqualTo(StringValue)
      record.`type` must beEqualTo(`type`.$Unknown)
      val recordFromJson = checkSerialization(record, """{"object":"string value","type":"$Unknown"}""")
      recordFromJson.`object` must beEqualTo(StringValue)
      recordFromJson.`type` must beEqualTo(`type`.$Unknown)
    }

  }

  "Pegasus reserved words" should {

    "be escaped" in {
      val record = ClashingFieldRecord(StringValue)
      record.`data_` must beEqualTo(StringValue)
      val recordFromJson = checkSerialization(record, """{"data":"string value"}""")
      recordFromJson.`data_` must beEqualTo(StringValue)
    }

  }

  "Default fields" should {

    "use default when value not specified" in {
      val record = DefaultRecord()
      record.field must beEqualTo("default")
      val recordFromJson = checkSerialization(record, """{}""")
      recordFromJson.field must beEqualTo("default")
    }

    "use value when specified" in {
      val record = DefaultRecord(Some(StringValue))
      record.field must beEqualTo(StringValue)
      val recordFromJson = checkSerialization(record, """{"field":"string value"}""")
      recordFromJson.field must beEqualTo(StringValue)
    }

  }

  "Unboxing" should {

    "be supported" in {
      val record = RecordRecord(SimpleRecordValue)
      record match {
        case RecordRecord(SimpleRecord(string)) => string must beEqualTo(StringValue)
      }
    }

  }

  "Over 22 fields" should {

    "be supported" in {
      val record = ManyFieldsRecord()
      checkSerialization(record, """{}""")
      success
    }

  }

}
