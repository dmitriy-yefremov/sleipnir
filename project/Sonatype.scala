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

import sbt.Keys._
import xerial.sbt.Sonatype.SonatypeKeys._

object Sonatype {

  val Settings = Seq(

    sonatypeProfileName := "net.yefremov",

    pomExtra := {
      <url>https://github.com/dmitriy-yefremov/sleipnir</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/dmitriy-yefremov/sleipnir.git</connection>
        <developerConnection>scm:git:git@github.com:dmitriy-yefremov/sleipnir.git</developerConnection>
        <url>github.com/dmitriy-yefremov/sleipnir</url>
      </scm>
      <developers>
        <developer>
          <id>dyefremov</id>
          <name>Dmitriy Yefremov</name>
          <url>http://yefremov.net/</url>
        </developer>
        <developer>
          <id>apadmarao</id>
          <name>Anirudh Padmarao</name>
          <url>https://www.linkedin.com/pub/anirudh-padmarao/58/357/a56</url>
        </developer>
      </developers>
    }
  )

}