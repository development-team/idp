package org.idp.kb.model.domain

import org.semanticweb.owl.model.OWLIndividual
import java.net.URI
import org.idp.kb.{Util, OWLSaveHelper, Constant}

/**
 * @author Max Talanov
 * @date 21.11.2009
 * Time: 1:00:38
 */

class Field(uriString: String, typeString: String) extends AnyDomain {
  def this(uriString: String) = this (uriString, "String")

  def this(d: AnyDomain) = this (d.OWLURIString)

  def OWLURIString = uriString

  def getOWLURIString = uriString

  def OWLURI = URI create (Constant.domainBase + Constant.localNamesPrefix + uriString)

  def getIndividual = Constant.dataFactory.getOWLIndividual(OWLURI)

  def getIsNotString = {

    dataType != "String"
  }

  val dataType: String = {
    Util.findPropertyValue(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.hasDataType)) match {
      case Some(setOWLIndividual) => {
        // Found one in the KB
        val dTIndividual = setOWLIndividual.toList(0)
        val types = Util.findDataPropertyValueSet(dTIndividual, URI create (Constant.domainBase
                + Constant.localNamesPrefix + Constant.value))
        if (types.size > 0) {
          types.toList(0).getLiteral
        } else {
          dTIndividual.toString
        }
      }
      case None => {
        // generate
        if (typeString.length > 0) {
          typeString
        } else {
          Constant.defaultFieldType
        }
      }
    }
  }

  def getDataType = dataType

  /**
   * Finds in field definition of the KB the setter method name, or generate based on uriString.
   */
  val setterMethod: String = {
    Util.findDataPropertyValue(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.hasSetterMethod)) match {
      case Some(setOWLTyped) => {
        // Found one in the KB
        if (setOWLTyped.size > 0) {
          setOWLTyped.toList(0).getLiteral
        } else {
          // generate
          if (uriString.length > 0) {
            "set" + uriString.substring(0, 1).toUpperCase + uriString.substring(1)
          } else {
            "set"
          }
        }
      }
      case None => {
        // generate
        if (uriString.length > 0) {
          "set" + uriString.substring(0, 1).toUpperCase + uriString.substring(1)
        } else {
          "set"
        }
      }
    }
  }

  def getSetterMethod = setterMethod

  def getSetterMethodInvocation = {
    if (dataType.length > 0) {
      getSetterMethod
    } else {
      ""
    }
  }

  val getterMethod: String = {
    Util.findDataPropertyValue(getIndividual, URI create (Constant.domainBase
            + Constant.localNamesPrefix + Constant.hasGetterMethod)) match {
      case Some(setOWLTyped) => {
        // Found one in the KB
        if (setOWLTyped.size > 0) {
          setOWLTyped.toList(0).getLiteral
        } else {
          // generate
          if (uriString.length > 0) {
            "get" + uriString.substring(0, 1).toUpperCase + uriString.substring(1)
          } else {
            "get"
          }
        }

      }
      case None => {
        // generate
        if (uriString.length > 0) {
          "get" + uriString.substring(0, 1).toUpperCase + uriString.substring(1)
        } else {
          "get"
        }
      }
    }
  }

  def getGetterMethod = getterMethod

  def getGetterMethodInvocation = {
    if (dataType.length > 0) {
      getGetterMethod
    } else {
      ""
    }
  }

  def classURIString = Constant.domainBase + Constant.localNamesPrefix + Constant.fieldClassURIString

  override def save: OWLIndividual = {
    OWLSaveHelper.saveIndividual(individ, Constant.domainBase + Constant.localNamesPrefix + Constant.fieldClassURIString)
  }

  override def toString: String = uriString

  private val body: String = {
    val bodyTypedConstantSet = Util.findDataPropertyValueSet(individ,
      URI create (Constant.domainBase
              + Constant.localNamesPrefix
              + Constant.value))
    if (bodyTypedConstantSet.size > 0) {
      bodyTypedConstantSet.toList(0).getLiteral
    } else {
      ""
    }
  }

  def getBody: String = body
}