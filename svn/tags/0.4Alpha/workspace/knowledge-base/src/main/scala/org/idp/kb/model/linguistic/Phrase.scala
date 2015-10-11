package org.idp.kb.model.linguistic

import java.net.URI

import org.semanticweb.owl.model.{OWLIndividual, AddAxiom}
import org.idp.kb.{OWLSavable, OWLSaveHelper, Constant}
import org.idp.kb.model.AnyFromStatement
/*
 * Class implements prase as the stanford-parser typed dependancy.
 */
class Phrase(governing: Word, dependant: Word) extends OWLSavable with AnyFromStatement {
  override def toString = "{ " + this.governing + ", " + this.dependant + " }"
  override def save: OWLIndividual = {
    val gIndivid = governing save;
    val dIndivid = dependant save;
    val hasWord = Constant.dataFactory.getOWLObjectProperty(URI create Constant.localNamesPrefix + Constant.hasWordPropertyURIString)
    val phrase = OWLSaveHelper.saveIndividual(OWLName, Constant.baseURI + Constant.localNamesPrefix + Constant.phraseURIString)
    val gPropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(phrase, hasWord, gIndivid)
    val dPropAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(phrase, hasWord, dIndivid)
    val addgPropAddAxiom = new AddAxiom(Constant.ontology, gPropAssertion)
    Constant.owlManager applyChange addgPropAddAxiom
    val adddPropAddAxiom = new AddAxiom(Constant.ontology, dPropAssertion)
    Constant.owlManager applyChange adddPropAddAxiom
    phrase
  }
  override def OWLName = Constant.phraseURIString + Constant.classDelimiter + 
    this.governing.OWLName + Constant.delimiter + this.dependant.OWLName
  
  override def classURIString = Constant.phraseURIString
  override def OWLURIString = classURIString + Constant.classDelimiter +
    this.OWLName
}
