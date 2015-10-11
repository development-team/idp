package ubique.idp.batchprocessor.prolog.adapter;

import scala.collection.mutable.{HashMap, ArrayBuffer}
import scala.collection.immutable.Stack
import jp.ac.kobe_u.cs.prolog.lang.{Term, ListTerm, SymbolTerm}

object Mapping2PredicatesAdapter {
  type PathMapping = HashMap[Stack[String],String]
  type PredicateMappingArray = ArrayBuffer[PredicateMapping]
  type PredicateMapping = Tuple2[Term, SymbolTerm]
  
  /**
  *  Maps PathMapping to PredicateMapping
  * @param in PathMapping = HashMap[Stack[String],String] Stack of nodenames collected along the path to destination node
  * @return res PredicateMappingArray = ArrayBuffer[Tuple2[Term,SymbolTerm]] ArrayBuffer of Tuple2 of ListTerm of path and Substitutuon name
  */
  
  def map(in: PathMapping): PredicateMappingArray = {
    val res: PredicateMappingArray = new ArrayBuffer[PredicateMapping]()
    if (in.size > 0) {
      var pathListArray: ArrayBuffer[Term] = new ArrayBuffer[Term]()
      var listTerm: ListTerm = null
      for ((aKey,aValue) <- in) {
        aKey.map[Unit]((aNode)=> {
          val symb = SymbolTerm.makeSymbol(aNode)
          if (listTerm == null) {
            listTerm = new ListTerm(symb, SymbolTerm.makeSymbol("[]"))
          } else {
            listTerm = new ListTerm(symb, listTerm)
          }
        })
        res += new Tuple2(listTerm, SymbolTerm.makeSymbol(aValue))
      }
    }
    res
  }
  
  /** 
    *  Creates Term list on base of Stack[String]
    * @param path - Stack[String] path accumulated
    * @return Term - the ListTerm
    */
  def stack2TermList (path: Stack[String]): Term = {
    var lastTerm: Term = SymbolTerm.makeSymbol("[]");
    path.map[Unit]((aNode)=> {
      val s = SymbolTerm.makeSymbol(aNode)
      lastTerm = new ListTerm(s, lastTerm)
    })
    lastTerm
  }
}
