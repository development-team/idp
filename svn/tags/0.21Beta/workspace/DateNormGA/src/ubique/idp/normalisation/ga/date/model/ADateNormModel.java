package ubique.idp.normalisation.ga.date.model;

import java.util.ArrayList;

import ubique.idp.normalisation.ga.date.command.DateNormCommand;

/**
 * Abstract class for Date models.
 * @author talanovm
 *
 */
abstract public class ADateNormModel {

	protected String source;
	protected ArrayList<DateNormCommand> commands;
	protected int cursor;
	protected int startPos;
	protected Integer year;
	protected Integer month;
	protected Integer day;
	
	@Override
	public String toString() {
		return "Abstract DateModel";
	}

	/**
	 * Adds current command to ArrayList and sets cursor to position specified.
	 * @param command - command that have been applied
	 * @param startProcessedPos - position where processing ended
	 */
	abstract public void applyCurrent(DateNormCommand command, int startProcessedPos);
	
	/**
	 * Adds current command to ArrayList and sets cursor to position specified.
	 * @param command - command that have been applied
	 * @param startProcessedPos - position where processing ended
	 * @param endProcessedPos - position where processing started
	 */
	abstract public void applyCurrent(DateNormCommand command, int startProcessedPos, int endProcessedPos);
	
	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}
	
	public ArrayList<DateNormCommand> getCommands() {
		return commands;
	}
	
	public void addCommand(DateNormCommand command) {
		this.commands.add(command);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public String readCurrent() {
		return this.source.substring(this.cursor);
	}

	public int getStartPos() {
		return startPos;
	}
	
}
