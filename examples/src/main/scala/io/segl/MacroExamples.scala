package io.segl

import SeglMacros._
import org.slf4j.LoggerFactory

object MacroExamples {
  private implicit val logger = LoggerFactory.getLogger("macros")

  def foo(a: String, b: Int) = traceAround { a.toUpperCase * b }

  def main(args: Array[String]) {
    println(foo("macros rules!! ", 3))
  }
}
