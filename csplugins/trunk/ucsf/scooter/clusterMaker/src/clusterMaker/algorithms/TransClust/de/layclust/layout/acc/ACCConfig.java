package de.layclust.layout.acc;


import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import de.layclust.taskmanaging.io.ConfigFile;

public class ACCConfig {
		
	public static int multiplicatorForIterations = 10000;
	//type of the ants to be used, use the class-name as string
	public static String antType = "MemoryAnt";
	//constant for pickup-probability
	public static double kp = 0.15;
	//constant for drop-probability
	public static double kd = 0.2;
	//number of ants
	public static int noAnts = 1;
	//alpha is used in the computation of the neighbourhood function to scale 
	//dissimilarities, range between 0 and 1
	public static double alpha = 1.0;
	//for "JumpingAnts" and extending classes: the maximum range 
	//an ant can jump with a step in one dimension of the grid
	public static int maxStepsize = 20;
	//for "JumpingAntsWithIncreasingViewField" and extending classes:
	//the maximum view size of an ant, it increases linear over time
	public static int maxViewSize = 2;
	//for "MemoryAnts" and extending classes:
	//the size of the ants memory
	public static int memorySize = 50;
	public static double normaliseThreshold = 1.0;
	public static int multiplicatorForGridSize = 25;
	public static int multiplicatorForMaxStepsize = 15;
	
	
	public static int sa_iterations = 10000;
	public static double sa_m = 90;
	public static double  sa_n = 6;
	public static double sa_wakeUpModifier = 0;
	
	/**
	 * This methods loads every necessary parameters from the given ConfigFile for
	 * ACC
	 * 
	 * @param rb
	 *            The PropertyResourceBundle object for the config file.
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


	/**
	 * Print the config parameters to the given ConfigFile.
	 * @param confile The ConfigFile to write to.
	 */
	public static void printParametersToConfig(ConfigFile confile) {
		
		confile.printSubHeader("ACC");
		confile.printParameter("acc.multiplicatorForIterations", ""+ACCConfig.multiplicatorForIterations);
		confile.printParameter("acc.antType", ACCConfig.antType);
		confile.printParameter("acc.kp", ""+ACCConfig.kp);
		confile.printParameter("acc.kd", ""+ACCConfig.kd);
		confile.printParameter("acc.noAnts", ""+ACCConfig.noAnts);
		confile.printParameter("acc.alpha", ""+ACCConfig.alpha);
		confile.printParameter("acc.maxStepsize", ""+ACCConfig.maxStepsize);
		confile.printParameter("acc.maxViewSize", ""+ACCConfig.maxViewSize);
		confile.printParameter("acc.memorySize", ""+ACCConfig.memorySize);
		confile.printParameter("acc.normaliseThreshold",""+ACCConfig.normaliseThreshold);
		confile.printParameter("acc.multiplicatorForGridSize", ""+ACCConfig.multiplicatorForGridSize);
		confile.printParameter("acc.multiplicatorForMaxStepsize", ""+ACCConfig.multiplicatorForMaxStepsize);
		confile.printnewln();
		confile.printnewln();
		confile.printnewln();
		
	}
	
}
