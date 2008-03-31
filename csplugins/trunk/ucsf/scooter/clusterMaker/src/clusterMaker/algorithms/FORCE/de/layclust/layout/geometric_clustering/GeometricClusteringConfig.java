package de.layclust.layout.geometric_clustering;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import de.layclust.taskmanaging.TaskConfig;

public class GeometricClusteringConfig {

	// ---------------- CONFIG VARIABLES - SINGLE-LINKAGING ---------------- //

	public static double minDistance = 0.01;
	public static double maxDistance = 5;
	public static double stepsize = 0.01;
	public static double stepsizeFactor = 0.01;

	// --------------------------------------------------------------------------------------

	// ----------------------- CONFIG VARIABLES - K-MEANS ----------------------

	public static int kLimit = 30;
	public static int maxInitStartConfigs = 10;

	// --------------------------------------------------------------------------------------

	/**
	 * This method loads every necessary parameters for single linkage clustering
	 * from the given ConfigFile
	 * 
	 * @param ConfigFileName
	 *            Location of the ConfigFile
	 */
	public static void initSLCFromConfigFile(PropertyResourceBundle rb)
			throws MissingResourceException {

		minDistance = Double.parseDouble(rb.getString("slc.minDistance").trim());
		maxDistance = Double.parseDouble(rb.getString("slc.maxDistance").trim());
		stepsize = Double.parseDouble(rb.getString("slc.stepsize").trim());
		stepsizeFactor = Double.parseDouble(rb.getString("slc.stepsizeFactor").trim());

	}
	
	/**
	 * This method
	 * @param rb
	 * @throws MissingResourceException
	 */
	public static void initKmeansFromConfigFile(PropertyResourceBundle rb) 
			throws MissingResourceException{
		
		kLimit = Integer.parseInt(rb.getString("km.maxK").trim());
		maxInitStartConfigs = Integer.parseInt(rb.getString("km.maxInitStartConfigs"));
		
	}
	
}
