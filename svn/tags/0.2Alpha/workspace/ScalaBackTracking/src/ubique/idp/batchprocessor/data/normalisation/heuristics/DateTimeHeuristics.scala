package ubique.idp.batchprocessor.data.normalisation.heuristics;

object DateTimeHeuristics {

  def isMonth(a: String): boolean = {
    false
  }
  def monthDict(in: String): int = in match { 
    case (str:String) if str.trim.startsWith("янв") => 1 
    case (str:String) if str.trim.startsWith("фев") => 2 
    case (str:String) if str.trim.startsWith("мар") => 3 
    case (str:String) if str.trim.startsWith("апр") => 4 
    case (str:String) if str.trim.startsWith("май") => 5
    case (str:String) if str.trim.startsWith("июн") => 6
    case (str:String) if str.trim.startsWith("июл") => 7 
    case (str:String) if str.trim.startsWith("авг") => 8 
    case (str:String) if str.trim.startsWith("сен") => 9 
    case (str:String) if str.trim.startsWith("окт") => 10 
    case (str:String) if str.trim.startsWith("ноя") => 11 
    case (str:String) if str.trim.startsWith("дек") => 12 
  }
  def isDay (a: String): boolean = false
  def isYear(a: String): boolean = false
  
  def seqHeuristics: Array[String => boolean] = {
    val seq = new Array[String => boolean](3)
    seq(0) = isDay
    seq(1) = isMonth
    seq(2) = isYear
    seq
  }
  
  def createTokens(in: String): Array[String] = {
    // I have to consider wheather this is proper to split according to spaces
    val pattern = "\\p{Punct}"
    in split " "
  }
}
