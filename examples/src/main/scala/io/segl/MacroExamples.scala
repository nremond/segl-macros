package io.segl

import SeglMacros._
import org.slf4j.LoggerFactory

object MacroExamples {
  private implicit val logger = LoggerFactory.getLogger("macros")

  def main(args: Array[String]) {
    val a = 2
    val b = 3
    info(a + b)
  }
}
