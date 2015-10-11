package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.{Statement, Util}
/*
 * Simple implementation of OR operator with 2 operands.
 */

class OrOperator (individ: OWLIndividual) extends Operator {

  
  def interpret(statement: Statement, expressionObject: Value, operand: Value): Value = {
    Util.boolean2Value(Util.value2Boolean(expressionObject) || Util.value2Boolean(operand))
  }
}
