/**
 * Copyright Talanov Max under GPL v.2
 */
package ubique.idp.normalisation.ga.date.fitnessfunction;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import ubique.idp.normalisation.ga.date.command.DateNormCommand;
import ubique.idp.normalisation.ga.date.model.DateMatcherModel;

/**
 * Class that implements GPFitnessFunction with string to normalise.
 * 
 * @author Talanov Max.
 * @deprecated
 */
public class DateFitnessFunction extends GPFitnessFunction {

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
	final String monthCommand = "ubique.idp.normalisation.ga.date.MonthNormCommand";
	final String yearCommand = "ubique.idp.normalisation.ga.date.YearNormCommand";
	final String dayCommand = "ubique.idp.normalisation.ga.date.DayNormCommand";
	final String delimiterCommand = "ubique.idp.normalisation.ga.date.DelimiterChangeCommand";

	final double validWeight = 1.0;
	final double delimiterFirstWeight = 0.3;
	final double splitedWeight = 0.5;
	final double completenessWeight = 0.5;
	final double repetitiveWeight = 0.1;
	final double tooLongWeight = 0.1;
	final double seqWeight = 0.1;

	private final boolean DEBUG = false;

	/**
	 * Public constructor.
	 */
	public DateFitnessFunction(String dateString) {
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
		// DateModel dm = new DateModel("1969 jul 20");
		DateMatcherModel dm = new DateMatcherModel(this.dateString);
		// DateModel dm = new DateModel("30.07.1971");
		a_program.setApplicationData(dm);
		try {
			// Execute the program.
			// --------------------
			a_program.execute_void(0, noargs);
			// a_program.execute_void(1, noargs);
			// Determine success of individual.
			// --------------------------------
			dm = (DateMatcherModel) a_program.getApplicationData();

			// validation of the date 0.9
			error += validWeight
					* this.validateDate(dm.getYear(), dm.getMonth(), dm
							.getDay());
			double errorV = error;
			// check completeness of split according to delimiter
			// error += splitedWeight * completeSplit(dm.getDestination().length);

			// check repetitive elements
			error += repetitiveWeight * repetitiveValidation(dm.getCommands());
			double errorR = error;
			// check completeness of date
			// error += completenessWeight
			//		* this.completenessValid(dm.getDestination().length, dm
			//				.getCommands());
			// double errorC = error;
			// check for too long chromosome
			error += tooLongWeight * this.tooLong(dm.getCommands().size());
			double errorTo = error;
			// check sequence and add penalty on less probable
			error += seqWeight
					* this.sequenceProb(dm.getCommands(), yearCommand,
							monthCommand, dayCommand);
			double errorS = error;

			if (DEBUG) {
				System.out.println(dm);
				System.out.println(" error: " + error + " v:" + errorV + " r:"
						+ errorR + " to:" + errorTo + " s:"
						+ errorS);

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

	private double completeSplit(int elementsNum) {
		double error = 0.0;
		if (elementsNum < 2) {
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
	 * Check if date normalisation performed if number of elements of split date
	 * more than 2
	 * 
	 * @param elementsNum
	 *            number of elements of split date
	 * @param commands
	 *            performed commands array
	 * @return penalty of 1.0 if DayNormCommand not found
	 */
	private double completenessValid(int elementsNum,
			ArrayList<DateNormCommand> commands) {
		double error = 0.9;
		boolean day = false;
		boolean month = false;
		boolean year = false;

		if (elementsNum > 2) {
			// contains the proper command according to name of a class
			for (DateNormCommand command : commands) {
				if (command.getClass().getName().equals(dayCommand) && !day) {
					error -= 0.3;
					day = true;
				}
				if (command.getClass().getName().equals(monthCommand) && !month) {
					error -= 0.3;
					month = true;
				}
				if (command.getClass().getName().equals(yearCommand) && !year) {
					error -= 0.3;
					year = true;
				}
			}
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
			if (!commands.get(0 + offset).getClass().getName().equals(
					monthCommand)) {
				error += 0.5;
				if (!commands.get(0 + offset).getClass().getName().equals(
						yearCommand)) {
					error += 0.5;
				}
			}
		} else if (length == 3 + offset) {
			if (!commands.get(1 + offset).getClass().getName().equals(
					monthCommand)) {
				error += 0.5;
				if (!commands.get(0 + offset).getClass().getName().equals(
						monthCommand)) {
					error += 0.5;
				}
			} else if (!commands.get(0 + offset).getClass().getName().equals(
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
}
