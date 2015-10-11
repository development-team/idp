package ubique.idp.normalisation.ga.date.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ubique.idp.utils.Tuple2;
import ubique.idp.utils.Tuple3;
import ubique.idp.utils.date.DateHelper;

public class DayNormalizer implements ANormalizer<Integer> {

	private int maxDate;
	private int minDate;
	
	public DayNormalizer() {
		this.initDate();
	}
	
	private void initDate() {
		Tuple2<Integer, Integer> dayPair = DateHelper.minMaxDay();
		this.minDate = dayPair._1();
		this.maxDate = dayPair._2();
	}
	
	/**
	 * Returns Tuple2 of resulting day and end of matcher, or null.
	 * 
	 * @param probDay -
	 *            string that represents probable day
	 * @return - Tuple2 of resulting year and end of matcher
	 */	
	public Tuple3<Integer, Integer, Integer> normalise(String probDay) {
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(probDay);
		boolean b = m.find();
		if (b) {
			Integer cRes = this.considerDay(m.group());
			if (cRes != null) {
				return new Tuple3<Integer, Integer, Integer>(cRes, m.start(), m.end());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private Integer considerDay(String probDay) {
		try {
			Integer intProbDay = new Integer(probDay);
			if (intProbDay.compareTo(this.minDate) >= 0
					&& intProbDay.compareTo(this.maxDate) <= 0) {
				return intProbDay;
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}

	}
}
