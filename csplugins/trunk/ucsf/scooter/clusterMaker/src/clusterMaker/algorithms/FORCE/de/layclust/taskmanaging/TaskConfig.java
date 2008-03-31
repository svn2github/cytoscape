package de.layclust.taskmanaging;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import de.layclust.layout.LayoutFactory;
import de.layclust.layout.geometric_clustering.GeometricClusteringFactory;
import de.layclust.layout.parameter_training.ParameterTrainingFactory;
import de.layclust.layout.postprocessing.PostProcessingFactory;

public class TaskConfig {
	
	// --------------------- FIXED VARIABLES ---------------------- //	
	/* program details */
	public final static String NAME = "FORCEnD_ACC";	
	public final static String VERSION = "1.0";
	public final static String[] AUTHORS = {"Sita Lange: sita.lange@cebitec.uni-bielefeld.de",
		"Nils Kleinboelting: nils.kleinboelting@cebitec.uni-bielefeld.de",
		"Tobias Wittkop: tobias.wittkop@cebitec.uni-bielefeld.de",
		"and Jan Baumbach: jan.baumbach@cebitec.uni-bielefeld.de"};	
	
	public final static String NL = System.getProperty("line.separator"); //newline
	public final static String FS = System.getProperty("file.separator"); //slash
	
	public final static String DEFAULTCONFIG = "Default.conf";
	public final static int CLUSTERING_MODE = 0;
	public final static int GENERAL_TRAINING_MODE = 1;
	
	public static boolean USECONFIGFILE = true;
	

	// ------------------------------------------------------------------- //
	
	
	//	 ----------------------- INTERNAL VARIABLES ----------------------- //
	
	public static int ccEdgesType = LayoutFactory.CC2DARRAY;
	public static int[] layouterIntTypes;
	public static int parameterTrainingType;
	public static int geometricClusteringType;
	public static int postProcessingType;
	
	//	 --------------------------------------------------------------------------- //

	
	// --------------------- INPUT/CONFIG VARIABLES ---------------------- //	
	
	/* ---- file paths ---- */
	public static String outConfigPath;
//	public static String configPath = "E:\\Extras\\Eclipse_Workplace\\FORCEnD\\FORCEnD_ACC\\FORCE_nd_acc.conf"; //home
	public static String clustersPath = "clusters.cls"; //TODO take out!
	public static String transitiveConnectedComponents;
	public static String cmPath;
	public static String infoPath = "test.info";
	
	
	/* ---- general ---- */
	public static boolean useThreadsForCCs= false;
	public static String ccEdgesClass = "CC2DArray";
	public static boolean verbose = false;
	public static int mode = CLUSTERING_MODE;
	public static boolean info = false; //default is that no info file is created
	
	
	/* ---- layouting ----*/
	public static String layouterClasses = "FORCEnDLayouter"; //use correct class names
	public static int dimension = 2;
	public static int cost_minimum= -100; //TODO take out?
	
	
	/* ---- parameter training for the layouters ---- */
	public static String parameterTrainingClass = "ParameterTraining_SE";
	public static boolean doLayoutParameterTraining = false;
	public static int noOfParameterConfigurationsPerGeneration = 15; //minimum = 2!!
	public static int noOfGenerations = 3; //min number of generations = 1;
	public static boolean useThreadsForParameterTraining = false;
	
	
	/* ---- geometric clustering ---- */
	public static String geometricClusteringClass = "SingleLinkageClusterer";
	
	/* ---- post-processing ---- */
	public static boolean doPostProcessing = true;
	public static String postProcessingClass = "PP_DivideAndReclusterRecursively";
	
	
//	/* ---- logging ---- */
//	public static boolean log = false;
////	public static String logPath = "de"+FS+"layclust"+FS+"data"+FS+"defaultLog.log";
////	public static String logPath = "FORCEnD.log";
//	public static boolean flushLogFileImmediately = true; 
	

	// ------------------------------------------------------------------- //

	
	// ----------------------------- OTHER ----------------------------- //
	
	/* ---- add clustering score for one cc to a total score ---- */
	public static double totalScoreSum = 0;
	/**
	 * This method adds one clustering score to the total clustering score for
	 * the whole directory. Since in can be accessed from mutiple threads,
	 * it is declared synchronized.
	 * @param score The score to be added to the total score.
	 */
	public static synchronized void addClusteringScoreToSum(double score){
		totalScoreSum += score;
	}
	
	
	/**
	 * This methods loads every necessary parameters from the given ConfigFile
	 * 
	 * @param ConfigFileName
	 *            Location of the ConfigFile
	 */
	public static void initFromConfigFile(PropertyResourceBundle rb)
			throws MissingResourceException, InvalidTypeException {
		
		useThreadsForCCs = Boolean.parseBoolean(rb.getString(
				"general.useThreadsForCCs").trim());
		
		ccEdgesClass = rb.getString("general.ccEdgesDataStructure").trim();
		
		ccEdgesType = LayoutFactory.getCCEdgesTypeByClass(ccEdgesClass);
		
		verbose = Boolean.parseBoolean(rb.getString("general.verbose"));
		
		dimension = Integer.parseInt(rb.getString("layout.dimension").trim());
		
		layouterClasses = (rb.getString("general.layouters").trim());
		
		geometricClusteringClass = rb.getString("general.geometricClusterer").trim();
		
		geometricClusteringType = GeometricClusteringFactory.
				getClustererTypeByClass(geometricClusteringClass);
		
		postProcessingClass = rb.getString("general.postProcessor").trim();
		
		postProcessingType = PostProcessingFactory.getPostProcessorTypeByClass(
				postProcessingClass); 
		
		parameterTrainingClass = rb.getString("layout.parameterTraining").trim();
		
		parameterTrainingType = ParameterTrainingFactory.getParameterTrainingTypeByClass(
				parameterTrainingClass);
		
		doLayoutParameterTraining = Boolean.parseBoolean(rb.getString(
				"layout.doParameterTraining").trim());
		
		noOfParameterConfigurationsPerGeneration = Integer.parseInt(rb.getString(
				"layout.generationSize").trim());
		
		noOfGenerations = Integer.parseInt(rb.getString("layout.noOfGenerations").trim());
		
		useThreadsForParameterTraining = Boolean.parseBoolean(rb.getString(
				"layout.useThreadsForParameterTraining").trim());

		doPostProcessing = Boolean.parseBoolean(rb.getString(
				"general.doPostProcessing").trim());
		
		postProcessingClass = rb.getString("general.postProcessor").trim();
		
		postProcessingType = PostProcessingFactory.getPostProcessorTypeByClass(
				postProcessingClass);

		//TODO check whether all parameters are read - logging
	}
	
	//	 ------------------------------------------------------------------- //

}