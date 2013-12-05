package io.segl

import scala.language.experimental.macros

object SeglMacros {
  import scala.reflect.macros.Context

  def info(param: Any): Unit = macro infoImpl

  def infoImpl(c: Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    val paramRep = show(param.tree)
    val paramRepExpr = c.Expr[String](Literal(Constant(paramRep)))

    //println(showRaw(c.enclosingClass))

    //find name of val with type org.slf4j.Logger in the enclosing class
    val loggerTermName = {
      //get body of enclosing class
      val body = c.enclosingClass match {
        case ModuleDef(_, _, Template(_, _, b)) => b
        case ClassDef(_, _, _, Template(_, _, b)) => b
        case unknown =>
          c.abort(c.enclosingPosition, s"Unknown type of enclosing class: ${unknown.getClass}")
      }
      //collect only Val definitions and transform to a tuple (typeName -> valName)
      val typeAndName = body.collect { case ValDef(_, termName, t, _) => t.tpe.toString -> termName }
      val foundLogger = typeAndName.find(_._1 == "org.slf4j.Logger")
      foundLogger.getOrElse(
        c.abort(c.enclosingPosition, s"Could not find field of type 'org.slf4j.Logger' in enclosing class"))._2
    }

//    val logger = LoggerFactory.getLogger("test")
//    println(showRaw(reify(if(logger.isInfoEnabled) logger.info("test"))))

    //build AST for logger call
    c.Expr[Unit](If(Apply(Select(Ident(loggerTermName), newTermName("isInfoEnabled")), Nil),
      Apply(Select(Ident(loggerTermName), newTermName("info")),
        List(reify(paramRepExpr.splice + " = " + param.splice).tree)),
      c.literalUnit.tree))
  }


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
