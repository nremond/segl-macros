package io.segl

import scala.language.experimental.macros

object SeglMacros {
  import scala.reflect.macros.Context

  def hello(): Unit = macro helloImpl

  def helloImpl(c: Context)(): c.Expr[Unit] = {
    import c.universe._
    reify { println("Hello World!") }
  }
}
