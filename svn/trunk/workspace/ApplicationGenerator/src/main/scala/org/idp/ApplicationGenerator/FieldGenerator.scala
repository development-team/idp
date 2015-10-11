package org.idp.ApplicationGenerator

class FieldGenerator extends Generator{
  
  def apply (fieldName: String): String = {
    var res = ""
    var normalised = fieldName
    if (! Util.isEntityNameCorrect(fieldName)) {
      normalised = Util.normaliseEntityName(fieldName)
    }
    
//    val fD = Util.getFieldDeclaration(Language, normalised)
//    val gD = Util.getGetterMethodDeclaration(Language, normalised)
//    val sD = Util.getSetterMethodDeclaration(Language, normalised)
//    val res = "\\t" + fD + "\\n \\t" + gD + "\\n \\t" + sD
    
    res
    
  } 

}
