package io.segl

import scala.reflect.macros.Context
import org.slf4j.LoggerFactory

object SeglMacros {

  import scala.language.experimental.macros

  def hello(dude: String): Unit = macro helloImpl

  def helloImpl(c: Context)(dude: c.Expr[String]): c.Expr[Unit] = {
    import c.universe._
    reify {
      println(s"Hello ${dude.splice}")
    }
  }

  def trace(param: Any): Unit = macro traceImpl

  def traceImpl(c: Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    val paramRep = show(param.tree)
    val paramRepExpr = c.Expr[String](Literal(Constant(paramRep)))
    reify {
      println(s"${paramRepExpr.splice} =  ${param.splice}")
    }
  }


  def info(param: Any): Unit = macro infoImpl

  def infoImpl(c: Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    //println(showRaw(c.enclosingClass))
    val body = c.enclosingClass match {
      case ModuleDef(_, _, Template(_, _, b)) => b
      case unknown => c.abort(c.enclosingPosition, s"Unknown type of enclosing class:${unknown.getClass}")
    }
    val typeAndName = body.collect {
      case ValDef(_, termName, t, _) => t.tpe.toString -> termName
    }
    val foundLogger = typeAndName.find(_._1 == "org.slf4j.Logger")
    val loggerTermName = foundLogger.getOrElse(c.abort(c.enclosingPosition,
      s"Could not find field of type 'org.slf4j.Logger in enclosing class"))._2

    val logger = LoggerFactory.getLogger("logger_test")
    println(showRaw(reify(logger.info("test1"))))
    println(showRaw(reify(if (logger.isInfoEnabled) logger.info("test2"))))

    val paramRep = show(param.tree)
    val paramRepExpr = c.Expr[String](Literal(Constant(paramRep)))


    c.Expr[Unit](Apply(Select(Ident(loggerTermName), newTermName("info")),
      List(reify(s"${paramRepExpr.splice} = ${param.splice}").tree)))


    c.Expr[Unit](If(Apply(Select(Ident(loggerTermName),
      newTermName("isInfoEnabled")), Nil),
      Apply(Select(Ident(loggerTermName), newTermName("info")),
        List(reify(s"${paramRepExpr.splice} = ${param.splice}").tree)),
      c.literalUnit.tree))
  }

}





