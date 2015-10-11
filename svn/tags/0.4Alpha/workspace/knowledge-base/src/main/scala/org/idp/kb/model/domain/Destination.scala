package org.idp.kb.model.domain

import org.idp.kb.{OWLSaveHelper, Constant}

class Destination(name:String, description: String) extends ActionObject(name, description) {
  
  override def isDestination = true

  override def save() = {
    OWLSaveHelper.saveIndividual(name, Constant.baseURI + Constant.localNamesPrefix + Constant.destinationURIString)
  }
  
}
