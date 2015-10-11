package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.{Util, Statement}
/*
 * This class is implementation of the operator that checks if the specified statement has specified verb.
 */
class HasVerbOperator(individ: OWLIndividual) extends Operator {

  /*
   * This method checks if the specified statement has spefied in operand verb.
   * @param statement
   * @param expressionObject the object to use operator, currently ignored.
   * @param operand the value to check.
   */
  override def interpret(statement: Statement, expressionObject: Value, operand: Value): Value = {
    Util.boolean2Value(statement.getVerb.OWLURI == operand.OWLURI)
  }

}
