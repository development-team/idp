package org.idp.kb.model.cogitative.decision

import org.semanticweb.owl.model.{OWLIndividual, OWLDescription}
import org.idp.kb.Constant

/*
 * This class is representation of AnyCommunicational in OWL data model.
 */
class Value(individual: OWLIndividual) {
  def OWLURI = individual.getURI

  def apply = individual

  def types = {
    val it = individual.getTypes(Constant.ontology).iterator
    var res = Set[OWLDescription]()
    while (it.hasNext) {
      res = res + it.next
    }
    res
  }

  def isTrue = OWLURI == Constant.trueURI

  def isFalse = OWLURI == Constant.falseURI

  override def toString() = individual.getURI.toString

}
