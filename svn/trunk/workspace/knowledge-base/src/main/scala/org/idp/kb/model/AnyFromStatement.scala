package org.idp.kb.model

import java.net.URI

trait AnyFromStatement {
  
  def classURIString: String 
  def OWLURIString: String
  def OWLURI = URI create OWLURIString
}
