package org.idp.kb.model.cogitative.decision

import java.net.URI

import org.apache.log4j.Logger
import org.idp.kb.Statement

/*
 * The implementation of two operands expression
 */
// TODO make it multioperands 
class Expression(expressionURI:URI, 
                 defExpressionObject: AnyExpression,
                 operator:Operator, 
                 defOperand:AnyExpression) extends AnyExpression{
  
  val log = Logger.getLogger(this.getClass)
  
  def getExpressionObject = defExpressionObject
  
  def getOperand = defOperand
  
  def apply (statement: Statement): Value = {
    log debug operator.interpret(statement, getOperand(statement, this.defExpressionObject), getOperand (statement, this.defOperand))
    operator.interpret(statement, getOperand(statement, this.defExpressionObject), getOperand (statement, this.defOperand))
  }
  
  private def getOperand(statement: Statement, expression: AnyExpression): Value = {
    expression(statement)
  }
  
  def OWLURI = expressionURI
}
