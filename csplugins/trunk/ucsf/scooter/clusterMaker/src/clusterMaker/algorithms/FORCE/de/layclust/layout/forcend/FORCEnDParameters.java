package de.layclust.layout.forcend;




import java.util.Random;

import de.layclust.layout.parameter_training.IParameters;

public class FORCEnDParameters implements IParameters {
	
//	public float initCircleDistance = Config.initCircleDistance;
	
	private double attractionFactor = 0;
	private double repulsionFactor = 0;
	private double maximalDisplacement = 0;
	private int iterations = 0;
	private float temperature = 0;
	private double influenceOfGraphSizeToForces = 0;
	
//	private int number = 0;
	
	private double score = 0;
	
	
//	public float attractionPower = Config.attractionPower;
//	public float repulsionPower = Config.repulsionPower;
//	public float radius = Config.initCircleDistance;	
//	public int runs = Config.runs;
//	public double bestDistance = 0;
//	public int numEdgeChanges =0;
//	public int generation = 0;
//	public double costs = 0;	
//	public String topOrRandom = "R"; // "T"
//	public String parents = "";
	
//	public FORCEnDParameters(){
//	}

	/**
	 * Sets the parameters of this instance to the mean value of the parameters given 
	 * in the array.
	 * @param configurations The array of all parameters for which the mean should be calculated.
	 * @param number The configuration number??
	 * 
	 */
	public void combineConfigurationsMean(IParameters[] configurations) {
		initialiseToZero();
		/* add all values */
		for(int i=0; i<configurations.length;i++){
			this.attractionFactor +=((FORCEnDParameters) configurations[i]).
					getAttractionFactor();
			this.influenceOfGraphSizeToForces += ((FORCEnDParameters) 
					configurations[i]).getInfluenceOfGraphSizeToForces();
			this.iterations += ((FORCEnDParameters) configurations[i]).getIterations();
			this.maximalDisplacement += ((FORCEnDParameters) 
					configurations[i]).getMaximalDisplacement();
			this.repulsionFactor += ((FORCEnDParameters) configurations[i]).getRepulsionFactor();
			this.temperature += ((FORCEnDParameters) configurations[i]).getTemperature();
			// TODO add extra param here
		}
		/* divide by the number of configurations */
		this.attractionFactor /= configurations.length;
		this.influenceOfGraphSizeToForces /= configurations.length;
		this.iterations /= configurations.length;
		this.maximalDisplacement /= configurations.length;
		this.repulsionFactor /= configurations.length;
		this.temperature /= configurations.length;
		//TODO add extra param here
	}

	public void combineConfigurationsRandomly(IParameters[] configurations) {

		initialiseToZero();
		
		int pos;
		
		Random generator = new Random();
		
		pos = generator.nextInt(configurations.length);
		this.attractionFactor = ((FORCEnDParameters) configurations[pos]).
				getAttractionFactor();
				
		pos = generator.nextInt(configurations.length);
		this.influenceOfGraphSizeToForces = ((FORCEnDParameters) 
				configurations[pos]).getInfluenceOfGraphSizeToForces();
		
		pos = generator.nextInt(configurations.length);
		this.iterations = ((FORCEnDParameters) configurations[pos]).getIterations();
		
		pos = generator.nextInt(configurations.length);
		this.maximalDisplacement = ((FORCEnDParameters) configurations[pos]).
				getMaximalDisplacement();
		
		pos = generator.nextInt(configurations.length);
		this.repulsionFactor = ((FORCEnDParameters) configurations[pos]).
				getRepulsionFactor();
		
		pos = generator.nextInt(configurations.length);
		this.temperature = ((FORCEnDParameters) configurations[pos]).
				getTemperature();
		// TODO add extra param here
	}
	
	public void combineParametersRandomlyAndGetNewRandom(IParameters[] configurations){
		
		Random generator = new Random();		
		int pos;
		
		int randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.attractionFactor = ((FORCEnDParameters) configurations[pos]).
					getAttractionFactor();			
		} else { // get a new random parameter
			this.attractionFactor = 10 * generator.nextDouble();
		}
		
		randomOrOld = generator.nextInt(2);
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.influenceOfGraphSizeToForces = ((FORCEnDParameters) 
					configurations[pos]).getInfluenceOfGraphSizeToForces();
		} else { // get a new random parameter
			this.influenceOfGraphSizeToForces = 0.5 + 4*generator.nextDouble();
		}
		
		randomOrOld = generator.nextInt(2);
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.iterations = ((FORCEnDParameters) configurations[pos]).getIterations();
		} else { // get a new random parameter
			this.iterations = 10 + generator.nextInt(200);
		}
		
		randomOrOld = generator.nextInt(2);
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.maximalDisplacement = ((FORCEnDParameters) configurations[pos]).
					getMaximalDisplacement();
		} else { // get a new random parameter
			this.maximalDisplacement = 50 + generator.nextInt(500);
		}
		
		randomOrOld = generator.nextInt(2);
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.repulsionFactor = ((FORCEnDParameters) configurations[pos]).
					getRepulsionFactor();
		} else { // get a new random parameter
			this.repulsionFactor = 10 * generator.nextDouble();
		}
		
		randomOrOld = generator.nextInt(2);
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.temperature = ((FORCEnDParameters) configurations[pos]).
					getTemperature();
		} else { // get a new random parameter
			this.temperature = 50 + generator.nextInt(2500);
		}
		
		// TODO add extra param here and check ranges for random generation
		
	}

//	public IParameters copy(int number) {
//		FORCEnDParameters copyParam = new FORCEnDParameters();
//		copyParam.setAttractionFactor(this.attractionFactor);
//		copyParam.setInfluenceOfGraphSizeToForces(this.influenceOfGraphSizeToForces);
//		copyParam.setIterations(this.iterations);
//		copyParam.setMaximalDisplacement(this.maximalDisplacement);
//		copyParam.setRepulsionFactor(this.repulsionFactor);
//		copyParam.setTemperature(this.temperature);
////		 TODO add extra param here		
//		return copyParam;
//			
//	}

	public void createRandomConfiguration() {
		
		Random generator = new Random();
		
//		c.radius = 10 + generator.nextInt(300);
//		c.attractionFactor = 0.2 + 3 * generator.nextDouble();
		this.attractionFactor = 10 * generator.nextDouble();
//		c.repulsionFactor = 0.2 + 6 * generator.nextDouble();
		this.repulsionFactor = 10 * generator.nextDouble();
//		c.repulsionFactor = 0;
		this.iterations = 10 + generator.nextInt(100);
//		c.rounds = 10;
//		c.runs = 1 + generator.nextInt(2);
//		randomParam.runs = 1;
		this.temperature = 50 + generator.nextInt(2500);
		this.influenceOfGraphSizeToForces = 0.5 + 4*generator.nextDouble();
		this.maximalDisplacement = 50 + generator.nextInt(500);

//		randomParam.parents = "IV"; // stands for "in vitro" ;-)	
		// TODO check whether ranges are good or not
	}

//	/**
//	 * Compares the parameters of this instance to the ones of the given instance
//	 * and returns true if all of them are equal. Otherwise false is returned. Also if
//	 * the given instance is a different implementation of {@link IParameters}, then 
//	 * false is returned.
//	 * @param param2 The instance of {@link IParameters} to which it should be compared.
//	 * @return A boolean, whether this instance is equal to the given instance.
//	 */
//	public boolean equals(IParameters param2) {
//	
//		if(this.getClass() != param2.getClass()){ //TODO check whether this really returns the correct result!!
//			return false;
//		}		
//		if(this.attractionFactor != ((FORCEnDParameters) param2).
//				getAttractionFactor()){
//			return false;
//		}
//		if(this.influenceOfGraphSizeToForces != ((FORCEnDParameters) 
//				param2).getInfluenceOfGraphSizeToForces()){
//			return false;
//		}		
//		if(this.iterations != ((FORCEnDParameters) param2).getIterations()){
//			return false;
//			}		
//		if(this.maximalDisplacement != ((FORCEnDParameters) param2).
//				getMaximalDisplacement()){
//			return false;
//			}
//		if(this.repulsionFactor  != ((FORCEnDParameters) param2).getRepulsionFactor()){
//			return false;
//		}
//		if(this.temperature  != ((FORCEnDParameters) param2).getTemperature()){
//			return false;
//		}
//		return true;
//	}

	public void initialiseToZero() {
		this.attractionFactor = 0;
		this.influenceOfGraphSizeToForces = 0;
		this.maximalDisplacement = 0;
		this.repulsionFactor = 0;
		this.iterations = 0;
		this.temperature = 0;
		// TODO add extra param here
	}

	public void readParametersFromConfig() {	
		this.attractionFactor = FORCEnDLayoutConfig.attractionFactor;
		this.repulsionFactor = FORCEnDLayoutConfig.repulsionFactor;
		this.maximalDisplacement = FORCEnDLayoutConfig.maximalDisplacement;
		this.iterations = FORCEnDLayoutConfig.iterations;
		this.temperature = FORCEnDLayoutConfig.temperature;
		this.influenceOfGraphSizeToForces = FORCEnDLayoutConfig.influenceOfGraphSizeToForces;
		//TODO add extra param here
	}

	public void saveParametersToConfig() {
		FORCEnDLayoutConfig.attractionFactor = this.attractionFactor;
		FORCEnDLayoutConfig.influenceOfGraphSizeToForces = this.influenceOfGraphSizeToForces;
		FORCEnDLayoutConfig.maximalDisplacement = this.maximalDisplacement;
		FORCEnDLayoutConfig.repulsionFactor = this.repulsionFactor;
		FORCEnDLayoutConfig.iterations = this.iterations;
		FORCEnDLayoutConfig.temperature = this.temperature;
		//TODO add extra param here
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

	/**
	 * @return the score The clustering cost for these set of parameters.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param cost The clustering cost for these set of parameters.
	 */
	public void setScore(double score) {
		this.score = score;
	}
	
	public String toString(){
		StringBuffer paramString = new StringBuffer();
		
		paramString.append("FORCEnD paramter Configuration:");
//		paramString.append(" number - ");
//		paramString.append(this.number);
		paramString.append("\n score - ");
		paramString.append(this.score);
		paramString.append("\n iterations - ");
		paramString.append(this.iterations);
		paramString.append("\n attractionFactor - ");
		paramString.append(this.attractionFactor);
		paramString.append("\n repulsionFactor - ");
		paramString.append(this.repulsionFactor);
		paramString.append("\n influenceOfGraphSizeToForces - ");
		paramString.append(this.influenceOfGraphSizeToForces);
		paramString.append("\n maximalDisplacement - ");
		paramString.append(this.maximalDisplacement);
		paramString.append("\n temperature - ");
		paramString.append(this.temperature);
				
		return paramString.toString();
	}


}
