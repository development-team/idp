package org.idp.kb.model.domain

import org.semanticweb.owl.model.OWLIndividual
import org.idp.kb.model.Subject

abstract class RemoveAction(individ: OWLIndividual) extends AnyAction {
  def apply(subject: Subject, objects: List[Object]) = {
    //TODO create object to and object what
  }

  def OWLURIString = individ.getURI.toString
}
