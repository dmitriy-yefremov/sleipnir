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

package net.yefremov.sleipnir

import java.io.File
import net.yefremov.sleipnir.generator.Generator
import net.yefremov.sleipnir.parser.Parser

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