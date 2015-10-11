package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.OWLIndividual

import org.apache.log4j.Logger
import org.idp.kb.{Util, Statement, Constant}
/*
 * Simple implementation of AND operator with 2 operands.
 */

class AndOperator (individ: OWLIndividual) extends Operator {

  val log = Logger.getLogger(this.getClass)
  
  def interpret(statement: Statement, expressionObject: Value, operand: Value): Value = {
    Util boolean2Value (Util.value2Boolean(expressionObject) && Util.value2Boolean(operand))
  }
}
