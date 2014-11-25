import sbt._
import Keys._
import twirl.sbt.TwirlPlugin._
import com.linkedin.sbt.MintPlugin
import com.linkedin.sbt.core.ext.LiKeys._

object Sleipnir extends Build {

  /**
   * A dummy aggregator project. It does nothing by itself, but lets us see the root folder in Intellij.
   */
  lazy val sleipnir = project.in(file(".")).aggregate(sleipnirGenerator, sleipnirSbtPlugin, sampleData)

  /**
   * The generator project. For now it includes both the code to generate Scala classes and the runtime code needed to
   * use the generated classes.
   */
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
    .settings(Twirl.settings: _*)
    .settings(commands ++= Seq(MintPlugin.buildCmd))

  /**
   * The generator in the form of an SBT plugin.
   */
  lazy val sleipnirSbtPlugin = project.in(file("sbt-plugin"))
    .dependsOn(sleipnirGenerator)
    .aggregate(sleipnirGenerator)
    .settings(
      sbtPlugin := true
    )
    .settings(commands ++= Seq(MintPlugin.buildCmd))

  /**
   * Some sample PDSC files to test the generator. All the craziness below is needed to build the generator and actually
   * use it withing the same SBT project.
   */
  lazy val sampleData = project.in(file("sample-data"))
    .dependsOn(sleipnirGenerator)
    .settings(forkedVmSleipnirGeneratorSettings: _*)

  lazy val forkedVmSleipnirGenerator = taskKey[Seq[File]]("Sleipnir generator executed in a forked VM")

  val forkedVmSleipnirGeneratorSettings = Seq(
    forkedVmSleipnirGenerator in Compile := {
      val src = sourceDirectory.value / "main" / "pegasus"
      val dst = sourceManaged.value
      val classpath = (dependencyClasspath in Runtime in sleipnirGenerator).value.files
      runForkedGenerator(src, dst, classpath)
    },
    sourceGenerators in Compile <+= (forkedVmSleipnirGenerator in Compile)
  )

  def runForkedGenerator(src: File, dst: File, classpath: Seq[File]): Seq[File] = {
    val mainClass = "com.linkedin.sleipnir.Sleipnir"
    val args = Seq(src.toString, src.toString, dst.toString)
    val result = new Fork.ForkScala(mainClass).fork(
      None,
      Nil,
      classpath,
      args,
      None,
      false,
      StdoutOutput
    ).exitValue()

    if (result != 0) {
      sys.error("Trouble with code generator")
    }

    Seq.empty
  }

}
