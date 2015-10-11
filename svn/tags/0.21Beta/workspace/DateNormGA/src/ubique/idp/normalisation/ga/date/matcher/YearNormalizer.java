/**
 * 
 */
package ubique.idp.normalisation.ga.date.matcher;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ubique.idp.utils.Tuple3;

/**
 * @author talanovm
 *
 */
public class YearNormalizer implements ANormalizer<Integer> {
	
	private int currentYear;

	public YearNormalizer() {
		this.currentYear = (Calendar.getInstance()).get(Calendar.YEAR) - 2000;
	}
	
	/**
	 * Returns Tuple2 of resulting year and end of matcher, or null.
	 * 
	 * @param probYear -
	 *            string that represents probable year
	 * @return - Tuple2 of resulting year and end of matcher
	 * @see ubique.idp.normalisation.ga.date.matcher.ANormalizer#normalise(java.lang.String)
	 */
	public Tuple3<Integer, Integer, Integer> normalise(String probYear) {
		Pattern p;
		p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(probYear);
		boolean b = m.find();
		if (b) {
			Integer cRes = this.considerYear(m.group());
			if (cRes != null) {
				return new Tuple3<Integer, Integer, Integer>(cRes, m.start(), m.end());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private Integer considerYear(String probYear) {
		try {
			Integer intProbYear = new Integer(probYear);
			if (intProbYear >= 0 && intProbYear < 100) {
				if (intProbYear.compareTo(this.currentYear) > 0) {
					return 1900 + intProbYear;
				} else {
					return 2000 + intProbYear;
				}
			} else if (intProbYear >= 100 && intProbYear < 1000) {
				return null;
			} else if (intProbYear >= 1000 && intProbYear < 10000) {
				return intProbYear;
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
