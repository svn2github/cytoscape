package de.layclust.layout.acc;

import java.util.Random;

import de.layclust.layout.forcend.FORCEnDParameters;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.taskmanaging.TaskConfig;

public class ACCParameters implements IParameters {
	
	
	 
	//the no of iterations to be performed
	private int noOfIterations;
	//multiplicator to determine the number of iterations:
	private int multiplicatorForIterations;
	//type of the ants to be used, use the class-name as string
	private String antType;
	//constant for pickup-probability
	private double kp;
	//constant for drop-probability
	private double kd;
	//number of ants
	private int noAnts;
	//the dimension of the grid
	private int dimension;
	//alpha is used in the computation of the neighbourhood function to scale 
	//dissimilarities, range between 0 and 1
	private double alpha;
	//for "JumpingAnts" and extending classes: the maximum range 
	//an ant can jump with a step in one dimension of the grid
	private int maxStepsize;
	//for "JumpingAntsWithIncreasingViewField" and extending classes:
	//the maximum view size of an ant, it increases linear over time
	private int maxViewSize;
	//for "MemoryAnts" and extending classes:
	//the size of the ants memory
	private int memorySize;
	//threshold for the normalisation:
	private double normaliseThreshold;
	//multiplicator for the grid-size:
	private int multiplicatorForGridSize = 15;
	//multiplicator for maxStepsize:
	private int multiplicatorForMaxStepsize;
	

	public void combineConfigurationsMean(IParameters[] configurations) {
		initialiseToZero();
		for(int i=0; i<configurations.length;i++){
			noOfIterations += ((ACCParameters) configurations[i]).getNoOfIterations();
			multiplicatorForIterations += ((ACCParameters) configurations[i]).getMultiplicatorForIterations(); 
			antType = ACCConfig.antType;
			kp += ((ACCParameters) configurations[i]).getKp();
			kd += ((ACCParameters) configurations[i]).getKd(); 
			noAnts += ((ACCParameters) configurations[i]).getNoAnts();
			dimension += ((ACCParameters) configurations[i]).getDimension(); 
			alpha += ((ACCParameters) configurations[i]).getAlpha();
			maxStepsize += ((ACCParameters) configurations[i]).getMaxStepsize(); 
			maxViewSize += ((ACCParameters) configurations[i]).getMaxViewSize();
			memorySize += ((ACCParameters) configurations[i]).getMemorySize();
			normaliseThreshold += ((ACCParameters) configurations[i]).getNormaliseThreshold();
			multiplicatorForGridSize += ((ACCParameters) configurations[i]).getMultiplicatorForGridSize();
		}
		noOfIterations /= configurations.length;
		multiplicatorForIterations /= configurations.length;
		kp /= configurations.length;
		kd /= configurations.length;
		noAnts /= configurations.length;
		dimension /= configurations.length;
		alpha /= configurations.length;
		maxStepsize /= configurations.length;
		maxViewSize /= configurations.length;
		memorySize /= configurations.length;
		normaliseThreshold /= configurations.length;
		multiplicatorForGridSize /= configurations.length;
	}
	
	/**
	 * Sets the parameters of this instance to the mean value of the parameters given 
	 * in the array.
	 * @param configurations The array of all parameters for which the mean should be calculated.
	 * @param number The configuration number??
	 * 
	 */
	public void combineConfigurationsRandomly(IParameters[] configurations) {
		initialiseToZero();
		int pos;
		
		Random generator = new Random();
		
		pos = generator.nextInt(configurations.length);
		noOfIterations = ((ACCParameters) configurations[pos]).getNoOfIterations();
		
		pos = generator.nextInt(configurations.length);
		multiplicatorForIterations += ((ACCParameters) configurations[pos]).getMultiplicatorForIterations();

		antType = ACCConfig.antType;
		
		pos = generator.nextInt(configurations.length);
		kp += ((ACCParameters) configurations[pos]).getKp();
		
		pos = generator.nextInt(configurations.length);
		kd += ((ACCParameters) configurations[pos]).getKd();
		
		pos = generator.nextInt(configurations.length);
		noAnts += ((ACCParameters) configurations[pos]).getNoAnts();
		
		pos = generator.nextInt(configurations.length);
		dimension += ((ACCParameters) configurations[pos]).getDimension();
		
		pos = generator.nextInt(configurations.length);
		alpha += ((ACCParameters) configurations[pos]).getAlpha();
		
		pos = generator.nextInt(configurations.length);
		maxStepsize += ((ACCParameters) configurations[pos]).getMaxStepsize();
		
		pos = generator.nextInt(configurations.length);
		maxViewSize += ((ACCParameters) configurations[pos]).getMaxViewSize();
		
		pos = generator.nextInt(configurations.length);
		memorySize += ((ACCParameters) configurations[pos]).getMemorySize();
		
		pos = generator.nextInt(configurations.length);
		normaliseThreshold += ((ACCParameters) configurations[pos]).getNormaliseThreshold();
		
		pos = generator.nextInt(configurations.length);
		multiplicatorForGridSize += ((ACCParameters) configurations[pos]).getMultiplicatorForGridSize();

	}

	public IParameters copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void createRandomConfiguration() {
		Random generator = new Random();
		noOfIterations = 0;
		multiplicatorForIterations = 5000 * generator.nextInt(10);
		kp = generator.nextDouble();
		kd = generator.nextDouble();
		noAnts = generator.nextInt(10);
		//TODO: randomise?
		dimension = 2;
		alpha = generator.nextDouble();
		maxStepsize = generator.nextInt(50);
		maxViewSize = generator.nextInt(5);
		memorySize = generator.nextInt(100);
		normaliseThreshold = generator.nextDouble();
		multiplicatorForGridSize = 10+generator.nextInt(40);
	}

	public boolean equals(IParameters param2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void initialiseToZero() {
		noOfIterations = 0;
		multiplicatorForIterations = 0;
		antType = "MemoryAnt";
		kp = 0;
		kd = 0;
		noAnts = 0;
		dimension = 0;
		alpha = 0;
		maxStepsize = 0;
		maxViewSize = 0;
		memorySize = 0;
		normaliseThreshold = 0;
		multiplicatorForGridSize = 0;
	}

	public void readParametersFromConfig() {		
		noOfIterations = ACCConfig.noOfIterations;
		multiplicatorForIterations = ACCConfig.multiplicatorForIterations;
		antType = ACCConfig.antType;
		kp = ACCConfig.kp;
		kd = ACCConfig.kd;
		noAnts = ACCConfig.noAnts;
		dimension = ACCConfig.dimension;
		alpha = ACCConfig.alpha;
		maxStepsize = ACCConfig.maxStepsize;
		maxViewSize = ACCConfig.maxViewSize;
		memorySize = ACCConfig.memorySize;
		normaliseThreshold = ACCConfig.normaliseThreshold;
		multiplicatorForGridSize = ACCConfig.multiplicatorForGridSize;
		multiplicatorForMaxStepsize = ACCConfig.multiplicatorForMaxStepsize;
	}

	public void saveParametersToConfig() {
		// TODO Auto-generated method stub

	}

	public IParameters copy(int number) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * @return score The clustering score for this set of parameters.
	 */
	public double getScore() {
		return 0;
	}

	/**
	 * @param score The clustering score for this set of parameters.
	 */
	public void setScore(double score) {
		// TODO Auto-generated method stub
		
	}

	public void combineParametersRandomlyAndGetNewRandom(
			IParameters[] configurations) {
		Random generator = new Random();		
		int pos;

		int randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.noOfIterations = ((ACCParameters) configurations[pos]).getNoOfIterations();			
		} else { // get a new random parameter
			this.noOfIterations = 0;
		}

		randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.multiplicatorForIterations = ((ACCParameters) configurations[pos]).getMultiplicatorForIterations();			
		} else { // get a new random parameter
			this.multiplicatorForIterations = 5000 * generator.nextInt(10);
		}

		randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.kp = ((ACCParameters) configurations[pos]).getKp();	
		} else { // get a new random parameter
			this.kp = generator.nextDouble();
		}

		randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.kd = ((ACCParameters) configurations[pos]).getKd();	
		} else { // get a new random parameter
			this.kd = generator.nextDouble();
		}

		randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.noAnts = ((ACCParameters) configurations[pos]).getNoAnts();	
		} else { // get a new random parameter
			this.noAnts = generator.nextInt(10);
		}

		randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.dimension = ((ACCParameters) configurations[pos]).getDimension();	
		} else { // get a new random parameter
			this.dimension = ACCConfig.dimension;
		}

		randomOrOld = generator.nextInt(2);		
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.alpha = ((ACCParameters) configurations[pos]).getAlpha();	
		} else { // get a new random parameter
			this.	alpha = generator.nextDouble();
		}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0){ // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.maxStepsize = ((ACCParameters) configurations[pos]).getMaxStepsize();	
			} else { // get a new random parameter
				this.maxStepsize = generator.nextInt(50);
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0){ // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.maxViewSize = ((ACCParameters) configurations[pos]).getMaxViewSize();	
			} else { // get a new random parameter
				this.maxViewSize = generator.nextInt(5);
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0){ // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.memorySize = ((ACCParameters) configurations[pos]).getMemorySize();	
			} else { // get a new random parameter
				this.memorySize = generator.nextInt(100);
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.normaliseThreshold = ((ACCParameters) configurations[pos]).getNormaliseThreshold();	
			} else { // get a new random parameter
				this.normaliseThreshold = generator.nextDouble();
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.multiplicatorForGridSize = ((ACCParameters) configurations[pos]).getMultiplicatorForGridSize();	
			} else { // get a new random parameter
				this.multiplicatorForGridSize = 10+generator.nextInt(40);
			}





	}

	/**
	 * Returns the parameter alpha, which is used to scale dissimilarities between items.
	 * @return alpha parameter alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * Sets the parameter alpha, which is used to scale dissimilarities between items.
	 * @param alpha parameter alpha.
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	/**
	 * Returns the Type of ants.
	 * @return antType Type of ant ('SimpleAnt','JumpingAnt','JumpingAntWitchIncreasingViewField','MemoryAnt')
	 */
	public String getAntType() {
		return antType;
	}

	/**
	 * Sets the Type of ants.
	 * @param antType Type of ant ('SimpleAnt','JumpingAnt','JumpingAntWitchIncreasingViewField','MemoryAnt')
	 */
	public void setAntType(String antType) {
		this.antType = antType;
	}

	/**
	 * Returns the dimension.
	 * @return dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Sets the dimension.
	 * @param dimension
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	/**
	 * Returns kd - the higher kd, the higher the probability to drop an item.
	 * @return kd
	 */
	public double getKd() {
		return kd;
	}

	/**
	 * Sets kd - the higher kd, the higher the probability top drop an item. 
	 * @param kd
	 */
	public void setKd(double kd) {
		this.kd = kd;
	}

	/**
	 * Returns kp, the higher kp, the higher the probability to pick up an item. 
	 * @return kp
	 */
	public double getKp() {
		return kp;
	}

	/**
	 * Sets kp, the higher kp, the higher the probability to pick up an item. 
	 * @param kp
	 */
	public void setKp(double kp) {
		this.kp = kp;
	}

	/**
	 * Returns the maximal stepsize an ant performs in one step.
	 * @return maxStepsize
	 */
	public int getMaxStepsize() {
		return maxStepsize;
	}
	/**
	 * Sets the maximal stepsize an ant performs in one step.
	 * @param maxStepsize
	 */
	public void setMaxStepsize(int maxStepsize) {
		this.maxStepsize = maxStepsize;
	}

	/**
	 * Returns the maximal view size of an ant.
	 * @return maxViewSize
	 */
	public int getMaxViewSize() {
		return maxViewSize;
	}

	/**
	 * Sets the maximal view size of an ant.
	 * @param maxViewSize
	 */
	public void setMaxViewSize(int maxViewSize) {
		this.maxViewSize = maxViewSize;
	}

	/**
	 * Returns the memory-size of an ant (only MemoryAnts).
	 * @return memorySize
	 */
	public int getMemorySize() {
		return memorySize;
	}
	
	/**
	 * Sets the memory-size of an ant (only MemoryAnts).
	 * @param memorySize
	 */
	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	/**
	 * Returns the number of Ants.
	 * @return noAnts
	 */
	public int getNoAnts() {
		return noAnts;
	}

	/**
	 * Sets the number of Ants.
	 * @param noAnts
	 */
	public void setNoAnts(int noAnts) {
		this.noAnts = noAnts;
	}

	/**
	 * Returns the number of Iterations.
	 * @return noOfIterations
	 */
	public int getNoOfIterations() {
		return noOfIterations;
	}

	/**
	 * Sets the number of Iterations.
	 * @param noOfIterations
	 */
	public void setNoOfIterations(int noOfIterations) {
		this.noOfIterations = noOfIterations;
	}

	/**
	 * Returns the multiplicator for Iterations. Iterations = multiplicator * no of items
	 * @return multiplicatorForIterations
	 */
	public int getMultiplicatorForIterations() {
		return multiplicatorForIterations;
	}

	/**
	 * Sets the multiplicator for Iterations. Iterations = multiplicator * no of items
	 * @param multiplicatorForIterations
	 */
	public void setMultiplicatorForIterations(int multiplicatorForIterations) {
		this.multiplicatorForIterations = multiplicatorForIterations;
	}

	/**
	 * Returns the normalise-threshold. It is used to weight (dis-)similarities stronger (>1) or
	 * weaker (0-1). If the highest value in the similarity-matrix is higher then the highest 
	 * absolute value of all negative numbers, then dissimilarities are weighted, otherwise
	 * similaritys are weighted.
	 * @return normaliseThreshold
	 */
	public double getNormaliseThreshold() {
		return normaliseThreshold;
	}

	/**
	 * Sets the normalise-threshold. It is used to weight (dis-)similarities stronger (>1) or
	 * weaker (0-1). If the highest value in the similarity-matrix is higher then the highest 
	 * absolute value of all negative numbers, then dissimilarities are weighted, otherwise
	 * similaritys are weighted.
	 * @param normaliseThreshold
	 */
	public void setNormaliseThreshold(double normaliseThreshold) {
		this.normaliseThreshold = normaliseThreshold;
	}

	/**
	 * Returns the multiplicator for the grid size. The number of places on the grid is:
	 * multiplicatorForGridSize * number of items
	 * @return multiplicatorForGridSize
	 */
	public int getMultiplicatorForGridSize() {
		return multiplicatorForGridSize;
	}

	/**
	 * Sets the multiplicator for the grid size. The number of places on the grid is:
	 * multiplicatorForGridSize * number of items
	 * @param multiplicatorForGridSize
	 */
	public void setMultiplicatorForGridSize(int multiplicatorForGridSize) {
		this.multiplicatorForGridSize = multiplicatorForGridSize;
	}

	/**
	 * Returns the multicplicator for the maximal stepsize of an ant.
	 * @return multiplicatorForMaxStepsize
	 */
	public int getMultiplicatorForMaxStepsize() {
		return multiplicatorForMaxStepsize;
	}
	
	/**
	 * Sets the multiplicator for the maximal stepsize of an ant.
	 * @param multiplicatorForMaxStepsize
	 */
	public void setMultiplicatorForMaxStepsize(int multiplicatorForMaxStepsize) {
		this.multiplicatorForMaxStepsize = multiplicatorForMaxStepsize;
	}

}
