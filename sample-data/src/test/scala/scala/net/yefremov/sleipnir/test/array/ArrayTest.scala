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

package scala.net.yefremov.sleipnir.test.array

import com.linkedin.data.ByteString
import net.yefremov.sleipnir.test.CustomPoint
import scala.net.yefremov.sleipnir.test.{SimpleEnum,SleipnirSpec}

class ArrayTest extends SleipnirSpec {

  "Array types" should {

    "support primitive values" in {
      val array = Seq(StringValue)
      val record = ArrayPrimitiveRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["string value"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support complex values" in {
      val array = Seq(SimpleRecordValue)
      val record = ArrayComplexRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":[{"field":"string value"}]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support enum values" in {
      val array = Seq(SimpleEnum.Foo)
      val record = ArrayEnumRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["Foo"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support unknown enum values" in {
      val arrayEnumRecordSchema = ArrayEnumRecord(Seq()).schema()
      val recordFromJson = fromJson[ArrayEnumRecord]("""{"arrayField":["Baz"]}""", arrayEnumRecordSchema)
      recordFromJson.arrayField must beEqualTo(Seq(SimpleEnum.$Unknown))
    }

    "support bytes values" in {
      val array = Seq(ByteString.copy(Array[Byte](100)))
      val record = ArrayBytesRecord(array)
      record.arrayField must beEqualTo(array)
      val recordFromJson = checkSerialization(record, """{"arrayField":["d"]}""")
      recordFromJson.arrayField must beEqualTo(array)
    }

    "support custom java bindings" in {
      val customPoint = new CustomPoint(1, 2)
      val customPoints = Seq(customPoint)
      val record = CustomPointArrayRecord(customPoints)
      record.field must beEqualTo(customPoints)
      val recordFromJson = checkSerialization(record, """{"field":["1,2"]}""")
      recordFromJson.field must beEqualTo(customPoints)
    }

    "support custom names through typerefs" in {
      val array = Seq(SimpleRecordValue)
      val customArray = CustomNamedArray(array)
      customArray.items must beEqualTo(array)
      val customArrayFromJson = checkSerialization(customArray, """[{"field":"string value"}]""")
      customArrayFromJson.items must beEqualTo(array)
    }

  }

}
