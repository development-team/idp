package ubique.idp.normalisation.ga.date.command;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.matcher.DayNormalizer;
import ubique.idp.normalisation.ga.date.model.ADateNormModel;
import ubique.idp.utils.Tuple3;

/**
 * Matching normalizer for day component of date.
 * 
 * @author talanovm
 * 
 */
public class DayNormMatcherCommand extends DateNormCommand implements
		IMutateable {

	private transient DayNormalizer dn;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8617178350700970057L;
   
	/**
	 * Simple public constructor with max and min date values initialisation.
	 * 
	 * @param conf -
	 *            GP configuration
	 * @throws InvalidConfigurationException -
	 *             in case super constructor fails
	 */
	public DayNormMatcherCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
		this.dn = new DayNormalizer();
	}

	/**
	 * Public constructor.
	 * 
	 * @param a_conf -
	 *            GP configuration
	 * @param a_arity
	 * @param type
	 * @throws InvalidConfigurationException -
	 *             in case super constructor fails
	 */
	public DayNormMatcherCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
		this.dn = new DayNormalizer();
	}

	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		return new YearNormMatcherCommand(getGPConfiguration());
	}

	@Override
	public String toString() {
		return "DayNormMatcher";
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
		String probDay = dm.readCurrent();
		int cursor = dm.getCursor();
		if (probDay == null) {
			dm.applyCurrent(this, cursor);
			dm.setDay(null);
		} else {
			Tuple3<Integer, Integer, Integer> res = this.dn.normalise(probDay);
			if (res != null) {
				dm.applyCurrent(this, cursor + res._2(), cursor + res._2());
				dm.setDay(res._1());
			} else {
				dm.applyCurrent(this, cursor);
				dm.setDay(null);
			}
		}
	}

}
