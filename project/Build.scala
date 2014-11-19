import sbt._
import Keys._
import twirl.sbt.TwirlPlugin._

object Sleipnir extends Build {

  lazy val sleipnir = project.in(file(".")).aggregate(generator, sampleData)

  lazy val generator = project
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
        "ch.qos.logback" % "logback-classic" % "1.1.2",
        "com.linkedin.pegasus" % "data" % "1.15.9",
        "org.scalariform" %% "scalariform" % "0.1.4"
      )
    )
    .settings(Twirl.settings: _*)

  lazy val sampleData = project.in(file("sample-data"))
    .dependsOn(generator)
    .settings(
      sleipnirGenerator in Compile := {
        val src = sourceDirectory.value / "main" / "pegasus"
        val dst = sourceManaged.value
        val classpath = (dependencyClasspath in Runtime in generator).value.files
        runSleipnirGenerator(src, dst, classpath)
      },
      sourceGenerators in Compile <+= (sleipnirGenerator in Compile)
    )

  lazy val sleipnirGenerator = taskKey[Seq[File]]("Sleipnir Generator")

  def runSleipnirGenerator(src: File, dst: File, classpath: Seq[File]): Seq[File] = {
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