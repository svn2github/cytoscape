/* 
 * Created on 21. January 2007
 * 
 */
package de.layclust.layout.postprocessing;

import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.taskmanaging.ClusterPostProcessingTask;

/**
 * This class is for improving the clustering results with respect to the WGCEP problem.
 * First, clusters are merged together if this is benificial. Then each cluster that
 * is larger than size three is re-clustered using the input cluster settings. The clusters
 * are recursively re-clustered until no more improvements can be found. I.e. until the 
 * clusters are not subdivides by the re-clustering process anymore.
 * After this the methods of the class {@link PP_RearrangeAndMergeBest} are
 * used to recursively rearrange single nodes and merge clusters if this improves
 * the overall score.
 * 
 * @author Sita Lange
 */
public class PP_DivideAndReclusterRecursively implements IPostProcessing {

	private ConnectedComponent cc = null;

	private double bestScore = 0;

	private IParameters[] params = null;

	private int[] layouterIntTypes = null;

	/**
	 * Initialises the class with the appropriate connected component for
	 * which the clustering results are to be improved.
	 */
	public void initPostProcessing(ConnectedComponent cc) {
		this.cc = cc;
		this.bestScore = cc.getClusteringScore();
	}

	/**
	 * This sets the information as to which layouter should be used and its
	 * parameters. Or which combination of layouters should be used. This method
	 * MUST be called before run()!!
	 * 
	 * @param param
	 *            The parameters for the layouting phase.
	 * @param layoutType
	 *            The type of layouter to be used.
	 */
	public void setLayoutingInfo(IParameters[] params, int[] layouterIntTypes) {
		this.params = params;
		this.layouterIntTypes = layouterIntTypes;
	}

	/**
	 * Runs the post-processing. 1. Initial merge 2. Recursive reclustering of
	 * clusters greater than size 3. 3. Recursive re-arrangement and merging of
	 * resulting clusters.
	 * 
	 */
	public void run() {

		/* merging step */
		Vector<Vector<Integer>> clusterObject = PostProcessingUtility
				.createClusterObject(this.cc, false);
		HashSet<String> alreadyCompared = new HashSet<String>();
		ClusterObjectComparator comparator = new ClusterObjectComparator();
		PostProcessingUtility.mergeCluster(clusterObject, alreadyCompared,
				this.cc, comparator, true);

		/* update clustering info in the connected component */
		this.bestScore = PostProcessingUtility.updateClusterInfoInCC(
				clusterObject, this.cc);

		/* recursively recluster each cluster >3 until there is no improvement */
		Vector<Vector<Integer>> finalClusterObject = new Vector<Vector<Integer>>();
		recursiveReclustering(finalClusterObject, clusterObject);

		/* update clustering info in the connected component */
		this.bestScore = PostProcessingUtility.updateClusterInfoInCC(
				finalClusterObject, this.cc);

		/* do post processing - merge and rearrange */
		PP_RearrangeAndMergeBest postProcess1 = new PP_RearrangeAndMergeBest();
		postProcess1.initPostProcessing(this.cc);
		postProcess1.run();
	}

	
	/**
	 * This method goes through each cluster in the tmpClusterObject and
	 * re-clusters them. If the clusters have less than 4 objects, then it is
	 * added to the finalClusterObject. If after the re-clustering, no changes
	 * have occured (it is still one cluster) it is added to the
	 * finalClusterObject. Otherwise if changes have occured the sub-clusters
	 * are then re-clustered again. This occurs recursively until no more
	 * improvements are found.
	 * 
	 * @param finalClusterObject
	 *            This should only include finished clusters that can not be
	 *            improved anymore by reclustering.
	 * @param tmpClusterObject
	 *            This includes all clusters that are to be re-clustered.
	 */
	private void recursiveReclustering(
			Vector<Vector<Integer>> finalClusterObject,
			Vector<Vector<Integer>> tmpClusterObject) {

		for (int i = 0; i < tmpClusterObject.size(); i++) {
			Vector<Integer> cluster = tmpClusterObject.get(i);
			int clusterSize = cluster.size();

			/*all clusters <=3 are taken care of in the rearrange method of
			 * post-processing */
			if (clusterSize <= 3) {
				finalClusterObject.add(cluster);
			} else {
				
				/* run clustering for this one cluster to see if an improvement can be found */
				ConnectedComponent ccForCluster = this.cc
						.createConnectedComponentForCluster(i, cluster);
				ClusterPostProcessingTask clusterTask = new ClusterPostProcessingTask(
						ccForCluster, this.params, this.layouterIntTypes);
				clusterTask.run();
				
				/* if there has been no change, add this cluster to the final cluster object */
				if (ccForCluster.getNumberOfClusters() == 1) {
					finalClusterObject.add(cluster);
				
				/* otherwise recluster */
				} else {
					
					/* new cluster object for the resulting clusters of the improved cluster */
					Vector<Vector<Integer>> nextClusterObject = PostProcessingUtility
							.createClusterObject(ccForCluster, true);
					recursiveReclustering(finalClusterObject, nextClusterObject);
				}
			}
		}
	}
}
