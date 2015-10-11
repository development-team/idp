package org.idp.kb.model.cogitative.property

trait ComprehensionProperty {
	def apply(statement: Statement): Option[AnyFromStatement]
}
