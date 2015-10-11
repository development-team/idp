/**
 * Copyright Talanov Max under GPL v.2
 */
package ubique.idp.normalisation.ga.date.fitnessfunction;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import ubique.idp.normalisation.ga.date.command.DateNormCommand;
import ubique.idp.normalisation.ga.date.matcher.ANormalizer;
import ubique.idp.normalisation.ga.date.matcher.DayNormalizer;
import ubique.idp.normalisation.ga.date.matcher.MonthNormalizer;
import ubique.idp.normalisation.ga.date.matcher.YearNormalizer;
import ubique.idp.normalisation.ga.date.model.DateMatcherModel;
import ubique.idp.utils.Tuple3;

/**
 * Class that implements GPFitnessFunction with string to normalise.
 * 
 * @author Talanov Max.
 * 
 */
public class DateMatcherFitnessFunction extends GPFitnessFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3890381780315812973L;

	/**
	 * String to normalise.
	 */
	private String dateString = null;

	/**
	 * Supplemental parameters for date normalisation process.
	 */
	final String monthCommand = "MonthNormMatcherCommand";
	final String yearCommand = "YearNormMatcherCommand";
	final String dayCommand = "DayNormMatcherCommand";

	final double validWeight = 1.0;
	final double delimiterFirstWeight = 0.3;
	final double splitedWeight = 0.5;
	final double completenessWeight = 0.5;
	final double repetitiveWeight = 0.1;
	final double tooLongWeight = 0.1;
	final double seqWeight = 0.01;

	private final boolean DEBUG = false;

	/**
	 * Public constructor.
	 */
	public DateMatcherFitnessFunction(String dateString) {
		this.dateString = dateString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgap.gp.GPFitnessFunction#evaluate(org.jgap.gp.IGPProgram)
	 */
	@Override
	protected double evaluate(IGPProgram a_subject) {
		return computeRawFitness(a_subject);
	}

	public double computeRawFitness(final IGPProgram a_program) {
		double error = 0.0f;
		Object[] noargs = new Object[0];
		// Initialise local stores.
		// ------------------------
		a_program.getGPConfiguration().clearStack();
		a_program.getGPConfiguration().clearMemory();

		// Initialise application data
		DateMatcherModel dm = new DateMatcherModel(this.dateString);
		a_program.setApplicationData(dm);
		try {
			// Execute the program.
			// --------------------
			a_program.execute_void(0, noargs);
			a_program.execute_void(1, noargs);
			// Determine success of individual.
			// --------------------------------
			dm = (DateMatcherModel) a_program.getApplicationData();
			
			// for debug purposes
			ArrayList<DateNormCommand> commands = dm.getCommands();
			if (commands.get(0).getClass().getName().endsWith(
					monthCommand)) {
				if (commands.size() > 1 && commands.get(1).getClass().getName().endsWith(
						yearCommand)) {
					error += 0.5;
				}
			}
			

			// validation of the date (0.9)
			error += validWeight
					* this.validateDate(dm.getYear(), dm.getMonth(), dm
							.getDay());
			double errorV = error;

			// check for completeness of processing (0.5)
			error += completenessWeight
					* completenessValidation(dm.getStartPos(), dm.getCursor(),
							dm.getSource());

			// check repetitive elements (0.1)
			error += repetitiveWeight * repetitiveValidation(dm.getCommands());
			double errorR = error;

			// check for too long chromosome (0.1)
			error += tooLongWeight * this.tooLong(dm.getCommands().size());
			double errorTo = error;

			// check sequence and add penalty on less probable (0.1)
			error += seqWeight
					* this.sequenceProb(dm.getCommands(), yearCommand,
							monthCommand, dayCommand);
			double errorS = error;

			if (DEBUG) {
				System.out.println(dm);
				System.out.println(" error: " + error + " v:" + errorV + " r:"
						+ errorR + " to:" + errorTo + " s:" + errorS);

			}
		} catch (IllegalStateException iex) {
			error = GPFitnessFunction.MAX_FITNESS_VALUE;
		}
		return error;
	}

	private double validateDate(Integer year, Integer month, Integer day) {
		double error = 0.0;
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		df.setLenient(false);
		try {
			Date date = df.parse("" + day + "." + month + "." + year);
			if (DEBUG) {
				System.out.println(date);
			}
		} catch (ParseException e) {
			error += 1.0;
		}
		return error;
	}

	private double tooLong(int size) {
		double error = 0.0;
		if (size > 4) {
			error = 1.0;
		}
		return error;
	}

	/**
	 * Evaluates most probable sequence of the normalisation functions been
	 * performed: in following way: <br/> For three positions normalisation
	 * <br/> day mon year <br/> year mon day <br/> mon day year <br/> <br/> For
	 * two positions normalisation<br/> mon year <br/> year mon <br/>
	 * 
	 * @param commands
	 *            commands(functions) list been performed
	 * @param yearCommand
	 *            name of the Year command class
	 * @param monthCommand
	 *            name of the Month command class
	 * @param dayCommand
	 *            name of the day command class
	 * @return penalty
	 */
	private double sequenceProb(ArrayList<DateNormCommand> commands,
			String yearCommand, String monthCommand, String dayCommand) {
		double error = 0.0;
		// delimiter command is mandatory and is not checked in this method,
		// but I use offset.
		int offset = 0;
		int length = commands.size();
		if (length < 2 + offset) {
			error += 1.0;
		} else if (length == 2 + offset) {
			if (!commands.get(0 + offset).getClass().getName().endsWith(
					monthCommand)) {
				error += 0.5;
				if (!commands.get(0 + offset).getClass().getName().endsWith(
						yearCommand)) {
					error += 0.5;
				}
			}
		} else if (length == 3 + offset) {
			if (!commands.get(1 + offset).getClass().getName().endsWith(
					monthCommand)) {
				error += 0.5;
				if (!commands.get(0 + offset).getClass().getName().endsWith(
						monthCommand)) {
					error += 0.5;
				}
			} else if (!commands.get(0 + offset).getClass().getName().endsWith(
					dayCommand)) {
				error += 0.3;
			}
		}
		return error;
	}

	private double repetitiveValidation(ArrayList<DateNormCommand> commands) {
		Iterator<DateNormCommand> itCI = commands.iterator();
		int counter = 0;
		while (itCI.hasNext() && counter < 2) {
			String commandClassName = itCI.next().getClass().getName();
			counter = 0;
			Iterator<DateNormCommand> itCJ = commands.iterator();
			while (itCJ.hasNext() && counter < 2) {
				if (commandClassName.equals(itCJ.next().getClass().getName())) {
					counter++;
				}
			}
		}
		if (counter < 2) {
			return 0.0;
		} else {
			return 1.0;
		}
	}

	private double completenessValidation(int startPos, int endPos,
			String probDate) {
		int length = probDate.length();
		String head = "";
		String tail = "";
		List<ANormalizer<Integer>> normalizers = new CopyOnWriteArrayList<ANormalizer<Integer>>();
		normalizers.add(new YearNormalizer());
		normalizers.add(new MonthNormalizer());
		normalizers.add(new DayNormalizer());
		int normalizersSize = normalizers.size();
		List<ANormalizer<Integer>> matchedNormalizers = new ArrayList<ANormalizer<Integer>>();
		double res = 0.0;
		boolean toProcess = false;
		// incoming string analysis
		if ((startPos == length && endPos == 0)
				|| (startPos == 0 && endPos == 0)) {
			// completely not processed
			toProcess = true;
			head = probDate;
		} else if (startPos < length && endPos > 0) {
			toProcess = true;
			if (startPos >= 0)
				head = probDate.substring(0, startPos);
			if (endPos <= length)
				tail = probDate.substring(endPos);
		} else if (startPos == 0 && endPos == length) {
			// completely processed
			toProcess = false;
		} else {
			throw new IllegalArgumentException(
					"Invalid parameters of date model provided");
		}
		// run three matchers on 2 substrings
		if (toProcess) {
			List<ANormalizer<Integer>> validationRes = new ArrayList<ANormalizer<Integer>>();
			// head
			if (head.length() > 0) {
				validationRes = completenessValidationRec(normalizers, head);
				if (validationRes != null && validationRes.size() > 0) {
					normalizers.removeAll(validationRes);
					matchedNormalizers.addAll(validationRes);
				}
			}
			// tail
			if (tail.length() > 0) {
				validationRes = completenessValidationRec(normalizers, tail);
				if (validationRes != null && validationRes.size() > 0) {
					matchedNormalizers.addAll(validationRes);
				}
			}
			res = matchedNormalizers.size() / normalizersSize;
		}
		return res;
	}

	/**
	 * Recursively runs through string to match the normalizers
	 * 
	 * @param normalizers
	 * @param toParse -
	 *            String to parse
	 * @return List<ANormalizers> - the list of matched normalizers
	 */
	private List<ANormalizer<Integer>> completenessValidationRec(
			List<ANormalizer<Integer>> normalizers, String toParse) {
		Iterator<ANormalizer<Integer>> it = normalizers.iterator();
		List<ANormalizer<Integer>> res = new CopyOnWriteArrayList<ANormalizer<Integer>>();
		if (normalizers.size() > 0) {
			while (it.hasNext() && toParse.length() > 0) {
				ANormalizer<Integer> current = it.next();
				Tuple3<Integer, Integer, Integer> matchingRes = current
						.normalise(toParse);
				if (matchingRes != null && normalizers.size() > 0) {
					res.add(current);
					normalizers.remove(current);
					int start = matchingRes._2();
					int end = matchingRes._3();
					// split to head and tail matchingRes has following
					// structure
					// Result of normalisation, starPos of match, end pos of
					// match
					if (start > 0) {
						List<ANormalizer<Integer>> match = completenessValidationRec(
								normalizers, toParse.substring(0, start));
						if (match != null && match.size() > 0) {
							normalizers.removeAll(match);
							res.addAll(match);
						}
					}
					if (end < toParse.length() && normalizers.size() > 0) {
						List<ANormalizer<Integer>> match = completenessValidationRec(
								normalizers, toParse.substring(end));
						if (match != null && match.size() > 0) {
							normalizers.removeAll(match);
							res.addAll(match);
						}
					}
				}
			}
			return res;
		} else {
			return null;
		}
	}
}
