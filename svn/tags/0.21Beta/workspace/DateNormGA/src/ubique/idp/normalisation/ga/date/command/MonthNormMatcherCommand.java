package ubique.idp.normalisation.ga.date.command;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import scala.collection.jcl.Ranged.Comparator;
import ubique.idp.normalisation.ga.date.matcher.MonthNormalizer;
import ubique.idp.normalisation.ga.date.matcher.YearNormalizer;
import ubique.idp.normalisation.ga.date.model.ADateNormModel;
import ubique.idp.utils.Tuple2;
import ubique.idp.utils.Tuple3;
import ubique.idp.utils.string.StringLengthReverseComporator;

public class MonthNormMatcherCommand extends DateNormCommand implements
		IMutateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4770496270114264140L;

	private transient MonthNormalizer normalizer;
	/**
	 * Simple public constructor.
	 * 
	 * @param conf -
	 *            GP configuration
	 * @throws InvalidConfigurationException
	 *             if super constructor fails
	 */

	public MonthNormMatcherCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		super(conf);
		this.normalizer = new MonthNormalizer();
	}

	/**
	 * Public constructor.
	 * 
	 * @param a_conf -
	 *            GP configuration
	 * @param a_arity -
	 *            arity of the command
	 * @param type -
	 *            void
	 * @throws InvalidConfigurationException
	 *             if super constructor fails
	 */
	public MonthNormMatcherCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
		this.normalizer = new MonthNormalizer();
	}

	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		return new YearNormMatcherCommand(getGPConfiguration());
	}

	@Override
	public String toString() {
		return "MonthNormMatcher";
	}

	/**
	 * Executes void node of the chromosome.
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
		String probMonth = dm.readCurrent();
		int cursor = dm.getCursor();
		if (probMonth == null) {
			dm.applyCurrent(this, cursor);
			dm.setMonth(null);
		} else {
			Tuple3<Integer, Integer, Integer> res = this.normalizer.normalise(probMonth
					.toLowerCase());
			if (res != null) {
				dm.applyCurrent(this, cursor + res._2(), cursor + res._3());
				dm.setMonth(res._1());
			} else {
				dm.applyCurrent(this, cursor);
				dm.setMonth(null);
			}
		}
	}

}
