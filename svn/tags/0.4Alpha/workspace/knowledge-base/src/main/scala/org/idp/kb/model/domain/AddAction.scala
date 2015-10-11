package org.idp.kb.model.domain

import org.idp.kb.Constant

/*
 * DAO with business logic of AddAction of the domain.
 */
abstract class AddAction(name:String, domainObjects: List[AnyDomain]) extends AnyAction {
  
  override def classURIString = Constant.addActionURIString
  override def OWLURIString = classURIString + Constant.classDelimiter + name
  
  val obj = domainObjects.filter(o => o.isInstanceOf[ActionObject])
  val destination = domainObjects.filter(o => o.isInstanceOf[Destination])
  
  override def toString = "(" + OWLURIString + " : obj=" + obj + ", dest=" + destination + ")"
}
