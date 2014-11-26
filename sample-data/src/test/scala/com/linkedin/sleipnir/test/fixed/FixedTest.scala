package com.linkedin.sleipnir.test.fixed

import java.security.MessageDigest

import com.linkedin.data.ByteString
import com.linkedin.sleipnir.test.SleipnirSpec
import org.apache.commons.lang.StringEscapeUtils

class FixedTest extends SleipnirSpec {

  private val Bytes = MessageDigest.getInstance("MD5").digest("Yo!".getBytes)

  private val AvroString = "yp\u001F\u00F5 \u001Bs$\n\u00AF\u00BF\"\u00B8i\u0014T"

  private val JsonString = "\"yp\\u001F\u00F5 \\u001Bs$\\n\u00AF\u00BF\\\"\u00B8i\\u0014T\""

  "Fixed types" should {

    "should support instantiating from an array of bytes" in {
      val fixed = MD5(Bytes)
      toJson(fixed) must beEqualTo(JsonString)
    }

    "should support instantiating from a ByteString object" in {
      val fixed = MD5(ByteString.copy(Bytes))
      toJson(fixed) must beEqualTo(JsonString)
    }

    "should support instantiating from a string" in {
      val fixed = MD5(AvroString)
      toJson(fixed) must beEqualTo(JsonString)
    }

    "should work as a record field" in {
      val fixed = MD5(Bytes)
      val record = MD5Record(fixed)
      val json = toJson(record)
      json must beEqualTo("{\"fixed\":" + JsonString + "}")
      val recordFromJson = fromJson[MD5Record](json)
      recordFromJson.fixed must beEqualTo(fixed)
    }

  }

}
