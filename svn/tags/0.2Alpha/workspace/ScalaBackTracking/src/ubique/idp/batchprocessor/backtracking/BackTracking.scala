package ubique.idp.batchprocessor.backtracking;

object BackTracking {
  import scala.collection.immutable.Stack
  
  type HFunc = Stack[String => boolean]
  type HFuncStackOption = Option[Stack[String => boolean]]
  
  def backTrack(tokens: Array[String], seq: Array[String=>boolean]): HFuncStackOption = {
    backTrackRec(tokens, seq, new Stack[String=>boolean])
  }
  /** Runs heuristicts on first element of the tokens list, 
  * if heuristics returns true returs the last element of the heuristics functions array 
  * @param restTokens - arry of tokens
  * @param hSeq - array of heursitics functions */
  def backTrackRec (restTokens: Array[String], hSeq: Array[String=>boolean], store: Stack[String => boolean]): 
  HFuncStackOption = {
    if (restTokens.size == 0) Some(store)
    if (hSeq == null || hSeq.size == 0) throw new IllegalArgumentException("hSeq heuristics must be set")
    val e = new ArrayIndexOutOfBoundsException("Sequence is out of bounds")
    val localStore = new Stack[String=>boolean]
    val localSeq = hSeq.filter((f) => !store.contains(f))
    while (localStore.size < localSeq.size 
        && next(localSeq, localStore).isDefined 
        // check if function  in sequence returns true
        && !localSeq(currentIndex(localSeq, localStore).getOrElse(throw e))(restTokens(0))
        // check rest part recursively
        && !backTrackRec(restTokens.slice(1,restTokens.size), localSeq , 
            store.push(localSeq(currentIndex(localSeq,localStore).getOrElse(throw e)))).isDefined
        ) 
    {
      localStore.push(localSeq(currentIndex(localSeq,localStore).getOrElse(throw e)))
    }
    // check if we ran out of all elements
    if (localStore.size < localSeq.size) Some(store.push(localStore.top))
    else None
  }
  
  def next(seq: Array[String=>boolean], store: Stack[String=>boolean]): Option[String=>boolean] = {
    if (seq.size == store.size) None
    else {
      if (store.isEmpty) Some(seq(0))
      else {
        val i = seq.indexOf[String => boolean](store.top)
        if (i == -1) None
        else if (i < seq.size-1) Some(seq(i+1))
        else None
      }
    }
  }
  
  def currentIndex(seq: Array[String=>boolean], store: Stack[String=>boolean]): Option[Int] = {
    val i = seq.indexOf[String => boolean](store.top)
    if (i== -1) None
    else Some(i)
  }
}
