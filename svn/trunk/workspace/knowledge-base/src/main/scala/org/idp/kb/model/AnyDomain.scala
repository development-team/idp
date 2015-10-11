package org.idp.kb.model

import org.semanticweb.owl.model.OWLIndividual

trait AnyDomain {
}

object AnyDomain {
  type Comp = org.idp.kb.model.cogitative.Comprehension

  def apply(individ: OWLIndividual): AnyDomain = {
    var c: Class[Integer] = Integer.TYPE
    var cC: Class[AnyAction] = AnyAction.getClass.asInstanceOf[Class[AnyAction]]
    var cS: Class[BigInt] = BigInt.getClass.asInstanceOf[Class[BigInt]]
    null
  }

  def getType(x: AnyVal): Class[_] = x match {
    case _: Byte => java.lang.Byte.TYPE
    case _: Short => java.lang.Short.TYPE
    case _: Int => java.lang.Integer.TYPE
    case _: Long => java.lang.Long.TYPE
    case _: Float => java.lang.Float.TYPE
    case _: Double => java.lang.Double.TYPE
    case _: Char => java.lang.Character.TYPE
    case _: Boolean => java.lang.Boolean.TYPE
    case _: Unit => java.lang.Void.TYPE
  }
}