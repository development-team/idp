package org.idp.kb.model.domain

import java.net.URI
import org.idp.kb.{OWLSaveHelper, Constant}

class ActionObject(name: String, desciption: String) extends AnyDomain {

  override def classURIString = Constant.anyDomainURIString
  override def OWLURIString = classURIString + Constant.classDelimiter + name
  
  def getName() = name
  
  override def isActionObject = true

  override def save() = {
    OWLSaveHelper.saveIndividual(name, Constant.baseURI + Constant.localNamesPrefix + Constant.actionObjectURIString)
  }
}
