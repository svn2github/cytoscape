package clusterMaker.algorithms.FORCE;


import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import clusterMaker.algorithms.FORCE.ConnectedComponent;

public class SingleLinkageClusterer{

     // ---------------- CONFIG VARIABLES - SINGLE-LINKAGING ---------------- //

        public static double minDistance = 0.01;
        public static double maxDistance = 5;
        public static double stepsize = 0.01;
        public static double stepsizeFactor = 0.01;

	
	private int noOfClusters;
	
	private double bestDistance;
	
	private ConnectedComponent cc;
	
	
	/**
	 * This constructor initializes the necessary parameters for the single linkage clustering. Parameters can be
	 * changed in the config file
	 * @param cc The connected component for which the clustering has to be made.
	 */
	public SingleLinkageClusterer(ConnectedComponent cc, double singleLinkageDistance) {
		
		this.cc = cc;
		this.bestDistance = singleLinkageDistance;
	}
	
	/**
	 * Run Clustering using input best distance. Return Array mapping Node to Cluster.
	 * 
	 */
	public int[] run(){
		
			
	       
		 //bestDistance = 0;
		int[] clusters = new int[this.cc.getNodeNumber()];
		//double currentDistance = this.minDistance;
		
		Vector<Integer> putativeNeighbors[] = new Vector[clusters.length];
		//Vector<Integer> putativeNeighbors_orig[] = new Vector[clusters.length];
		
		for (int i = 0; i < putativeNeighbors.length; i++) {
			putativeNeighbors[i] = new Vector<Integer>();
			//putativeNeighbors_orig[i] = new Vector<Integer>();
		}
		
		for (int i = 0; i < clusters.length; i++) {	
			double[] pos_i = this.cc.getCCPostions(i);
			for (int j = i+1; j < clusters.length; j++) {
				double[] pos_j = this.cc.getCCPostions(j);
				double distance = calculateEuclidianDistance(pos_i, pos_j);
				if(distance<(this.maxDistance*this.maxDistance)){
					putativeNeighbors[i].add(j);
					putativeNeighbors[j].add(i);
					//putativeNeighbors_orig[i].add(j);
					//putativeNeighbors_orig[j].add(i);
				}
			}
		}
		

		this.noOfClusters = calculateClusters((bestDistance*bestDistance), clusters,putativeNeighbors);
		return clusters;

	
	}
	
	/**
	 * This method determines the best clustering obtained from geometric single linkage clustering
	 * for a fixed distance
	 * @param distance The maximal distance between two nodes to be assigned to one cluster
	 * @param clusters Array of integers, where position equals a proteinNumber and value is the assigned clusternumber
	 * @return number of clusters
	 */
	private int calculateClusters(double distance, int[] clusters, Vector<Integer>[] putativeNeighbors){
		
		int clusterNumber = 0;

		boolean[] remaining2 = new boolean[clusters.length];
		
		
		for (int i = 0; i < remaining2.length; i++) {
			if(remaining2[i]) continue;
			recursiveClusterCalculate(i, remaining2, clusterNumber, clusters, distance,putativeNeighbors);
			clusterNumber++;
		}
		return clusterNumber;
	}
	
	
	private void recursiveClusterCalculate(int seed, boolean[] remaining2, int currentClusterNumber, int[] clusters, double distance, Vector<Integer>[] putativeNeighbors) {
		
		if(remaining2[seed]) return;
		
		clusters[seed] = currentClusterNumber;
		
		remaining2[seed] = true;
		
		double[] pos_seed = this.cc.getCCPostions(seed);
		
		Vector<Integer> v = putativeNeighbors[seed];
		
		for (int i = 0; i < v.size(); i++) {
			int node = v.get(i);
			if(remaining2[node]) continue;
			
			double[] pos_node = this.cc.getCCPostions(node);
			
			boolean isSmaller = calculateIfEuclidianDistanceIsSmaller(pos_seed, pos_node,distance);
			
			if(isSmaller){
			
				recursiveClusterCalculate(node, remaining2, currentClusterNumber, clusters, distance, putativeNeighbors);
				
			}else{
				v.remove(i);
				putativeNeighbors[i].removeElement(seed);
				i--;
			}		
		}
	}


	private boolean calculateIfEuclidianDistanceIsSmaller(double[] node1, double[] node2, double distance) {
		double euclidianDistance = 0;
		
		for (int i = 0; i < node1.length; i++) {
			euclidianDistance += ((node1[i]-node2[i])*(node1[i]-node2[i])); 	
			if(euclidianDistance>distance) return false;
		}
		
		return true;
	}

	/**
	 * This method simply calculates the euclidian distance of two arrays. 
	 * @param node1 positionarray of first node
	 * @param node2 positionarray of second node
	 */
	private double calculateEuclidianDistance(double[] node1, double[] node2){
				
		double euclidianDistance = 0;
		
		for (int i = 0; i < node1.length; i++) {
			euclidianDistance += ((node1[i]-node2[i])*(node1[i]-node2[i])); 		
		}
//		euclidianDistance = Math.sqrt(euclidianDistance);
		
		return euclidianDistance;
	}

	public double getBestDistance() {
		return bestDistance;
	}

	public void setBestDistance(double bestDistance) {
		this.bestDistance = bestDistance;
	}

    public int get_clust_num(){
	return noOfClusters;
    }
	
	
}
