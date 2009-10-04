package de.layclust.greedy;

import java.util.Arrays;
import java.util.Vector;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.datastructure.ICCEdges;

public class GreedyClusterer {

	private ConnectedComponent cc;
	
	private int[] listOfElementsSortedByCosts;
	
	private boolean changed = false;	
	private ICCEdges icce;
	
	private int count;
	
	public GreedyClusterer(ConnectedComponent cc){
		
		this.cc = cc;
		this.listOfElementsSortedByCosts = new int[cc.getNodeNumber()];
		this.icce = cc.getCCEdges();
		this.count=cc.getNodeNumber();
		
		cluster();
	}
	
	
	private void cluster(){
		
		
		generateSortedList();
		

		double minCosts = Double.POSITIVE_INFINITY;
		int minInt = -1;
		
//		for (int i = 0; i < this.listOfElementsSortedByCosts.length; i++) {
//			
//			double costs = calculateCostsForClusterStartingWithSpecificNode(this.listOfElementsSortedByCosts[i],false);
//			
//			if(costs<minCosts){
//				minCosts=costs;
//				minInt = i;
//			}
//			
//		}
//		
//		calculateCostsForClusterStartingWithSpecificNode(this.listOfElementsSortedByCosts[minInt],true);
		calculateCostsForClusterStartingWithSpecificNode(this.listOfElementsSortedByCosts[0],true);
		
		
		
	}
	
	private double calculateCostsForClusterStartingWithSpecificNode(int node_i,
			boolean change) {
		
		this.count = this.cc.getNodeNumber();
		
		Vector<Vector<Integer>> clusters = new Vector<Vector<Integer>>();
		
		boolean already[] = new boolean[this.cc.getNodeNumber()];
		
		
		Vector<Integer> cluster = new Vector<Integer>();
		cluster.add(node_i);
		already[node_i] = true;
		count--;
		
		addNodesToClusterRecursivly(already,cluster);
		
//		removeWorsts(already, cluster);
		
		clusters.add(cluster);
		
		for (int i = 0; i < this.listOfElementsSortedByCosts.length; i++) {
			
			node_i = this.listOfElementsSortedByCosts[i];
			
			if(!already[node_i]){
				
				cluster = new Vector<Integer>();
				cluster.add(node_i);
				already[node_i] = true;
				count--;
				
				addNodesToClusterRecursivly(already,cluster);
				
//				removeWorsts(already, cluster);
				
				clusters.add(cluster);
			}
			if(i==this.listOfElementsSortedByCosts.length&&count>0) i =0;
			
		}
		
		
		int[] clusters2nodes = new int[this.cc.getNodeNumber()];
		
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster = clusters.get(i);
			
			for (int j = 0; j < cluster.size(); j++) {
				clusters2nodes[cluster.get(j)] = i;
			}
		}
		
		if(change){
			int noOfClusters = clusters.size();
			this.cc.initialiseClusterInfo(noOfClusters);
			this.cc.setClusteringScore(this.cc.calculateClusteringScore(clusters2nodes));
			this.cc.setClusters(clusters2nodes);
			this.cc.calculateClusterDistribution();
			return this.cc.getClusteringScore();
		}else{
			return this.cc.calculateClusteringScore(clusters2nodes);
		}
		
	}


	private void addNodesToClusterRecursivly(boolean[] already,
			Vector<Integer> cluster) {
		
		double minCosts = Double.MAX_VALUE;
		int minNode = -1;
		for (int i = 0; i < this.cc.getNodeNumber(); i++) {
			
			if(already[i]) continue;
			double costs = calculateCostsForAddingNode(cluster, i);
			
			if(costs<minCosts){
				minCosts = costs;
				minNode = i;
			}
			
		}
		if(minCosts>0){
			removeWorstRecursively(already,cluster);
			return;
		}
		
		count--;
		cluster.add(minNode);
		already[minNode] = true;
//		removeWorstRecursively(already,cluster);
		addNodesToClusterRecursivly(already, cluster);
	}


	private void removeWorstRecursively(boolean[] already,
			Vector<Integer> cluster) {

		double minCost = Double.POSITIVE_INFINITY;
		int minInt = -1;
		
		for (int i = 0; i < cluster.size(); i++) {
			
			double costs = calculateCostsForRemovingNode(cluster, cluster.get(i));
			
			if(costs<minCost){
				minCost=costs;
				minInt = i;
			}
			
		}
		if(minCost<0){
			count++;
			already[cluster.get(minInt)]=false;
			cluster.remove(minInt);
			this.changed = true;
			removeWorstRecursively(already, cluster);
			this.changed=false;
		}
		if(changed){
			changed=false;
			addNodesToClusterRecursivly(already, cluster);
		}
//		removeWorstTwoRecursively(already, cluster);
	
		return;
		
	}
	
	
	private void removeWorsts(boolean[] already, Vector<Integer> cluster){
		
		double minCost = Double.POSITIVE_INFINITY;
		int minInt = -1;
		double[] worst = new double[cluster.size()];
		
		
		for (int i = 0; i < cluster.size(); i++) {
			
			double costs = calculateCostsForRemovingNode(cluster, cluster.get(i));
			worst[i] = costs;
			
			if(costs<minCost){
				minCost=costs;
				minInt = i;
			}
			
		}
		
		double[] worstCopy = new double[worst.length];
		System.arraycopy(worst, 0, worstCopy, 0, worst.length);
		
		Arrays.sort(worst);
		
		int[] worstElements = new int[worst.length];
		
		boolean[] already2 = new boolean[worst.length];
		for (int i = 0; i <worst.length; i++) {
			
			int position = 0;
			for (int j = 0; j < worstCopy.length; j++) {
				if(worst[i]!=worstCopy[j]||already2[j]) continue;
				
				position = j;
				already2[j] = true;
				break;
			}
			
			worstElements[worst.length-1-i] = cluster.get(position);
			
		}
		
//		System.out.println(Arrays.toString(worstElements));
//		System.out.println(Arrays.toString(worst));
		
		
		minCost = Double.POSITIVE_INFINITY;
		int minI = -1;
		double border = (double)(((double) worstElements.length) / ((double) 1));
		for (int i = 0; i < border; i++) {
			
			double costs = 0;
			
			
			for (int j = 0; j < i; j++) {
				
				costs+=calculateCostsForRemovingNode(cluster, worstElements[j]);
				
				for (int j2 = 0; j2 < i; j2++) {
					if(j==j2) continue;
					costs-= this.icce.getEdgeCost(worstElements[j], worstElements[j2]);
				}
				
				
			}
			
			if(costs<minCost){
				minCost = costs;
				minI = i; 
			}
			
		}
		
		if(minCost<0){
			
			for (int i = 0; i < minI; i++) {
				cluster.removeElement(worstElements[i]);
				already[worstElements[i]] = false;
				count++;
			}
			
			System.out.println(minCost);
			System.out.println(this.cc.getNodeNumber());
			System.out.println(minI);
			System.out.println();
		}
		
		
	}
	
	
	
	private void removeWorstTwoRecursively(boolean[] already, Vector<Integer> cluster){
		
		double minCost = Double.POSITIVE_INFINITY;
		int minInt = -1;
		int minInt2 = -1;
		
		for (int i = 0; i < cluster.size(); i++) {
			
			int node_i = cluster.get(i);
			
			for (int j = i; j < cluster.size(); j++) {
				
				int node_j = cluster.get(j);
				
				double costs = calculateCostsForRemovingNode(cluster, node_i)+ calculateCostsForRemovingNode(cluster, node_j)-(2*this.icce.getEdgeCost(node_i, node_j));
				
				if(costs<minCost){
					minCost=costs;
					minInt = node_i;
					minInt2 = node_j;
				}
			}
		}
		
//		System.out.println("minCost2: " + minCost);
		if(minCost<0){
			already[minInt]=false;
			already[minInt2] = false;
			count++;
			count++;
			cluster.removeElement(minInt);
			cluster.removeElement(minInt2);
			this.changed = true;
			removeWorstTwoRecursively(already, cluster);
			this.changed=false;
		}
		if(changed){
//			changed=false;
			removeWorstRecursively(already, cluster);
//			addNodesToClusterRecursivly(already, cluster);
		}
		
		
		return;
		
	}
	
	
	private void removeWorstRecursively(boolean[] already, Vector<Integer> cluster, int number){
		
		System.out.println("number \t" + number);
		
		System.out.println("clusterSize \t " + cluster.size());
		
		double minCost = Double.POSITIVE_INFINITY;
		Vector<Integer> minConstellation = new Vector<Integer>();
		
		Vector<Vector<Integer>> possibilities = new Vector<Vector<Integer>>();
		
		
		
		fillPossibilities(possibilities,new Vector<Integer>(),cluster.size(),number,number,-1);
		
		System.out.println("possibiliesSize \t" + possibilities.size());
		System.out.println("nChoosek \t" + nChooseK(cluster.size(), number));
		System.out.println();
		
		for (int i = 0; i < possibilities.size(); i++) {
			Vector<Integer> constellation = possibilities.get(i);
			
			double costs = 0;
			
			for (int j = 0; j < constellation.size(); j++) {
				
				costs += calculateCostsForRemovingNode(cluster, cluster.get(j));
				
				for (int j2 = 0; j2 < constellation.size(); j2++) {
					
					if(j==j2) continue;
			
					costs-= icce.getEdgeCost(cluster.get(j), cluster.get(j2));
					
				}
				
			}
			
			if(costs<minCost){
				minCost=costs;
				minConstellation = constellation;
			}
			
		}
		
		if(minCost<0){
			
			for (int i = 0; i < minConstellation.size(); i++) {
				already[minConstellation.get(i)] = false;
				cluster.removeElement(minConstellation.get(i));
				this.changed = true;
				removeWorstRecursively(already, cluster,number);
				this.changed=false;
			}
			
		}else if(number!=2){
			removeWorstRecursively(already, cluster,number-1);
		}
		
		
		
	}


	private void fillPossibilities(Vector<Vector<Integer>> posibilities,Vector<Integer> current,int clusterSize, int number,int maxNumber,int currentInt) {
		
		if(number==0){
			posibilities.add((Vector<Integer>) current.clone());
			return;
		}
		
		for (int i = currentInt+1; i < clusterSize; i++) {
			
			current.add(i);
			fillPossibilities(posibilities, current, clusterSize, number-1, maxNumber, i);
			current.removeElement(i);
			
		}
		
	}


	private int nChooseK(int n, int k){
	
		int nChooseK = n;
		int kFak = 1;
	
		for (int i = 1; i < k; i++) {
			nChooseK *= (nChooseK-i);
			kFak*=i;
		}
		kFak*=k;
		
		nChooseK /= kFak; 
		
		return nChooseK;
	}
	
	private double calculateCostsForRemovingNode(Vector<Integer> cluster, int j) {
		
		double costs = 0;
		
		for (int i = 0; i < cluster.size(); i++) {
			
			int node_i = cluster.get(i);
			
			if(node_i==j)continue;
						
			costs += this.icce.getEdgeCost(node_i, j);
			
		}
		return costs;
	}	
	
	private double calculateCostsForAddingNode(Vector<Integer> clusters, int j) {
		
		double costs = 0;
		
		for (int i = 0; i < clusters.size(); i++) {
			
			int node_i = clusters.get(i);
						
			costs-= this.icce.getEdgeCost(node_i, j);
			
		}
		
	
		
		return costs;
	}


	private void generateSortedList(){
		
		ICCEdges icce = this.cc.getCCEdges();
		
		double costs[] = new double[cc.getNodeNumber()];
		
		for (int i = 0; i < cc.getNodeNumber(); i++) {
			
			double cost = 0;
			
			for (int j = 0; j < cc.getNodeNumber(); j++) {
				
				if(i==j) continue;
				
				cost+= icce.getEdgeCost(i, j);
				
				
			}
			costs[i] = cost;
	
		}
		
		double[] costsClone = new double[costs.length];
		System.arraycopy(costs, 0, costsClone, 0, costs.length);
		
		Arrays.sort(costs);
		
		boolean[] already = new boolean[costs.length];
		for (int i = costs.length-1; i >= 0; i--) {
			
			int position = 0;
			for (int j = 0; j < costsClone.length; j++) {
				if(costs[i]!=costsClone[j]||already[j]) continue;
				
				position = j;
				already[j] = true;
				break;
			}
			
			this.listOfElementsSortedByCosts[costs.length-1-i] = position;
			
		}
		
	}
	
}
