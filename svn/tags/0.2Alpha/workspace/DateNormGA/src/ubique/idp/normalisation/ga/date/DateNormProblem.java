package ubique.idp.normalisation.ga.date;

import org.jgap.InvalidConfigurationException;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.util.SystemKit;

import ubique.idp.normalisation.ga.date.command.DayNormCommand;
import ubique.idp.normalisation.ga.date.command.DelimiterChangeCommand;
import ubique.idp.normalisation.ga.date.command.MonthNormCommand;
import ubique.idp.normalisation.ga.date.command.YearNormCommand;
import ubique.idp.normalisation.ga.date.fitnessfunction.DateFitnessFunction;
import ubique.idp.normalisation.ga.date.model.DateModel;
/**
 * @deprecated
 */
public class DateNormProblem extends GPProblem {

	@Override
	public GPGenotype create() throws InvalidConfigurationException {
		Class[] types = { CommandGene.VoidClass, CommandGene.VoidClass };
		Class[][] argTypes = { {}, {} };
		int[] minDepths = new int[] { 0, 1 };
		int[] maxDepths = new int[] { 0, 1 };
		GPConfiguration conf = getGPConfiguration();
		CommandGene[][] nodeSets = {
				{ new DelimiterChangeCommand(conf, "\\s*/\\s*"),
						new DelimiterChangeCommand(conf, "\\s*\\.\\s*"),
						new DelimiterChangeCommand(conf, "\\s+"),
						new DelimiterChangeCommand(conf, "\\s*-\\s*"),
						new DelimiterChangeCommand(conf, "\\s*\\,?\\s+"), },
				{
						new SubProgram(conf, new Class[] {
								CommandGene.VoidClass, CommandGene.VoidClass,
								CommandGene.VoidClass }),
						new SubProgram(conf, new Class[] {
								CommandGene.VoidClass, CommandGene.VoidClass,
								CommandGene.VoidClass, CommandGene.VoidClass }),
						new DayNormCommand(conf), new MonthNormCommand(conf),
						new YearNormCommand(conf), 
				} 
		};

		// Create genotype with initial population.
		// ----------------------------------------
		return GPGenotype.randomInitialGenotype(conf, types, argTypes,
				nodeSets, minDepths, maxDepths, 5000, new boolean[] { false,
						false }, true);
	}

	public DateNormProblem(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
	}

	public static void main(String[] args) {
		final int EVOLUTIONSNUMBER = 20000;
		final int depth = 7;
		final int populationSize = 500;
		try {
			System.out
					.println("Normalize of arbitrary date problem with set of year, month and date normalisation functions");
			GPConfiguration config = new GPConfiguration();
			config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
			config.setMaxInitDepth(depth);
			config.setPopulationSize(populationSize);
			final DateNormProblem problem = new DateNormProblem(config);
			GPFitnessFunction func = problem.createFitFunc();
			config.setFitnessFunction(func);
			config.setCrossoverProb(0.9f);
			config.setReproductionProb(0.1f);
			config.setNewChromsPercent(0.3f);
			config.setStrictProgramCreation(true);
			config.setUseProgramCache(true);
			// config.setPreservFittestIndividual(true);

			GPGenotype gp = problem.create();
			gp.setVerboseOutput(true);
			// Simple implementation of running evolution in a thread.
			// -------------------------------------------------------
			final Thread t = new Thread(gp);

			config.getEventManager().addEventListener(
					GeneticEvent.GPGENOTYPE_EVOLVED_EVENT,
					new GeneticEventListener() {
						public void geneticEventFired(GeneticEvent a_firedEvent) {
							GPGenotype genotype = (GPGenotype) a_firedEvent
									.getSource();
							int evno = genotype.getGPConfiguration()
									.getGenerationNr();
							double freeMem = SystemKit.getFreeMemoryMB();
							int sleepInterval = 30;
							if (evno % 100 == 0) {
								double bestFitness = genotype
										.getFittestProgram().getFitnessValue();
								System.out.println("Evolving generation "
										+ evno + ", best fitness: "
										+ bestFitness + ", memory free: "
										+ freeMem + " MB");
							}
							if (evno > EVOLUTIONSNUMBER) {
								t.stop();
								double bestFitness = genotype
										.getFittestProgram().getFitnessValue();
								System.out.println("Evolving generation "
										+ evno + ", best fitness: "
										+ bestFitness + ", memory free: "
										+ freeMem + " MB");
							} else {
								try {
									// Collect garbage if memory low.
									// ------------------------------
									if (freeMem < 50) {
										System.gc();
										t.sleep(500);
									} else {
										// Avoid 100% CPU load.
										// --------------------
										Thread.sleep(sleepInterval);
									}
								} catch (InterruptedException iex) {
									iex.printStackTrace();
									System.exit(1);
								}
							}
						}
					});

			GeneticEventListener myGeneticEventListener = new GeneticEventListener() {
				/**
				 * New best solution found.
				 * 
				 * @param a_firedEvent
				 *            GeneticEvent
				 */
				public void geneticEventFired(GeneticEvent a_firedEvent) {
					GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
					int evno = genotype.getGPConfiguration().getGenerationNr();
					String indexString = "" + evno;
					while (indexString.length() < 5) {
						indexString = "0" + indexString;
					}

					IGPProgram best = genotype.getAllTimeBest();
					try {
						DateModel dm = (DateModel) best.getApplicationData();
						System.out.println(dm);
					} catch (ClassCastException iex) {
						iex.printStackTrace();
					}
					double bestFitness = genotype.getFittestProgram()
							.getFitnessValue();
					if (bestFitness < 0.001) {
						genotype.outputSolution(best);
						System.out.println(best.getApplicationData());
						t.stop();
						// System.exit(0);
					}
				}
			};

			config.getEventManager().addEventListener(
					GeneticEvent.GPGENOTYPE_NEW_BEST_SOLUTION,
					myGeneticEventListener);
			t.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private GPFitnessFunction createFitFunc() {
		return new DateFitnessFunction("30.07.1971");
	}
}
