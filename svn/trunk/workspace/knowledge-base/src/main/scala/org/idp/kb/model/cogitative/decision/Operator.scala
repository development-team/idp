package org.idp.kb.model.cogitative.decision

import java.net.URI

import org.semanticweb.owl.model.OWLIndividual
import org.apache.log4j.Logger
import org.idp.kb.{Statement, Constant}

/**
 * Trait for the logical operators to manipulate with statements
 */

trait Operator {
  def interpret(statement: Statement, expressionObject: Value, operand: Value): Value
}
/*
 * Factory creates the operator based on OWLIndividual
 */
object Operator {
  val log = Logger.getLogger(this.getClass)
  val hasObjectTypeRE = ("(" + Constant.hasObjectTypeURIString + ")").r
  val hasObjectRE = ("(" + Constant.base + Constant.localNamesPrefix + Constant.hasObjectURIString + ")").r
  val hasSubjectRE = ("(" + Constant.base + Constant.localNamesPrefix + Constant.hasSubjectURIString + ")").r
  val hasVerbRE = ("(" + Constant.base + Constant.localNamesPrefix + Constant.hasVerbURIString + ")").r
  val andRE = ("(" + Constant.base + Constant.localNamesPrefix + Constant.andURIString + ")").r
  val orRE = ("(" + Constant.base + Constant.localNamesPrefix + Constant.orURIString + ")").r
  val notRE = ("(" + Constant.base + Constant.localNamesPrefix + Constant.notURIString + ")").r

  def apply(individ: OWLIndividual): Operator = {
    // log debug individ.getURI
    // log debug hasObjectTypeRE
    if (individ.getURI.toString.endsWith(Constant.hasObjectTypeURIString)) {
      return new HasObjectTypeOperator(individ)
    } else if (individ.getURI.toString.endsWith(Constant.hasObjectURIString)) {
      return new HasObjectOperator(individ)
    } else if (individ.getURI.toString.endsWith(Constant.hasSubjectURIString)) {
      return new HasSubjectOperator(individ)
    }
      
//    individ.getURI.toString match {
//      case hasObjectTypeRE(uri) => new HasObjectTypeOperator(individ)
//      case hasObjectRE(uri) => new HasObjectOperator(individ)
//      case hasSubjectRE(uri) => new HasSubjectOperator(individ)
//      case hasVerbRE(uri) => new HasVerbOperator(individ)
//      case andRE(uri) => new AndOperator(individ)
//      case orRE(uri) => new OrOperator(individ)
//      case notRE(uri) => new NotOperator(individ)
//    }
   null
  }
}
