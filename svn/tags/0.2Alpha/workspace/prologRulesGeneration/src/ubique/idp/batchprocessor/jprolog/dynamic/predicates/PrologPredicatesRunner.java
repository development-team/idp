/**
 * 
 */

package ubique.idp.batchprocessor.jprolog.dynamic.predicates;

import java.util.Stack;

import jp.ac.kobe_u.cs.prolog.builtin.PRED_$unify_2;
import jp.ac.kobe_u.cs.prolog.lang.ListTerm;
import jp.ac.kobe_u.cs.prolog.lang.Predicate;
import jp.ac.kobe_u.cs.prolog.lang.Prolog;
import jp.ac.kobe_u.cs.prolog.lang.PrologControl;
import jp.ac.kobe_u.cs.prolog.lang.SymbolTerm;
import jp.ac.kobe_u.cs.prolog.lang.Term;
import jp.ac.kobe_u.cs.prolog.lang.VariableTerm;

/**
 * First attempt to use PrologCafe predicates dynamically
 * 
 * @author Max Talanov
 * 
 */
public class PrologPredicatesRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PredicateC45Rule transform = new PredicateC45Rule();
		Predicate transformDate = new Predicate() {
			private static final long serialVersionUID = 1L;
			public Term arg1, arg2;

			@Override
			public Predicate exec(Prolog engine) {

				SymbolTerm s1 = SymbolTerm.makeSymbol("normDate");
				SymbolTerm s2 = SymbolTerm.makeSymbol("date");

				Term a1, a2;
		        a1 = engine.aregs[1].dereference();
		        a2 = engine.aregs[2].dereference();
		        Predicate cont = engine.cont;

		        if ( !s1.unify(a2, engine.trail) ) return engine.fail();
		        return new PRED_$unify_2(a1, s2, cont);
			}

			@Override
			public int arity() {
				return 2;
			}

			@Override
			public void setArgument(Term[] args, Predicate cont) {
				// TODO Auto-generated method stub
				arg1 = args[0];
				arg2 = args[1];
				this.cont = cont;
			}

			public String toString() {
		        return "transformDate(" + arg1 + ", " + arg2 + ")";
		    }
		};

		Predicate transformCalendar = new Predicate() {
			private static final long serialVersionUID = 1L;
			public Term arg1, arg2;
			
			SymbolTerm s0 = SymbolTerm.makeSymbol("doc");
			SymbolTerm s1 = SymbolTerm.makeSymbol("header");
			SymbolTerm s2 = SymbolTerm.makeSymbol("post_date");
			SymbolTerm s3 = SymbolTerm.makeSymbol("[]");
			ListTerm s4 = new ListTerm(s2, s3);
			ListTerm s5 = new ListTerm(s1, s4);
			ListTerm s6 = new ListTerm(s0, s5);
			SymbolTerm rule = SymbolTerm.makeSymbol("monthFull");

			@Override
			public Predicate exec(Prolog engine) {

				SymbolTerm s0 = SymbolTerm.makeSymbol("doc");
				SymbolTerm s1 = SymbolTerm.makeSymbol("header");
				SymbolTerm s2 = SymbolTerm.makeSymbol("post_date");
				SymbolTerm s3 = SymbolTerm.makeSymbol("[]");
				ListTerm s4 = new ListTerm(s2, s3);
				ListTerm s5 = new ListTerm(s1, s4);
				ListTerm s6 = new ListTerm(s0, s5);
				SymbolTerm rule = SymbolTerm.makeSymbol("monthFull");

				Term a1, a2;
		        a1 = engine.aregs[1].dereference();
		        a2 = engine.aregs[2].dereference();
		        Predicate cont = engine.cont;
				
		        if ( !s6.unify(a2, engine.trail) ) return engine.fail();
		        return new PRED_$unify_2(a1, rule, cont);
			}

			@Override
			public int arity() {
				return 2;
			}

			@Override
			public void setArgument(Term[] args, Predicate cont) {
				arg1 = args[0];
				arg2 = args[1];
				this.cont = cont;
			}
			
			public String toString() {
		        return "transformCalendar(" + arg1 + ", " + arg2 + ") {" + s6 + ", " + rule + "}";
		    }
		};

		transform.add(transformCalendar);
		/// transform.add(transformDate);
		System.out.println(" predicate created ");
		PrologControl p = new PrologControl();
		
		// setup list
		
		SymbolTerm s1 = SymbolTerm.makeSymbol("calendar");
		SymbolTerm s2 = SymbolTerm.makeSymbol("normMonth");
		SymbolTerm s3 = SymbolTerm.makeSymbol("[]");
		ListTerm s4 = new ListTerm(s2, s3);
		ListTerm s5 = new ListTerm(s1, s4);
		
		// stack implementation
		Stack<String> path = new Stack<String>();
		path.push("doc");
		path.push("header");
		path.push("post_date");

		ListTerm lt = null;
		while (!path.empty()) {
			SymbolTerm symb = SymbolTerm.makeSymbol(path.pop());
			if (lt == null) {
				lt = new ListTerm(symb, s3);
			} else {
				lt = new ListTerm(symb, lt);
			}
		}
		
		Term a1 = SymbolTerm.makeSymbol("monthFull");
		Term a3 = new VariableTerm();
		Term a2 = new VariableTerm();
		Term[] argsP = { a2, lt };

		p.setPredicate(transform, argsP);
		System.out.println(" argements set ");
		
		System.out.println("* get all results from predicates");
		boolean r = p.call();
		while(r) {
			// System.out.print(a1.toString());
			System.out.println(a2.toString());
			r = p.redo();
		}
		
		System.out.println("* get one result");
		if (p.execute(transform, argsP)) {
			// System.out.println(a1.toString());
			System.out.println(a2.toString());
		} else
			System.out.println("fail");
		 
	}

}
