/*
 *  Created on 3. December 2007
 */
package de.layclust.postprocessing;

import java.util.HashSet;
import java.util.Vector;

import de.layclust.datastructure.ConnectedComponent;

/**
 * @author sita
 *
 */
public class PP_RearrangeAndMergeBest implements IPostProcessing {

	private ConnectedComponent cc = null;
	private double bestScore = 0;
	
	/**
	 * Initialises the post processing object, which needs to be done before run() us called.
	 */
	public void initPostProcessing(ConnectedComponent cc) {
		this.cc = cc;		
		this.bestScore = cc.getClusteringScore();
	}
	
	/**
	 * Runs the post processing.
	 */
	public void run() {

		int[] clusters = cc.getClusters();
		
		Vector<Vector<Integer>> clusterObject = PostProcessingUtility.createClusterObject(this.cc, false); 
		
		boolean findBetterScore = true;
		double score;
		while(findBetterScore){
			score = this.bestScore;
			findBetterScore = onePostProcessingRound(clusters, clusterObject, score);
		}
		
		this.cc.initialiseClusterInfo(clusterObject.size());
		this.cc.setClusters(clusters);
		this.cc.setClusteringScore(this.cc.calculateClusteringScore(clusters)); // TODO take this out
		this.cc.calculateClusterDistribution();	
	}
	
	private boolean onePostProcessingRound(int[] clusters, Vector<Vector<Integer>> clusterObject, double oldScore){
		
		rearrangeSingleNodes(clusterObject, clusters);
		
		HashSet<String> alreadyCompared = new HashSet<String>();
		mergeBest(clusterObject, alreadyCompared);

		updateClusters(clusters, clusterObject);			
		
		if(this.bestScore < oldScore){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the clusters array from the information in the cluster vector object.
	 * @param clusters The clusters array to be updated.
	 * @param clusterObject The cluster vector object with the current information.
	 */
	protected void updateClusters(int[] clusters, Vector<Vector<Integer>> clusterObject){

		for(int i=0;i<clusterObject.size();i++){
			Vector<Integer> oneCluster = clusterObject.get(i);
			/* remove clusters of size 0 */
			if(oneCluster.size()==0){
				clusterObject.remove(oneCluster);
				--i;
				continue;
			}
			/* update cluster numbers for each node */
			for(int j=0;j<oneCluster.size();j++){
				clusters[oneCluster.get(j)] = i;
			}
		}
	}


	

	
	/**
	 * Looks at each single node and tries putting it in another cluster. This is checked for each cluster
	 * and the node is put into the cluster with the best score improvement, otherwise it is left in the original cluster.
	 * @param clusterObject
	 * @param clusters
	 */
	private void rearrangeSingleNodes(Vector<Vector<Integer>> clusterObject, int[] clusters ){
		
		boolean changed = false;
		
		/* for every node */
		for(int i=0;i<clusters.length;i++){
			double bestCostDiff = 0;
			int oldCluster = clusters[i];
			int newCluster = clusters[i];
			
			/* try putting node i in each other cluster*/
			double costDiff = 0;
			for(int j = 0;j<clusterObject.size();j++){			
				if(oldCluster!=j){
					costDiff = calculateCostChange(i, clusterObject.get(oldCluster), 
							clusterObject.get(j));
					if(costDiff < bestCostDiff){
						bestCostDiff = costDiff;
						newCluster = j;
					}
				}
			}
			
			/* try creating a new cluster for node i */
			Vector<Integer> newEmptyVector = new Vector<Integer>();
			costDiff = calculateCostChange(i, clusterObject.get(oldCluster), newEmptyVector);
			if(costDiff<bestCostDiff){
				changed = true;
				bestCostDiff = costDiff;
				newCluster = clusterObject.size();
				clusterObject.add(newEmptyVector);
			}

			
			/* if some better clustering has been found, then move node to new cluster */
			if(oldCluster != newCluster){
				Integer node_i = Integer.valueOf(i);
				Vector<Integer> oldClusterVector = clusterObject.get(oldCluster);
				oldClusterVector.remove(node_i);
				clusterObject.get(newCluster).add(node_i);
				this.bestScore +=bestCostDiff;
//				System.out.println("edited clusters!!");
			}
			
			/* if a new cluster has been created break loop and start again */
			if(changed){
				updateClusters(clusters, clusterObject);
				rearrangeSingleNodes(clusterObject, clusters);
				break;
			}

		}		
		if(!changed)
			updateClusters(clusters, clusterObject);
	}
	
	/**
	 * Calculates the cost change caused by moving one node from one cluster to
	 * another. The overall costs for the graph need not be computed, just the 
	 * actual changes, which saves a lot of time.
	 * @param node_i
	 * @param oldCluster The old cluster in which node i was.
	 * @param newCluster The new cluster to which node i is to be assigned to.
	 * @return The change in costs from the old clustering to the new one.
	 */
	private double calculateCostChange(int node_i, Vector<Integer> oldCluster, 
			Vector<Integer> newCluster){
		double costChange = 0;
		
		/* all edges from node i to every other node in the old cluster need
		 * to be deleted.
		 */
		for (int j = 0; j < oldCluster.size(); j++) {
			int node_k = oldCluster.get(j);
			if(node_k!=node_i){
				double cost = cc.getCCEdges().getEdgeCost(node_i, node_k);
				costChange += cost;
			}
		}
		
		/* edges from all nodes in the new cluster need to be added to the
		 * new node
		 */
		for (int j = 0; j < newCluster.size(); j++) {
			int node_k = newCluster.get(j);
			
			double cost = cc.getCCEdges().getEdgeCost(node_i, node_k);
			costChange -= cost;	
		}		
		return costChange;
	}
	
	/**
	 * Searches through all pairs of clusters and merges the pair, which gives the greatest cost
	 * advantages. That means the pair, which when merged reduces the overall cost the most.
	 * This is recursively repeated until no new merges occur.
	 * @param clusterObject The object with all clusters and their respective nodes.
	 * @param alreadyCompared A HashSet that stores all clusters that have already been compared.
	 */
	protected void mergeBest(Vector<Vector<Integer>> clusterObject, 
			HashSet<String> alreadyCompared){
		
		for(int i=0;i<clusterObject.size();i++){
			double bestCostChange = 0;
			Integer bestMergeCluster = i;
			Vector<Integer> v1 = clusterObject.get(i);
			Vector<Integer> best_merge = v1;
			/* check all other clusters for the best merge of two clusters */
			for(int j=i+1;j<clusterObject.size();j++){
					Vector<Integer> v2 = clusterObject.get(j);
					/* only check each cluster pair once */
					if(!alreadyCompared.contains(v1.hashCode()+"#"+ v2.hashCode())){
						alreadyCompared.add(v1.hashCode()+"#"+v2.hashCode());
						double costChange = calculateCostChange(clusterObject.get(i), clusterObject.get(j));
						if(costChange<bestCostChange){
							bestCostChange = costChange;
							bestMergeCluster = j;
							best_merge = v2;
						}
					}
			}
			if(bestMergeCluster != i){ 
				/* merge clusters */
				v1.addAll(best_merge);
				
				this.bestScore += bestCostChange;
				
				/* remove the merged cluster */
				clusterObject.remove(best_merge);
				
				/* recursive call of the method to find other merges */
				mergeBest(clusterObject, alreadyCompared);
				break;
			}
		}		
	}
	
	/**
	 * Calculates the cost change for merging the two given clusters and returns this.
	 * @param cluster1 First cluster to be merged.
	 * @param cluster2 Second cluster to be merged.
	 * @return The cost change for merging the two clusters.
	 */
	private double calculateCostChange(Vector<Integer> cluster1, Vector<Integer> cluster2){
		double costChange = 0;
		
		for (int i = 0; i < cluster1.size(); i++) {
			int node_i = cluster1.get(i);
			for (int j = 0; j < cluster2.size(); j++) {
				int node_j = cluster2.get(j);
				double cost = cc.getCCEdges().getEdgeCost(node_i, node_j);
				costChange -=cost;
			}
		}	
		return costChange;
	}

	/**
	 * The current best score for post processing
	 * @return the bestScore
	 */
	protected double getBestScore() {
		return bestScore;
	}
}
