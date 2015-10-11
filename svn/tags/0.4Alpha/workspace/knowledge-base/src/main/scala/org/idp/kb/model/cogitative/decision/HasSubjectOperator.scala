package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.OWLIndividual
import java.net.URI
import org.idp.kb.{Util, Constant, Statement}
/*
 * This class is implementation of the operator that checks if the specified statement has specified subject.
 */
class HasSubjectOperator(individ:OWLIndividual) extends Operator {

  /*
   * This method checks if the specified statement has specified in operand subject.
   * @param statement
   * @param expressionObject the object to use operator, currently ignored.
   * @param operand the value to check.
   * @return Value true if OWLURI of the subject in specified statement matches the operand OWLURI
   */
   override def interpret(statement: Statement, expressionObject: Value, operand: Value): Value = {
     Util.boolean2Value(URI.create(Constant.base + Constant.localNamesPrefix + statement.getSubject.OWLURI) == operand.OWLURI)
   }
}
