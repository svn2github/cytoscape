
package de.layclust.layout.parameter_training;


public interface IParameters{
	
	/**
	 * Creates a parameters object from the config class.
	 * @return An implementation of the IParameters 
	 * 			interface with the parameters from the config class.
	 */
	public void readParametersFromConfig();
	
	/**
	 * Changes the parameters in the config class to the ones in this instance.
	 *
	 */
	public void saveParametersToConfig();

	public void createRandomConfiguration();
	
	public void initialiseToZero();
	
//	public void combineConfigurationsMean(IParameters[] configurations) ;
	
	public void combineConfigurationsRandomly(IParameters[] configurations);
	
	public void combineParametersRandomlyAndGetNewRandom(IParameters[] configurations);

//	public boolean equals(IParameters param2);
	
//	public IParameters copy(int number);
	
	public String toString();
	
	public double getScore();
	
	public void setScore(double score);
	


}
