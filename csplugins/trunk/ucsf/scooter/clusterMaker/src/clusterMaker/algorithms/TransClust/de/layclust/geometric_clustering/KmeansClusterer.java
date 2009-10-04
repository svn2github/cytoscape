package de.layclust.geometric_clustering;

import java.util.Hashtable;
import java.util.Random;

import de.layclust.datastructure.ConnectedComponent;

public class KmeansClusterer implements IGeometricClusterer {
	
	private ConnectedComponent cc;
	
	private int maxK;
	
	private int bestK;
	
	private double bestCosts;
	
	private int[] bestCluster;
	
	private double[] center;
	
	private double span;
	
	private int maxRuns;
	
	public KmeansClusterer(){}
	
	
	/**
	 * Initialises the object with a {@link ConnectedComponent} and 
	 * also the other global properties from the Config file.
	 */
	public void initGeometricClusterer(ConnectedComponent cc) {
		this.cc = cc;
		this.maxK = cc.getNodeNumber();
		if(this.maxK>GeometricClusteringConfig.kLimit){
			this.maxK = GeometricClusteringConfig.kLimit;
		}
		this.bestCosts = Double.MAX_VALUE;
		this.center = new double[cc.getCCPostions(0).length];
		this.span = calculateCenterAndSpan(cc.getCCPositions(), center);
		this.maxRuns = GeometricClusteringConfig.maxInitStartConfigs;
	}

	public void run() {
		
		int[] clusters = new int[this.cc.getNodeNumber()];
		
		for (int i = 1; i < maxK; i++) {
			
			kmeans(i,clusters);
			
			double costs = this.cc.calculateClusteringScore(clusters);
			
			double bestCostWithK = costs;
			
			int run = 0;
			
			
			// multiple runs for the same k.  best costs for fixed k are stored in bestCostWithK and overall best costs as usual in bestCosts
			while(run<this.maxRuns){
				
				if(costs<bestCosts){
					bestCosts = costs;
					bestK = i;
					bestCluster = copyClusters(clusters);
				}
				
				kmeans(i,clusters);
				costs = this.cc.calculateClusteringScore(clusters);
				if(costs<bestCostWithK){
					bestCostWithK = costs;
				}
				run++;
				
			}
			
			if(bestCostWithK>(bestCosts*bestCosts)) break;		
			
		}
		
		int numberOfCluster = bestK;
		this.cc.initialiseClusterInfo(numberOfCluster);
		this.cc.setClusteringScore(bestCosts);
		this.cc.setClusters(bestCluster);
		this.cc.calculateClusterDistribution();
		
	}
	
	private void kmeans(int k, int[] clusters){
		
		double nodePositions[][] = this.cc.getCCPositions();
		
		double seedPositions[][] = new double[k][nodePositions[0].length];
		
		int[] clustersOld = copyClusters(clusters);
		
		clustersOld[0] = -1;

		
		// different initialize methods
		
		initializeSeedpositions(seedPositions, nodePositions);
		
//		initializeSeedpositionsInCenter(seedPositions, nodePositions);
		
//		initializeRandomSeedpositions(seedPositions, nodePositions);
		
		while(!isClusterEqual(clusters, clustersOld)){
			
			clustersOld = copyClusters(clusters);
			
			calculateClusters(seedPositions, clusters, nodePositions);	
			
			calculateNewSeedPositions(seedPositions, clusters, nodePositions);
			
		}
		
	}
	
	private void initializeSeedpositions(double[][] seedPositions,double[][] nodePositions){
		
		Hashtable<Integer, Boolean> h = new Hashtable<Integer, Boolean>();
		
		h.put(-1, true);
		
		Random r = new Random();
		
		for (int i = 0; i < seedPositions.length; i++) {
			
			int number = -1;
			
			while(h.containsKey(number)){
				number = r.nextInt(nodePositions.length);
			}
			
			h.put(number, true);
			
			seedPositions[i] = this.cc.getCCPostions(number).clone();
					
		}
		
	}
	
	private void initializeSeedpositionsInCenter(double[][] seedPositions,double[][] nodePositions){
		
		Random r = new Random();
		
		for (int i = 0; i < seedPositions.length; i++) {
			
			for (int j = 0; j < seedPositions[i].length; j++) {
				
				double epsilon = r.nextDouble()/this.span;
				
				epsilon = r.nextDouble();
				
				boolean sigma = r.nextBoolean();
				
				if(sigma){
					
					seedPositions[i][j] = this.center[j] + epsilon; 
					
				}else{
					
					seedPositions[i][j] = this.center[j] - epsilon; 
					
				}	
				
			}
					
		}
		
	}
	
	
	private void initializeRandomSeedpositions(double[][] seedPositions,double[][] nodePositions){
		
		Random r = new Random();
		
		for (int i = 0; i < seedPositions.length; i++) {
			
			for (int j = 0; j < seedPositions[i].length; j++) {
				
				double epsilon = r.nextDouble()*(span/2);
				
				epsilon = r.nextDouble();
				
				boolean sigma = r.nextBoolean();
				
				if(sigma){
					
					seedPositions[i][j] = this.center[j] + epsilon; 
					
				}else{
					
					seedPositions[i][j] = this.center[j] - epsilon; 
					
				}	
				
			}
					
		}
		
	}
	
	
	private double calculateCenterAndSpan(double[][] nodePositions,double[] center) {
		
		double span = 0;
		
		double[] min = new double[nodePositions[0].length];
		
		double[] max = new double[nodePositions[0].length];
		
		for (int i = 0; i < max.length; i++) {
			
			min[i] = Double.POSITIVE_INFINITY;
			max[i] = Double.NEGATIVE_INFINITY;
			
		}
		
		
		for (int i = 0; i < nodePositions.length; i++) {
			
			double[] position = nodePositions[i];
			
			for (int j = 0; j < position.length; j++) {
				
				if(position[j]<min[j]) min[j] = position[j];
				
				if(position[j]>max[j]) max[j] = position[j];
				
			}
			
		}
		
		for (int i = 0; i < center.length; i++) {
			
			center[i] = (min[i] + max[i])/2;
			
		}
		
		span = Math.sqrt(calculateEuclidianDistance(min, max));
				
		return span;
		
	}

	private void calculateClusters(double[][] seedPositions, int[] clusters, double[][] nodePositions){
		
		for (int i = 0; i < clusters.length; i++) {
			
			double[] position = nodePositions[i];
			
			int bestSeed = -1;
			
			double bestDistance = Double.MAX_VALUE;
			
			for (int j = 0; j < seedPositions.length; j++) {
				
				double[] seedPosition = seedPositions[j];
				
				double distance = calculateEuclidianDistance(position, seedPosition);

				if(distance<bestDistance){

					bestDistance = distance;
					bestSeed = j;
					
				}
				
				
			}//end for seedPositions
			
			clusters[i] = bestSeed;
			
		}//end for clusters
		
	}
	
	private void calculateNewSeedPositions(double[][] seedPositions, int[] clusters,double[][] nodePositions){
		
		int[] clusterSizes = new int[seedPositions.length];		
		
		for (int i = 0; i < seedPositions.length; i++) {
			seedPositions[i] = new double[nodePositions[0].length];
			for (int j = 0; j < seedPositions[i].length; j++) {
				seedPositions[i][j]=0;
			}
		}
		
		
		for (int i = 0; i < clusters.length; i++) {
			
			double position[] = nodePositions[i];
			
			clusterSizes[clusters[i]]++;
			
			seedPositions[clusters[i]] = positionAdd( position,  seedPositions[clusters[i]]);
			
		}
		
		for (int i = 0; i < seedPositions.length; i++) {
			seedPositions[i] = dividePosition(seedPositions[i],clusterSizes[i]);
		}
		
		
	}
	

	
	private double[] dividePosition(double[] position, int divisor) {
		
		double[] resultingPosition = new double[position.length];
		
		for (int i = 0; i < resultingPosition.length; i++) {
			resultingPosition[i] = position[i]/((double) divisor);
		}
		
		return resultingPosition;
	}

	private double[] positionAdd(double[] position1, double[] position2) {
		
		double[] resultingPosition = new double[position1.length];
		
		for (int i = 0; i < resultingPosition.length; i++) {
			resultingPosition[i] = position1[i] + position2[i];
		}
		
		return resultingPosition;
	}

	private boolean isClusterEqual(int[] clusters1, int[] clusters2){
		
		for (int i = 0; i < clusters2.length; i++) {
			if(clusters1[i]!=clusters2[i]){
				return false;
			}
		}
		
		return true;
		
	}
	
	private int[] copyClusters(int[] clusters){
		
		int[] clustersCopy = new int[clusters.length];
		
		for (int i = 0; i < clustersCopy.length; i++) {
			clustersCopy[i] = clusters[i];
		}
		
		return clustersCopy;
		
	}
	
	private double calculateEuclidianDistance(double[] node1, double[] node2){
		
		double euclidianDistance = 0;
		
		for (int i = 0; i < node1.length; i++) {
			euclidianDistance += ((node1[i]-node2[i])*(node1[i]-node2[i])); 		
		}
		
		return euclidianDistance;
	}
}
