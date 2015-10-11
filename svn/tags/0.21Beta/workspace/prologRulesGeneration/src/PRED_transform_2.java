import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;

/*
 * *** Please do not edit ! ***
 * @(#) PRED_transform_2.java
 * @procedure transform/2 in transform.pl
 */

/*
 * @version Prolog Cafe 0.8 November 2003
 * @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 * @author Naoyuki Tamura    (tamura@kobe-u.ac.jp)
 */

public class PRED_transform_2 extends Predicate {
    static Predicate transform_2_1 = new PRED_transform_2_1();
    static Predicate transform_2_2 = new PRED_transform_2_2();
    static Predicate transform_2_sub_1 = new PRED_transform_2_sub_1();

    public Term arg1, arg2;

    public PRED_transform_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1; 
        arg2 = a2; 
        this.cont = cont;
    }

    public PRED_transform_2(){}
    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0]; 
        arg2 = args[1]; 
        this.cont = cont;
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.cont = cont;
        return call(engine);
    }

    public Predicate call(Prolog engine) {
        engine.setB0();
        return engine.jtry(transform_2_1, transform_2_sub_1);
    }

    public int arity() { return 2; }

    public String toString() {
        return "transform(" + arg1 + ", " + arg2 + ")";
    }
}

class PRED_transform_2_sub_1 extends PRED_transform_2 {

    public Predicate exec(Prolog engine) {
        return engine.trust(transform_2_2);
    }
}

class PRED_transform_2_1 extends PRED_transform_2 {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("normDate");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("date");

    public Predicate exec(Prolog engine) {
        Term a1, a2;
        a1 = engine.aregs[1].dereference();
        a2 = engine.aregs[2].dereference();
        Predicate cont = engine.cont;

        if ( !s1.unify(a2, engine.trail) ) return engine.fail();
        return new PRED_$unify_2(a1, s2, cont);
    }
}

class PRED_transform_2_2 extends PRED_transform_2 {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("calendar");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("normMonth");
    static SymbolTerm s3 = SymbolTerm.makeSymbol("[]");
    static ListTerm s4 = new ListTerm(s2, s3);
    static ListTerm s5 = new ListTerm(s1, s4);
    static SymbolTerm s6 = SymbolTerm.makeSymbol("monthFull");

    public Predicate exec(Prolog engine) {
        Term a1, a2;
        a1 = engine.aregs[1].dereference();
        a2 = engine.aregs[2].dereference();
        Predicate cont = engine.cont;

        if ( !s5.unify(a2, engine.trail) ) return engine.fail();
        return new PRED_$unify_2(a1, s6, cont);
    }
}

