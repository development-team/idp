/**
 * 
 */
package ubique.idp.normalisation.ga.date.command;

import java.util.Calendar;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.model.DateModel;

/**
 * Year string normalisation command.
 * @author talanovm
 * @deprecated 
 */
public class YearNormCommand extends DateNormCommand implements IMutateable {
	int currentYear;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8656681093694112145L;

	/**
	 * Simple constructor with default arity = 0 and returnType = VoidClass
	 * 
	 * @param conf
	 * @throws InvalidConfigurationException
	 */
	public YearNormCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
		this.currentYear = (Calendar.getInstance()).get(Calendar.YEAR) - 2000;
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
	public YearNormCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
		this.currentYear = (Calendar.getInstance()).get(Calendar.YEAR) - 2000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgap.gp.IMutateable#applyMutation(int, double)
	 */
	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		return new DayNormCommand(getGPConfiguration());
	}

	@Override
	public String toString() {
		return "YearNorm";
	}

	public void execute_void(ProgramChromosome aChrom, int aN, Object[] args) {
		DateModel dm = (DateModel)getDateModel(aChrom);
		String probYear = dm.readCurrent();
		if (probYear == null) {
			dm.applyCurrent(this, new Boolean(false), null);
			dm.setYear(null);
		} else {
			Integer res = this.normalise(probYear);
			dm.applyCurrent(this, new Boolean(res != null), "" + res);
			dm.setYear(res);
		}
	}

	private Integer normalise(String probYear) {
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
