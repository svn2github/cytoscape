package de.layclust.fixedparameterclustering;

import java.util.Arrays;

public class FixedParameterTreeNode {
	
	
	private float [][] edgeCosts;
	private boolean[][] clusters;
	private float costs;
	public int size;
	
	public FixedParameterTreeNode(int size, float costs, int elementNumber){
		
		this.size = size;
		this.edgeCosts = new float[size][size];
		this.clusters = new boolean[size][elementNumber];
		this.costs = costs;

	}

	public float[][] getEdgeCosts() {
		return edgeCosts;
	}

	public void setEdgeCosts(float[][] edgeCosts) {
		this.edgeCosts = edgeCosts;
	}

	public float getCosts() {
		return costs;
	}

	public void setCosts(float costs) {
		this.costs = costs;
	}


	public boolean[][] getClusters() {
		return clusters;
	}

	public void setClusters(boolean[][] clusters) {
		this.clusters = clusters;
	}
	
	public FixedParameterTreeNode copy(){
		FixedParameterTreeNode fptn = new FixedParameterTreeNode(this.size,this.costs,this.getClusters()[0].length);
		
		for (int i = 0; i < this.size; i++) {
			System.arraycopy(this.edgeCosts[i], 0, fptn.getEdgeCosts()[i], 0, this.size);
			System.arraycopy(this.getClusters()[i], 0, fptn.getClusters()[i], 0, this.getClusters()[i].length);
		}
		return fptn;
	}

}
