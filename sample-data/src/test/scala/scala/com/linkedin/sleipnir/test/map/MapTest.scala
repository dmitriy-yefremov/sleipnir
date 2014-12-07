package scala.com.linkedin.sleipnir.test.map

import com.linkedin.data.ByteString
import scala.com.linkedin.sleipnir.test.{SimpleEnum, SleipnirSpec}

class MapTest extends SleipnirSpec {

  "Map types" should {

    "support primitive values" in {
      val map = Map("key" -> StringValue)
      val record = MapPrimitiveRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":"string value"}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

    "support complex values" in {
      val map = Map("key" -> SimpleRecordValue)
      val record = MapComplexRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":{"field":"string value"}}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

    "support enum values" in {
      val map = Map("key" -> SimpleEnum.Foo)
      val record = MapEnumRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":"Foo"}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

    "support bytes values" in {
      val map = Map("key" -> ByteString.copy(Array[Byte](100)))
      val record = MapBytesRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":"d"}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

    "support custom names through typerefs" in {
      val map = Map("key" -> SimpleRecordValue)
      val wrapper = CustomNamedMap(map)
      wrapper.map must beEqualTo(map)
      val wrapperFromJson = checkSerialization(wrapper, """{"key":{"field":"string value"}}""")
      wrapperFromJson.map must beEqualTo(map)
    }
  }

}
