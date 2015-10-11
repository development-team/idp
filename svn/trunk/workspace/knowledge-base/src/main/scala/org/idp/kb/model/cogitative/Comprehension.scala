package org.idp.kb.model.cogitative

import decision.Value
import org.semanticweb.owl.model.{OWLIndividual, AddAxiom, OWLObjectPropertyExpression}

import java.net.URI
import org.apache.log4j.Logger

import org.idp.kb.model.cogitative.property.{AnyObjectWithType, HasSubjectValue, HasObjectValue}
import org.idp.kb.{OWLSaveHelper, OWLSavable, Statement, Util, Constant}
import org.idp.kb.model.AnyFromStatement
import org.idp.kb.model.domain._
/*
 * Class to create comprehension of specified any object or subject from statement, based on OWLIndividual of comprehension rule from AnyCogitative.
 */
class Comprehension(individ: OWLIndividual, statement: Statement) extends OWLSavable {
  
  def this (value: Value, statement: Statement) = this(value.apply, statement)
 
  val log = Logger.getLogger(this.getClass)
  /*
   * Finds subject or object from statement that is linked to comprehension individual.
   */
  val anyFromStament = getAndCheckAnyFromStatement(getPropertyOfComprehension)
  
  /*
   * Creates the OWLIndividual of the comprehension and AnyDomain of linked to comprehension.
   * @return AnyDomain of current comprehension.
   */
  def apply(): Option[AnyDomain] = {
    save
    val anyDomain = getAnyDomain
    anyDomain match {
      case Some(aD) => saveAnyDomain(aD)
      case None => None
    }
    anyDomain
  }
  
  /*
   * Creates comprehension individual linked with statement individual.
   * @return OWLIndividal of comprehension
   */
  def save(): OWLIndividual = {
    val anyFromStatementInd = Constant.dataFactory.getOWLIndividual(URI create(Constant.baseURI + Constant.localNamesPrefix + anyFromStament.OWLURIString))
    val comp = OWLSaveHelper.saveIndividual(OWLURIString, Constant.baseURI + Constant.localNamesPrefix + classURIString)
    val hasComprehension = Constant.dataFactory.getOWLObjectProperty(URI create Constant.localNamesPrefix + Constant.hasComprehensionURIString)
    val propAssertion = Constant.dataFactory.getOWLObjectPropertyAssertionAxiom(anyFromStatementInd, hasComprehension, comp)
    val addPropAddAxiom = new AddAxiom(Constant.ontology, propAssertion)
    Constant.owlManager applyChange addPropAddAxiom
    comp
  }
  
  def classURIString = Constant.comprehensionURIString
  def OWLURIString = classURIString + Constant.classDelimiter +
    this.anyFromStament.OWLURIString
  def OWLName = OWLURIString
  
  /*
   * Creates proper domain individual and saves it.
   * @return AnyDomain instance of descendants of AnyDomain
   */
  private def getAnyDomain: Option[AnyDomain] = {
    
    log debug ("getDomainIndividual " + getDomainIndividual(individ))
    
    log debug Util.getDomainIndividuals
    
    for (val domainIndivid <- getDomainIndividual(individ)) {
      // log  info "getAnyDomain " + domainIndivid
      
      if (Util.isIndividualOfType(domainIndivid, URI create Constant.base + Constant.localNamesPrefix + Constant.addActionURIString)) {
        // create add action
        val act = new AddFieldAction(anyFromStament.OWLURIString, Util.resetDomainIndividuals)
        return Some(act)
      } else if(Util.isIndividualOfType(domainIndivid, URI create Constant.base + Constant.localNamesPrefix + Constant.actionObjectURIString)) {
        // create action object
        // get name of the object
        val name = anyFromStament.OWLURIString.substring(anyFromStament.OWLURIString.indexOf("Phrase."))
        val splittedURIString = anyFromStament.OWLURIString.split("\\.")
        val description = splittedURIString(3).substring(0, splittedURIString(3).indexOf("_")) + " to " + splittedURIString(4)
        val actionObject = new ActionObject(name, description)
        // TODO add ObjectType processing
        Util.addDomainIndividuals(actionObject)
        return Some(actionObject)
      } else if (Util.isIndividualOfType(domainIndivid, URI create Constant.base + Constant.localNamesPrefix + Constant.destinationURIString)) {
        // create action object
        // get name of the object
        val name = anyFromStament.OWLURIString.substring(anyFromStament.OWLURIString.indexOf("Phrase."))
        val splittedURIString = anyFromStament.OWLURIString.split("\\.")
        val description = splittedURIString(3).substring(0, splittedURIString(3).indexOf("_")) + " to " + splittedURIString(4)
        val dest = new Destination(name, description)
        Util.addDomainIndividuals(dest)
        return Some(dest)
      }
    }
    None
  }

  /*
  * Saves the domain individuals that are represented as objects of AnyDomain.
  * @param anyDomain the object to save.
  * @return OWLIndividual saved.
  */
  def saveAnyDomain(anyDomain: AnyDomain): OWLIndividual = {
    anyDomain.save
  }

  /*
   * Getter for individual from domain from comprehension rule.
   */
  private def getDomainIndividual(comprehensionIndivid: OWLIndividual): Option[OWLIndividual] = {
    for(val individ <- Util.findPropertyValue(comprehensionIndivid, 
                                              URI create Constant.base + Constant.localNamesPrefix + Constant.hasAnyDomainURIString)) {
      return Some(individ.toList(0))
    }
    None
  }
  
  private def getPropertyOfComprehension(): Pair[OWLObjectPropertyExpression, java.util.Set[OWLIndividual]] = {
    val propertiesMap = scala.collection.jcl.Map(individ.getObjectPropertyValues(Constant.ontology))
    val res = propertiesMap.filter((oP) => {
       Constant.comprehensionProperties.filter(
         cP => oP._1.asOWLObjectProperty.getURI.toString.endsWith(cP)).length > 0
    })
    if (!res.isEmpty) {
      res.toList(0)
    } else {
      throw new IllegalArgumentException("no comprehension property is set")
    }
  }
  
  private def getAnyFromStatement(objPropIndivid:Pair[OWLObjectPropertyExpression, java.util.Set[OWLIndividual]]): 
    Option[AnyFromStatement] = {
    // simple and ugly way
    if (objPropIndivid._1.asOWLObjectProperty.getURI.toString.endsWith(Constant.anyObjectWithTypeURIString)) {
      return ((new AnyObjectWithType(scala.collection.jcl.Set(objPropIndivid._2).toList(0).getURI))(statement))
    } else if (objPropIndivid._1.asOWLObjectProperty.getURI.toString.endsWith(Constant.hasSubjectValueURIString)) {
      return (new HasSubjectValue(scala.collection.jcl.Set(objPropIndivid._2).toList(0).getURI))(statement)
    } else if (objPropIndivid._1.asOWLObjectProperty.getURI.toString.endsWith(Constant.hasObjectValueURIString)) {
      return (new HasObjectValue(scala.collection.jcl.Set(objPropIndivid._2).toList(0).getURI))(statement)
    } 
    throw new IllegalArgumentException("unknown comprehension property is set")
  }
  
  private def getAndCheckAnyFromStatement(objPropIndivid:Pair[OWLObjectPropertyExpression, java.util.Set[OWLIndividual]]): 
    AnyFromStatement = {
      getAnyFromStatement(objPropIndivid) match {
        case Some(x) => x
        case None => throw new IllegalArgumentException("Any from statement not found")
      }
    }
}
