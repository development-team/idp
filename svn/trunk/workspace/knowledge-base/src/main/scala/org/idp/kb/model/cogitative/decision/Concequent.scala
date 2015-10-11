package org.idp.kb.model.cogitative.decision

import java.net.URI

import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.{Statement, Util, Constant}
import org.apache.log4j.Logger
import org.idp.kb.model.cogitative.Comprehension
import org.idp.kb.model.domain.AnyDomain

class Concequent(individ: OWLIndividual) {
  val log = Logger.getLogger(this.getClass)
  def OWLURI = individ.getURI
  // comprehension
  val resComp = Util.findPropertyValue(individ, URI create (Constant.base
          + Constant.localNamesPrefix
          + Constant.hasComprehensionURIString))
  // implication
  val implication = Util.findPropertyValue(individ, URI create (Constant.base
          + Constant.localNamesPrefix
          + Constant.hasImplicationURIString))


  def apply(statement: Statement): Option[AnyDomain] = {
    implication match {
      case Some(implSet) => {
        if (implSet.size > 0) {
          for (val impl1 <- implSet) {
            // log debug impl1
            val list = Util.domainIndividuals
            val impl = new Implication(impl1)
            val res = impl(statement)
            res
          }
        }
      }
      case None => {}
    }

    resComp match {
      case Some(i) => (new Comprehension(i.toList(0), statement)).apply
      case None => throw new IllegalArgumentException("property  "
              + Constant.hasImplicationURIString
              + " or " + Constant.hasComprehensionURIString
              + " not found "
              + " for individual " + individ.getURI)
    }
  }
}
