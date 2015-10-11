package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.{Util, Statement}
/*
 * Simple boolean implementation of NOT operators with 1 operand.
 */

class NotOperator (individ: OWLIndividual) extends Operator {

  /*
   * This method returns true if expressionObject parametere is false and false otherwise.
   * @param statement currently ignored
   * @param expressionObject the object to use operator, must be object of the OWL model.
   * @param opereand currently ignored
   */
  def interpret(statement: Statement, expressionObject:Value, operand:Value) = 
    Util boolean2Value (!Util.value2Boolean(expressionObject))
}
