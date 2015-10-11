package org.idp.kb.model.cogitative.property

import java.net.URI

import org.apache.log4j.Logger

class HasObjectValue(valueURI: URI) extends ComprehensionProperty {
  
  private val log = Logger.getLogger(this.getClass)
  
  /*
   * Actually runs the hasObjectValue property operation over statement.
   */
  def apply (statement: Statement): Option[AnyFromStatement] = {
    val res = statement.getObjects.filter(o => { 
      (URI create (Constant.base + Constant.localNamesPrefix + o.OWLURI)) == valueURI
    })
    if (res.length > 0) {
      Some(res(0))
    } else {
      None
    }
  }
}
