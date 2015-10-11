package org.idp.kb.model.linguistic

import org.semanticweb.owl.model.OWLIndividual

class Word(value: String) extends OWLSavable with AnyFromStatement{
  def this () = this (null)
  def hasValue = value.size > 0
  override def toString = value
  override def save:OWLIndividual = {
    OWLSaveHelper.saveIndividual(OWLName, 
                                  Constant.baseURI + Constant.localNamesPrefix + Constant.wordURIString)
  }
  override def OWLName: String = Constant.wordURIString + Constant.classDelimiter + 
    value
  
  override def classURIString = Constant.wordURIString
  override def OWLURIString = classURIString + Constant.classDelimiter +
    this.OWLName
}