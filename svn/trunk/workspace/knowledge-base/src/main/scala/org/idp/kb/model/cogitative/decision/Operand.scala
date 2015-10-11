package org.idp.kb.model.cogitative.decision

import java.net.URI

/*
 * This class is representation of Operand class in OWL data model and actually 
 * has value (representation of hasValue objectProperty).
 */

class Operand(operandURI: URI, value: Value) extends AnyExpression {
  def getValue = value
  def OWLURI = operandURI
  def apply(statement: Statement) = value 
}
