package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.{Field, Module}
import org.antlr.stringtemplate.StringTemplate
import org.apache.log4j.Logger

/**
 * @author Max Talanov
 * @date 15.12.2009
 * Time: 2:03:17
 */

class JSPPageGenerator extends ModuleGenerator {
  val log = Logger.getLogger(this.getClass)

  override def apply(module: Module): String = {
    getBody(module, getContext(module))
  }

  private def getBody(module: Module, context: Map[String, List[Field]]): String = {
    val bodyNotProcessed = module.getBody
    val sT = new StringTemplate(bodyNotProcessed)
    context.map(keyValue => {
      keyValue._2.map(vl => {
        sT.setAttribute(keyValue._1, vl)
      })
    })
    return sT.toString
  }

  protected def getFields(module: Module): String = ""

  protected def getSuperClass(module: Module): String = ""

  protected def getConstructors(module: Module, context: Map[String, List[Field]]): String = ""

  protected def getMethods(module: Module, context: Map[String, List[Field]]): String = ""

}