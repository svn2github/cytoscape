package de.layclust.layout.acc;


import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import de.layclust.taskmanaging.TaskConfig;

public class ACCConfig {
	//number of ants per item:
	public static final double ANT_FACTOR = 0.7;
	//multiplicator for size:
	public static final int MULTIPLICATOR_FOR_SIZE = 15;
	//scale for dissimilarities in neighbourhood-function:
	public static final float ALPHA =(float) 0.1;
	
	//the no of iterations to be performed
	public static int noOfIterations = 100000; //TODO nils will dies rausnehmen
	
	
	public static int multiplicatorForIterations = 10000;
	//type of the ants to be used, use the class-name as string
	public static String antType = "MemoryAnt";
	//constant for pickup-probability
	public static double kp = 0.15;
	//constant for drop-probability
	public static double kd = 0.2;
	//number of ants
	public static int noAnts = 1;
	//the dimension of the grid
	public static int dimension = TaskConfig.dimension;
	//alpha is used in the computation of the neighbourhood function to scale 
	//dissimilarities, range between 0 and 1
	public static double alpha = 1.0;
	//for "JumpingAnts" and extending classes: the maximum range 
	//an ant can jump with a step in one dimension of the grid
	public static int maxStepsize = 20;
	//for "JumpingAntsWithIncreasingViewField" and extending classes:
	//the maximum view size of an ant, it increases linear over time
	public static int maxViewSize = 1;
	//for "MemoryAnts" and extending classes:
	//the size of the ants memory
	public static int memorySize = 20;
	public static double normaliseThreshold = 1.0;
	public static int multiplicatorForGridSize = 25;
	public static int multiplicatorForMaxStepsize = 15;
	
	
	/**
	 * This methods loads every necessary parameters from the given ConfigFile for
	 * ACC
	 * 
	 * @param ConfigFileName
	 *            Location of the ConfigFile
	 */
	public static void initFromConfigFile(PropertyResourceBundle rb)
			throws MissingResourceException{

		multiplicatorForIterations = Integer.parseInt(rb.getString("acc.multiplicatorForIterations"));
		antType = rb.getString("acc.antType").trim();
		kp = Double.parseDouble(rb.getString("acc.kp").trim());
		kd = Double.parseDouble(rb.getString("acc.kd").trim());
		noAnts = Integer.parseInt(rb.getString("acc.noAnts").trim());
		alpha = Double.parseDouble(rb.getString("acc.alpha").trim());
		maxStepsize = Integer.parseInt(rb.getString("acc.maxStepsize").trim());
		maxViewSize = Integer.parseInt(rb.getString("acc.maxViewSize").trim());
		memorySize = Integer.parseInt(rb.getString("acc.memorySize").trim());
		normaliseThreshold = Double.parseDouble(rb.getString("acc.normaliseThreshold").trim());
		multiplicatorForGridSize = Integer.parseInt(rb.getString("acc.multiplicatorForGridSize").trim());
		multiplicatorForMaxStepsize = Integer.parseInt(rb.getString("acc.multiplicatorForMaxStepsize").trim());

	}
	
}
