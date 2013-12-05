package io.segl

import scala.language.experimental.macros

object SeglMacros {
  import scala.reflect.macros.Context

  def hello(name: String): Unit = macro helloImpl

  def helloImpl(c: Context)(name: c.Expr[String]): c.Expr[Unit] = {
    import c.universe._
    reify { println(s"Hello ${name.splice}!") }
  }
}
