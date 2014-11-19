package org.restli.example.fortune

import java.io.StringReader
import java.security.MessageDigest

import com.linkedin.data.template.JacksonDataTemplateCodec

/**
 * Test playground.
 */
object Test extends App {

  val dataTemplateCodec = new JacksonDataTemplateCodec()

  val fortune1 = Fortune("Today is a good day", FunninessLevel.BLAH, None)
  val fortune2 = Fortune("Tomorrow will be your lucky day", FunninessLevel.LMAO, Some(1.5))

  val fortunes = Seq(fortune1, fortune2)

  val signature = MD5(MessageDigest.getInstance("MD5").digest("Yo!".getBytes))

  val expirationDate = BoxExpirationDateUnion(EpochDate(System.currentTimeMillis()))

  val box = Box(fortunes, Map("1" -> 1, "2" -> 2), expirationDate, signature)
  println(s"Box: $box")

  val json = dataTemplateCodec.dataTemplateToString(box)
  println(s"JSON: $json")

  val boxData = dataTemplateCodec.readMap(new StringReader(json))
  val boxFromJson = new Box(boxData)
  println(s"Box from JSON: $boxFromJson")

  val exp = boxFromJson.expirationDate match {
    case BoxExpirationDateUnion(string: String) => s"Union value is a string: $string"
    case BoxExpirationDateUnion(date: EpochDate) => s"Union value is a date: $date"
  }
  println(exp)

}
