package ubique.idp.utils.string;

import java.util.Comparator;

/**
 * Class to compare length of 2 strings
 * @author talanovm
 *
 */
public class StringLengthReverseComporator implements Comparator<String> {

	/**
	 * Compares lengths of the specified strings and returns positive result in case first string is shorter than second.
	 * @param o1 - first string to compare
	 * @param o2 - second string to compare
	 * @return o2.length() - o1.length()
	 */
	@Override
	public int compare(String o1, String o2) {
		if (o1.length() == o2.length()) return - o1.compareTo(o2);
		return (o2.length() - o1.length());
	}

}
