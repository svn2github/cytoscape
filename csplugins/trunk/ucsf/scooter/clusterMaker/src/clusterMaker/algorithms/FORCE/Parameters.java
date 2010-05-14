package clusterMaker.algorithms.FORCE;

import java.util.Random;

//import de.layclust.layout.parameter_training.IParameters;

public class Parameters {
	
//	public float initCircleDistance = Config.initCircleDistance;
	
	private double attractionFactor = 0;
	private double repulsionFactor = 0;
	private double maximalDisplacement = 0;
	private int iterations = 0;
	private float temperature = 0;
	private double influenceOfGraphSizeToForces = 0;
	
	
        public Parameters(int iterations,double attractionFactor,double repulsionFactor,double maximalDisplacement,float temperature,double influenceOfGraphSizesToForces){
	    
	    this.iterations = iterations;
	    this.repulsionFactor = repulsionFactor;
	    this.maximalDisplacement = maximalDisplacement;
	    this.temperature = temperature;
	    this.influenceOfGraphSizeToForces = influenceOfGraphSizeToForces;
	}
	
	
	

	/**
	 * @return the attractionFactor
	 */
	public double getAttractionFactor() {
		return attractionFactor;
	}

	/**
	 * @param attractionFactor the attractionFactor to set
	 */
	public void setAttractionFactor(double attractionFactor) {
		this.attractionFactor = attractionFactor;
	}

	/**
	 * @return the influenceOfGraphSizeToForces
	 */
	public double getInfluenceOfGraphSizeToForces() {
		return influenceOfGraphSizeToForces;
	}

	/**
	 * @param influenceOfGraphSizeToForces the influenceOfGraphSizeToForces to set
	 */
	public void setInfluenceOfGraphSizeToForces(double influenceOfGraphSizeToForces) {
		this.influenceOfGraphSizeToForces = influenceOfGraphSizeToForces;
	}

	/**
	 * @return the maximalDisplacement
	 */
	public double getMaximalDisplacement() {
		return maximalDisplacement;
	}

	/**
	 * @param maximalDisplacement the maximalDisplacement to set
	 */
	public void setMaximalDisplacement(double maximalDisplacement) {
		this.maximalDisplacement = maximalDisplacement;
	}

	/**
	 * @return the repulsionFactor
	 */
	public double getRepulsionFactor() {
		return repulsionFactor;
	}

	/**
	 * @param repulsionFactor the repulsionFactor to set
	 */
	public void setRepulsionFactor(double repulsionFactor) {
		this.repulsionFactor = repulsionFactor;
	}

	/**
	 * @return the rounds
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param rounds the rounds to set
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	/**
	 * @return the temperature
	 */
	public float getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	

}
