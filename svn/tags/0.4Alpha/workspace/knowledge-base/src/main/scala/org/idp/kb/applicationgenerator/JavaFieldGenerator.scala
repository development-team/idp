package org.idp.kb.applicationgenerator

import org.idp.kb.model.domain.Field

/**
 * @author talanovm
 * @date 14.12.2009
 * Time: 10:09:14
 */

class JavaFieldGenerator extends FieldGenerator {
  /*
  * Creates the property declaration, getter and setter declaration as the String.
  * @param Field to generate.
  * @return String representation of property declaration and getter and setter methods.
  */
  override def apply(field: Field): String = {
    var res = ""
    var gD = ""
    var sD = ""
    var fD = ""

    var normalised = field.OWLURIString
    if (field.getBody.trim.length > 0) {
      normalised = field.getBody.trim
    }
    if (!Util.isEntityNameCorrect(normalised)) {
      normalised = Util.normaliseEntityName(normalised)
    }
    fD = "private " + field.getDataType + " " + normalised + ";"
    gD = generateGetter(field, field.getDataType, normalised)
    if (field.getSetterMethod.length > 0) {
      sD = "public " + " void " + " " + field.getSetterMethod + "( " + field.getDataType + " in) { this." + normalised + " = in ; }"
    } else {
      sD = ""
    }
    res = "\n\t" + fD + "\n \t" + gD + "\n \t" + sD
    res
  }

  private def generateGetter(field: Field, dataType: String, normalizedName: String): String = {
    if (field.getGetterMethod.length > 0) {
      "public " + dataType + " " + field.getGetterMethod + "() { return this." + normalizedName + "; }"
    } else {
      ""
    }
  }
}