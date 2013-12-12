package io.segl


import SeglMacros._
import org.slf4j.LoggerFactory


object MacroExamples {

private val logger = LoggerFactory.getLogger("MacroProlo")


  def main(args: Array[String]) {

    // ex1
  	hello("nico")

    // ex2
    val a = 1
    val b = 5
    trace (a+b)


    // ex3
    info(a+b)
  }
}
