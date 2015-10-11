/**
 * Copyright Talanov Max.
 */
package ubique.idp.normalisation.ga.date.model;

import java.util.ArrayList;

import ubique.idp.normalisation.ga.date.command.DateNormCommand;

/**
 * Model to deal with all manipulations with date string.
 * 
 * @author Talanov Max
 * @see DateModel
 */
public class DateMatcherModel extends ADateNormModel {

	/**
	 * Public constructor.
	 * 
	 * @param source -
	 *            the String to process
	 */
	public DateMatcherModel(String source) {
		this.commands = new ArrayList<DateNormCommand>();
		this.source = source;
		this.cursor = 0;
		this.startPos = this.source.length();
		this.year = null;
		this.month = null;
		this.day = null;
	}

	@Override
	public String toString() {
		String res = "";
		res += "[" + year + "-" + month + "-" + day + "] ";
		res += "<";
		for (DateNormCommand item : this.commands) {
			res += item + ", ";
		}
		res += ">";
		return res;
	}

	/**
	 * Adds current command to ArrayList and sets cursor to position specified.
	 * 
	 * @param command -
	 *            command that have been applied
	 * @param processedPos -
	 *            position where processing ended
	 */
	public void applyCurrent(DateNormCommand command, int endProcessedPos) {
		this.commands.add(command);
		this.setCursor(endProcessedPos);
	}

	/**
	 * Adds current command to ArrayList and sets cursor to position specified.
	 * 
	 * @param command -
	 *            command that have been applied
	 * @param startProcessPos -
	 *            position where processing started
	 * @param endProcessedPos -
	 *            position where processing ended
	 */
	public void applyCurrent(DateNormCommand command, int startProcessedPos,
			int endProcessedPos) {
		this.commands.add(command);
		if (this.startPos > startProcessedPos)
			this.startPos = startProcessedPos;
		this.setCursor(endProcessedPos);
	}

	public void setSource(String source) {
		this.source = source;
	}
}
