package org.idp.kb.model.cogitative.decision

import java.net.URI
import org.semanticweb.owl.model.OWLIndividual

import org.idp.kb.model.domain.AnyDomain

import org.apache.log4j.Logger
import org.idp.kb.{Util, Constant, Statement}

class CogitativeHypothesis(individ: OWLIndividual) {
  
  val log = Logger.getLogger(this.getClass)
  
  private val implication: Option[Implication] = {
    Util.findPropertyValue(individ, 
                           URI create(Constant.base
                                      + Constant.localNamesPrefix 
                                      + Constant.hasImplicationURIString)) 
    match {
      case Some(indSet) => Some(new Implication(indSet.toList(0)))
      case None => None
    }
  }
  private val probability: Option[Double] = {
    Util.findDataPropertyValue(individ, 
                           URI create(Constant.base
                                      + Constant.localNamesPrefix 
                                      + Constant.probabilityURIString)) 
    match {
      case Some(valueSet) => Some((new java.lang.Double(valueSet.toList(0).getLiteral)).asInstanceOf[Double])
      case None => None
    }
  }
  
  def getProbability = this.probability
  
  /*
   * Runs the hypothesis.
   * @param Statement to check if current hypothesis is applicable to it.
   * @return Some[Pair[Value, Double]] if hypothesis is applicable for the specified statement, None if not.
   */
  def apply(statement: Statement): Option[Pair[AnyDomain, Double]] = {
    log debug ("apply (statement " + statement + ")" )
    implication match {
      case Some(i) => {
        log debug "impl"
        probability match {
          case Some(p) => {
            log debug "prob"
            i(statement) match {
              case Some(anyDomain) => Some(Pair(anyDomain, p))
              case None => None
            }
          }
          case None => None
        }
      }
      case None => None
    }
  }
}
