/**
 * 
 */
package ubique.idp.normalisation.ga.date;

import java.text.DateFormat;
import java.text.ParseException;

import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.util.SystemKit;

import ubique.idp.normalisation.ProblemRunner;
import ubique.idp.normalisation.ga.date.fitnessfunction.DateMatcherFitnessFunction;
import ubique.idp.normalisation.ga.date.model.ADateNormModel;

/**
 * @author Talanov Max
 * 
 */
public class DateNormProblemRunner implements ProblemRunner {
	static ADateNormModel bestDateModel = null;

	public DateNormProblemRunner() {

	}

	public String run(String in) {
		final int EVOLUTIONSNUMBER = 150;
		final int depth = 7;
		final int populationSize = 20;
		try {
			System.out
					.println("Normalize of arbitrary date problem with set of year, month and date normalisation functions");
			GPConfiguration.reset();
			GPConfiguration config = new GPConfiguration();
			config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
			config.setMaxInitDepth(depth);
			config.setPopulationSize(populationSize);
			final DateNormMatcherProblem problem = new DateNormMatcherProblem(
					config);
			// GPFitnessFunction func = problem.createFitFunc();
			GPFitnessFunction func = new DateMatcherFitnessFunction(in);
			config.setFitnessFunction(func);
			config.setCrossoverProb(0.9f);
			config.setReproductionProb(0.1f);
			config.setNewChromsPercent(0.3f);
			config.setStrictProgramCreation(true);
			config.setUseProgramCache(true);
			// config.setPreservFittestIndividual(true);

			GPGenotype gp = problem.create();

			gp.setVerboseOutput(false);
			// Simple implementation of running evolution in a thread.
			// -------------------------------------------------------
			final Thread t = new Thread(gp);

			/**
			 * New generation evolved.
			 * 
			 * @param a_firedEvent
			 *            GeneticEvent
			 */
			GeneticEventListener genotypeEvolvedGeneticEventListener = new GeneticEventListener() {
				public void geneticEventFired(GeneticEvent a_firedEvent) {
					GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
					int evno = genotype.getGPConfiguration().getGenerationNr();
					double freeMem = SystemKit.getFreeMemoryMB();
					int sleepInterval = 30;
					if (evno % 10 == 0) {
						double bestFitness = genotype.getFittestProgram()
								.getFitnessValue();
						System.out.println("Evolving generation " + evno
								+ ", best fitness: " + bestFitness
								+ ", memory free: " + freeMem + " MB" 
								+ ", thread " + t.getName());
					}
					if (evno > EVOLUTIONSNUMBER) {
						double bestFitness = genotype.getFittestProgram()
								.getFitnessValue();
						System.out.println("Evolving generation " + evno
								+ ", best fitness: " + bestFitness
								+ ", memory free: " + freeMem + " MB");
						t.stop();
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
							// System.exit(1);
						}
					}
				}
			};

			config.getEventManager().addEventListener(
					GeneticEvent.GPGENOTYPE_EVOLVED_EVENT,
					genotypeEvolvedGeneticEventListener);

			/**
			 * New best solution found.
			 * 
			 * @param a_firedEvent
			 *            GeneticEvent
			 */
			GeneticEventListener bestSolutionGeneticEventListener = new GeneticEventListener() {
				public void geneticEventFired(GeneticEvent a_firedEvent) {
					GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
					int evno = genotype.getGPConfiguration().getGenerationNr();
					String indexString = "" + evno;
					while (indexString.length() < 5) {
						indexString = "0" + indexString;
					}

					IGPProgram best = genotype.getAllTimeBest();
					try {
						ADateNormModel dm = (ADateNormModel) best
								.getApplicationData();
						// System.out.println(dm);
					} catch (ClassCastException iex) {
						iex.printStackTrace();
					}
					double bestFitness = genotype.getFittestProgram()
							.getFitnessValue();
					if (bestFitness < 0.001) {
						genotype.outputSolution(best);
						// System.out.println(best.getApplicationData());
						bestDateModel = (ADateNormModel) best
								.getApplicationData();
						t.stop();
						// System.exit(0);
					}
				}
			};

			config.getEventManager().addEventListener(
					GeneticEvent.GPGENOTYPE_NEW_BEST_SOLUTION,
					bestSolutionGeneticEventListener);
			t.start();
			t.join();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		// Create date and return it in standard way, if possible
		if (bestDateModel != null && bestDateModel.getDay() != null
				&& bestDateModel.getMonth() != null
				&& bestDateModel.getYear() != null) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			try {
				return df.format(df.parse("" + bestDateModel.getDay() + "."
						+ bestDateModel.getMonth() + "."
						+ bestDateModel.getYear()));
			} catch (ParseException e) {
				return in;
			}
		} else {
			return in;
		}
	}
}
