package de.layclust.test;



import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.datastructure.CostMatrixReader;
import de.layclust.layout.IParameters;
import de.layclust.layout.parameter_training.IParameterTraining;
import de.layclust.taskmanaging.InvalidInputFileException;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;
import de.layclust.taskmanaging.TaskUtility;
import de.layclust.taskmanaging.io.ClusterFile;
import de.layclust.taskmanaging.io.InfoFile;


public class ClusterManagerThreadTest {
	
	private static Logger log = Logger.getLogger(ClusterManagerThreadTest.class.getName());
	
	private String cmPath;
	private IParameters[] layouterParameters = null;
	private ArrayList<ConnectedComponent> connectedComponents = null;
	private ArrayList<Thread> allThreads;
	private int noOfThreads;
	
	private Semaphore trackThreadsSemaphore;
	
	private static double totalScoreSum = 0; // total score for all cc
	
	public ClusterManagerThreadTest(String cmPath){
		this.cmPath = cmPath;
		
		String configString = TaskConfig.printConfiguration().toString();
		log.info(configString);
		InfoFile.appendToProjectDetails(configString);
		
		/* check how many threads are left over to see if it is possible to use threads at all
		 *  and set noOfThreads to a minimum of 1 */
		this.noOfThreads = TaskConfig.maxNoThreads;
		if(this.noOfThreads ==0 || this.noOfThreads ==1){
			TaskConfig.useThreads = false;
//			TaskConfig.useThreadsForParameterTraining = false;
			this.noOfThreads = 1;
		}
		if(TaskConfig.useThreads){
			log.info("Using threads with a maximum of "+this.noOfThreads+" running parallel");
			this.allThreads = new ArrayList<Thread>();
		}
		
		this.trackThreadsSemaphore = new Semaphore(TaskConfig.maxNoThreads, true);
	}
	
	/**
	 * Sets transitive connected components file if in input directory. Finds all cost matrix files
	 * given and creates a new {@link ConnectedComponent} instance for each and adds them to
	 * the list of ConnectedComponents.
	 * 
	 * Also initialises the {@link IParameters} from the input configuration.
	 * @throws InvalidInputFileException 
	 *
	 */
	public void initParametersAndCCs() throws InvalidInputFileException{
		
		this.connectedComponents = new ArrayList<ConnectedComponent>();
		
		/* read the input file or directory */
		File cmFile = new File(this.cmPath);
		if(cmFile.isDirectory()){
			
			log.finer("Input is a directory!");
			
			/* get all files in the directory */
			File[] files = cmFile.listFiles();
			
			/* check boolean whether cm files exist in directory */
			boolean noCostMatrices = true; 
			
			for(int i = 0; i < files.length; i++){
				String filePath = files[i].toString();
				/* find tcc file in directory */
				if(filePath.endsWith(".tcc") || filePath.endsWith(".rtcc")){
					TaskConfig.transitiveConnectedComponents = filePath;
					log.info("Transitive connected components file: "+filePath);
					InfoFile.appendToProjectDetails("Transitive connected component file: "+filePath);
				}
				
				/* find cm files*/
				if(files[i].toString().endsWith(".cm") || files[i].toString().endsWith(".rcm")){
					noCostMatrices = false; //cm files exist	
					
					// create the connected component (cc) object and add to list
					CostMatrixReader cmReader = new CostMatrixReader(files[i]);
					ConnectedComponent cc = cmReader.getConnectedComponent();
					connectedComponents.add(cc);
				}
				
			}

			if(noCostMatrices){
				throw new InvalidInputFileException("There are no cost matrix " +
						"files in the input directory, " +
						"or check whether the file extension equals .cm or .rcm");
			}
			
		} else{
			
			/* only one cost matrix file is given */
			log.finer("One cm file given");
			
			TaskConfig.useThreads = false; //no threads for only one cm!
			
			 /* only one cost matrix as input - start clustering process */
			if(cmFile.toString().endsWith(".cm")){
				
				// create the connected component (cc) object and add to list
				CostMatrixReader cmReader = new CostMatrixReader(cmFile);
				ConnectedComponent cc = cmReader.getConnectedComponent();
				connectedComponents.add(cc);
			} else {
				throw new InvalidInputFileException("The input cost matrix is of " +
						"wrong file type. The file extension should be \".cm\" or \".rcm\"");						
			}
		}	
		
		
		/* initialise parameters from config */
//		LayoutFactory.EnumLayouterClass[] layouterEnumTypes = TaskConfig.layouterEnumTypes;
		layouterParameters = new IParameters[TaskConfig.layouterEnumTypes.length];
		for(int i=0;i<TaskConfig.layouterEnumTypes.length;i++){			

				IParameters param = TaskConfig.layouterEnumTypes[i].createIParameters();
				param.readParametersFromConfig();
				layouterParameters[i] = param;
		
		}
	}

	/**
	 * Runs the clustering with the given configurations in the config class: {@link TaskConfig}.
	 * Clusters each {@link ConnectedComponent} separately and waits until all are done. 
	 * Differes between the modes clustering and general training. Creates a Config file if
	 * the training mode is used.
	 * @throws InvalidInputFileException If the file/directory given produces an error.
	 * @throws InvalidTypeException An incorrect method implementation was given, or some
	 * other error occured with this.
	 */
	public void runClustering() throws InvalidInputFileException, InvalidTypeException {
			
		/* initialise ClusterFile if in clustering mode */
		ClusterFile clusterFile = null;
		if(TaskConfig.mode == TaskConfig.CLUSTERING_MODE){
			log.fine("Running clustering in clustering mode!");
			clusterFile = new ClusterFile();
			clusterFile.instantiateFile(TaskConfig.clustersPath);
			clusterFile.printPreProcessingClusters(TaskConfig.transitiveConnectedComponents);
		} 
		
		/* check whether connectedComponents has been initialised */
		if(this.connectedComponents==null){
			log.warning("Incorrect use of the ClusteringManager, the connected components list" +
					"hadn't been initialised. Called method to initialise this and the parameters from " +
					"the config");
			this.initParametersAndCCs();
		}
		

		
		/* go through cc list and start training for each and control thread use */
		ArrayList<Semaphore> allSemaphores = new ArrayList<Semaphore>();
		Semaphore trackThreadsSemaphore = new Semaphore(TaskConfig.maxNoThreads, true);
		for(int i=0;i<this.connectedComponents.size();i++){
			Semaphore semaphore = new Semaphore(1);
			allSemaphores.add(semaphore);
			runClusteringForOneConnectedComponent(this.connectedComponents.get(i), clusterFile, semaphore, trackThreadsSemaphore);					
		}

		/* wait for all clustering tasks to finish */
		for (Semaphore s : allSemaphores) {
			try {
				s.acquire();
			} catch (InterruptedException e) {
				log.severe(e.getMessage());
				e.printStackTrace();
			}
		}

		if(clusterFile!=null)
			clusterFile.closeFile();
		
		
		/* END OF CLUSTERING */	 
//		log.info("Clustering scores sum: "+TaskConfig.totalScoreSum);
//		if(TaskConfig.mode == TaskConfig.CLUSTERING_MODE){
//				InfoFile.appendLnProjectResults("Total sum of clustering scores for given input: "+TaskConfig.totalScoreSum);
//		}
//		/* set score to IParameters objects for general training mode */
//		if(TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE){
//			log.fine("Setting parameters score for training mode!");
//			for (IParameters parameter : this.layouterParameters) {
//				parameter.setScore(TaskConfig.totalScoreSum);
//				//TODO reset to zero here??
//			}
//		}
		
		
		log.info("Clustering scores sum: "+totalScoreSum);
		if(TaskConfig.mode == TaskConfig.CLUSTERING_MODE){
				InfoFile.appendLnProjectResults("Total sum of clustering scores for given input: "+totalScoreSum);
		}
		/* set score to IParameters objects for general training mode */
		if(TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE){
			log.fine("Setting parameters score for training mode!");
			for (IParameters parameter : this.layouterParameters) {
				parameter.setScore(totalScoreSum);			
				
			}
			
		}
		totalScoreSum = 0;//TODO reset to zero here??
	}
	
	/**
	 * Runs clustering for one {@link ConnectedComponent} and sets the total score to
	 * the parameters if in the general training mode.
	 * @param cc The connected component object.
	 * @param clusterFile The clusters file (null if in general training mode)
	 * @param semaphore The Semaphore to give to the clustering task to keep track of it.
	 * @throws InvalidInputFileException 
	 */
	private void runClusteringForOneConnectedComponent(ConnectedComponent cc, 
			ClusterFile clusterFile, Semaphore semaphore, Semaphore trackThreadsSemaphore) throws InvalidInputFileException{
		
		/* check whether layouterParameters has been initialised */
		if(this.layouterParameters==null){
			log.warning("Incorrect use of the ClusteringManager, the layouter parameters list" +
					"hadn't been initialised. Called method to initialise this and the connected components from " +
					"the config");

			this.initParametersAndCCs();
		}
		
		/* if in clustering mode and layout parameter training is used, then
		 * create the parameters using the parameter training for each individual 
		 * connected component
		 */

		if(TaskConfig.mode == TaskConfig.CLUSTERING_MODE){
			if(TaskConfig.doLayoutParameterTraining){
				for(int i=0;i<this.layouterParameters.length;i++){
					/* start parameter training for the cc */				
					IParameterTraining paramTrain = TaskConfig.parameterTrainingEnum.createParameterTrainer();
					paramTrain.initialise(TaskConfig.layouterEnumTypes[i], 
							TaskConfig.noOfParameterConfigurationsPerGeneration,
							TaskConfig.noOfGenerations);
					IParameters bestparam = paramTrain.run(cc);
					log.fine("PARAMETER TRAINING RESULT\n: "+cc.getCcPath()+"\n"+bestparam.toString());
					this.layouterParameters[i] = bestparam;
				}				
			}
		}
		
		/* run clustering with the previously determined parameters */
		ClusteringTask clusterTask = new ClusteringTask(cc, this.layouterParameters,
				TaskConfig.layouterEnumTypes, clusterFile);
		
		if(TaskConfig.useThreads){
			clusterTask.setSemaphore(semaphore);
			clusterTask.setMaxThreadSemaphore(trackThreadsSemaphore);
			Thread t = new Thread(clusterTask);
			this.allThreads.add(t);
			t.start();
//			(new Thread(clusterTask)).start();
			
		}else{
			clusterTask.run(); 
		}
	}

	/**
	 * @return the connectedComponents
	 */
	public ArrayList<ConnectedComponent> getConnectedComponents() {
		return connectedComponents;
	}

	/**
	 * @param connectedComponents the connectedComponents to set
	 */
	public void setConnectedComponents(
			ArrayList<ConnectedComponent> connectedComponents) {
		this.connectedComponents = connectedComponents;
	}

	/**
	 * @return the layouterParameters
	 */
	public IParameters[] getLayouterParameters() {
		return layouterParameters;
	}

	/**
	 * @param layouterParameters the layouterParameters to set
	 */
	public void setLayouterParameters(IParameters[] layouterParameters) {
		this.layouterParameters = layouterParameters;
	}
	
	/**
	 * This method adds one clustering score to the total clustering score for
	 * the whole directory. 
	 * 
	 * @param score The score to be added to the total score.
	 */
	public static synchronized void addClusteringScoreToSum(double score){
		totalScoreSum += score;
	}

	/**
	 * @return the totalScoreSum
	 */
	public double getTotalScoreSum() {
		return totalScoreSum;
	}

	/**
	 * @param totalScoreSum the totalScoreSum to set
	 */
	public void setTotalScoreSum(double totalScoreSum) {
		totalScoreSum = totalScoreSum;
	}
	
	/**
	 * Stops all currently running threads that have been started in runOneConnectedComponent.
	 *
	 */
	public void stopAllRunningThreads(){
		if(this.allThreads!=null){
			for (Thread t : this.allThreads) {
				if(t.isAlive()){
					t.stop();
				}
			}
		}
	}
	
	public static void main (String[] args){
		long time  = System.currentTimeMillis();
		
		TaskConfig.clustersPath = "test.cls";
		TaskConfig.verbose = true;
		TaskConfig.useThreads = true;
		TaskConfig.maxNoThreads = 7;
		ClusterManagerThreadTest cmtt = new ClusterManagerThreadTest("de/layclust/data/cm/");

		
		try {
			cmtt.initParametersAndCCs();
			cmtt.runClustering();
			time = System.currentTimeMillis() - time;
			log.info("time taken for complete clustering: "+TaskUtility.convertTime(time));
		} catch (InvalidInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
