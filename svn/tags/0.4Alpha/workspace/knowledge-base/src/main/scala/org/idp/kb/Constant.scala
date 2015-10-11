package org.idp.kb

import java.net.URI
import java.io.File
import org.apache.log4j.Logger
import org.semanticweb.owl.apibinding.OWLManager
import org.semanticweb.owl.model.OWLIndividual

import org.semanticweb.owl.util.OWLOntologyMerger

object Constant {
  val log = Logger.getLogger(this.getClass)

  def self = "Menta"

  def subject = "nsubj"

  def dobject = "dobj"

  def base = "http://.idp.org/ontologies/2009/7/LiguisticCogitativeDomainModelShallow"

  def applicationKBBase = "http://idp.org/ontologies/2009/6/Application-KB.owl"

  def domainBase = "http://idp.org/ontologies/2009/10/TargetApp.owl"

  def localNamesPrefix = "#"

  def classDelimiter = "."

  def delimiter = "_"

  def baseURI = URI create base

  def thingURIString = "Thing"

  def trueURIString = "true"

  def trueURI = URI create (base + localNamesPrefix + trueURIString)

  def trueIndividual = {
    findIndividual(trueURI) match {
      case None => dataFactory.getOWLIndividual(trueURI)
      case Some(x) => x
    }
  }

  def falseURIString = "false"

  def falseURI = URI create (base + localNamesPrefix + falseURIString)

  def falseIndividual: OWLIndividual = {
    findIndividual(falseURI) match {
      case None => dataFactory.getOWLIndividual(falseURI)
      case Some(x) => x
    }
  }

  def AnyBooleanURIString = "AnyBoolean"

  def BooleanURIString = "Boolean"

  def wordURIString = "Word"

  def phraseURIString = "Phrase"

  def statementURIString = "Statement"

  def objectURIString = "Object"

  def objectTypeURIString = "ObjectType"

  def subjectURIString = "Subject"

  def comprehensionURIString = "Comprehension"

  def physicalURI = new File("LinguisticCogitativeDomainModelShort.owl").toURI
  // def applicationKBPhysicalURI = new File("Application-KB.owl").toURI
  def applicationKBPhysicalURI = new File("TargetAppRDF.owl").toURI

  def physicalOutputURI: URI = new File("LinguisticCogitativeDomainModelShort.owl").toURI
  // create physicalOutputFile
  def hasWordPropertyURIString = "hasWord"

  def hasObjectTypeURIString = "hasObjectType"

  def hasObjectTypeURI = URI create (base + localNamesPrefix + hasObjectTypeURIString)

  def hasObjectURIString = "Operator.hasObject"

  def hasSubjectURIString = "Operator.hasSubject"

  def hasPhraseURIString = "Operator.hasPhrase"

  def hasVerbURIString = "Operator.hasVerb"

  def andURIString = "Operator.and"

  def orURIString = "Operator.or"

  def notURIString = "Operator.not"

  def OperandURIString = "Operand"

  def ExpressionURIStrirg = "Expression"

  def ExpressionObjectURIString = "ExpressionObject"

  def hasValueURIString = "hasValue"

  def hasExpressionObjectURIString = "hasExpressionObject"

  def hasOperandURIString = "hasOperand"

  def hasOperatorURIString = "hasOperator"

  // Concequent object properties
  def hasExpressionURIString = "hasExpression"

  def hasConcequentURIString = "hasConcequent"

  def hasImplicationURIString = "hasImplication"

  def hasComprehensionURIString = "hasComprehension"

  def hasRepresentationURIString = "hasRepresentation"

  def hasAnyDomainURIString = "hasAnyDomain"

  def hasStatementURIString = "hasStatement"

  def probabilityURIString = "probability"

  // Domain classes, individuals object properties
  def anyDomainURIString = "AnyDomain"

  def anyActionURIString = "AnyAction"

  def addActionURIString = "AddAction"

  def addFieldActionURIString = "AddFieldActon"

  def actionObjectURIString = "ActionObject"

  def destinationURIString = "Destination"

  def sourceURIString = "Source"

  def removeActionURIString = "RemoveAction"

  def moduleClassURIString = "Module"

  def bLLClassURIString = "BLL"

  def pagesClassURIString = "Pages"

  def tablesClassURIString = "Tables"

  def methodClassURIString = "Method"

  def fieldClassURIString = "Field"

  def hasMethodURIString = "hasMethodDefinition"

  def hasFieldURIString = "hasFieldDefinition"

  def hasImportURIString = "import"

  def hasPackageURIString = "package"

  def defaultFieldType = "String"

  def hasConstructorURIString = "hasContructorDefinition"

  def hasFileURIString = "hasFile"

  def implementsURIString = "implements"

  def extendsURIString = "extends"

  def hasModuleURIString = "dependsOn"

  def linkedToModuleURIString = "linkedToModule"

  def addressURIString = "address"

  def isTypeURIString = "is"

  // def domainBaseURIString = "file:/home/max/idpWorkspace/jobOfferOWL/3models.kaon"

  def anyObjectWithTypeURIString = "anyObjectWithType"

  def hasSubjectValueURIString = "hasSubjectValue"

  def hasObjectValueURIString = "hasObjectValue"

  def comprehensionProperties = List[String](anyObjectWithTypeURIString, hasSubjectValueURIString, hasObjectValueURIString)

  //Field properties
  def hasDataType = "isDataType"

  def hasTypeURIString = "hasType"

  def hasSetterMethod = "setterMethod"

  def hasGetterMethod = "getterMethod"

  def hasReturnType = "hasReturnType"

  def hasThrows = "throws"

  def hasInputParams = "hasInputParams"

  def hasBody = "hasBody"

  def value = "value"

  def hasClass = "isClass"

  def interfaceURIString = "Interface"

  def pagesURIStrig = "Pages"

  def bllURIString = "BLL"

  def tablesURIString = "Tables"

  val owlManager = OWLManager createOWLOntologyManager ()
  // val ontology = owlManager createOntology baseURI
  owlManager loadOntologyFromPhysicalURI applicationKBPhysicalURI
  owlManager loadOntologyFromPhysicalURI physicalURI
  //val ontology = owlManager loadOntologyFromPhysicalURI physicalURI
  val dataFactory = owlManager.getOWLDataFactory
  val merger = new OWLOntologyMerger(owlManager);
  val ontology = merger.createMergedOntology(owlManager, URI create Constant.domainBase);

  def findIndividual(uri: URI): Option[OWLIndividual] = {
    val individualsSet = scala.collection.jcl.Set[OWLIndividual](ontology.getReferencedIndividuals)
    var res: Option[OWLIndividual] = None
    for (val it <- individualsSet) {
      if (it.getURI == uri) {
        res = Some(it)
      }
    }
    res
  }
}
