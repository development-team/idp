/**
 * Copyright Talanov Max, GPL v.2. 
 */
package ubique.idp.normalisation.ga.date.command;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.model.DateModel;

/**
 * Implements delimiter change and re-splits date string in DateModel.
 * 
 * @author Talanov Max
 * @see ubique.idp.normalisation.ga.date.model.DateModel
 * @deprecated
 */
public class DelimiterChangeCommand extends DateNormCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246452899895747657L;

	private String delimiter = "\\s+"; 
	
	/**
	 * Public constructor uses default delimiter value.
	 * 
	 * @param conf
	 * @throws InvalidConfigurationException
	 */
	public DelimiterChangeCommand(GPConfiguration conf)
			throws InvalidConfigurationException {
		this(conf, "\\s+");
	}

	/**
	 * Public constructor that defines delimiter regexp.
	 * 
	 * @param conf
	 * @param delimiter
	 * @throws InvalidConfigurationException
	 */
	public DelimiterChangeCommand(GPConfiguration conf, String delimiter)
			throws InvalidConfigurationException {
		super(conf);
		this.delimiter = delimiter;
	}

	/**
	 * Public constructor.
	 * @param a_conf
	 * @param a_arity
	 * @param type
	 * @throws InvalidConfigurationException
	 */
	public DelimiterChangeCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
	}
	
	@Override
	public String toString() {
		return "Delmiter:'" + this.delimiter + "'";
	}

	public void execute_void(ProgramChromosome aChrom, int aN, Object[] args) {
		DateModel dm = (DateModel)getDateModel(aChrom);
		String dateString = dm.getSource();
		String[] splittedDateString = dateString.split(this.delimiter);
		dm.setDestination(splittedDateString);
		dm.setDelimiter(this.delimiter);
		dm.addCommand(this);
	}
}
