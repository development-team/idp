package org.idp.kb.model

import java.net.URI

import org.idp.kb.model.linguistic.Phrase
import org.semanticweb.owl.model.{OWLIndividual, AddAxiom}

/**
 * The class to implement statement object
 */
class Object(phrase: Phrase, objectType: Option[ObjectType]) extends OWLSavable with AnyFromStatement {
  private def prefix = "object_"
  def this (phrase: Phrase) = this (phrase, None)
  def getPhrase = this.phrase
  def getObjectType = this.objectType
  override def toString = "[" + phrase + ":" + objectType + "]"
  
  override def save: OWLIndividual = {
    val obj = OWLSaveHelper.saveIndividual(OWLName, Constant.baseURI + Constant.localNamesPrefix + Constant.objectURIString)
    val phr = this.phrase save;
    val hasPhrase = Constant.dataFactory.getOWLObjectProperty(URI create Constant.localNamesPrefix + Constant.hasPhraseURIString)
    val phrasePropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(obj, hasPhrase, phr)
    val addPhrasePropAddAxiom = new AddAxiom(Constant.ontology, phrasePropAssertion)
    Constant.owlManager applyChange addPhrasePropAddAxiom
    for (val oT <- this.objectType) {
      val objT = oT save;
      val hasObjectType = Constant.dataFactory.getOWLObjectProperty(URI create Constant.localNamesPrefix + Constant.hasObjectTypeURIString)
      val objectTypePropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(obj, hasObjectType, objT)
      val addPropAddAxiom = new AddAxiom(Constant.ontology, objectTypePropAssertion)
      Constant.owlManager applyChange addPropAddAxiom
    }
    obj
  }
  override def OWLName = Constant.objectURIString + Constant.classDelimiter +
    this.phrase.OWLName
  
  override def classURIString = Constant.objectURIString
  override def OWLURIString = classURIString + Constant.classDelimiter +
    this.phrase.OWLName
}
