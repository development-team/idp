/**
 * 
 */
package ubique.idp.batchprocessor.jprolog.dynamic.predicates;

import jp.ac.kobe_u.cs.prolog.lang.Predicate;
import jp.ac.kobe_u.cs.prolog.lang.Prolog;

/**
 * @author mtalanov
 * 
 */
public class PredicateC45RuleSub extends PredicateC45Rule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5495541408822141274L;
	private Predicate next = null;
	private Predicate current = null;
	
	public PredicateC45RuleSub(Predicate p) {
		this.current = p;
	}

	@Override
	public Predicate exec(Prolog engine) throws Exception {
		if (next != null) {
			return engine.retry(this.current, this.next);
		} else {
			return engine.trust(this.current);
		}
	}

	/**
	 * Setter for link to next element in Predicate sequence.
	 * @param next Predicate
	 */
	public void setNext(Predicate next) {
		this.next = next;
	}
	
	@Override
	public String toString() {
		return "subPred(" + arg1 + ", " + arg2 + ", {" +  this.current + "}  -> " + this.first + ")";
	}

}
