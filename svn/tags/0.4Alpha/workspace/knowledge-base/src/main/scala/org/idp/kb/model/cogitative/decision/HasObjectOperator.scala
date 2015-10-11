package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.OWLIndividual
import java.net.URI
import org.idp.kb.{Constant, Statement, Util}
/*
 * This class is implementation of the operator that checks if the specified statement has specified object.
 */
class HasObjectOperator(individ: OWLIndividual) extends Operator {

  /*
   * This method checks if the specified statement has spefied in operand object.
   * @param statement
   * @param expressionObject the object to use operator, currently ignored.
   * @param operand the value to check.
   */
  def interpret(statement: Statement, expressionObject: Value, operand: Value): Value = {
    val res = for (val o <- statement.objects 
                   if URI.create(Constant.base + Constant.localNamesPrefix + o.OWLURIString) == operand.OWLURI) yield o
    Util.boolean2Value(res.length > 0)
  }
}
