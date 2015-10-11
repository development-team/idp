package org.idp.kb

import org.semanticweb.owl.model.OWLIndividual

trait OWLSavable {
  def save: OWLIndividual
  def OWLName: String
}
