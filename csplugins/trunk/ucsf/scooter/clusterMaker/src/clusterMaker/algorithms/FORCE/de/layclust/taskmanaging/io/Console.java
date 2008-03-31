/* 
 * Created on 28. January 2008
 * 
 */
package de.layclust.taskmanaging.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

// import org.apache.log4j.Logger;

import de.layclust.layout.LayoutFactory;
import de.layclust.layout.acc.ACCConfig;
import de.layclust.layout.acc.ACCLayouter;
import de.layclust.layout.forcend.FORCEnDLayoutConfig;
import de.layclust.layout.geometric_clustering.GeometricClusteringConfig;
import de.layclust.layout.geometric_clustering.GeometricClusteringFactory;
import de.layclust.layout.parameter_training.ParameterTrainingFactory;
import de.layclust.layout.postprocessing.PostProcessingFactory;
import de.layclust.taskmanaging.ClusteringManager;
import de.layclust.taskmanaging.InvalidInputFileException;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;
import de.layclust.taskmanaging.TaskUtility;

/**
 * Parses the input from the console and starts the appropriate mode with the
 * given input parameters.
 * 
 * @author sita
 * 
 */
public class Console {
	
//    private static org.apache.log4j.Logger log = Logger
//    	.getLogger(LogClass.class);

	private String cmPath = null;

//	private int mode = 0;

	private String[] args = null;

	public Console(String[] args) throws InvalidInputFileException,
			ArgsParseException {
		this.args = args;
		parseArgsAndInitProgram();
	}

	/**
	 * This method parses the input parameters from the console and starts the
	 * program with the correct parameters and in the correct mode. At this
	 * stage all input parameters are in this form: key value. Both key and
	 * value contain no spaces and if the value does, then it is bounded by
	 * apostrophes.
	 * 
	 * @throws InvalidInputFileException
	 * @throws ArgsParseException
	 */
	private void parseArgsAndInitProgram() throws InvalidInputFileException,
			ArgsParseException {

		System.out.println("-----------------------------------");
		System.out.println("Running ... " + TaskConfig.NAME + " v"
				+ TaskConfig.VERSION);
		Date date = new Date(System.currentTimeMillis());
		System.out.println(date.toString());
		System.out.println("-----------------------------------");

			
		findAndReadConfigIfGivenAndSetMode();
		
		initGivenParameters();


		/* start clustering */
		if (TaskConfig.mode == TaskConfig.CLUSTERING_MODE) {
			ClusteringManager clustermanage = new ClusteringManager(
					TaskConfig.cmPath);
			try {
				long time = System.currentTimeMillis();
				clustermanage.runClustering();
				time = System.currentTimeMillis() - time;
				System.out
						.println("Time taken for complete clustering process: "
								+ TaskUtility.convertTime(time));
			} catch (InvalidInputFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/* start general training */
		else if (TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE) {
			// TODO
		}
		/* no such mode */
		else {
			System.out.println("ERROR: This mode does not exist: "
					+ TaskConfig.mode);
		}

	}



	private void initGivenParameters() throws ArgsParseException {

		int i = 0;
		String key = null;
		String value = null;
		while (i < args.length) {
			key = args[i].trim();
			value = args[i + 1].trim();
			if (!key.startsWith("-")) {
				throw new ArgsParseException(
						"ERROR: This key does not begin with a '-', or the input is in the wrong format! Key="
								+ key);
			} else if (value.startsWith("-")) {
				throw new ArgsParseException(
						"ERROR: This value starts with a '-', which indicates that it is a key. Please look at your input again!");
			} else {
				setParameter(key, value);
			}
			i += 2;
		}

		// TODO Auto-generated method stub

	}

	/**
	 * This method tries to set the given parameter in the respective config. If
	 * this is not possible then an exception is thrown since this implies that
	 * either the given key is not valid, or the value is of an unfitting type.
	 * The mode is already set (or should be) when this method is called.
	 * 
	 * @param key
	 *            The key for the parameter.
	 * @param value
	 *            The value for the parameter.
	 * @throws ArgsParseException
	 * @throws ArgsParseException
	 *             If the given key does not exist, or if the given value has
	 *             the wrong type.
	 */
	private void setParameter(String key, String value)
			throws ArgsParseException {

		try {
			if (key.equals("-i")) {
					TaskConfig.cmPath = value;
			} else if (key.equals("-cf")) {
					TaskConfig.USECONFIGFILE = Boolean.parseBoolean(value);
			} else if (key.equals("-o")) {
				if(TaskConfig.mode == TaskConfig.CLUSTERING_MODE){
					TaskConfig.clustersPath = value;
				} else if (TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE){
					TaskConfig.outConfigPath = value;
				}
			} else if (key.equals("-verbose")) {
				TaskConfig.verbose = Boolean.parseBoolean(value);
			} else if (key.equals("-info")) {
				TaskConfig.info = Boolean.parseBoolean(value);
			} else if (key.equals("-l")) {
				TaskConfig.layouterClasses = value;
				TaskConfig.layouterIntTypes = LayoutFactory.getTypeArrayFromLayoutersString();
			} else if (key.equals("-g")) {
				TaskConfig.geometricClusteringClass = value;
				TaskConfig.geometricClusteringType = GeometricClusteringFactory.
					getClustererTypeByClass(value);
			} else if (key.equals("-p")) {	
				TaskConfig.postProcessingClass = value;
				TaskConfig.postProcessingType = PostProcessingFactory.getPostProcessorTypeByClass(value);
			} else if (key.equals("-e")) {
				TaskConfig.ccEdgesClass = value;
				TaskConfig.ccEdgesType = LayoutFactory.getCCEdgesTypeByClass(value);
			} else if (key.equals("-t")) {
				TaskConfig.useThreadsForCCs = Boolean.parseBoolean(value);
			} else if (key.equals("-ld")) {
				int dim = Integer.parseInt(value);
				if(dim<2){
					throw new ArgsParseException("The dimension given is too small: "+value);
				}
				if(dim>3){
					System.out.println("If using the ACCLayouter, then any dimension greater than 3 is very time expensive. Recommended are dimensions 2 or 3.");
					// log.warn("If using the ACCLayouter, then any dimension greater than 3 is very time expensive. Recommended are dimensions 2 or 3.");
				}
				TaskConfig.dimension = dim;
			} else if (key.equals("-lp")) {
				TaskConfig.parameterTrainingClass = value;
				TaskConfig.doLayoutParameterTraining = true;
				ParameterTrainingFactory.getParameterTrainingTypeByClass(value);
			} else if (key.equals("-lps")) {
				int no = Integer.parseInt(value);
				if(no<2){
					throw new ArgsParseException("The number of parameter configurations per generation need to be at least two! Number given="+value);
				}
				TaskConfig.noOfParameterConfigurationsPerGeneration = no;
			} else if (key.equals("-lpn")) {
				int no = Integer.parseInt(value);
				if(no<1){
					throw new ArgsParseException("The number of generations for the layout parameter training is too small! Number given="+value);
				}
				TaskConfig.noOfParameterConfigurationsPerGeneration = no;
			} else if (key.equals("-fa")) {
				FORCEnDLayoutConfig.attractionFactor = Double.parseDouble(value);
			} else if (key.equals("-fr")) {
				FORCEnDLayoutConfig.repulsionFactor = Double.parseDouble(value);
			} else if (key.equals("-fd")) {
				FORCEnDLayoutConfig.maximalDisplacement = Double.parseDouble(value);
			} else if (key.equals("-fi")) {
				FORCEnDLayoutConfig.iterations = Integer.parseInt(value);
			} else if (key.equals("-ft")) {
				FORCEnDLayoutConfig.temperature = Float.parseFloat(value);
			} else if (key.equals("-fg")) {
				FORCEnDLayoutConfig.influenceOfGraphSizeToForces = Double.parseDouble(value);				
			} else if (key.equals("-aix")) {
				ACCConfig.multiplicatorForIterations = Integer.parseInt(value);
			} else if (key.equals("-agx")) {
				ACCConfig.multiplicatorForGridSize = Integer.parseInt(value);
			} else if (key.equals("-asx")) {
				ACCConfig.multiplicatorForMaxStepsize = Integer.parseInt(value);
			} else if (key.equals("-at")) {
				ACCConfig.antType = value;
			} else if (key.equals("-akp")) {
				ACCConfig.kp = Double.parseDouble(value);
			} else if (key.equals("-akd")) {
				ACCConfig.kd = Double.parseDouble(value);
			} else if (key.equals("-an")) {
				ACCConfig.noAnts = Integer.parseInt(value);
			} else if (key.equals("-aa")) {
				ACCConfig.alpha = Double.parseDouble(value);
			} else if (key.equals("-as")) {
				ACCConfig.maxStepsize = Integer.parseInt(value);
			} else if (key.equals("-av")) {
				ACCConfig.maxViewSize = Integer.parseInt(value);
			} else if (key.equals("-az")) {
				ACCConfig.normaliseThreshold = Double.parseDouble(value);
			} else if (key.equals("-sm")) {
				GeometricClusteringConfig.minDistance = Double.parseDouble(value);
			} else if (key.equals("-sx")) {
				GeometricClusteringConfig.maxDistance = Double.parseDouble(value);
			} else if (key.equals("-ss")) {
				GeometricClusteringConfig.stepsize = Double.parseDouble(value);
			} else if (key.equals("-sf")) {
				GeometricClusteringConfig.stepsizeFactor = Double.parseDouble(value);
			} else if (key.equals("-km")) {
				GeometricClusteringConfig.kLimit = Integer.parseInt(value);
			} else if (key.equals("-ki")) {
				GeometricClusteringConfig.maxInitStartConfigs = Integer.parseInt(value);
				
				//********************************************
				//  TODO: Add extra input variables here !!
				//********************************************
				
			} else {
				throw new ArgsParseException("This key does not exist: " + key);
			}

		} catch (Exception e) {
			throw new ArgsParseException("The value is of the wrong type: "
					+ value);
		}

	}

	/**
	 * Looks through the input variables to see if a config file is defined. If
	 * so all parameters are read from this config file. If some do not exist, a
	 * warning is given, but the program continues. It may be the case that
	 * these parameters are unwanted or belong to an unused implementation.
	 * It also looks if a mode was given and sets this, otherwise the default is used.
	 * 
	 * @throws InvalidInputFileException
	 *             If the given config class does not end in .conf.
	 */
	private void findAndReadConfigIfGivenAndSetMode() throws InvalidInputFileException, ArgsParseException {

		String configPath = TaskConfig.DEFAULTCONFIG;
		for (int i = 0; i < args.length; i++) {

			/* check for config parameter */
			if (args[i].trim().equals("-config")) {
				String value = args[i + 1].trim();
				if (value.endsWith(".conf")) {
					configPath = value;
				} else {
					throw new InvalidInputFileException(
							"An invalid config file was entered. The file must end with '.conf'. Please try again! Given file="
									+ value);
				}
			}
			/* check for if -cf parameter is set */
			if (args[i].trim().equals("-cf")) {
				
				TaskConfig.USECONFIGFILE = Boolean.parseBoolean(args[i + 1].trim());
				
			}
			/* check for mode parameter */
			if(args[i].trim().equals("-mode")){
				String value = args[i + 1].trim();
				try{
					int md = Integer.parseInt(value);
					if(md == TaskConfig.GENERAL_TRAINING_MODE){
						TaskConfig.mode = TaskConfig.GENERAL_TRAINING_MODE;
					}
					else if(md == TaskConfig.CLUSTERING_MODE){
						TaskConfig.mode = TaskConfig.CLUSTERING_MODE;
					}
					else {
						throw new ArgsParseException("The given mode is incorrect - it does not exist! "+md);
					}
				}catch(Exception e){
					throw new ArgsParseException("The given mode is not an interger value: "+value);
				}
				
			}
			++i;
		}

		/*
		 * read given config file - if it doesn't contain some resources,
		 * default values are taken
		 */
		
		if(TaskConfig.USECONFIGFILE){
			
			try {
				FileInputStream s = new FileInputStream(configPath);
				PropertyResourceBundle configrb = new PropertyResourceBundle(s);

				System.out.println("INFO: Using config file " + configPath);

				TaskConfig.initFromConfigFile(configrb);
				ACCConfig.initFromConfigFile(configrb);
				FORCEnDLayoutConfig.initFromConfigFile(configrb);
				GeometricClusteringConfig.initSLCFromConfigFile(configrb);
				GeometricClusteringConfig.initKmeansFromConfigFile(configrb);

			} catch (MissingResourceException ex) {
				System.err
						.println("WARNING: Resources are missing in the given config file: "
								+ TaskConfig.DEFAULTCONFIG
								+ ", key="
								+ ex.getKey()
								+ ". Either you have defined these parameters in the input, or the default values are used from the "
								+ TaskConfig.DEFAULTCONFIG
								+ ". Or these parameters do not interest you, because they belong to an unused implemtation.");
			} catch (IOException ex) {
				System.out
						.println("ERROR: Unable to read the given config file: "
								+ configPath);
				System.exit(-1);
			} catch (InvalidTypeException ex) {
				System.out
						.println("ERROR: You have perhaps given an incorrect class name of an implemtation. Please note that this is case sensitive.");
				ex.printStackTrace();
				System.exit(-1);
			}
			
			
		}

		
		
	}

}
