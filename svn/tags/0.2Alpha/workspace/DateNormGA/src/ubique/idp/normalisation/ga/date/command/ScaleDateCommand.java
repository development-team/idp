package ubique.idp.normalisation.ga.date.command;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.helper.ScaleDate;
import ubique.idp.normalisation.ga.date.model.ADateNormModel;
import ubique.idp.utils.Tuple2;
import ubique.idp.utils.date.DateHelper;

/**
 * Scaling date command, to setup elements that are not setup in date.
 * 
 * @author talanovm
 * 
 */

public class ScaleDateCommand extends DateNormCommand {
	ScaleDate mscale = ScaleDate.DAY;
	/**
	 * 
	 */
	private static final long serialVersionUID = 9057206268956752093L;

	/**
	 * Simple public constructor with default scale of day.
	 * 
	 * @param conf -
	 *            GP configuration
	 * @throws InvalidConfigurationException -
	 *             in case super constructor fails
	 */
	public ScaleDateCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
	}

	public ScaleDateCommand(GPConfiguration conf, ScaleDate scale)
			throws InvalidConfigurationException {
		super(conf);
		this.mscale = scale;
	}

	/**
	 * Complex public constructor with default scale of day.
	 * 
	 * @param a_conf -
	 *            GP configuration
	 * @param a_arity
	 * @param type
	 * @throws InvalidConfigurationException -
	 *             in case super constructor fails
	 */
	public ScaleDateCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
	}

	@Override
	public String toString() {
		return "ScaleDate:" + this.mscale;
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

		Tuple2<Integer, Integer> datePair = DateHelper.minMaxDay();
		int day = datePair._1();
		datePair = DateHelper.minMaxMonth();
		int month = datePair._1();
		switch (this.mscale) {
		case MONTH:
			if (dm.getMonth() == null) {
				dm.setMonth(month);
			}
			/*
			 * if (dm.getDay() == null) { dm.setDay(day); }
			 */
			break;
		case DAY:
			if (dm.getDay() == null) {
				dm.setDay(day);
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid variant of Date scale");
		}
	}

}
