# Sleipnir [![Build Status](https://travis-ci.org/dmitriy-yefremov/sleipnir.svg?branch=master)](https://travis-ci.org/dmitriy-yefremov/sleipnir)
Sleipnir is a generator of native Scala bindings for rest.li [Pegasus](https://github.com/linkedin/rest.li/wiki/DATA-Data-Schema-and-Templates) schemas. The code is implemented with the following ideas in mind:

1. Use as much of Pegasus infrastructure as possible: schema files parsing is reused, generated classes extend RecordTemplate/DataTemplate and work on top of DataMap and so on.
2. Low tech code generation: we use Twirl templates (aka Play Scala templates), more on this here.

The [sleipnir-sample](https://github.com/dmitriy-yefremov/sleipnir-sample) project uses Sleipnir to generate Scala bindings for a Play server.
# Usage
You can use Sleipnir in a couple ways: as a command line tool or as an SBT plugin.

## Command Line Tool
In SBT, run:

    project sleipnirGenerator
    runMain net.yefremov.sleipnir.Sleipnir <resolving path> <source dir> <target dir> [namespace prefix]

For example, to generate bindings for the schemas in the sample-data project, run:

    runMain net.yefremov.sleipnir.Sleipnir  sample-data/src/main/pegasus sample-data/src/main/pegasus sample-data/src/main/codegen

## Sbt Plugin
To use Sleipnir in your SBT project, you should add the sleipnir plugin, and add its settings to your project.
1. Add the following line to plugins.sbt:
        addSbtPlugin("net.yefremov.sleipnir" % "sleipnir-sbt-plugin" % "<version>")
2. Add the sleipnir settings to the project that defines the Pegasus schemas:
        import net.yefremov.sleipnir.sbt.SleipnirPlugin._

        lazy val exampleProject = Project(...)
            .settings(sleipnirSettings: _*)
            .settings(
              sleipnirSourceDirectory := baseDirectory.value / "data" / "src" / "main" / "pegasus",
              sleipnirDestinationDirectory := baseDirectory.value / "data" / "src" / "main" / "codegen"
            )

The changes above will enable generation of Scala bindings for PDSC files defined within your project. You may also want to generate bindings for PDSC files defined in a dependency of your project. This is necessary if the dependency does not generate Scala bindings as a part of its own build process.

To generate bindings for PDSC files of all dependencies:

        lazy val exampleProject = Project(...)
            .settings(sleipnirDownstreamSettings: _*)

Also, you may enable filtering of dependencies that will be processed:

        lazy val exampleProject = Project(...)
            .settings(sleipnirDownstreamSettings: _*)
            .settings(dataTemplatesDependenciesFilter := DependencyFilter.allPass -- moduleFilter(organization = "net.yefremov.example-service"))

Only dependencies unfiltered by the dataTemplatesDependenciesFilter will have Scala bindings generated.
