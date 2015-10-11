package ubique.idp.batchprocessor.prolog;

import jp.ac.kobe_u.cs.prolog.lang.{Predicate, Term, Prolog, SymbolTerm}
import jp.ac.kobe_u.cs.prolog.builtin.PRED_$unify_2

/**
  * Creates C 4.5 rule as follows 
  * <code>c45([path], subst) :- path = specified_path</code>
  * on base of specified list (path) and substitution name
  */
class C45Rule(list: Term, subst: SymbolTerm) extends Predicate{
  
  var arg1: Term = null
  var arg2: Term = null
  
  override def arity() = 2
  
  override def setArgument(args: Array[Term], cont: Predicate) {
    arg1 = args(0)
    arg2 = args(1)
  }
  
  override def toString(): String = {
    "C4.5(" + arg1 + ", " + arg2 + ") {" + list + ", " + subst + "}"
  }
  
  override def exec(engine: Prolog): Predicate = {
    val a1 = engine.aregs(1).dereference()
    val a2 = engine.aregs(2).dereference()
    val cont = engine.cont
    
    if ( !list.unify(a2, engine.trail) ) { 
      return engine.fail()
    }
    return new PRED_$unify_2(a1, subst, cont)
  }
  
}
