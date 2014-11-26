package org.restli.example.fortune

import java.io.StringReader

import com.linkedin.data.template.JacksonDataTemplateCodec

/**
 * Test playground.
 */
object Test extends App {

  val dataTemplateCodec = new JacksonDataTemplateCodec()

  val fortune1 = Fortune("Today is a good day", FunninessLevel.BLAH, None)
  val fortune2 = Fortune("Tomorrow will be your lucky day", FunninessLevel.LMAO, Some(1.5))

  val fortunes = Seq(fortune1, fortune2)

  val box = Box(fortunes, Map("1" -> 1, "2" -> 2))
  println(s"Box: $box")

  val json = dataTemplateCodec.dataTemplateToString(box)
  println(s"JSON: $json")

  val boxData = dataTemplateCodec.readMap(new StringReader(json))
  val boxFromJson = new Box(boxData)
  println(s"Box from JSON: $boxFromJson")

}
