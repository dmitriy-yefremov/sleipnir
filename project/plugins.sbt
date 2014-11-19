//resolvers += "spray repo" at "http://repo.spray.io"

//addSbtPlugin("io.spray" % "sbt-twirl" % "0.7.0")

//sbt-twirl depends in scala-io, so adding it here

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
