/* 
* Created on 4. October 2007
 * 
 */
package de.layclust.layout.forcend;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

/**
 * This class contains the input static variables that apply only to the force layouting phase.
 * Also includes methods for reading and writing the parameters from a config file. 
 * 
 * @author sita
 */
public class FORCEnDLayoutConfig {
	
	//	 --------------------- FIXED VARIABLES ---------------------- //
	
	/* set nodes on same position to a minimal distance, so it is not zero */
	public static final double MIN_DISTANCE = 0.001;
	/* the minimal distance a node can move in one iteration, saves unnecessary steps */
	// TODO check which value is suitable! In which range do the values move in?
	public static final double MIN_MOVEMENT = 1e-7;
	
	//	 ------------------------------------------------------------------- //
	
	// -------------------- CONFIG VARIABLES --------------------- //
	public static double attractionFactor = 1.2448524402942829;
	public static double repulsionFactor = 1.6866447301914302;
	public static double maximalDisplacement = 1000;
	public static int iterations = 150;
	public static float temperature = 633;
	public static double influenceOfGraphSizeToForces = 1.3198015648987826 ;
	// ------------------------------------------------------------------- //
	
	
	/**
	 * This methods loads every necessary parameters from the given ConfigFile for
	 * FORCEnD
	 * 
	 * @param ConfigFileName
	 *            Location of the ConfigFile
	 */
	public static void initFromConfigFile(PropertyResourceBundle rb)
			throws MissingResourceException {

		attractionFactor = Double.parseDouble(rb.getString("forcend.attractionFactor").
				trim());
		repulsionFactor = Double.parseDouble(rb.getString("forcend.repulsionFactor").
				trim());
		maximalDisplacement = Double.parseDouble(rb.getString(
				"forcend.maximalDisplacement").trim());
		iterations = Integer.parseInt(rb.getString("forcend.iterations").trim());
		temperature = Float.parseFloat(rb.getString("forcend.temperature").trim());
		influenceOfGraphSizeToForces = Double.parseDouble(rb.getString(
				"forcend.influenceOfGraphSizeToForces").trim());
	}
	
}
