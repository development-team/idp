package org.idp.kb.model.domain

import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.Constant
import java.net.URI

/*
 * Trait for the root domain concept.
 */

abstract class AnyDomain {
  
  def classURIString: String
  def OWLURIString: String
  def isDestination = false
  def isActionObject = false
  def save(): OWLIndividual

  val individ = Constant.dataFactory.getOWLIndividual(URI create Constant.domainBase + Constant.localNamesPrefix + OWLURIString)
}

/*
 * Factory method for any domain concept.
 */
object AnyDomain {
  import org.apache.log4j.Logger
  val log = Logger.getLogger(this.getClass)
  
  val addActionAbsoluteURIString = Constant.domainBase + Constant.localNamesPrefix + Constant.addActionURIString
  val removeActionAbsoluteURIString = Constant.domainBase + Constant.localNamesPrefix + Constant.removeActionURIString
  val addActionRE = ("(" + addActionAbsoluteURIString + ")").r
  val removeActionRE = ("(" + removeActionAbsoluteURIString + ")").r
}