package scala.com.linkedin.sleipnir.test.fixed

import java.security.MessageDigest

import com.linkedin.data.ByteString

import scala.com.linkedin.sleipnir.test.SleipnirSpec

class FixedTest extends SleipnirSpec {

  private val Bytes = MessageDigest.getInstance("MD5").digest("Yo!".getBytes)

  private val BytesString = ByteString.copy(Bytes)

  private val AvroString = "yp\u001F\u00F5 \u001Bs$\n\u00AF\u00BF\"\u00B8i\u0014T"

  private val JsonString = "\"yp\\u001F\u00F5 \\u001Bs$\\n\u00AF\u00BF\\\"\u00B8i\\u0014T\""

  "Fixed types" should {

    "should support instantiating from an array of bytes" in {
      val fixed = Fixed16(Bytes)
      fixed.bytes must beEqualTo(BytesString)
      toJson(fixed) must beEqualTo(JsonString)
    }

    "should support instantiating from a ByteString object" in {
      val fixed = Fixed16(BytesString)
      fixed.bytes must beEqualTo(BytesString)
      toJson(fixed) must beEqualTo(JsonString)
    }

    "should support instantiating from a string" in {
      val fixed = Fixed16(AvroString)
      fixed.bytes must beEqualTo(BytesString)
      toJson(fixed) must beEqualTo(JsonString)
    }

  }

}
