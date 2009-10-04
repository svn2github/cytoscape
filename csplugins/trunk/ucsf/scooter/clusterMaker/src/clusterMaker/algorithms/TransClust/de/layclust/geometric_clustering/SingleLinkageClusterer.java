package de.layclust.geometric_clustering;

import java.util.Vector;

import de.layclust.datastructure.ConnectedComponent;

public class SingleLinkageClusterer implements IGeometricClusterer{

	private double minDistance;
	
	private double maxDistance;
	
	private double stepsize;
	
	private double stepsizeFactor;
	
	private int noOfClusters;
	
	private double bestDistance;
	
	private ConnectedComponent cc;
	
	/**
	 * Creates instance of SingleLinkageClusterer with no parameters. This
	 * still needs to be initialised.
	 *
	 */
	public SingleLinkageClusterer(){}
	
//	/**
//	 * This constructor initializes the necessary parameters for the single linkage clustering. Parameters can be
//	 * changed in the config file
//	 * @param cc The connected component for which the clustering has to be made.
//	 */
//	public SingleLinkageClusterer(ConnectedComponent cc ) {
//		
//		this.cc = cc;
//		this.minDistance = GeometricClusteringConfig.minDistance;
//		this.maxDistance = GeometricClusteringConfig.maxDistance;
//		this.stepsize = GeometricClusteringConfig.stepsize;
//		this.stepsizeFactor = GeometricClusteringConfig.stepsizeFactor;
//		
//	}
	
	public void initGeometricClusterer(ConnectedComponent cc){
		this.cc = cc;
		this.minDistance = GeometricClusteringConfig.minDistance;
		this.maxDistance = GeometricClusteringConfig.maxDistance;
		this.stepsize = GeometricClusteringConfig.stepsize;
		this.stepsizeFactor = GeometricClusteringConfig.stepsizeFactor;
	}
	
	/**
	 * This method determines the best clustering obtained from geometric single linkage clustering
	 * within a range between minDistance and maxDistance
	 */
	public void run(){
		try{
			long time = System.currentTimeMillis();
		double bestScore = Double.MAX_VALUE;
		bestDistance = 0;
		int[] clusters = new int[this.cc.getNodeNumber()];
		double currentDistance = this.minDistance;
		
		Vector<Integer> putativeNeighbors[] = new Vector[clusters.length];
		Vector<Integer> putativeNeighbors_orig[] = new Vector[clusters.length];
		
		for (int i = 0; i < putativeNeighbors.length; i++) {
			putativeNeighbors[i] = new Vector<Integer>();
			putativeNeighbors_orig[i] = new Vector<Integer>();
		}
		
		for (int i = 0; i < clusters.length; i++) {	
			double[] pos_i = this.cc.getCCPostions(i);
			for (int j = i+1; j < clusters.length; j++) {
				double[] pos_j = this.cc.getCCPostions(j);
				double distance = calculateEuclidianDistance(pos_i, pos_j);
				if(distance<(this.maxDistance*this.maxDistance)){
					putativeNeighbors[i].add(j);
					putativeNeighbors[j].add(i);
					putativeNeighbors_orig[i].add(j);
					putativeNeighbors_orig[j].add(i);
				}
			}
		}
		
		Vector<Double> distances = new Vector<Double>();
		while(currentDistance<this.maxDistance){
			distances.add(currentDistance);
			currentDistance +=this.stepsize;
			this.stepsize+=(this.stepsizeFactor*this.stepsize);
		}
		distances.add(Double.MAX_VALUE);
		
		
		// start with minDistance and increase upto maxDistance. Save distance which produces minimal costs 
		for (int i = distances.size()-1; i >= 0; i--) {
			currentDistance = distances.get(i);
		
			double currentScore = 0;
			calculateClusters((currentDistance*currentDistance), clusters,putativeNeighbors);
			currentScore = this.cc.calculateClusteringScore(clusters);
			if(currentScore<=bestScore){
				bestScore = currentScore;
				bestDistance = currentDistance;
			}
		
		}
		this.noOfClusters = calculateClusters((bestDistance*bestDistance), clusters,putativeNeighbors_orig);
		this.cc.initialiseClusterInfo(this.noOfClusters);
		this.cc.setClusteringScore(bestScore);
		this.cc.setClusters(clusters);
		this.cc.calculateClusterDistribution();
		time = System.currentTimeMillis() - time;
//		System.out.println("Time for geometric clustering: "+TaskUtility.convertTime(time));
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * This method determines the best clustering obtained from geometric single linkage clustering
	 * for a fixed distance
	 * @param distance The maximal distance between two nodes to be assigned to one cluster
	 * @param clusters Array of integers, where position equals a proteinNumber and value is the assigned clusternumber
	 * @return number of clusters
	 */
	private int calculateClusters(double distance, int[] clusters, Vector<Integer>[] putativeNeighbors) throws Exception{
		
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
	
	
}