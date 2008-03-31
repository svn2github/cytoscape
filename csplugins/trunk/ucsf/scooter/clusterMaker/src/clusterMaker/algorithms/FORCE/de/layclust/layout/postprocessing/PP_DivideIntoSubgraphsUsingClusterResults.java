/* 
 * Created on 14. December 2007
 * 
 */
package de.layclust.layout.postprocessing;

import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.taskmanaging.ClusterPostProcessingTask;
import de.layclust.taskmanaging.ClusterTrainingTask;
import de.layclust.taskmanaging.TestingUtility;

public class PP_DivideIntoSubgraphsUsingClusterResults implements
		IPostProcessing {
	
	private ConnectedComponent cc = null;
	private double bestScore = 0;
	private IParameters[] params = null;
	private int[] layouterIntTypes = null;

	public void initPostProcessing(ConnectedComponent cc) {
		this.cc = cc;		
		this.bestScore = cc.getClusteringScore();
	}
	
	/**
	 * This sets the information as to which layouter should be used and its parameters. Or
	 * which combination of layouters should be used.
	 * This method MUST be called before run()!!
	 * @param param The parameters for the layouting phase.
	 * @param layoutType The type of layouter to be used.
	 */
	public void setLayoutingInfo(IParameters[] params, int[] layouterIntTypes){
		this.params = params;
		this.layouterIntTypes = layouterIntTypes;
	}

	public void run() {
		
		/* print score before any post processing */
		double scoreAtStart = this.cc.getClusteringScore();
//		System.out.println("Score before post processing: "+scoreAtStart);
		
		/* merging step */		
		Vector<Vector<Integer>> clusterObject = PostProcessingUtility.createClusterObject(this.cc, false);
		HashSet<String> alreadyCompared = new HashSet<String>();
		ClusterObjectComparator comparator = new ClusterObjectComparator();
		PostProcessingUtility.mergeCluster(clusterObject, alreadyCompared, this.cc, comparator, true);
		
		this.bestScore = PostProcessingUtility.updateClusterInfoInCC(clusterObject, this.cc);
		
		System.out.println("Score after initial merging: "+this.bestScore);
		System.out.println("test - calculated score: "+this.cc.getClusteringScore());// TODO delete
		TestingUtility.printClusteringInformation(cc);
		
		/* collection ConnectedComponent objects */
		Vector<ConnectedComponent> cCsOfSubgraphs = new Vector<ConnectedComponent>();
		
		/* start a new clustering procedure for clusters larger than 3 */
	    for(int i=0;i<clusterObject.size();i++){
			Vector<Integer> cluster = clusterObject.get(i);
			int clusterSize = cluster.size();

			/* if the clusters are to small, leave them in the clusters object and continue */
			if(clusterSize <= 3){
//				System.out.println("cluster too small: "+cluster.toString());
				continue;
			}
			
			/* remove cluster from cluster object and decrease i */ //TODO!!
			clusterObject.remove(i);
			--i;
			
			
			ConnectedComponent ccForCluster = this.cc.createConnectedComponentForCluster(i, cluster);
			cCsOfSubgraphs.add(ccForCluster);
			
			ClusterPostProcessingTask clusterTask = new ClusterPostProcessingTask(ccForCluster, this.params, this.layouterIntTypes);
			clusterTask.run();
		}
	    
	    for (int i = 0; i < cCsOfSubgraphs.size(); i++) {
			ConnectedComponent subCC = cCsOfSubgraphs.get(i);
			addClustersToTotalClusters(subCC, clusterObject);
		}
	    
	    /* update clustering information */
	    this.bestScore = PostProcessingUtility.updateClusterInfoInCC(clusterObject, this.cc);
		
		System.out.println("Score after division and re-clustering: "+this.bestScore);
		TestingUtility.printClusteringInformation(cc);
		
		
		
		/* do post post processing - merge and rearrange */
		PP_RearrangeAndMergeBest postProcess1 = new PP_RearrangeAndMergeBest();
		postProcess1.initPostProcessing(this.cc);
		postProcess1.run();
		

	}
	
	private void addClustersToTotalClusters(ConnectedComponent subCC, Vector<Vector<Integer>> clusterObject){
		int noOfClusters = subCC.getNumberOfClusters();
//		System.out.println("sub cluster size: "+noOfClusters);
		int[] subClusters = subCC.getClusters();
		
	
		/* initialise new clusters object */
		Vector<Vector<Integer>> newClusters = new Vector<Vector<Integer>>(noOfClusters);
		int[] clusterDistribution = subCC.getClusterInfo();
		for (int i = 0; i < clusterDistribution.length; i++) {
			newClusters.add(new Vector<Integer>(clusterDistribution[i]));
		}
		
		/* fill new clusters object */
		for (int i = 0; i < subClusters.length; i++) {
			int originalNo = Integer.parseInt(subCC.getObjectID(i));
			newClusters.get(subClusters[i]).add(originalNo);
		}
		
		/* merge new clusters object with old clusters object */
		for (int i = 0; i < noOfClusters; i++) {
			clusterObject.add(newClusters.get(i));
		}
	}

	/**
	 * Gets the score for the current clustering.
	 * @return the bestScore
	 */
	protected double getBestScore() {
		return bestScore;
	}

}
