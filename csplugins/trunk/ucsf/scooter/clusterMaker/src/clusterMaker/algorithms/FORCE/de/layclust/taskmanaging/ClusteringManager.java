package de.layclust.taskmanaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.concurrent.Semaphore;

import de.layclust.layout.LayoutFactory;
import de.layclust.layout.acc.ACCConfig;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.data.CostMatrixReader;
import de.layclust.layout.forcend.FORCEnDLayoutConfig;
import de.layclust.layout.geometric_clustering.GeometricClusteringConfig;
import de.layclust.layout.parameter_training.IParameterTraining;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.layout.parameter_training.ParameterTrainingFactory;
import de.layclust.taskmanaging.io.ClusterFile;


public class ClusteringManager {
	
//	private static Logger log = Logger.getAnonymousLogger("de.layclust.taskmanaging.ClusterManager");
	private String cmPath;
	
	public ClusteringManager(String cmPath){
		this.cmPath = cmPath;
	}
	
	public void runClustering() throws InvalidInputFileException, InvalidTypeException {
		
		/* read the input file or directory */
		File cmFile = new File(this.cmPath);
		if(cmFile.isDirectory()){
			
			/* get all files in the directory */
			File[] files = cmFile.listFiles();
			
			ClusterFile clusterFile = new ClusterFile();
			clusterFile.instantiateFile(TaskConfig.clustersPath);
			if(TaskConfig.transitiveConnectedComponents == null){
				for (int i = 0; i < files.length; i++) {
					String filePath = files[i].toString();
					if(filePath.endsWith(".tcc") || filePath.endsWith(".rtcc")){
						TaskConfig.transitiveConnectedComponents = filePath;
					}
				}
				if(TaskConfig.transitiveConnectedComponents == null){
//					throw new InvalidInputFileException("No transitive connected component file (.tcc or .rtcc) has been given for this directory!");
				}
			}
			clusterFile.printPreProcessingClusters(TaskConfig.transitiveConnectedComponents);

			/* check boolean whether cm files exist in directory */
			boolean noCostMatrices = true; 
			
			/* iterate over files and start clustering process for each cm file */	
			ArrayList<Semaphore> semaphoreCollection = new ArrayList<Semaphore>(files.length);
			for(int i=0;i<files.length;i++){
				if(files[i].toString().endsWith(".cm") || files[i].toString().endsWith(".rcm")){
					noCostMatrices = false; //cm files exist	
					Semaphore semaphore = new Semaphore(1);
					semaphoreCollection.add(semaphore);
					runClusteringForOneConnectedComponent(files[i], clusterFile, semaphore);
				}
			}
		 
			if(noCostMatrices){
				throw new InvalidInputFileException("There are no cost matrix " +
						"files in the input directory, " +
						"or check whether the file extension equals .cm or .rcm");
			}
			
			/* wait for all clustering tasks to finish */
			if(TaskConfig.useThreadsForCCs){
				for (Semaphore s : semaphoreCollection) {
					try {
						s.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			clusterFile.closeFile();
			
		} else{
			
			/* TODO generate clusters file and pass onto clustering task for
			 * the results.
			 */

			ClusterFile clusterFile = new ClusterFile();
			clusterFile.instantiateFile(TaskConfig.clustersPath);
			
			TaskConfig.useThreadsForCCs = false; //no threads for only one cm!
			
			 /* only one cost matrix as input - start clustering process */
			if(cmFile.toString().endsWith(".cm")){
				runClusteringForOneConnectedComponent(cmFile, clusterFile, null);
			} else {
				throw new InvalidInputFileException("The input cost matrix is of " +
						"wrong file type. The file extension should be \".cm\" or \".rcm\"");						
			}
			clusterFile.closeFile();
		}
		
		
		/* END OF CLUSTERING */	 
		if(TaskConfig.verbose){
			System.out.println("Clustering scores sum: "+TaskConfig.totalScoreSum);
		}
	}
	
	private void runClusteringForOneConnectedComponent(File cmFile, 
			ClusterFile clusterFile, Semaphore semaphore) 
		throws InvalidTypeException {
		
		// create the connected component (cc) object
		CostMatrixReader cmReader = new CostMatrixReader(cmFile);
//		System.out.println(cmFile);
		ConnectedComponent cc = cmReader.getConnectedComponent();
		
		/* create parameter array for the layouters - either
		 * with or without parameter training */		
		int[] layouterIntTypes = LayoutFactory.getTypeArrayFromLayoutersString();
		IParameters[] layouterParams = new IParameters[layouterIntTypes.length];
		for(int i=0;i<layouterIntTypes.length;i++){			
			
			if(TaskConfig.doLayoutParameterTraining){								
				/* start parameter training for the cc */				
				IParameterTraining paramTrain = ParameterTrainingFactory.
					getParameterTrainerByType(TaskConfig.parameterTrainingType);
				paramTrain.initialise(layouterIntTypes[i], 
						TaskConfig.noOfParameterConfigurationsPerGeneration,
						TaskConfig.noOfGenerations);
				IParameters bestparam = paramTrain.run(cc);
//				System.out.println("best param configuration: "+bestparam.toString());
				layouterParams[i] = bestparam;
			
			} else { /* get parameters from config */				
				IParameters param = LayoutFactory.getParametersByType(layouterIntTypes[i]);
				param.readParametersFromConfig();
				layouterParams[i] = param;
			}			
		}
		
		/* run clustering with the previously determined parameters */
		ClusteringTask clusterTask = new ClusteringTask(cc, layouterParams,
				layouterIntTypes, clusterFile);
		if(TaskConfig.useThreadsForCCs){
			clusterTask.setSemaphore(semaphore);
			(new Thread(clusterTask)).start();
			
		}else{
			clusterTask.run(); 
		}
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		System.out.println("User directory: "+System.getProperty("user.dir"));
		
		/* load default config, so that all configurations have a default value
		 * these can be changed by a given config file or as input parameters.
		 */
		try{
			FileInputStream s = new FileInputStream(TaskConfig.DEFAULTCONFIG);
			PropertyResourceBundle configrb = new PropertyResourceBundle(s);
			
			TaskConfig.initFromConfigFile(configrb);
			ACCConfig.initFromConfigFile(configrb);
			FORCEnDLayoutConfig.initFromConfigFile(configrb);
			GeometricClusteringConfig.initSLCFromConfigFile(configrb);
			GeometricClusteringConfig.initKmeansFromConfigFile(configrb);
			
		} catch(MissingResourceException ex){
			System.err.println("ERROR: Resources are missing in the default config file: "+
					TaskConfig.DEFAULTCONFIG+", key="+ ex.getKey()+".");
			
			ex.printStackTrace();
			System.exit(-1);
		} catch(IOException ex){
			System.out.println("ERROR: Unable to read the default config file: "+
					TaskConfig.DEFAULTCONFIG);
			ex.printStackTrace();
			System.exit(-1);
		} catch(InvalidTypeException ex){
			ex.printStackTrace();
			System.exit(-1);
		}
		
		
		ClusteringManager clustermanage = new ClusteringManager(TaskConfig.cmPath);
		try {
			long time = System.currentTimeMillis();
			clustermanage.runClustering();
			time = System.currentTimeMillis() - time;
			System.out.println("Time taken for complete clustering process: "+TaskUtility.convertTime(time));
		} catch (InvalidInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
		
		
//		clustermanage.runClustering2();
		
//		/* create logging file */
//		Log.createFiles(TaskConfig.logPath);
//		Log.println(System.getProperty("user.dir"));
		


		
//		File clustersFile = 
//			new File("/homes/sita/Eclipse/Workspace/FORCEnD_ACC/de/layclust/data/cluster/testclusterfile.cls");
//		
//		try {
//			
//		    Handler fh = new FileHandler("de/layclust/data/logger.log");
//		    Logger.getLogger("").addHandler(fh);
//		    Logger.getLogger("").setLevel(Level.FINEST);
//		    
//
//			
//			/* append information to file */
//			BufferedWriter bw = new BufferedWriter(new FileWriter(clustersFile));
//
//			bw.write("this is a clusters file\n");
//	        bw.close();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		

		
		
//		Semaphore trackNoParameterConstellationsPerGenerationSemaphore = 
//			new Semaphore(TaskConfig.noOfParameterConstellationsPerGeneration);
		

		


		
//		System.out.println("generated clusters file: "+clustersFile.getPath());
		
		
//		/* close logging file */
//		Log.closeFiles();
		
	}
	
	

}
