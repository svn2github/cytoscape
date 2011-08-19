package clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.taskmanaging;

import java.util.Date;
import java.util.concurrent.Semaphore;

import cytoscape.logger.CyLogger;

import clusterMaker.algorithms.networkClusterers.TransClust.TransClustCluster;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.iterativeclustering.IteratorThread;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.layout.IParameters;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.taskmanaging.io.Console;
import clusterMaker.algorithms.networkClusterers.TransClust.de.layclust.taskmanaging.io.InfoFile;


/**
 * This class is the main organising class. It starts the program in the correct mode and calls
 * the appropriate classes in their correct order. It outputs the info file and is in
 * contact with the gui in case the process should be cancelled. If started from
 * the gui then it is possible to run this class in a separate thread, so that it is
 * still possible to use the gui. 
 * 
 * @author Sita Lange
 *
 */
public class ClusteringManagerTask implements Runnable {

	// private static Logger log = Logger.getLogger(Console.class.getName());
	private static CyLogger	log = CyLogger.getLogger(TransClustCluster.class);

	private Semaphore semaphore;
	
	private ClusteringManager clusterManager;
	
	public void run() {
		/* start clustering with given configuration */

		try {

			log.info("-----------------------------------");
			log.info("Running ... " + TaskConfig.NAME + " v"
					+ TaskConfig.VERSION);
			Date date = new Date(System.currentTimeMillis());
			log.info(date.toString());
			log.info("-----------------------------------");

			if (TaskConfig.mode == TaskConfig.CLUSTERING_MODE) {
				long time = System.currentTimeMillis();
				this.clusterManager = new ClusteringManager(
						TaskConfig.cmPath);
				try {
					this.clusterManager.initParametersAndCCs();
					this.clusterManager.runClustering();
//					System.out.println(this.clusterManager.getConnectedComponents().size());
					time = System.currentTimeMillis() - time;
					log.info("Time taken for complete clustering process: "
							+ TaskUtility.convertTime(time));
				} catch (InvalidInputFileException e) {
					log.fatal(e.getMessage(),e);
					// e.printStackTrace();
					System.exit(-1);
				} catch (InvalidTypeException e) {
					log.fatal(e.getMessage(),e);
					// e.printStackTrace();
					System.exit(-1);
				}
			}
			/* start general training */
			else if (TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE) {
				try {
					long time = System.currentTimeMillis();
					ClusteringManager clustermanage = new ClusteringManager(
							TaskConfig.cmPath);
					GeneralParameterTraining generalParameterTraining = new GeneralParameterTraining(
							clustermanage);
					IParameters[] layoutParams = generalParameterTraining
							.runGeneralTraining();

					/* save newly found parameters to config file */
					TaskConfig
							.saveConfigurationsToConfigFile(TaskConfig.outConfigPath);

					/* print out information about training */
					log
							.info(TaskConfig.NL
									+ "###### Best Parameter Configurations Found ######");
					for (int i = 0; i < layoutParams.length; i++) {
						log.info(layoutParams[i].toString());
						InfoFile.appendLnProjectResults(layoutParams[i]
								.toString());
					}

					time = System.currentTimeMillis() - time;
					log.info("Time taken for complete general training: "
							+ TaskUtility.convertTime(time));
				} catch (InvalidInputFileException e) {
					log.error(e.getMessage(),e);
					// e.printStackTrace();
				} catch (InvalidTypeException e) {
					log.error(e.getMessage(),e);
					// e.printStackTrace();
				}
			}else if(TaskConfig.mode == TaskConfig.COMPARISON_MODE || TaskConfig.mode == TaskConfig.HIERARICHAL_MODE){
				Thread t = new IteratorThread();
				t.start();
			}
			/* no such mode */
			else {
				log.error("ERROR: This mode does not exist: "
						+ TaskConfig.mode);
			}

			/* create info file */
			if (TaskConfig.info) {
				InfoFile info = new InfoFile();
				info.instantiateFile(TaskConfig.infoPath);
				info.createAndCloseInfoFile();
				
				/* clear information from StringBuffers in InfoFile */
				InfoFile.clearData();
			}

//			/* ====== CLEAN UP AT END ====== */

		} catch (Exception e) {
			log.error("ERROR occured in run with the following message: "+e.getMessage(),e);
			// e.printStackTrace();
		}
		
		/* ====== CLEAN UP AT END ====== */

		/* release permit in semaphore if necessary */
		if (semaphore != null) {
			semaphore.release();
		}
		
		/* reset the run button back to "RUN" and reset action command */

	}
	
	/**
	 * Tells the {@link ClusteringManager} instance to stop all started threads.
	 *
	 */
	public void stopAllThreads(){
		if(this.clusterManager!=null){
			this.clusterManager.stopAllRunningThreads();
		}
	}

	/**
	 * If the use of Semaphores is wanted, then one can be set. Otherwise it is
	 * null;
	 * 
	 * @param semaphore
	 *            The semaphore to set.
	 */
	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;

		if (semaphore != null) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// Thread interrupted, semaphore can't aquire
				e.printStackTrace();
			}
		}
	}
	
}
