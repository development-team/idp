package org.idp.kb.model

import java.net.URI

import org.idp.kb.model.linguistic.Phrase

import org.semanticweb.owl.model.{OWLIndividual, AddAxiom}
import org.idp.kb.{OWLSavable, Constant, OWLSaveHelper}

/**
 * The class to implement statement subject
 */
class Subject(phrase: Phrase) extends OWLSavable with AnyFromStatement {
  private val prefix = "subject_"
  def getPhrase = phrase
  override def toString = phrase.toString
  override def save: OWLIndividual = {
    val phr = phrase save;
    val hasPhrase = Constant.dataFactory.getOWLObjectProperty(URI create Constant.localNamesPrefix + Constant.hasPhraseURIString)
    val subject = OWLSaveHelper.saveIndividual(OWLName, Constant.baseURI + Constant.localNamesPrefix + Constant.subjectURIString)
    val phrasePropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(subject, hasPhrase, phr)
    val addPhrasePropAddAxiom = new AddAxiom(Constant.ontology, phrasePropAssertion)
    Constant.owlManager applyChange addPhrasePropAddAxiom
    subject
  }
  override def OWLName = OWLURIString
  
  override def classURIString = Constant.subjectURIString
  override def OWLURIString = classURIString + Constant.classDelimiter +
    this.phrase.OWLName
}
