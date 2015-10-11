package org.idp.kb.model

import java.net.URI
import org.semanticweb.owl.model.{AddAxiom, OWLIndividual}

class ObjectType(shortName: String, longName: String) extends OWLSavable with AnyFromStatement {
  override def toString = shortName
  override def save: OWLIndividual = {
    OWLSaveHelper.saveIndividual(OWLName, Constant.baseURI + Constant.localNamesPrefix + Constant.objectTypeURIString)
  }
  override def OWLName = Constant.objectTypeURIString + Constant.classDelimiter +
    shortName
  
  override def classURIString = Constant.objectTypeURIString
  override def OWLURIString = classURIString + Constant.classDelimiter +
    this.shortName
  
}
