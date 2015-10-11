package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.{Field, Method}

trait MethodGenerator extends Generator {
  def apply(method: Method, context: Map[String, List[Field]]): String

  protected def getInputParams(method: Method): String

  protected def getMethodName(method: Method): String

  protected def getBody(method: Method, context: Map[String, List[Field]]): String

  protected def getReturnType(method: Method): String
}
