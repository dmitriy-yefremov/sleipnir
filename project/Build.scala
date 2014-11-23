import sbt._
import Keys._
import twirl.sbt.TwirlPlugin._
import com.linkedin.sbt.MintPlugin

object Sleipnir extends Build {

  val twirlSettings = Twirl.settings ++ Seq(
    libraryDependencies := libraryDependencies.value.filterNot(_.organization == "io.spray")
  )

  lazy val sleipnirGenerator = project.in(file("generator"))
    .settings(
      libraryDependencies ++= Seq(
        "org.clapper" %% "grizzled-slf4j" % "1.0.1",
        "org.slf4j" % "slf4j-simple" % "1.7.2",
        "com.linkedin.pegasus" % "data" % "1.15.9",
        "org.scalariform" %% "scalariform" % "0.1.4",
        "org.scala-lang" % "scala-reflect" % "2.10.3",
        "org.apache.commons" % "commons-lang3" % "3.3.2"
      )
    )
    .settings(twirlSettings: _*)
    .settings(commands ++= Seq(MintPlugin.buildCmd))

  lazy val sleipnirSbtPlugin = project.in(file("sbt-plugin"))
    .settings(
      sbtPlugin := true
    )
    .settings(commands ++= Seq(MintPlugin.buildCmd))
    .dependsOn(sleipnirGenerator)
    .aggregate(sleipnirGenerator)


  lazy val sleipnir = project.in(file(".")).aggregate(sleipnirGenerator, sleipnirSbtPlugin)

}
