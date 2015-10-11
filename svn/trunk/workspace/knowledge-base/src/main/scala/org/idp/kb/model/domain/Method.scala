package org.idp.kb.model.domain

import java.net.URI
import org.idp.kb.{Util, OWLSaveHelper, Constant}
import org.semanticweb.owl.model.{OWLTypedConstant, OWLIndividual}

/**
 * User: Max Talanov
 * Date: 21.11.2009
 * Time: 0:48:43
 */

class Method(uriString: String) extends AnyDomain {
  def OWLURIString = uriString

  def classURIString = Constant.domainBase + Constant.localNamesPrefix + Constant.methodClassURIString

  def OWLURI = URI create (Constant.domainBase + Constant.localNamesPrefix + uriString)

  val individual = Constant.dataFactory.getOWLIndividual(OWLURI)

  def getIndividual = individual

  val returnTypeIndivid: Option[OWLIndividual] = {
    val rT = Util.findPropertyValue(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.hasReturnType)).getOrElse(Set[OWLIndividual]())
    if (rT.size > 0) {
      Some(rT.toList(0))
    } else {
      None
    }
  }
  val returnType = {
    returnTypeIndivid match {
      case Some(rTI) => Util.findDataPropertyValue(rTI, URI create (Constant.domainBase
              + Constant.localNamesPrefix + Constant.hasClass))
      case None => None
    }

  }

  def getReturnType = returnType

  val throwsIndividuals: Set[OWLIndividual] = {
    Util.findPropertyValueSet(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.hasThrows))
  }
  val throws: Set[String] = {
    if (throwsIndividuals.size > 0) {
      var res = Set[String]()
      for (val tI <- throwsIndividuals) {
        val throwsValues = Util.findDataPropertyValueSet(tI, URI create (Constant.domainBase + Constant.localNamesPrefix + Constant.hasClass))
        if (throwsValues.size > 0) {
          res = res + throwsValues.toList(0).getLiteral
        }
      }
      res
    } else {
      Set[String]()
    }
  }

  def getThrows = throws


  val inputParams: Set[Pair[String, String]] = {
    Util.findPropertyValue(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.hasInputParams))
            .getOrElse(Set[OWLIndividual]())
            .map(x => {
      // the type of the parameter
      val propTypeSet = Util.findPropertyValueSet(x, URI create (Constant.domainBase
              + Constant.localNamesPrefix + Constant.hasTypeURIString))
      val paramType: String = if (propTypeSet.size > 0) {
        propTypeSet.toList(0).getURI.toString.substring(Constant.domainBase.length + Constant.localNamesPrefix.length)
      } else {
        ""
      }
      // the name of the parameter
      val propSet = Util.findDataPropertyValueSet(x, URI create (Constant.domainBase
              + Constant.localNamesPrefix + Constant.value))
      val paramName: String = if (propSet.size > 0) {
        propSet.toList(0).getLiteral
      } else {
        ""
      }
      Pair(paramName, paramType)
    })
  }

  def getInputParams: Set[Pair[String, String]] = inputParams

  val bodyIndivid = Util.findPropertyValue(getIndividual, URI create (Constant.domainBase
          + Constant.localNamesPrefix + Constant.hasBody)).getOrElse(Set[OWLIndividual]()).toList(0)
  val body: Option[Set[OWLTypedConstant]] = Util.findDataPropertyValue(bodyIndivid, URI create (Constant.domainBase
          + Constant.localNamesPrefix + Constant.value))

  def getBody = body

  val methodName: String = {
    val value = Util.findDataPropertyValue(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.value))
    value match {
      case Some(methodNameSet) => {
        if (methodNameSet.size > 0) {
          methodNameSet.toList(0).getLiteral
        } else {
          OWLURIString
        }
      }
      case None => OWLURIString
    }
  }

  def getMethodName = methodName

  override def save: OWLIndividual = {
    OWLSaveHelper.saveIndividual(individ, Constant.domainBase + Constant.localNamesPrefix + Constant.methodClassURIString)
  }

  override def toString = methodName + "(" + getInputParams + ")"
}