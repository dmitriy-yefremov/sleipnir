package com.linkedin.sleipnir

import java.io.File

import com.linkedin.sleipnir.generator.Generator
import com.linkedin.sleipnir.parser.Parser

object Sleipnir extends Parser with Generator {

  def main(args: Array[String]) = {

    if (args.length < 3) {
      println("Usage: Main <resolving path> <source dir> <target dir>")
    } else {
      val resolverPath = args(0)
      val sourceDir = new File(args(1))
      val targetDir = new File(args(2))

      run(resolverPath, sourceDir, targetDir)
      ()
    }
  }

  def run(resolverPath: String, sourceDir: File, targetDir: File): Seq[File] = {
    val sourceFiles = expandSource(sourceDir)
    val schemas = parseSources(sourceFiles, resolverPath)
    schemas.flatMap(schema => processSchema(schema, targetDir))
  }

}