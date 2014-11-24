import sbt._
import Keys._
import twirl.sbt.TwirlPlugin._
import com.linkedin.sbt.MintPlugin
import com.linkedin.sbt.core.ext.LiKeys._

object Sleipnir extends Build {

  //TODO: dyefremo: filter out all "spray.io" dependencies as we don't have them in the artifactory
  val twirlSettings = Twirl.settings ++ Seq(
    libraryDependencies := libraryDependencies.value.filterNot(_.organization == "io.spray")
  )

  lazy val sleipnirGenerator = project.in(file("generator"))
    .settings(
      productSpecDependencies ++= Seq(
        "external.grizzled-slf4j",
        "external.slf4j-simple",
        "external.commons-lang",
        "external.scalariform",
        "external.scala-reflect",
        "product.pegasus.data"
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
