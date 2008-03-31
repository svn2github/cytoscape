/* 
* Created on 11. December 2007
 * 
 */
package de.layclust.layout.parameter_training;

import de.layclust.taskmanaging.InvalidTypeException;

/**
 *  This class contains static methods that create the correct objects for
 * the different parameter training algorithms. When a new algorithm that implements
 * {@link IParameterTraining} is created by a developer, these methods need to be updated!
 * 
 * @author Sita Lange
 *
 */
public class ParameterTrainingFactory {
	
	public final static String ALLPARAMETERTRAINERS = "ParameterTraining_SE";
	
	/* simple evolutionary parameter training */
	public final static int PARAMETERTRAINING_SE = 0;
	public final static String PARAMETERTRAINING_SE_CLASSNAME = "ParameterTraining_SE";
	
	// ===============================//
	// ADD ADDITIONAL POST PROCESSORS HERE!!             //
	// ===============================//
	
	/**
	 * Here the correct implementation of the {@link IParameterTraining}
	 * interface is returned according to the given type.
	 * @param type The type of parameter trainer to create.
	 */
	public static IParameterTraining getParameterTrainerByType(int type)
		throws InvalidTypeException{
		
		if(type==PARAMETERTRAINING_SE){
			return new ParameterTraining_SE();	
		}
		
		// ===============================//
		// ADD ADDITIONAL POST PROCESSORS HERE!!             //
		// ===============================//
		
		else {
			throw new InvalidTypeException("ParameterTrainingFactory: This parameter training type " +
					"does not exist: "+type);
		}
	}
	
	/**
	 * This method takes the given class name of a post processing implementation and
	 * returns the internal int value for this class.
	 * @param className Class name of the {@link IParameterTraining} implementation.
	 * @return The int value for the implementation.
	 * @throws InvalidTypeException If the given class has not been implemented or has not been bound into the program correctly.
	 */
	public static int getParameterTrainingTypeByClass(String className) throws InvalidTypeException{
		if(className.equals(PARAMETERTRAINING_SE_CLASSNAME)){
			return PARAMETERTRAINING_SE;
		}

		// ===============================//
		// ADD ADDITIONAL POST PROCESSORS HERE!!             //
		// ===============================//
		
		else {
			throw new InvalidTypeException("ParameterTrainingFactory: This parameter training class " +
					"has not been implemented or has not been bound in the program correctly: "+className);
		}
	}
}