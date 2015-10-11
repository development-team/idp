package ubique.idp.normalisation.ga.date.command;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ubique.idp.normalisation.ga.date.matcher.ANormalizer;
import ubique.idp.normalisation.ga.date.model.ADateNormModel;
import ubique.idp.utils.Tuple3;

public class DateNormCommand extends CommandGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateNormCommand(final GPConfiguration aConf)
			throws InvalidConfigurationException {
		super(aConf, 0, CommandGene.VoidClass);
	}

	public DateNormCommand(GPConfiguration a_conf, int a_arity,
			Class<Object> type) throws InvalidConfigurationException {
		super(a_conf, a_arity, type);
	}

	public ADateNormModel getDateModel(ProgramChromosome aChrom) {
		return (ADateNormModel) aChrom.getIndividual().getApplicationData();
	}

	@Override
	public String toString() {
		return "DateNormCommand";
	}

}
