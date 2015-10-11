package org.idp.kb.model.cogitative.decision

import java.net.URI

import org.semanticweb.owl.model.OWLIndividual

import org.idp.kb.model.domain.AnyDomain

import org.apache.log4j.Logger
import org.idp.kb.{Util, Constant, Statement}
/*
 * Implication is special type of expression, always root, and was created as separate class only for reason 
 * not to implement expression validation in the prototype.
 */
class Implication(individ: OWLIndividual) {
  private val log = Logger.getLogger(this.getClass)

  private val expression: Option[AnyExpression] = {
    log debug "creating expression "
    Util.findPropertyValue(individ, URI create (Constant.base
            + Constant.localNamesPrefix
            + Constant.hasExpressionURIString)) match {
      case Some(indSet) => {
        log debug indSet
        Some(AnyExpression(indSet.toList(0)))
      }
      case None => None
    }
  }

  private val concequent: Option[Concequent] = {
    log debug (Constant.base + Constant.localNamesPrefix + Constant.hasConcequentURIString)
    Util.findPropertyValue(individ, URI create (Constant.base
            + Constant.localNamesPrefix
            + Constant.hasConcequentURIString)) match {
      case Some(indSet) => Some(new Concequent(indSet.toList(0)))
      case None => None
    }
  }

  private val implication: Option[Implication] = {
    Util.findPropertyValue(individ, URI create (Constant.base
            + Constant.localNamesPrefix
            + Constant.hasImplicationURIString)) match {
      case Some(indSet) => Some(new Implication(indSet.toList(0)))
      case None => None
    }
  }

  /*
  * Return domain object mapped in comprehension concept, if corresponding implication expression returns trueIndividual.
  * @param Statement to process.
  * @return AnyDomain if expression of the implication returns trueIndividual None otherwise.
  */
  def apply(statement: Statement): Option[AnyDomain] = {
    log debug "apply(statement " + statement + ")"
    log debug "expr " + this.expression
    this.expression match {
      case Some(x) => {
        log debug "expression " + expression
        log debug "concequent " + concequent
        if (Util.value2Boolean(x(statement))) {
          concequent match {
            case Some(c) => {
              log debug "concequent " + c
              c(statement) match {
                case Some(consequentRes) => {
                  log debug consequentRes
                  return Some(consequentRes)
                }
                case None => None
              }
            }
            case None => None
          }
        } else {
          None
        }
      }
      case None => None
    }
  }
}
