package org.idp.kb

import org.idp.kb.model.domain.AnyDomain

import java.net.URI

object ModulesUtil {

  var modules = Set[AnyDomain]()

  def setModules(sM: Set[AnyDomain]) = modules = sM

  def getModules: Set[AnyDomain] = modules

  def addModule(mod: AnyDomain): Set[AnyDomain] = {
    modules = modules ++ Set(mod)
    modules
  }


  /**
   * Checks if modules list contains module with specified URI.
   * @return true if finds, false otherwise
   */
  def modulesContains(uri: URI):Boolean = {
    modules.find(m => {
      m.OWLURIString == uri
    }) match {
      case Some(_) => true
      case None => false
    }
  }

  def addModules(inModules: Set[AnyDomain]) = {
    modules = modules ++ inModules
  }

  def findModule(uri: URI): Option[AnyDomain] = {
    modules.find(m => {
      m.individ.getURI == uri
    })
  }
  
}
