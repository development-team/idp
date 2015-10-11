package org.idp.kb.model.cogitative.decision

import java.net.URI

import org.semanticweb.owl.model.OWLIndividual

import org.apache.log4j.Logger
import org.idp.kb.{Statement, Util, Constant}
/*
 * The trait for any expression or value.
*/

trait AnyExpression {
  def OWLURI: URI

  def apply(statement: Statement): Value

}
/*
 * The factory to create the terminal(operand) or expressions
 */
object AnyExpression {
  val log = Logger.getLogger(this.getClass)
  val terminalURIString = Constant.base + Constant.localNamesPrefix + Constant.OperandURIString
  val expressionURIString = Constant.base + Constant.localNamesPrefix + Constant.ExpressionURIStrirg
  val expressionObjectURIString = Constant.base + Constant.localNamesPrefix + Constant.ExpressionObjectURIString
  val terminalRE = ("(" + terminalURIString + ")").r
  val expressionRE = ("(" + expressionURIString + ")").r
  val expressionObjectRE = ("(" + expressionObjectURIString + ")").r

  def apply(individ: OWLIndividual): AnyExpression = {
    log debug individ
    log debug "of class " + scala.collection.jcl.Set(individ.getTypes(Constant.ontology)).toList(0)
            .asOWLClass.getURI.toString
    scala.collection.jcl.Set(individ.getTypes(Constant.ontology)).toList(0)
            .asOWLClass.getURI.toString match {
    // TODO check grand parents for AnyExpression
      case terminalRE(uri) => {
        val res = Util.findPropertyValue(individ, URI create (Constant.base
                + Constant.localNamesPrefix
                + Constant.hasValueURIString))
        res match {
          case Some(i) => return new Operand(individ.getURI, new Value(i.toList(0)))
          case None => throw new IllegalArgumentException("property " + Constant.base
                  + Constant.localNamesPrefix
                  + Constant.hasValueURIString + " not found")
        }
      }
      case expressionObjectRE(uri) => {
        val res = Util.findPropertyValue(individ, URI create (Constant.base
                + Constant.localNamesPrefix
                + Constant.hasValueURIString))
        res match {
          case Some(i) => return new Operand(individ.getURI, new Value(i.toList(0)))
          case None => throw new IllegalArgumentException("property " + Constant.base
                  + Constant.localNamesPrefix
                  + Constant.hasValueURIString + " not found")
        }
      }
      case _ => {
        val res = Util.findPropertyValue(individ, URI create (Constant.base
                + Constant.localNamesPrefix
                + Constant.hasExpressionObjectURIString))
        res match {
          case None => throw new IllegalArgumentException("individual " + individ.getURI
                  + " does not contain property " + Constant.base
                  + Constant.localNamesPrefix
                  + Constant.hasExpressionObjectURIString)
          case Some(eo) => {
            log debug ("eo " + eo)
            Util.findPropertyValue(individ, URI create (Constant.base + Constant.localNamesPrefix + Constant.hasOperatorURIString)) match {
              case None => throw new IllegalArgumentException("individual " + individ.getURI
                      + " does not contain property " + Constant.base
                      + Constant.localNamesPrefix
                      + Constant.hasOperatorURIString)
              case Some(operator) => {
                log debug ("operator " + operator)
                Util.findPropertyValue(individ, URI create (Constant.base + Constant.localNamesPrefix + Constant.hasOperandURIString)) match {
                  case None => throw new IllegalArgumentException("individual " + individ.getURI
                          + " does not contain property " + Constant.base
                          + Constant.localNamesPrefix
                          + Constant.hasOperandURIString)
                  case Some(operand) => {
                    log debug ("operand" + operand.toList(0).getURI)
                    return new Expression(URI create expressionURIString, AnyExpression(eo.toList(0)),
                      Operator(operator.toList(0)),
                      AnyExpression(operand.toList(0)))
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
