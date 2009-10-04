package de.layclust.layout.acc;

import java.util.Random;

import de.layclust.layout.IParameters;

public class ACCParameters implements IParameters {
	
	

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
	
	private double score;
	
	private int sa_iterations;
	private double sa_m;
	private double sa_n;
	
	

	public void combineConfigurationsMean(IParameters[] configurations) {
		//System.out.println("Combine Mean called.");
		initialiseToZero();
		for(int i=0; i<configurations.length;i++){
			multiplicatorForIterations += ((ACCParameters) configurations[i]).getMultiplicatorForIterations(); 
			antType = ACCConfig.antType;
			kp += ((ACCParameters) configurations[i]).getKp();
			kd += ((ACCParameters) configurations[i]).getKd(); 
			noAnts += ((ACCParameters) configurations[i]).getNoAnts();
			alpha += ((ACCParameters) configurations[i]).getAlpha();
			maxStepsize += ((ACCParameters) configurations[i]).getMaxStepsize(); 
			maxViewSize += ((ACCParameters) configurations[i]).getMaxViewSize();
			memorySize += ((ACCParameters) configurations[i]).getMemorySize();
			normaliseThreshold += ((ACCParameters) configurations[i]).getNormaliseThreshold();
			multiplicatorForGridSize += ((ACCParameters) configurations[i]).getMultiplicatorForGridSize();
			multiplicatorForMaxStepsize += ((ACCParameters) configurations[i]).getMultiplicatorForMaxStepsize();
			sa_iterations += ((ACCParameters) configurations[i]).getSa_iterations();
			sa_m += ((ACCParameters) configurations[i]).getSa_m();
			sa_n += ((ACCParameters) configurations[i]).getSa_n();
		}
		multiplicatorForIterations /= configurations.length;
		kp /= configurations.length;
		kd /= configurations.length;
		noAnts /= configurations.length;
		alpha /= configurations.length;
		maxStepsize /= configurations.length;
		maxViewSize /= configurations.length;
		memorySize /= configurations.length;
		normaliseThreshold /= configurations.length;
		multiplicatorForGridSize /= configurations.length;
		multiplicatorForMaxStepsize /= configurations.length;
		sa_iterations  /= configurations.length;
		sa_m  /= configurations.length;
		sa_n  /= configurations.length;
	}
	
	/**
	 * Sets the parameters of this instance to the mean value of the parameters given 
	 * in the array.
	 * @param configurations The array of all parameters for which the mean should be calculated.
	 * 
	 */
	public void combineConfigurationsRandomly(IParameters[] configurations) {
		initialiseToZero();
		int pos;
		
		Random generator = new Random();
		
		pos = generator.nextInt(configurations.length);
		
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

		pos = generator.nextInt(configurations.length);
		multiplicatorForMaxStepsize += ((ACCParameters) configurations[pos]).getMultiplicatorForMaxStepsize();
		
		pos = generator.nextInt(configurations.length);
		sa_iterations += ((ACCParameters) configurations[pos]).getSa_iterations();
		
		pos = generator.nextInt(configurations.length);
		sa_m += ((ACCParameters) configurations[pos]).getSa_m();
		
		pos = generator.nextInt(configurations.length);
		sa_n += ((ACCParameters) configurations[pos]).getSa_n();
		
		pos = generator.nextInt(configurations.length);
		
		
	}

	public IParameters copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void createRandomConfiguration() {
		//System.out.println("Random Configuration called.");
		Random generator = new Random();
		multiplicatorForIterations = 1000 * (5+generator.nextInt(11));
		kp = generator.nextDouble();
		kd = generator.nextDouble();
		noAnts = 1;
		antType = ACCConfig.antType;
		alpha = generator.nextDouble();
		//maxStepsize = generator.nextInt(49)+1;
		maxStepsize = 25;
		maxViewSize = 1+generator.nextInt(2);
		//memorySize = 1+generator.nextInt(99);
		memorySize = 50;
		//normaliseThreshold = generator.nextDouble();
		normaliseThreshold = 1.0;
		//multiplicatorForGridSize = 10+generator.nextInt(40);
		multiplicatorForGridSize = 25;
		//multiplicatorForMaxStepsize = 1+generator.nextInt(49);
		multiplicatorForMaxStepsize = 15;
		if(this.multiplicatorForMaxStepsize >= multiplicatorForGridSize) {
			this.multiplicatorForMaxStepsize = multiplicatorForGridSize-1;
		}
		
		sa_iterations = 1000 * (1+generator.nextInt(10));
		sa_m = 100 * generator.nextDouble();
		sa_n = 10 * generator.nextDouble();
	}

//	public boolean equals(IParameters param2) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	public void initialiseToZero() {
		multiplicatorForIterations = 0;
		antType = ACCConfig.antType;
		kp = 0;
		kd = 0;
		noAnts = 0;
		alpha = 0;
		maxStepsize = 0;
		maxViewSize = 0;
		memorySize = 0;
		normaliseThreshold = 0;
		multiplicatorForGridSize = 0;
		multiplicatorForMaxStepsize = 0;
		sa_iterations = 0;
		sa_m = 0;
		sa_n = 0;
	}

	public void readParametersFromConfig() {		
		multiplicatorForIterations = ACCConfig.multiplicatorForIterations;
		antType = ACCConfig.antType;
		kp = ACCConfig.kp;
		kd = ACCConfig.kd;
		noAnts = ACCConfig.noAnts;
		alpha = ACCConfig.alpha;
		maxStepsize = ACCConfig.maxStepsize;
		maxViewSize = ACCConfig.maxViewSize;
		memorySize = ACCConfig.memorySize;
		normaliseThreshold = ACCConfig.normaliseThreshold;
		multiplicatorForGridSize = ACCConfig.multiplicatorForGridSize;
		multiplicatorForMaxStepsize = ACCConfig.multiplicatorForMaxStepsize;
		sa_iterations = ACCConfig.sa_iterations;
		sa_m = ACCConfig.sa_m;
		sa_n = ACCConfig.sa_n;
	}

	public void saveParametersToConfig() {
		ACCConfig.multiplicatorForIterations = this.multiplicatorForIterations;
		ACCConfig.antType = this.antType;
		ACCConfig.kp = this.kp;
		ACCConfig.kd = this.kd;
		ACCConfig.noAnts = this.noAnts;
		ACCConfig.alpha = this.alpha;
		ACCConfig.maxStepsize = this.maxStepsize;
		ACCConfig.maxViewSize = this.maxViewSize;
		ACCConfig.memorySize = this.memorySize;
		ACCConfig.normaliseThreshold = this.normaliseThreshold;
		ACCConfig.multiplicatorForGridSize = this.multiplicatorForGridSize;
		ACCConfig.multiplicatorForMaxStepsize = this.multiplicatorForMaxStepsize;
		ACCConfig.sa_iterations = sa_iterations;
		ACCConfig.sa_m = sa_m;
		ACCConfig.sa_n = sa_n;
		
	}
	
	/**
	 * @return score The clustering score for this set of parameters.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score The clustering score for this set of parameters.
	 */
	public void setScore(double score) {
		this.score = score;
	}

	public void combineParametersRandomlyAndGetNewRandom(
			IParameters[] configurations) {
		//System.out.println("Combine Parameters called.");
		Random generator = new Random();		
		int pos;

		int randomOrOld = generator.nextInt(2);				
		if(randomOrOld == 0){ // get an old parameter
			pos = generator.nextInt(configurations.length);
			this.multiplicatorForIterations = ((ACCParameters) configurations[pos]).getMultiplicatorForIterations();			
		} else { // get a new random parameter
			multiplicatorForIterations = 1000 * (5+generator.nextInt(11));
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
			this.noAnts = 1;
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
				this.maxStepsize = 1+generator.nextInt(49);
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0){ // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.maxViewSize = ((ACCParameters) configurations[pos]).getMaxViewSize();	
			} else { // get a new random parameter
				this.maxViewSize = 1+generator.nextInt(2);
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0){ // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.memorySize = ((ACCParameters) configurations[pos]).getMemorySize();	
			} else { // get a new random parameter
				//this.memorySize = 1+generator.nextInt(99);
				this.memorySize = 50;
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.normaliseThreshold = ((ACCParameters) configurations[pos]).getNormaliseThreshold();	
			} else { // get a new random parameter
				//this.normaliseThreshold = generator.nextDouble();
				this.normaliseThreshold = 1.0;
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.multiplicatorForGridSize = ((ACCParameters) configurations[pos]).getMultiplicatorForGridSize();	
			} else { // get a new random parameter
				//this.multiplicatorForGridSize = 10+generator.nextInt(40);
				this.multiplicatorForGridSize = 25;
			}
			
			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.multiplicatorForMaxStepsize = ((ACCParameters) configurations[pos]).getMultiplicatorForMaxStepsize();	
			} else { // get a new random parameter
				//this.multiplicatorForMaxStepsize = 1+generator.nextInt(49);
				this.multiplicatorForMaxStepsize = 15;
			}
			if(this.multiplicatorForMaxStepsize >= multiplicatorForGridSize) {
				this.multiplicatorForMaxStepsize = multiplicatorForGridSize-1;
			}

			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.sa_iterations = ((ACCParameters) configurations[pos]).getSa_iterations();	
			} else { // get a new random parameter
				this.	sa_iterations = 1000 * (1+generator.nextInt(20));
			}
			
			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.sa_m = ((ACCParameters) configurations[pos]).getSa_m();	
			} else { // get a new random parameter
				this.	sa_m = 100 * generator.nextDouble();
			}
			
			randomOrOld = generator.nextInt(2);		
			if(randomOrOld == 0) { // get an old parameter
				pos = generator.nextInt(configurations.length);
				this.sa_n = ((ACCParameters) configurations[pos]).getSa_n();	
			} else { // get a new random parameter
				this.	sa_n = 10 * generator.nextDouble();
			}
			
			
			this.antType = ACCConfig.antType;




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
		if(this.multiplicatorForMaxStepsize >= multiplicatorForGridSize) {
			this.multiplicatorForMaxStepsize = multiplicatorForGridSize-1;
		}
		return multiplicatorForMaxStepsize;
	}
	
	/**
	 * Sets the multiplicator for the maximal stepsize of an ant.
	 * @param multiplicatorForMaxStepsize
	 */
	public void setMultiplicatorForMaxStepsize(int multiplicatorForMaxStepsize) {
		this.multiplicatorForMaxStepsize = multiplicatorForMaxStepsize;
	}

	/**
	 * Creates the string representation of the parameters.
	 * @return Returns The String representation of the object.
	 */
	public String toString(){
		StringBuffer paramString = new StringBuffer();
		
		paramString.append("ACC paramter Configuration:");
		paramString.append("\n multiplicator for iterations - ");
		paramString.append(this.multiplicatorForIterations);
		paramString.append("\n kp - ");
		paramString.append(this.kp);
		paramString.append("\n kd - ");
		paramString.append(this.kd);
		paramString.append("\n noAnts - ");
		paramString.append(this.noAnts);
		paramString.append("\n antType - ");
		paramString.append(this.antType);
		paramString.append("\n alpha - ");
		paramString.append(this.alpha);
		paramString.append("\n maxStepSize - ");
		paramString.append(this.maxStepsize);
		paramString.append("\n maxViewSize - ");
		paramString.append(this.maxViewSize);
		paramString.append("\n memorySize - ");
		paramString.append(this.memorySize);
		paramString.append("\n normaliseThreshold - ");
		paramString.append(this.normaliseThreshold);
		paramString.append("\n multiplicatorForGridSize - ");
		paramString.append(this.multiplicatorForGridSize);
		paramString.append("\n multiplicatorForMaxStepsize - ");
		paramString.append(this.multiplicatorForMaxStepsize);
		
		paramString.append("\n sa_iterations: - ");
		paramString.append(this.sa_iterations);
		paramString.append("\n sa_m: - ");
		paramString.append(this.sa_m);
		paramString.append("\n sa_n: - ");
		paramString.append(this.sa_n);
		
		return paramString.toString();
	}

	public int getSa_iterations() {
		return sa_iterations;
	}

	public void setSa_iterations(int sa_iterations) {
		this.sa_iterations = sa_iterations;
	}

	public double getSa_m() {
		return sa_m;
	}

	public void setSa_m(double sa_m) {
		this.sa_m = sa_m;
	}

	public double getSa_n() {
		return sa_n;
	}

	public void setSa_n(double sa_n) {
		this.sa_n = sa_n;
	}
	
}
