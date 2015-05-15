# Sleipnir [![Build Status](https://travis-ci.org/dmitriy-yefremov/sleipnir.svg?branch=master)](https://travis-ci.org/dmitriy-yefremov/sleipnir)
Sleipnir is a generator of native Scala bindings for rest.li Pegasus schemas. The code is implemented with the following ideas in mind:

1. Use as much of Pegasus infrastructure as possible: schema files parsing is reused, generated classes extend RecordTemplate/DataTemplate and work on top of DataMap and so on.
2. Low tech code generation: we use Twirl templates (aka Play Scala templates), more on this here.

# Usage
You can use Sleipnir in a few different ways: as a command line tool or as an SBT plugin.

## Command Line Tool
    net.yefremov.sleipnir.Sleipnir <resolving path> <source dir> <target dir> [namespace prefix]
For example:

    net.yefremov.sleipnir.Sleipnir sample-data/src/main/pegasus sample-data/src/main/pegasus sample-data/target/scala-2.10/src_managed scala

## Sbt Plugin
To use Sleipnir in your Play / SBT project you need to add it as a plugin and import the corresponding settings.
1. Add the following line to plugins.sbt:
        addSbtPlugin("net.yefremov.sleipnir" % "sleipnirsbtplugin" % "<latest recommended version>")
2. Make the following changes in your Build.scala:
        ...
        import net.yefremov.sleipnir.sbt.SleipnirPlugin._
        ...

        lazy val dataTemplate = Project("data-template", file("data-template"))
        ...
            .settings(sleipnirSettings: _*)
        ...

The changes above will enable generation of Scala bindings for PDSC files defined within the project. You may also want to generate Scala classes for PDSC files defined in downstream dependencies. This should be considered a temporary solution until all downstream services will start generating Scala bindings as a part of their build processes.

        ...
        lazy val commonModule = Project(...)
            ...
            .settings(sleipnirDownstreamSettings: _*)
            ...

You may also enable filtering of dependencies that will be processed:

        lazy val commonModule = Project(...)
            ...
            .settings(sleipnirDownstreamSettings: _*)
            .settings(dataTemplatesDependenciesFilter := DependencyFilter.allPass -- moduleFilter(organization = "com.linkedin.notifications-api"))
            ...

Only dependencies that pass the filter specified in dataTemplatesDependenciesFilter will have Scala bindings generated.
