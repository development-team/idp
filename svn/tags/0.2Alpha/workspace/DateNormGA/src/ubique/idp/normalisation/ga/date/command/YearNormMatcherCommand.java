/**
 * 
 */
package ubique.idp.normalisation.ga.date.command;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.matcher.YearNormalizer;
import ubique.idp.normalisation.ga.date.model.ADateNormModel;
import ubique.idp.utils.Tuple3;

/**
 * Year normaliser that uses matching technique.
 * 
 * @author talanovm
 * 
 */
public class YearNormMatcherCommand extends DateNormCommand implements
		IMutateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7806717586767223035L;
	private transient YearNormalizer normalizer;

	/**
	 * Simple public constructor with arity = 0 and returnType = VoidClass
	 * 
	 * @param conf -
	 *            GP configuration
	 * @throws InvalidConfigurationException
	 *             in case super constructor throws
	 */
	public YearNormMatcherCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
		normalizer = new YearNormalizer();
	}

	/**
	 * Public constructor.
	 * 
	 * @param a_conf -
	 *            GP configuration
	 * @param a_arity -
	 *            command arity
	 * @param type -
	 *            commad type (Void, Integer)
	 * @throws InvalidConfigurationException -
	 *             in case super constructor fails
	 */
	public YearNormMatcherCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
		this.normalizer = new YearNormalizer();
	}

	/**
	 * Appiles mutation to DayNormMatcherCommand.
	 * 
	 * @param a_index -
	 *            not relevant
	 * @param a_percentage -
	 *            mutation rate, not used
	 * @see org.jgap.gp.IMutateable#applyMutation(int, double)
	 */
	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		return new DayNormMatcherCommand(getGPConfiguration());
	}

	@Override
	public String toString() {
		return "YearNormMatcher";
	}

	/**
	 * Executes void node of chromosome.
	 * 
	 * @param aChrom -
	 *            input chromosome
	 * @param aN -
	 *            not used
	 * @param args -
	 *            arguments, not used
	 */
	public void execute_void(ProgramChromosome aChrom, int aN, Object[] args) {
		ADateNormModel dm = getDateModel(aChrom);
		String probYear = dm.readCurrent();
		int cursor = dm.getCursor();
		if (probYear == null) {
			dm.applyCurrent(this, cursor);
			dm.setYear(null);
		} else {
			Tuple3<Integer, Integer, Integer> res = this.normalizer.normalise(probYear);
			if (res != null) {
				dm.applyCurrent(this, cursor + res._2(), cursor + res._3());
				dm.setYear(res._1());
			} else {
				dm.applyCurrent(this, cursor);
				dm.setYear(null);
			}
		}
	}

	
}
