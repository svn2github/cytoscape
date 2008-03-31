package de.layclust.layout.postprocessing;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.taskmanaging.TaskUtility;

/**
 * TODO: can result in clusters of size 0!
 * @author Tobias Wittkop
 *
 */
public class PostProcessingTobi implements IPostProcessing{

	ConnectedComponent cc;
	double bestScore;
	
	public void initPostProcessing(ConnectedComponent cc) {
		this.cc = cc;	
		this.bestScore = cc.getClusteringScore();
	}
	
	
	public  void run(){
		long time = System.currentTimeMillis();
		
		int[] clusters = this.cc.getClusters();
		
		Vector< Vector<Integer>> h = new Vector< Vector<Integer>> ();
		
		for (int i = 0; i < this.cc.getNumberOfClusters(); i++) {
			Vector<Integer> v = new Vector<Integer>();
			h.add(v);
		}
		
		for (int i = 0; i < clusters.length; i++) {
			h.get(clusters[i]).add(i);
		}
		
//		int[] test = clusters.clone();
		int[] test = new int[clusters.length];
		for (int i = 0; i < test.length; i++) {
			test[i]=clusters[i];
		}
		
		System.out.println("score_before = " + bestScore);
		
		mergeCluster(h, test, new Hashtable<String, Boolean>());
		
		System.out.println("score_merge = " + bestScore);
		
		rearrange(test,h);
		
		this.cc.initialiseClusterInfo(h.size());
		this.cc.setClusteringScore(bestScore);
		this.cc.setClusters(test);
		this.cc.calculateClusterDistribution();
		
		System.out.println("score_rearrange = " + bestScore);
		
		time = System.currentTimeMillis() - time;
		System.out.println("Time for post processing: "+TaskUtility.convertTime(time));
		
	}

	private void rearrange(int[] test, Vector<Vector<Integer>> h) {
		
		
		boolean isbreak = false;
		for (int i = 0; i < test.length; i++) {
			
			int cluster = test[i];
			for (int j = 0; j <h.size(); j++) {
				
				if(j!=cluster){
					test[i]=j;
					double score = this.bestScore + calculateCostChange(i, h.get(cluster), h.get(j));
//					double score2 = cc.calculateClusteringScore(test);
					
//					if(score!=score2){
//						System.out.println("#####################  " + score + "\t" + score2 );
//						System.out.println(h.get(cluster).contains(i));
//					}
					
					if(score<this.bestScore){
//						System.out.println("test " + bestScore);
						h.get(cluster).removeElement(i);
						h.get(j).add(i);
						this.bestScore = score;
						cluster = j;
//						rearrange(test, h);
//						isbreak = true;
//						break;
					}else{
						test[i]=cluster;
					}
				}	
			}
			if(isbreak) break;
		}
		
	}
	
	private double calculateCostChange(int i, Vector<Integer> oldCluster, Vector<Integer> newCluster){
		double costChange = 0;
		
		for (int j = 0; j < oldCluster.size(); j++) {
			int k = oldCluster.get(j);
			if(k!=i){
				double cost = cc.getCCEdges().getEdgeCost(i, k);
				costChange += cost;
			}
		}
		
		for (int j = 0; j < newCluster.size(); j++) {
			int k = newCluster.get(j);
			
			double cost = cc.getCCEdges().getEdgeCost(i, k);
			costChange -= cost;
		
		}
		
		return costChange;
	}
	
	
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
	

	private void mergeCluster(Vector<Vector<Integer>> h, int[] test, Hashtable<String,Boolean> already) {
		
//		System.out.println("test" + bestScore);
		boolean isbreak = false;
		
		for (int i = 0; i < h.size(); i++) {
			Vector<Integer> v1 = h.get(i);
			for (int j = i+1; j < h.size(); j++) {
				Vector<Integer> v2 = h.get(j);
				if(!already.containsKey(v1.toString()+"#"+ v2.toString())){
					already.put(v1.toString()+"#"+ v2.toString(), true);
				
					for (int k = 0; k < v2.size(); k++) {
						test[v2.get(k)] = i;
					}
					double score = bestScore + calculateCostChange(v1, v2);
					if(score<bestScore){
						bestScore = score;
						v1.addAll(v2);
						h.remove(j);
						mergeCluster(h, test,already);
						isbreak = true;
						break;
					}else{
						for (int k = 0; k < v2.size(); k++) {
							test[v2.get(k)] = j;
						}
					}
				}
			}
			if(isbreak) break;
		}
		
	}
	
}
