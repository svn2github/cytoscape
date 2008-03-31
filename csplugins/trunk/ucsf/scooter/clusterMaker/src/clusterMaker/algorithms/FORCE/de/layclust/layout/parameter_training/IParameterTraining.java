/**
 * 
 */
package de.layclust.layout.parameter_training;

import de.layclust.layout.data.ConnectedComponent;

/**
 * @author sita
 *
 */
public interface IParameterTraining {
	
	
	public void initialise(int type, int generationsSize, 
			int noOfGenerations);
	
	public IParameters run(ConnectedComponent cc);
	
	
	
//	public IParameters[] createRandomParameters(int n);
	
//	public IParameters[] getBestParameters(int n);
//	
//	public ArrayList<IParameters> getGenerationParameters();
//	
//	public void setGenerationParameters(ArrayList<IParameters> generationParam);
	


}
