package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.Field
import org.semanticweb.owl.model.OWLIndividual


trait FieldGenerator extends Generator {

  def apply(field: Field): String = ""
}
