package de.layclust.layout.postprocessing;

import java.util.HashSet;
import java.util.Vector;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.taskmanaging.TaskUtility;

public class PP_ImprovedRearrangeAndMerge implements IPostProcessing {
	
	private ConnectedComponent cc = null;
	private double bestScore = 0;
	



	public void initPostProcessing(ConnectedComponent cc) {
		this.cc = cc;
		this.bestScore = cc.getClusteringScore();

	}

	public void run() {
		long time = System.currentTimeMillis();
		
		/* initialise objects needed */
		Vector<Vector<Integer>> clusterObject = PostProcessingUtility.createClusterObject(this.cc, false);
		HashSet<String> alreadyCompared = new HashSet<String>();
		ClusterObjectComparator comparator = new ClusterObjectComparator();
		
		/* so long as the score is improved continue the post processing */
		System.out.println("Score before merging and rearrangement: "+this.bestScore);
		boolean improvedScore = true;
		while(improvedScore){
			PostProcessingUtility.rearrangeNodeToLevelTwo(clusterObject, this.cc);
			PostProcessingUtility.sortedMerge(clusterObject, comparator, alreadyCompared, this.cc, true);
			double newScore = PostProcessingUtility.updateClusterInfoInCC(clusterObject, this.cc);
			if(newScore<this.bestScore){
				this.bestScore = newScore;
				System.out.println("score improved");
			} else {
				improvedScore = false;
			}
		}
				
		System.out.println("Score after merging and rearrangement: "+this.bestScore);
		time = System.currentTimeMillis() - time;
		System.out.println("Time for PP_ImprovedRearrangeAndMerge: "+TaskUtility.convertTime(time));
	}
	
	
	/**
	 * @return the bestScore
	 */
	protected double getBestScore() {
		return bestScore;
	}

}
