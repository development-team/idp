package ubique.idp.normalisation.ga.date.matcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ubique.idp.utils.Tuple2;
import ubique.idp.utils.Tuple3;
import ubique.idp.utils.date.DateHelper;
import ubique.idp.utils.string.StringLengthReverseComporator;

public class MonthNormalizer implements ANormalizer<Integer> {

	// Months definition
	private int minMonth;
	private int maxMonth;

	private int JANUARY = 1;
	private int FEBRUARY = 2;
	private int MARCH = 3;
	private int APRIL = 4;
	private int MAY = 5;
	private int JUNE = 6;
	private int JULY = 7;
	private int AUGUST = 8;
	private int SEPTEMBER = 9;
	private int OCTOBER = 10;
	private int NOVEMBER = 11;
	private int DECEMBER = 12;

	// month definition in different languages
	private Map<String, Integer> initMonthListEn() {
		Map<String, Integer> res = new HashMap<String, Integer>();
		res.put("jan", JANUARY);
		res.put("feb", FEBRUARY);
		res.put("mar", MARCH);
		res.put("apr", APRIL);
		res.put("may", MAY);
		res.put("jun", JUNE);
		res.put("jul", JULY);
		res.put("aug", AUGUST);
		res.put("sep", SEPTEMBER);
		res.put("oct", OCTOBER);
		res.put("nov", NOVEMBER);
		res.put("dec", DECEMBER);
		return res;
	}

	private Map<String, Integer> initMonthListRu() {
		Map<String, Integer> res = new HashMap<String, Integer>();
		res.put("янв", JANUARY);
		res.put("фев", FEBRUARY);
		res.put("мар", MARCH);
		res.put("апр", APRIL);
		res.put("май", MAY);
		res.put("июн", JUNE);
		res.put("июл", JULY);
		res.put("авг", AUGUST);
		res.put("сен", SEPTEMBER);
		res.put("окт", OCTOBER);
		res.put("ноя", NOVEMBER);
		res.put("дек", DECEMBER);
		return res;
	}

	private Map<String, Integer> initMonthListLat() {
		Map<String, Integer> res = new TreeMap<String, Integer>(
				new StringLengthReverseComporator());
		res.put("I", JANUARY);
		res.put("II", FEBRUARY);
		res.put("III", MARCH);
		res.put("IV", APRIL);
		res.put("V", MAY);
		res.put("VI", JUNE);
		res.put("VII", JULY);
		res.put("VIII", AUGUST);
		res.put("IX", SEPTEMBER);
		res.put("X", OCTOBER);
		res.put("XI", NOVEMBER);
		res.put("XII", DECEMBER);
		return res;
	}

	private void initMonths() {
		Tuple2<Integer, Integer> dayPair = DateHelper.minMaxMonth();
		this.minMonth = dayPair._1() + 1;
		this.maxMonth = dayPair._2() + 1;
	}
	
	public MonthNormalizer() {
		this.initMonths();
	}
	
	/**
	 * Returns Tuple2 of resulting month and end of matcher, or null.
	 * 
	 * @param probMonth -
	 *            string that represents probable month
	 * @return - Tuple2 of resulting month and end of matcher
	 */
	public Tuple3<Integer, Integer, Integer> normalise(String probMonth) {
		Tuple3<Integer, Integer, Integer> res = null;
		try {

			Map[] monthLists = { this.initMonthListLat(), this.initMonthListEn(),
					this.initMonthListRu()};
			for (int i = 0; i < monthLists.length; i++) {
				res = monthLanguageHelper(monthLists[i], probMonth.toLowerCase());
				if (res != null) break;
			}
			if (res == null) {
				Pattern p = Pattern.compile("[0-9]{1,2}");
				Matcher m = p.matcher(probMonth);
				boolean b = m.find();
				if (b) {
					String strMonth = m.group();
					Integer intMonth = new Integer(strMonth).intValue();
					if (intMonth >= this.minMonth
							&& intMonth <= this.maxMonth) {
						return new Tuple3<Integer, Integer, Integer>(intMonth, m.start(), m.end());
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return res;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Tries to match one of the specified month names with content of probMonth
	 * string.
	 * 
	 * @param monthList -
	 *            HashMap with String month representation mapped to Integer
	 *            month number
	 * @param probMonth -
	 *            String that probably contains month
	 * @return - Tuple2 with Integer month, and Integer end position that
	 *         matched, otherwise null
	 */
	private Tuple3<Integer, Integer, Integer> monthLanguageHelper(
			Map<String, Integer> monthList, String probMonth) {
		Set<String> keySet = monthList.keySet();
		boolean found = false;
		String key = null;
		int start = -1;
		Iterator<String> it = keySet.iterator();
		while (it.hasNext() && !found) {
			key = it.next();
			start = probMonth.indexOf(key.toLowerCase());
			found = start > -1;
		}
		if (found && start > -1) {
			return new Tuple3<Integer, Integer, Integer>(monthList.get(key), start, start + key.length());
		} else {
			return null;
		}
	}

}
