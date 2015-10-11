/**
 * 
 */
package ubique.idp.utils;

import java.util.List;

/**
 * Implementation of Scala like tuple as parametrised class.
 * @author talanovm
 *
 */
public class Tuple2<T1, T2> {

	private T1 m1;
	private T2 m2;
	
	/**
	 * Public constructor.
	 * @param in1 - first member of tuple
	 * @param in2 - second member of tuple
	 */
	public Tuple2(T1 in1, T2 in2) {
		this.m1 = in1;
		this.m2 = in2;
	}

	/** 
	 * Getter for first member of tuple.
	 * @return - first member of tuple
	 */
	public T1 _1() {
		return m1;
	}

	/**
	 * Setter for first member of tuple.
	 * @param m1 - value to set member.
	 */
	public void _1(T1 m1) {
		this.m1 = m1;
	}

	/**
	 * Getter for second member of tuple.
	 * @return second member of tuple
	 */
	public T2 _2() {
		return m2;
	}

	/**
	 * Setter for second member of tuple. 
	 * @param m2 - second member of tuple
	 */
	public void _2(T2 m2) {
		this.m2 = m2;
	}
	
	@Override
	public String toString() {
		return "(" + m1 + ", " + m2 +")";
	}
}
