package org.idp.kb

import java.net.URI

import model.cogitative.decision.{Value}
import model.domain.{AnyDomain}
import org.apache.log4j.Logger

import org.semanticweb.owl.model._

/*
 * The class of utilities.
 */
object Util {
  private val log = Logger.getLogger(this.getClass)

  /*
  * Returns Set of the OWLIndividuals of the specified OWLIndividual for the specified property URI.
  * @param individ OWLIndividual the individual to search for property.
  * @param propertyURI URI the property URI.
  * @returns Option of the Set of OWLIndividuals or None if nothing found.
  */
  def findPropertyValue(individ: OWLIndividual, propertyURI: URI): Option[Set[OWLIndividual]] = {
    val propValSet = scala.collection.jcl.Map[OWLObjectPropertyExpression, java.util.Set[OWLIndividual]](
      individ.getObjectPropertyValues(Constant.ontology))
    for (val propVal <- propValSet) {
      if (propVal._1.asOWLObjectProperty.getURI == propertyURI) {
        // this is ugly conversion from java to scala types
        val res = scala.collection.jcl.Set(propVal _2)
        return Some(Set[OWLIndividual]() ++ res)
      }
    }
    None
  }

  /*
  * Returns Set of the OWLIndividuals of the specified OWLIndividual for the specified property URI.
  * @param individ OWLIndividual the individual to search for property.
  * @param propertyURI URI the property URI.
  * @return the Set of OWLIndividuals or None if nothing found.
  */
  def findPropertyValueSet(individ: OWLIndividual, propertyURI: URI): Set[OWLIndividual] = {
    val propValSet = scala.collection.jcl.Map[OWLObjectPropertyExpression, java.util.Set[OWLIndividual]](
      individ.getObjectPropertyValues(Constant.ontology))
    for (val propVal <- propValSet) {
      if (propVal._1.asOWLObjectProperty.getURI == propertyURI) {
        val res = scala.collection.jcl.Set(propVal _2)
        return Set[OWLIndividual]() ++ res
      }
    }
    Set[OWLIndividual]()
  }

  /*
  * Returns Set of the OWLTypedConstant of the specified OWLIndividual for the specified data property URI.
  * @param individ OWLIndividual the individual to search for property.
  * @param propertyURI URI the property URI.
  * @returns Option of the Set of OWLTypedConstant or None if nothing found.
  */

  def findDataPropertyValue(individ: OWLIndividual, propertyURI: URI): Option[Set[OWLTypedConstant]] = {
    val propValSet =
    scala.collection.jcl.Map[OWLDataPropertyExpression, java.util.Set[OWLConstant]](
      individ.getDataPropertyValues(Constant.ontology))

    for (val propVal <- propValSet) {
      if (propVal._1.asOWLDataProperty.getURI == propertyURI) {
        // this is ugly conversion from java to scala types
        // val res = scala.collection.jcl.Set(propVal._2).filter(x => x.isTyped).map(x => x.asOWLTypedConstant)
        val s = scala.collection.jcl.Set(propVal._2)
        val sf = s.filter(x => x.isTyped)
        val res = sf.map(x => x.asOWLTypedConstant)
        return Some(Set[OWLTypedConstant]() ++ res)
      }
    }
    None
  }

  /*
  * Returns Set of the OWLTypedConstant of the specified OWLIndividual for the specified data property URI.
  * @param individ OWLIndividual the individual to search for property.
  * @param propertyURI URI the property URI.
  * @returns the Set of OWLTypedConstant or None if nothing found.
  */

  def findDataPropertyValueSet(individ: OWLIndividual, propertyURI: URI): Set[OWLTypedConstant] = {
    val propValSet =
    scala.collection.jcl.Map[OWLDataPropertyExpression, java.util.Set[OWLConstant]](individ.getDataPropertyValues(Constant.ontology))

    for (val propVal <- propValSet) {
      if (propVal._1.asOWLDataProperty.getURI == propertyURI) {
        // Please mind the TYPED constant!
        val res = scala.collection.jcl.Set(propVal._2).filter(x => x.isTyped).map(x => x.asOWLTypedConstant)
        return Set[OWLTypedConstant]() ++ res
      }
    }
    Set[OWLTypedConstant]()
  }



  /*
  * Returns true if specified individual has specified type of OWLClass.
  * @param owlIndivid to check
  * @param owlType the class to check
  * @return true if owlIndivid has one of the types with URI of the specified owlClass.
  */
  def isIndividualOfType(owlIndivid: OWLIndividual, owlType: OWLClass): Boolean = {
    isIndividualOfType(owlIndivid, owlType.getURI)
  }

  /*
  * Returns true if specified individual has specified type of URI.
  * @param owlIndivid to check
  * @param owlType the class URI to check
  * @return true if owlIndivid has one of the types with URI of the specified owlClass.
  */
  def isIndividualOfType(owlIndivid: OWLIndividual, owlTypeURI: URI): Boolean = {
    val types = scala.collection.jcl.Set(owlIndivid.getTypes(Constant.ontology))
    val res = types.find(d => {
      // log debug (d.asOWLClass.getURI)
      // log debug (owlTypeURI)
      if (!d.isAnonymous) {
        return d.asOWLClass.getURI == owlTypeURI
      }
      false
    })
    res match {
      case Some(_) => true
      case None => false
    }
  }

  // Ugly non-functional hack for the Adding the field in the modules
  var domainIndividuals = scala.List[AnyDomain]()

  def getDomainIndividuals = domainIndividuals

  def setDomainIndividuals(di: List[AnyDomain]) = domainIndividuals = di

  def resetDomainIndividuals: List[AnyDomain] = {
    val res = scala.List() ::: domainIndividuals
    domainIndividuals = domainIndividuals.remove(_ => true)
    res
  }

  def addDomainIndividuals(individ: AnyDomain) = {
    domainIndividuals = domainIndividuals ::: List(individ)
    log debug domainIndividuals
  }


  /*
   * Returns value obect to identify Boolean meaning.
   */
  def boolean2Value(in: Boolean): Value = {
    if (in) {
      return new Value(Constant.trueIndividual)
    } else {
      return new Value(Constant.falseIndividual)
    }
  }

  /*
  * Returns true in case value is the same as trueIndividual.
  */
  def value2Boolean(value: Value): Boolean = value.isTrue
}