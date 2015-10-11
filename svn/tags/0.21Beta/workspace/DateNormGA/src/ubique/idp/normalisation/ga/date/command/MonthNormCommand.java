package ubique.idp.normalisation.ga.date.command;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.model.DateModel;

/**
 * Normalisation of month string command.
 * 
 * @author talanovm
 * @deprecated
 */
public class MonthNormCommand extends DateNormCommand implements IMutateable {

	private HashMap<String, Integer> monthList = new HashMap<String, Integer>();
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 72087362931805418L;

	public MonthNormCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
		this.initMonthList();
		this.initMonthListRu();
		this.initMonths();
	}

	private void initMonthList() {
		this.monthList.put("jan", JANUARY);
		this.monthList.put("feb", FEBRUARY);
		this.monthList.put("mar", MARCH);
		this.monthList.put("apr", APRIL);
		this.monthList.put("may", MAY);
		this.monthList.put("jun", JUNE);
		this.monthList.put("jul", JULY);
		this.monthList.put("aug", AUGUST);
		this.monthList.put("sep", SEPTEMBER);
		this.monthList.put("oct", OCTOBER);
		this.monthList.put("nov", NOVEMBER);
		this.monthList.put("dec", DECEMBER);
	}

	private void initMonthListRu() {
		this.monthList.put("янв", JANUARY);
		this.monthList.put("фев", FEBRUARY);
		this.monthList.put("мар", MARCH);
		this.monthList.put("апр", APRIL);
		this.monthList.put("май", MAY);
		this.monthList.put("июн", JUNE);
		this.monthList.put("июл", JULY);
		this.monthList.put("авг", AUGUST);
		this.monthList.put("сен", SEPTEMBER);
		this.monthList.put("окт", OCTOBER);
		this.monthList.put("ноя", NOVEMBER);
		this.monthList.put("дек", DECEMBER);
	}

	private void initMonths() {
		GregorianCalendar c = new GregorianCalendar();
		this.maxMonth = c.getMaximum(Calendar.MONTH) + 1;
		this.minMonth = c.getMinimum(Calendar.MONTH) + 1;
	}

	/**
	 * Public constructor.
	 * 
	 * @param a_conf
	 * @param a_arity
	 * @param type
	 * @throws InvalidConfigurationException
	 *             if super constructor fails
	 */
	public MonthNormCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
		this.initMonthList();
		this.initMonths();
	}

	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		return new YearNormCommand(getGPConfiguration());
	}

	@Override
	public String toString() {
		return "MonthNorm";
	}

	public void execute_void(ProgramChromosome aChrom, int aN, Object[] args) {
		DateModel dm = (DateModel) getDateModel(aChrom);
		String probMonth = dm.readCurrent();
		if (probMonth == null) {
			dm.applyCurrent(this, new Boolean(false), null);
			dm.setMonth(null);
		} else {
			Integer res = this.normalise(probMonth);
			dm.applyCurrent(this, new Boolean(res != null), "" + res);
			dm.setMonth(res);
		}
	}

	private Integer normalise(String probMonth) {
		try {
			int intMonth = new Integer(probMonth).intValue();
			if (intMonth >= this.minMonth + 1 && intMonth <= this.maxMonth + 1) {
				return (intMonth);
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			if (probMonth.length() < 3) {
				return null;
			} else {
				return monthDict(probMonth);
			}
		}

	}

	private Integer monthDict(String probMonth) {
		Set<String> keys = this.monthList.keySet();
		String aKey = "";
		for (String k : keys) {
			if (probMonth.toLowerCase().contains(k)) {
				aKey = k;
			}
		}
		// not found
		if (aKey == "") {
			return null;
		} else {
			return this.monthList.get(aKey);
		}
	}
}
