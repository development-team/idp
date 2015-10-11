/**
 * Copyright Talanov Max, GPL v.2. 
 */
package ubique.idp.normalisation.ga.date.command;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.model.DateModel;

/**
 * Implements Day normalization command for date normalization problem.
 * 
 * @author Talanov Max
 * @deprecated
 */
public class DayNormCommand extends DateNormCommand implements IMutateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6849361187456782749L;
	private Integer maxDate;
	private Integer minDate;
	private String[] endings = { "th", "rd", "st", "nd", "е", "ое", "ие" };

	/**
	 * Simple public constructor with max and min date values initialisation.
	 * 
	 * @param conf
	 * @throws InvalidConfigurationException
	 */
	public DayNormCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
		this.initDate();
	}

	private void initDate() {
		GregorianCalendar c = new GregorianCalendar();
		this.maxDate = c.getMaximum(Calendar.DAY_OF_MONTH);
		this.minDate = c.getMinimum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Public constructor.
	 * 
	 * @param a_conf
	 * @param a_arity
	 * @param type
	 * @throws InvalidConfigurationException
	 */
	public DayNormCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
		this.initDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgap.gp.IMutateable#applyMutation(int, double)
	 */
	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		return new YearNormCommand(getGPConfiguration());
	}

	@Override
	public String toString() {
		return "DayNorm";
	}

	public void execute_void(ProgramChromosome aChrom, int aN, Object[] args) {
		DateModel dm = (DateModel) getDateModel(aChrom);
		String probDay = dm.readCurrent();
		if (probDay == null) {
			dm.applyCurrent(this, new Boolean(false), null);
			dm.setDay(null);
		} else {
			Integer res = this.normalise(probDay);
			dm.applyCurrent(this, new Boolean(res != null), "" + res);
			dm.setDay(res);
		}
	}

	private Integer normalise(String probDay) {
		try {
			// clean the endings
			String ending = "";
			for (int i = 0; i < this.endings.length; i++) {
				if (probDay.contains(ending)) {
					probDay = probDay.replaceFirst(ending, "");
					break;
				}
			}

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
