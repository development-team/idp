package org.idp.kb.model.cogitative.property

import java.net.URI
import org.apache.log4j.Logger

class AnyObjectWithType(objectTypeURI: URI) extends ComprehensionProperty {
  val log = Logger.getLogger(this.getClass)
 
  def apply(statement: Statement): Option[AnyFromStatement] = {
    log debug ("apply : objectTypeURI" + objectTypeURI + " statement " + statement )
	val res = statement.objects.filter( o => {
		log debug o.getObjectType.getOrElse(new ObjectType("", "")).OWLURI.toString
		log debug objectTypeURI.toString.endsWith(o.getObjectType.getOrElse(new ObjectType("", "")).OWLURI.toString)
		objectTypeURI.toString.endsWith(
	    o.getObjectType.getOrElse(new ObjectType("", "")).OWLURI.toString)
	})
	log debug res
       if (res.length > 0 ) {
        Some(res(0))
       } else {
         None
       }
	}
}
