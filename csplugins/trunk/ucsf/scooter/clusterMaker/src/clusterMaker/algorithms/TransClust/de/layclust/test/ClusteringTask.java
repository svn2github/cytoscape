/* 
 * Created on 18. November 2007
 * 
 */
package de.layclust.test;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.geometric_clustering.IGeometricClusterer;
import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.ILayouter;
import de.layclust.layout.IParameters;
import de.layclust.layout.LayoutFactory;
import de.layclust.postprocessing.IPostProcessing;
import de.layclust.postprocessing.PP_DivideAndRecluster;
import de.layclust.postprocessing.PP_DivideAndReclusterRecursively;
import de.layclust.postprocessing.PostProcessingFactory;
import de.layclust.taskmanaging.TaskConfig;
import de.layclust.taskmanaging.TaskUtility;
import de.layclust.taskmanaging.io.ClusterFile;
import de.layclust.taskmanaging.io.InfoFile;

/**
 * This class carries out the final clustering process and writes the resulting
 * clusters into a clusters file. It is possible to run this in a thread, but
 * preferable not to as the ConnectedComponent objects can get quite large and
 * if too many are created at the same time it can come to memory problems.
 * 
 * @author Sita Lange 2007
 * 
 */
public class ClusteringTask implements Runnable {
	
	private static Logger log = Logger.getLogger(ClusteringTask.class.getName());

	/* the ConnectedComponent for which the clustering should be done */
	private ConnectedComponent cc;

	/* keeps track of the thread running status */
	private Semaphore semaphore = null;
	
	private Semaphore maxThreadSemaphore = null;

	/*
	 * The order of layouting algorithms that should be started. Each layouting
	 * algorithm has a int value as is defined in LayoutFactory.
	 */
	private LayoutFactory.EnumLayouterClass[] layouterEnumTypes = null;

	/*
	 * IParameter objects for each layouter in the same order as in
	 * layouterIntTypes.
	 */
	private IParameters[] parameters = null;

	/* the file where the resulting clusters are to be added to */
	private ClusterFile clusterFile = null;

	public ClusteringTask(ConnectedComponent cc, IParameters[] parameters,
			LayoutFactory.EnumLayouterClass[] layouterEnumTypes,
			ClusterFile clusterFile) {
		this.cc = cc;
		this.parameters = parameters;
		this.layouterEnumTypes = layouterEnumTypes;
		this.clusterFile = clusterFile;
	}

	/**
	 * Runs the clustering process for one {@link ConnectedComponent} and prints
	 * the results into the output file.
	 */
	public void run() {
		
		long time = System.currentTimeMillis();

		/* ====== LAYOUTING PHASE ====== */

		/* iterate over layouters */
		ILayouter previousLayouter = null;
		for (int i = 0; i < this.layouterEnumTypes.length; i++) {
			IParameters param = parameters[i];

			/* create correct layouter */
			ILayouter layouter = this.layouterEnumTypes[i].createLayouter();

			if (previousLayouter == null) {
				/* initialise cc positions if in clustering mode */
				if(TaskConfig.mode == TaskConfig.CLUSTERING_MODE){
					ILayoutInitialiser li = this.layouterEnumTypes[i]
					                                               .createLayoutInitialiser();
					li.initLayoutInitialiser(this.cc);


					/* initialise and run layouter */
					layouter.initLayouter(this.cc, li, param);
					layouter.run();
					previousLayouter = layouter;
				} else if(TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE){
					// else positions already set for training mode
					layouter.initLayouter(cc, param);
					layouter.run();
					previousLayouter = layouter;
				} else {
					log.severe("This mode does not exist: "+TaskConfig.mode);
				}
					
			} else {
				/*
				 * initialise and run layouter with previous calculated
				 * positions
				 */
				layouter.initLayouter(this.cc, previousLayouter, param);
				layouter.run();
			}
		}

		/* ====== GEOMETRIC CLUSTERING */
		IGeometricClusterer geoClust = TaskConfig.geometricClusteringEnum
				.createGeometricClusterer();
		geoClust.initGeometricClusterer(this.cc);
		geoClust.run();

		/* ====== POST-PROCESSING ====== */
		if (TaskConfig.doPostProcessing) {
			PostProcessingFactory.EnumPostProcessingClass ppEnum = TaskConfig.postProcessingEnum;
			IPostProcessing pp = ppEnum.createPostProcessor();
			pp.initPostProcessing(this.cc);

			// TODO this had to be edited if the post processors need
			// additional parameters.
			if (ppEnum
					.equals(PostProcessingFactory.EnumPostProcessingClass.PP_DIVIDE_AND_RECLUSTER)) {
				((PP_DivideAndRecluster) pp)
						.setLayoutingInfo(this.parameters,
								this.layouterEnumTypes);
			} else if (ppEnum
					.equals(PostProcessingFactory.EnumPostProcessingClass.PP_DIVIDE_AND_RECLUSTER_RECURSIVELY)) {
				((PP_DivideAndReclusterRecursively) pp).setLayoutingInfo(
						this.parameters, this.layouterEnumTypes);
			}
			/* run post processing */
			pp.run();
		}
		
		/* ====== SET SCORE ======= */
		double score = this.cc.getClusteringScore();
		/* add initial reduction cost to score */
		score += this.cc.getReductionCost();
		/* add clustering score to total score! */
		if(TaskConfig.mode == TaskConfig.GENERAL_TRAINING_MODE){
		}

		ClusterManagerThreadTest.addClusteringScoreToSum(score);

		
		/* ====== PRINT CLUSTERING INFO ====== */

		if (TaskConfig.mode == TaskConfig.CLUSTERING_MODE) {
			String ccPath = this.cc.getCcPath();
			int ccSize = this.cc.getNodeNumber();
			int[] distribution = this.cc.getClusterInfo();
			int clusterNo = this.cc.getNumberOfClusters();

			String delimiter = "\t";
			StringBuffer resultForCCBuffer = new StringBuffer(
					distribution.length * 4);
			resultForCCBuffer.append(ccPath);
			resultForCCBuffer.append(delimiter);
			resultForCCBuffer.append("size=");
			resultForCCBuffer.append(ccSize);
			resultForCCBuffer.append(delimiter);
			resultForCCBuffer.append("score=");
			resultForCCBuffer.append(score);
			resultForCCBuffer.append(delimiter);
			resultForCCBuffer.append("clusters=");
			resultForCCBuffer.append(clusterNo);
			resultForCCBuffer.append(delimiter);

			for (int i = 0; i < distribution.length; i++) {
				resultForCCBuffer.append(distribution[i]);
				resultForCCBuffer.append(" ");
			}
			resultForCCBuffer.append(delimiter);
			resultForCCBuffer.append("time=");
			time = System.currentTimeMillis() - time;
			resultForCCBuffer.append(TaskUtility.convertTime(time));
			resultForCCBuffer.append(delimiter);
			resultForCCBuffer.append("time-ms=");
			resultForCCBuffer.append(time);

			log.info(resultForCCBuffer.toString());
			InfoFile.appendLnProjectResults(resultForCCBuffer.toString());
		



		/* ====== PRINT RESULTS IN CLUSTERS FILE ====== */

			ArrayList<ArrayList<String>> clustersWithIDs = new ArrayList<ArrayList<String>>(
					this.cc.getNumberOfClusters());
	
			/* fill array list object with cluster array lists */
			for (int i = 0; i < this.cc.getNumberOfClusters(); i++) {
				clustersWithIDs.add(new ArrayList<String>());
			}
			/* fill cluster array list with object id's */
			for (int i = 0; i < this.cc.getNodeNumber(); i++) {
				int cluster = this.cc.getClusterNoForObject(i);
				String objectID = this.cc.getObjectID(i);
				clustersWithIDs.get(cluster).add(objectID);
			}
	
			/* print each cluster in cluster output file */
			for (ArrayList<String> cluster : clustersWithIDs) {
				clusterFile.printCluster(cluster);
			}
			clusterFile.flushbw();
		}

		
		/* ====== CLEAN UP AT END ====== */

		/* release permit in semaphores if necessary */
		if (semaphore != null) {
			semaphore.release();
		}
		if(this.maxThreadSemaphore != null){
			this.maxThreadSemaphore.release();
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
	
	/**
	 * Sets the Semaphore to keep track of how many threads are allowed to run in parallel.
	 * Also tries to aquire a permit. If none is available, the thread from where this method
	 * is called has to wait as long until one running thread has finished and released a permit.
	 * 
	 * @param semaphore The Semaphore to set and to acqire.
	 */
	public void setMaxThreadSemaphore(Semaphore semaphore){
		this.maxThreadSemaphore = semaphore;

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
