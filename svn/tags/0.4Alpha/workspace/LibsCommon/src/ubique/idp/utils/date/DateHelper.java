package ubique.idp.utils.date;

import java.util.Calendar;

import ubique.idp.utils.Tuple2;

public class DateHelper {

	/**
	 * Determines minimum and maximum of day of month values available.
	 * @return - Tuple2 of minimum and maximum of day of month
	 */
	public static Tuple2<Integer, Integer> minMaxDay() {
		Calendar c = Calendar.getInstance();
		return new Tuple2<Integer, Integer>(c.getMinimum(Calendar.DAY_OF_MONTH), c.getMaximum(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Determines minimum and maximum of month values available.
	 * @return - Tuple2 of minimum and maximum of month available
	 */
	public static Tuple2<Integer, Integer> minMaxMonth() {
		Calendar c = Calendar.getInstance();
		return new Tuple2<Integer, Integer>(c.getMinimum(Calendar.MONTH), c.getMaximum(Calendar.MONTH));
	}
}
