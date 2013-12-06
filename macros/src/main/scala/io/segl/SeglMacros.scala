package io.segl

import scala.language.experimental.macros

object SeglMacros {
  import scala.reflect.macros.Context

  def traceAround[T](body: T): T = macro traceAroundImpl[T]

  def traceAroundImpl[T: c.WeakTypeTag](c: Context)(body: c.Expr[T]) : c.Expr[T] = {
    import c.universe._

    val DefDef(_, name, _, params, _, _) = c.enclosingMethod
    val methodName = c.literal(name.decoded)

    val allParams = params.flatten
    val argsExpr = if(allParams.isEmpty) c.literal("no args.")
    else {
      val args = allParams.map(expr => {
        val name = expr.name.decoded
        val argName = c.literal(name)
        val argValue = c.Expr(Ident(newTermName(name)))
        reify { argName.splice + "='" + argValue.splice + "'"}
      })
      val repr = args.reduceLeft {
        (acc, expr) => reify(acc.splice + ", " + expr.splice)
      }
      reify { "args: " + repr.splice }
    }

    val valName = newTermName(c.fresh("aroundRes$"))
    val assignResult = ValDef(Modifiers(), valName, TypeTree(), body.tree)
    val resIdent = Ident(valName)
    val resValue = c.Expr(resIdent)
    val typeName = c.literal(weakTypeTag[T].tpe.typeSymbol.name.toString)

    c.Expr[T](Block(
      List(
        reify { println(s"Entering method '${methodName.splice}' with ${argsExpr.splice}") }.tree,
        assignResult,
        reify { println(s"Leaving method '${methodName.splice}' with result ${resValue.splice} (type: ${typeName.splice})") }.tree
      ),
      resIdent
    ))
  }



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
