package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.{Field, Method}
import org.antlr.stringtemplate.StringTemplate
import org.apache.log4j.Logger

/**
 * @author: talanovm
 * @date: 14.12.2009
 * Time: 10:08:59
 */

class JavaMethodGenerator extends MethodGenerator {
  val log = Logger.getLogger(this.getClass)
  
  def apply(method: Method, context: Map[String, List[Field]]): String = {
    "public " + getReturnType(method) + " " + getMethodName(method) + "(" + getInputParams(method) + ")\n" + getThrows(method) + "{\n" + getBody(method, context) + "\n}"
  }

  override protected def getReturnType(method: Method): String = {
    var res = ""
    // return type
    method.getReturnType match {
      case Some(rT) => {
        if (rT.size > 0) {
          rT.toList(0).getLiteral
        } else {
          ""
        }
      }
      case None => ""
    }
  }

  override protected def getBody(method: Method, context: Map[String, List[Field]]): String = {
    var res = ""
    // body
    method.getBody match {
      case Some(b) => {
        // process body
        if (b.size > 0) {
          val bodyNotProcessed = b.toList(0).getLiteral
          val sT = new StringTemplate(bodyNotProcessed)
          context.map(keyValue => {
            keyValue._2.map(vl => {
              // log debug (vl)
              sT.setAttribute(keyValue._1, vl)
            })
          })
          return sT.toString
        } else {
          return ""
        }
      }
      case None => return ""
    }
  }

  override protected def getInputParams(method: Method): String = {
    var res = ""
    // input parameters
    res = (for (val par <- method.getInputParams) yield par._2 + " " + par._1 + ",").foldLeft("")((x1, x2) => x1 + x2)

    if (res.length > 1) {
      res = res.substring(0, res.length - 1)
    }
    res
  }

  def getThrows(method: Method): String = {
    var res = ""
    val throws = method.getThrows
    if (throws.size > 0) {
      res = " throws "
      for (val th <- throws) {
        res = res + th + " ,"
      }
      res = res.substring(0, res.length -1)
    }
    res
  }

  override protected def getMethodName(method: Method): String = {
    // method name
    method.getMethodName match {
      case null => throw new IllegalArgumentException("No method name specified")
      case x: String => x
    }
  }
}