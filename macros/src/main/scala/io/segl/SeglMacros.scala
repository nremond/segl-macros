package io.segl

import scala.language.experimental.macros

object SeglMacros {
  import scala.reflect.macros.Context

  def trace(param: Any): Unit = macro traceImpl

  def traceImpl(c: Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    val paramRep = show(param.tree)
    val paramRepExpr = c.Expr[String](Literal(Constant(paramRep)))
    reify { println(paramRepExpr.splice + " = " + param.splice) }
  }


  def hello(name: String): Unit = macro helloImpl

  def helloImpl(c: Context)(name: c.Expr[String]): c.Expr[Unit] = {
    import c.universe._
    reify { println(s"Hello ${name.splice}!") }
  }
}
