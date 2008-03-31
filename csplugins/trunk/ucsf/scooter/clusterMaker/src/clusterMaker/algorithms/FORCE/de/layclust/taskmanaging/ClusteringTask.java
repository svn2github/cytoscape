/* 
 * Created on 18. November 2007
 * 
 */
package de.layclust.taskmanaging;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.ILayouter;
import de.layclust.layout.LayoutFactory;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.geometric_clustering.GeometricClusteringFactory;
import de.layclust.layout.geometric_clustering.IGeometricClusterer;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.layout.postprocessing.IPostProcessing;
import de.layclust.layout.postprocessing.PP_DivideAndReclusterRecursively;
import de.layclust.layout.postprocessing.PP_DivideIntoSubgraphsUsingClusterResults;
import de.layclust.layout.postprocessing.PostProcessingFactory;
import de.layclust.taskmanaging.io.ClusterFile;

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

	/* the ConnectedComponent for which the clustering should be done */
	private ConnectedComponent cc;

	/* keeps track of the thread running status */
	private Semaphore semaphore = null;

	/*
	 * The order of layouting algorithms that should be started. Each layouting
	 * algorithm has a int value as is defined in LayoutFactory.
	 */
	private int[] layouterIntTypes = null;

	/*
	 * IParameter objects for each layouter in the same order as in
	 * layouterIntTypes.
	 */
	private IParameters[] parameters = null;

	/* the file where the resulting clusters are to be added to */
	private ClusterFile clusterFile = null;

	public ClusteringTask(ConnectedComponent cc, IParameters[] parameters,
			int[] layouterIntTypes, ClusterFile clusterFile) {
		this.cc = cc;
		this.parameters = parameters;
		this.layouterIntTypes = layouterIntTypes;
		this.clusterFile = clusterFile;
	}

	/**
	 * Runs the clustering process for one {@link ConnectedComponent} and prints
	 * the results into the output file.
	 */
	public void run() {

		try {

			/* ====== LAYOUTING PHASE ====== */

			/* iterate over layouters */
			ILayouter previousLayouter = null;
			for (int i = 0; i < this.layouterIntTypes.length; i++) {
				IParameters param = parameters[i];

				/* create correct layouter */
				ILayouter layouter = LayoutFactory
						.getLayouterByType(this.layouterIntTypes[i]);

				if (previousLayouter == null) {
					/* initialise cc positions */
					ILayoutInitialiser li = LayoutFactory
							.getLayouterInitialiserByType(this.layouterIntTypes[i]);
					li.initLayoutInitialiser(this.cc);

					/* initialise and run layouter */
					layouter.initLayouter(this.cc, li, param);
					layouter.run();
					previousLayouter = layouter;
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
			IGeometricClusterer geoClust = GeometricClusteringFactory
					.getGeometricClusterByType(
							TaskConfig.geometricClusteringType, this.cc);
			geoClust.run();

			// TestingUtility.printClusteringInformation(this.cc);
			// System.out.println(this.parameters[0].toString());

			/* ====== POST-PROCESSING ====== */
			if (TaskConfig.doPostProcessing) {
				int ppType = TaskConfig.postProcessingType;
				IPostProcessing pp = PostProcessingFactory
						.getPostProcessorByType(ppType);
				pp.initPostProcessing(this.cc);

				// TODO this had to be edited if the post processors need
				// additional parameters.
				if (ppType == PostProcessingFactory.PP_DIVIDE_INTO_SUBGRAPHS_USING_RESULTS) {
					((PP_DivideIntoSubgraphsUsingClusterResults) pp)
							.setLayoutingInfo(this.parameters,
									this.layouterIntTypes);
				} else if (ppType == PostProcessingFactory.PP_DIVIDE_AND_RECLUSTER_RECURSIVELY) {
					((PP_DivideAndReclusterRecursively) pp).setLayoutingInfo(
							this.parameters, this.layouterIntTypes);
				}
				/* run post processing */
				pp.run();
			}

			/* ====== PRINT CLUSTERING INFO ====== */

			double score = this.cc.getClusteringScore();
			/* add initial reduction cost to score */
			score += this.cc.getReductionCost();
			if (TaskConfig.verbose) {
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

				System.out.println(resultForCCBuffer.toString());
			}

			/* add clustering score to total score! */
			TaskConfig.addClusteringScoreToSum(score);

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

			/* ====== CLEAN UP AT END ====== */

			/* release permit in semaphore if necessary */
			if (semaphore != null) {
				semaphore.release();
			}

		} catch (InvalidTypeException ex) {
			ex.printStackTrace();
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
