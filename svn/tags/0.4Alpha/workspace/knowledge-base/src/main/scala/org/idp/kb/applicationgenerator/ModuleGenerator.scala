package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.{Field, Module}
import java.net.URI
import org.idp.kb.{Constant}

trait ModuleGenerator extends Generator {

  /**
   * Returns the code that represents the module. 
   */
  def apply(module: Module): String

  /*
  * Returns the string for referencing the module.
  * @param Module to process.
  * @return String representation of the reference to the module.
  */
  def getReference(module: Module): String = {
    module.OWLURIString
  }

  protected def getFields(module: Module): String

  protected def getSuperClass(module: Module): String

  protected def getConstructors(module: Module, context: Map[String, List[Field]]): String

  protected def getMethods(module: Module, context: Map[String, List[Field]]): String

  /**
   * Recursively get the set Map of the URI of the module -> List of the fields of the module
   */
  protected def getContext(module: Module): Map[String, List[Field]] = {
    /*val modulesSet = module.getModules
    var context = Map[String, List[Field]]()
    modulesSet.map(mod => {
      val fieldSet: Set[Field] = mod.getFields
      var fieldArray = scala.List[Field]()
      for (val f <- fieldSet) {
        fieldArray = fieldArray ::: List(f)
      }
      context += mod.OWLURIString -> fieldArray
    })

    val fieldSet: Set[Field] = module.getFields
    var fieldArray = scala.List[Field]()
    for (val f <- fieldSet) {
      fieldArray = fieldArray ::: List(f)
    }
    context += module.OWLURIString -> fieldArray
    context*/
    getContextRec(module, Set[Module]()) match {
      case Some(c) => c
      case None => Map[String, List[Field]]()
    }
  }

  private def getContextRec(module: Module, modulesVisited: Set[Module]): Option[Map[String, List[Field]]] = {
    var context = Map[String, List[Field]]()
    if (!modulesVisited.contains(module)) {
      // place current module with it's fields
      val modulesVisitedWithCurrent = modulesVisited + module
      val fieldSet: Set[Field] = module.getFields
      var fieldArray = scala.List[Field]()
      for (val f <- fieldSet) {
        fieldArray = fieldArray ::: List(f)
      }
      context += module.OWLURIString -> fieldArray
      // process recursively the module of the current module
      val modulesSet = module.getModules
      for (val m <- modulesSet) {
        val resM: Option[Map[String, List[Field]]] = getContextRec(m, modulesVisitedWithCurrent)
        resM match {
          case Some(rM) => context ++= rM
          case None => // empty result ignored
        }
      }
      Some(context)
    } else {
      None
    }
  }
}

object ModuleGenerator {
  def apply(module: Module): ModuleGenerator = {
    val individ = module.individ
    if (org.idp.kb.Util.isIndividualOfType(individ, URI create (Constant.domainBase + Constant.localNamesPrefix + Constant.pagesURIStrig))) {
      return new JSPPageGenerator
    } else if (org.idp.kb.Util.isIndividualOfType(individ, URI create (Constant.domainBase + Constant.localNamesPrefix + Constant.tablesURIString))) {
      return new SQLAlterTableGenerator
    } else {
      return new JavaClassGenerator
    }
  }
}
