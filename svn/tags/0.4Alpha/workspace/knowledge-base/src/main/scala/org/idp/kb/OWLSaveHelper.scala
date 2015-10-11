package org.idp.kb

import java.net.URI
import org.semanticweb.owl.model.{AddAxiom, OWLIndividual}
object OWLSaveHelper {
  /*
   * This method saves the specified individual.
   * @param individualName the String relative name of the individual, without baseURI and localNamesPrefix
   * @param classURI the string of the OWL class of the individual
   */
  def saveIndividual(individualName: String, classURI: String): OWLIndividual = {
    val individ = Constant.dataFactory getOWLIndividual (URI create (Constant.baseURI
            + Constant.localNamesPrefix + individualName))
    var classURIFinal = Constant.baseURI + Constant.localNamesPrefix + Constant.thingURIString
    if (classURI != null) {
      classURIFinal = classURI
    }
    val assertion = Constant.dataFactory getOWLClassAssertionAxiom (
            individ, Constant.dataFactory getOWLClass (URI create classURI))
    val axiomChange = new AddAxiom(Constant.ontology, assertion)
    Constant.owlManager applyChange axiomChange
    //Constant.owlManager saveOntology (Constant.ontology, new RDFXMLOntologyFormat(), Constant.physicalURI)
    individ
  }

  /*
   * This method saves the specified individual.
   * @param individual the individual to save
   * @param classURI the string of the OWL class of the individual
   */
  def saveIndividual(individual: OWLIndividual, classURI: String): OWLIndividual = {

    var classURIFinal = Constant.baseURI + Constant.localNamesPrefix + Constant.thingURIString
    if (classURI != null) {
      classURIFinal = classURI
    }
    val assertion = Constant.dataFactory getOWLClassAssertionAxiom (
            individual, Constant.dataFactory getOWLClass (URI create classURI))
    val axiomChange = new AddAxiom(Constant.ontology, assertion)
    Constant.owlManager applyChange axiomChange
    individual
  }
}
