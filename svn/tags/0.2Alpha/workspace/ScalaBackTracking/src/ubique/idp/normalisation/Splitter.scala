package ubique.idp.normalisation;

/** Split  the string according to delimiter and return part specified by index
 * @param delimiter
 * @param input String to split
 */
   
class Splitter(delimiter: String, input: String) {
  val arrayInput = input.trim.split(delimiter)
  
  def getInputString = input
  def size = arrayInput.length
/** Returns the string specified by index
  * @param index int index of splitted part
  * @return String specified by index
  */
  def split(index: int):String = {
    try {
      this.arrayInput.apply(index)
    } catch {
      case (e: ArrayIndexOutOfBoundsException) => 
      throw new ArrayIndexOutOfBoundsException("Splitted string has less parts: " + size + " than required by index " + index)
    }
  }
}
