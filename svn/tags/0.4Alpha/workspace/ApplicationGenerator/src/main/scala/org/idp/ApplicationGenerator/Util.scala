package org.idp.ApplicationGenerator

object Util {
  val inCorrectString = "[^A-Za-z0-9-_]+"
  val correctReg = "[A-Za-z0-9-_]+".r
  val inCorrectReg = inCorrectString.r
  
  /*
   * Checks the input string if it could be used as the name in programming language like Java.
   * @param name the string to check.
   * @return true if the string could be used, false if not.
   */
  def isEntityNameCorrect(name: String): Boolean = {
    val res = inCorrectReg.findFirstIn(name)
    res match {
      case Some(str) => false
      case None => true
    }
  }
  
  /*
   * Returns normalised string, where all spaces are removed and next characters are Capital, all no alpha numeric characters are replaced with _ sign.
   * @param name to normalise.
   * @return normalised name.
   */
  def normaliseEntityName(name: String): String = {
    val splitted = name.split(" ")
    val res = (splitted.foldLeft("")){(part1, part2) => part1 + part2.substring(0,1).toUpperCase + part2.substring(1)}
    res.replaceAll(inCorrectString, "_")
    res 
  }  
}
