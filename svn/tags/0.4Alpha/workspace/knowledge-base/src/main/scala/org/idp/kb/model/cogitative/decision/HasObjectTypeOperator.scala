package org.idp.kb.model.cogitative.decision

import java.net.URI
import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.model.ObjectType
import org.idp.kb.{Constant, Statement}
import org.apache.log4j.Logger

/*
 * Class that implements Operator, check for the any object with specified objectType.
 */

class HasObjectTypeOperator(operatorIndividual: OWLIndividual) extends Operator {
  val log = Logger.getLogger(this.getClass)

  /*
   * This method checks if the specified statement has spefied in operand objectType.
   * @param statement
   * @param expressionObject the object to use operator, must be object of the OWL model, currently ignored as if it is AnyObject.
   * @param operand the URI to check.
   */
  override def interpret(statement: Statement, expressionObject: Value, operand: Value): Value = {
    log debug "interpret(" + statement + ", " + expressionObject + ", " + operand + ")"
    // TODO have to have check for individuals of the children of the AnyObject here
    /*
    val types = for (val t <- expressionObject.types 
         if t.asOWLClass.getURI == URI.create(Constant.base + Constant.localNamesPrefix + Constant.objectURIString)) yield t;
    if (types.size == 0) {
      return new Value(Constant.falseIndividual)
    }
    */

    val res = for (val o <- statement.objects
    if (URI.create(Constant.base
            + Constant.localNamesPrefix
            + o.getObjectType.getOrElse(new ObjectType("", "")).OWLURI)
            == operand.OWLURI)) yield o
    log debug res
    if (res.length > 0) {
      return new Value(Constant.trueIndividual)
    } else {
      return new Value(Constant.falseIndividual)
    }
  }
}
