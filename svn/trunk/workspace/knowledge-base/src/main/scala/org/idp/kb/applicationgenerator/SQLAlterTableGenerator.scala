package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.{Module, Field}

/**
 * @author Max Talanov
 * @date 16.12.2009
 * Time: 1:28:47
 */

class SQLAlterTableGenerator extends ModuleGenerator {
  val addCommand = " ADD "
  val removeCommand = " DROP "
  val addHeader = "ALTER TABLE "
  var removeHeader = "ALTER TABLE "

  def apply(module: Module): String = {
    var resAdd = addHeader + " " + module.OWLURIString
    if (module.addedFields.size > 0) {
      for (val field <- module.addedFields) {
        resAdd = resAdd + addCommand + field.getOWLURIString + " VARCHAR(50)"
      }
      module.resetAddedFields
    } else {
      resAdd = ""
    }

    var resRemove = removeHeader + " " + module.OWLURIString
    if (module.removedFields.size > 0) {
      for (val field <- module.removedFields) {
        resRemove = resRemove + removeCommand + field.getOWLURIString
      }
      module.resetRemovedFields
    } else {
      resRemove = ""
    }
    resAdd + "\n " + resRemove
  }


  protected def getFields(module: Module): String = ""

  protected def getSuperClass(module: Module): String = ""

  protected def getConstructors(module: Module, context: Map[String, List[Field]]): String = ""

  protected def getMethods(module: Module, context: Map[String, List[Field]]): String = ""

}