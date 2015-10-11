package org.idp.kb.model.domain

import java.net.URI
import org.semanticweb.owl.model.{OWLIndividual}
import org.idp.kb.{Util, Constant}
import org.apache.log4j.Logger

class AddFieldAction(name: String, domainObjects: List[AnyDomain]) extends AddAction(name: String, domainObjects: List[AnyDomain]) {
  val log = Logger.getLogger(this.getClass)
  val fieldPrefix = "Word.field_Word."
  val modulePrefix = "Word.page_Word."

  val moduleObject: Option[AnyDomain] = this.domainObjects.find(dO => {
    dO.isDestination
  })

  val fieldObject: Option[AnyDomain] = this.domainObjects.find(dO => {
    dO.isActionObject && !dO.isDestination
  })

  /*
  * Finds the module appropriate for the domainObject-s of the prep type and adds the field with name of domainObject-s with type dobject.
  */
  def apply(): Option[Module] = {

    moduleObject match {
      case Some(mO) => {
        val moduleName = mO.OWLURIString.substring(mO.OWLURIString.indexOf(modulePrefix) + modulePrefix.length)
        // TODO find modules
        val m = new Module(moduleName)
        if (Util.isIndividualOfType(m.getIndivid, URI create (Constant.domainBase
                + Constant.localNamesPrefix + Constant.bLLClassURIString))
                || Util.isIndividualOfType(m.getIndivid, URI create (Constant.domainBase
                + Constant.localNamesPrefix + Constant.pagesClassURIString))
                || Util.isIndividualOfType(m.getIndivid, URI create (Constant.domainBase
                + Constant.localNamesPrefix + Constant.tablesClassURIString))) {
          fieldObject match {
            case Some(fO) => {
              val fieldName = fO.OWLURIString.substring(fO.OWLURIString.indexOf(fieldPrefix) + fieldPrefix.length)
              return Some(m.addField(new Field(fieldName)))
            }
            case None => throw new IllegalArgumentException("No field found in the statement!")
          }
        } else {
          log error "Specified module:" + moduleName + " was not found!"
        }
      }
      case None => throw new IllegalArgumentException("No module found in the statement!")
    }
    None
  }

  override def save(): OWLIndividual = {
    //TODO correct this too strait forward approach
    this.apply match {
      case Some(module) => return module.save
      case None => return Constant.dataFactory.getOWLIndividual(URI create (Constant.base
              + Constant.localNamesPrefix + name))
    }
  }

}
