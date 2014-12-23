package scala.com.linkedin.sleipnir.test.map

import com.linkedin.data.ByteString
import com.linkedin.sleipnir.test.CustomPoint
import scala.com.linkedin.sleipnir.test.{SimpleEnum, SleipnirSpec}

class MapTest extends SleipnirSpec {

  "Map types" should {

    "support custom names through typerefs" in {
      val map = Map("key" -> SimpleRecordValue)
      val wrapper = CustomNamedMap(map)
      wrapper.map must beEqualTo(map)
      val wrapperFromJson = checkSerialization(wrapper, """{"key":{"field":"string value"}}""")
      wrapperFromJson.map must beEqualTo(map)
    }

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

    "support unknown enum values" in {
      val mapEnumRecordSchema = MapEnumRecord(Map()).schema()
      val recordFromJson = fromJson[MapEnumRecord]("""{"mapField":{"key":"Baz"}}""", mapEnumRecordSchema)
      recordFromJson.mapField must beEqualTo(Map("key" -> SimpleEnum.$Unknown))
    }

    "support bytes values" in {
      val map = Map("key" -> ByteString.copy(Array[Byte](100)))
      val record = MapBytesRecord(map)
      record.mapField must beEqualTo(map)
      val recordFromJson = checkSerialization(record, """{"mapField":{"key":"d"}}""")
      recordFromJson.mapField must beEqualTo(map)
    }

    "support array values" in {
      val map = Map("key" -> Seq(SimpleRecordValue))
      val wrapper = MapOfArrays(map)
      wrapper.map must beEqualTo(map)
      val wrapperFromJson = checkSerialization(wrapper, """{"key":[{"field":"string value"}]}""")
      wrapperFromJson.map must beEqualTo(map)
    }

    "support map values" in {
      val map = Map("key" -> Map("key" -> SimpleRecordValue))
      val wrapper = MapOfMaps(map)
      wrapper.map must beEqualTo(map)
      val wrapperFromJson = checkSerialization(wrapper, """{"key":{"key":{"field":"string value"}}}""")
      wrapperFromJson.map must beEqualTo(map)
    }

    "support custom java bindings" in {
      val customPointMap = Map("key" -> new CustomPoint(1, 2))
      val wrapper = MapCustomPoint(customPointMap)
      wrapper.map must beEqualTo(customPointMap)
      val wrapperFromJson = checkSerialization(wrapper, """{"key":"1,2"}""")
      wrapperFromJson.map must beEqualTo(customPointMap)
    }
  }

}
