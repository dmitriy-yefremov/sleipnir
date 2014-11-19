package com.linkedin.sleipnir.generator

/**
 * Represents content of a generated class.
 * @param name fully qualified class name
 * @param source generated source code
 * @author Dmitriy Yefremov
 */
case class GeneratedClass(name: String, source: String)
