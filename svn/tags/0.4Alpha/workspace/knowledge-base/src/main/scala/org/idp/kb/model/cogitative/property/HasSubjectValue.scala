package org.idp.kb.model.cogitative.property

import java.net.URI

class HasSubjectValue(value: URI) extends ComprehensionProperty {
  def apply(statement:Statement): Option[AnyFromStatement] = {
    if ((URI create (Constant.base + Constant.localNamesPrefix + statement.getSubject.OWLURI)) == value) {
      Some(statement.getSubject)
    } else {
      None
    }
  }
}
