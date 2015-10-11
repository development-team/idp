/**
 * Copyright Talanov Max, under GPL v.2
 */
package ubique.idp.normalisation.ga.date.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import ubique.idp.normalisation.ga.date.command.DateNormCommand;

/**
 * @author Talanov Max
 * @deprecated
 */
public class DateModel extends ADateNormModel {
	private String source;
	private String[] destination;
	// stores commands been generated
	private ArrayList<DateNormCommand> commands;
	private HashMap<String, Boolean> flags;
	private int cursor;
	private Integer year;
	private Integer month;
	private Integer day;
	private Calendar cDate;
 	private String delimiter;

	@Override
	public String toString() {
		String res = "d:'" + this.delimiter + "', {";
		for (String item : this.destination) {
			res = res + item + ", ";
		}
		res += "} ";
		res += "[" + year + "-" + month + "-" + day + "] ";
		res += "<";
		for (DateNormCommand item : this.commands) {
			res += item + ", ";
		}
		res += ">";
		return res;
	}

	public DateModel(String source) {
		this.commands = new ArrayList<DateNormCommand>();
		this.source = source;
		// this could be commented or deleted in case of delimiter start populating it's results
		this.destination = source.split("\\s+");
		// this.destination = new String[0];
		this.cursor = 0;
		this.year = null;
		this.month = null;
		this.day = null;
		cDate = GregorianCalendar.getInstance();
		cDate.clear();
		cDate.setLenient(false);
	}

	public String readCurrent() {
		if (this.cursor < this.destination.length) {
			return this.destination[this.cursor];
		} else {
			return null;
		}
	}

	public void writeCurrent(String in) {
		if (this.cursor < this.destination.length) {
			this.destination[this.cursor] = in;
		}
	}

	public void incCursor() {
		if (this.cursor < this.destination.length) {
			this.cursor++;
		}
	}

	public void resetCursor () {
		this.cursor = 0;
	}
	
	public void applyCurrent(DateNormCommand command, Boolean resFlag, String res) {
		this.commands.add(command);
		this.writeCurrent(res);
		this.incCursor();
	}
	
	
	
	public void addCommand(DateNormCommand command) {
		this.commands.add(command);
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

	public String[] getDestination() {
		return destination;
	}

	public int getCursor() {
		return cursor;
	}

	public HashMap<String, Boolean> getFlags() {
		return flags;
	}

	public ArrayList<DateNormCommand> getCommands() {
		return commands;
	}

	/**
	 * Getter for source string.
	 * @return source string
	 */
	public String getSource() {
		return source;
	}

	/** 
	 * Setter for destination array.
	 * @param destination destination array
	 */
	public void setDestination(String[] destination) {
		this.destination = destination;
		//TODO: get predifinition in place when delimiter problem will be solved 
		/*if(destination.length < 3) {
			// a bit of predifinition
			this.day = cDate.getActualMinimum(Calendar.DAY_OF_MONTH);
		} else {
			this.day = null;
		}*/
	}
	

	/**
	 * Setter for delimiter.
	 * @param delimiter String delimiter of the date string
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public void applyCurrent(DateNormCommand command, int processedPos) {
		this.addCommand(command);
		this.setCursor(processedPos);
	}

	@Override
	public void applyCurrent(DateNormCommand command, int startProcessed, int endProcessedPos) {
		this.addCommand(command);
		this.setCursor(endProcessedPos);
	}

}
