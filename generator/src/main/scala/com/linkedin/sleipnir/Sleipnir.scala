package com.linkedin.sleipnir

import java.io.File

import com.linkedin.sleipnir.generator.Generator
import com.linkedin.sleipnir.parser.Parser

object Sleipnir extends Parser with Generator {

  def main(args: Array[String]): Unit = {

    if (args.length < 3) {
      println("Usage: Sleipnir <resolving path> <source dir> <target dir> [namespace prefix]") // scalastyle:ignore
    } else {
      val resolverPath = args(0)
      val sourceDir = new File(args(1))
      val targetDir = new File(args(2))
      val namespacePrefix = if (args.length > 3) Some(args(3)) else None
      run(resolverPath, sourceDir, targetDir, namespacePrefix)
    }
  }

  def run(resolverPath: String, sourceDir: File, targetDir: File, namespacePrefix: Option[String] = None): Seq[File] = {
    val sourceFiles = expandSource(sourceDir)
    val schemas = parseSources(sourceFiles, resolverPath)
    processSchemas(schemas, targetDir, namespacePrefix)
  }

}