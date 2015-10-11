/**
 * 
 */
package ubique.idp.processing.state;

import java.io.Serializable;

/**
 * Class that represents state of document processing
 * @author m talanov
 *
 */
public class State implements Serializable {
	/**
	 * For Serializable
	 */
	private static final long serialVersionUID = 8225648477841935084L;

	private String stageName;
	
	private int stageNumber = 0;
	
	/** Stage percent of complete */
	private double percent;

	/** Number of processing stages */
	private int numStages;
	
	/** Flag to denote that stage is complete */
	private boolean complete = false;
	
	/**
	 * Public constructor.
	 * @param numStages - number of the stages
	 * @param initStage - name of initial stage
	 */
	public State(int numStages, String initStage) {
		this.numStages = numStages;
		this.stageName = initStage;
	}
	
	/**
	 * Public constructor.
	 * @param numStages - number of the stages
	 * @param initStage - name of initial stage
	 * @param initStageNumber - number of initial stage
	 */
	public State(int numStages, String initStage, int initStageNumber) {
		this.numStages = numStages;
		this.stageName = initStage;
		this.stageNumber = initStageNumber;
	}
	
	public void incrementStageNumber() {
		++ stageNumber;
	}
	
	/** Incrementing stage number and creating new stage with specified name.
	 * 
	 * @param stageName String name of new stage
	 */
 	public void incrementStage(String stageName) {
 		this.percent = 0.0;
 		this.stageName = stageName;
 		++ this.stageNumber;
 	}
	
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stage) {
		this.stageName = stage;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public double getTotalPercent() {
		return (stageNumber/numStages)*100 + (1/stageNumber) * percent;
	}
	public int getNumStages() {
		return numStages;
	}
	public void setNumStages(int numStages) {
		this.numStages = numStages;
	}

	public int getStageNumber() {
		return stageNumber;
	}

	public void setStageNumber(int stageNumber) {
		this.stageNumber = stageNumber;
	}
	
	public void setStage(int stageNum, String stageName) {
		this.stageNumber = stageNum;
		this.stageName = stageName;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
}
