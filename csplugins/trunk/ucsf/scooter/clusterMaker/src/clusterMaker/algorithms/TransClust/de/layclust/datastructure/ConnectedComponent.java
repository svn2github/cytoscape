/*
 * Created on 25. September 2007
 * 
 */

package de.layclust.datastructure;

import java.util.Vector;

import de.layclust.layout.LayoutFactory;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;

/**
 * This class describes a connected component of a graph.
 * 
 * @author Sita Lange
 */
public class ConnectedComponent {

	/* edges of the graph with weights */
	private ICCEdges ccEdges = null;

	/* positions of the vertices; not initialised at instance creation */
	private double[][] ccPositions = null;

	/*
	 * IDs of the objects in the correct order according to the starting
	 * allocation
	 */
	private String[] objectIDs = null;

	/* total number of nodes in the component */
	private int node_no = -1;

	/* cluster number for object in the same order as in objectIDs and is initialised at instance creation*/
	private int[] clusters = null;

	/* score for the clustering done on this connected component */
	private double clusteringScore = -1;

	/* total number of clusters for this connected component */
	private int numberOfClusters = -1;

	/*
	 * number of objects in each cluster, which have the same value as in the
	 * clusters array and needs to be initialised and filled after clustering process
	 */
	private int[] clusterDistribution = null;
	
	/* path to the cost matrix of this ConnectedComponent instance */
	private String ccPath = "";
	
//	/* boolean for reduced matrices */
//	private boolean isReduced = false;
	
	/* the costs that were accumulated during the reduction process.
	 * this needs to be added to the clustering score at the end! 
	 */
	private double reductionCost = 0.0;

	public ConnectedComponent(ICCEdges ccEdges, String[] object_ids, 
			String ccPath) {

		this.ccEdges = ccEdges;
		this.objectIDs = object_ids;
		this.node_no = object_ids.length;
		this.clusters = new int[node_no];
		this.ccPath = ccPath;

	}

	/**
	 * This class checks whether two positions are equal. NOTE both input arrays
	 * must be of equal size for this method to work correctly, but in the
	 * context of this program they always will be. This method only takes long
	 * if the positions are equal, as soon as one axis-position is unequal it
	 * returns false.
	 * 
	 * @param pos_a
	 *            First position in a double array.
	 * @param pos_b
	 *            Second position in a double array.
	 * @return boolean if it is equal or not.
	 */
	public boolean isPositionEqual(double[] pos_a, double[] pos_b) {
		// if(pos_a.length != pos_b.length) return false;
		for (int i = 0; i < pos_a.length; i++) {
			if (pos_a[i] != pos_b[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the ICCEdges object where the edge costs are saved in.
	 * 
	 * @return The object with the edge costs.
	 */
	public ICCEdges getCCEdges() {
		return ccEdges;
	}

	/**
	 * Sets the ICCEdges object where the edge costs are saved in.
	 * 
	 * @param ccEdges
	 *            The object with the edge costs.
	 */
	public void setCCEdges(ICCEdges ccEdges) {
		this.ccEdges = ccEdges;
	}

	/**
	 * Gets the array with the node positions, which is a 2-dimensional double
	 * array where each row i represents the position for node i. The size of
	 * the array is no. of nodes x dimension.
	 */
	public double[][] getCCPositions() {
		return ccPositions;
	}

	/**
	 * Gets the  node position of node i. This will be returned as double array where the size is the dimension
	 * @param i Node i
	 * @return double array position
	 */
	public double[] getCCPostions(int i){
		return ccPositions[i];
	}
	
	/**
	 * Sets the array with the node positions, which is a 2-dimensional double
	 * array where each row i represents the position for node i. The size of
	 * the array is no. of nodes x dimension.
	 * 
	 * @param ccPositions
	 *            The array with the node positions.
	 */
	public void setCCPositions(double[][] ccPositions) {
		this.ccPositions = ccPositions;
	}

	/**
	 * Gets the array where the proper object IDs are saved in. The ordering in
	 * the array is equal to the integers that are used throughout the program
	 * to address a certain object/node.
	 * 
	 * @return The array where the proper object IDs are saved in.
	 */
	public String[] getObjectIDs() {
		return objectIDs;
	}

	/**
	 * Sets the array where the proper object IDs are saved in. The ordering in
	 * the array is equal to the integers that are used throughout the program
	 * to address a certain object/node.
	 * 
	 * @param objectIDs
	 *            The array where the proper object IDs are saved in.
	 */
	public void setObjectIDs(String[] objectIDs) {
		this.objectIDs = objectIDs;
	}

	/**
	 * Gets the number of nodes for the connected component.
	 * 
	 * @return The number of nodes for the connected component.
	 */
	public int getNodeNumber() {
		return node_no;
	}

	/**
	 * Sets the number of nodes for the connected component.
	 * 
	 * @param node_no
	 *            The number of nodes for the connected component.
	 */
	public void setNodeNumber(int node_no) {
		this.node_no = node_no;
	}
	
	/**
	 * Initialises the clusterDistribution int[] with the size of the number of clusters for this
	 * component. Also it sets the numberOfClusters class variable.
	 * @param noOfClusters The number of clusters for this instance.
	 */
	public void initialiseClusterInfo(int noOfClusters){
		this.clusterDistribution = new int[noOfClusters];
//		setNumberOfClusters(noOfClusters);
		this.numberOfClusters = noOfClusters;
	}
	
	/**
	 * sets the clusterDistribution int[] with the size for each cluster for this
	 * component. 
	 * @param clusterDistribution The distribution of clustersizes.
	 */
	public void setClusterInfo(int[] clusterDistribution){
		this.clusterDistribution = clusterDistribution;
	}
	
	/**
	 * gets the clusterDistribution int[] with the size of each cluster for this
	 * component. 
	 * @return clusterDistribution The distribution of clustersizes.
	 */
	public int[] getClusterInfo(){
		return this.clusterDistribution;
	}
	
	/**
	 * calculates the clusterDistribution int[] with the size of the number of clusters for this
	 * component. This method should only be used after clustering.
	 * 
	 */
	public void calculateClusterDistribution(){
		for (int i = 0; i < this.clusters.length; i++) {
			this.clusterDistribution[this.clusters[i]]++;
		}
	}
	
	/**
	 * Sets the clusters. The array clusters consists of the clusternumbers (between 0 and noOfCluster-1). 
	 * @param clusters array of clusters where position is the proteinnumber and the value the clusternumber
	 */
	public void setClusters(int[] clusters){
		this.clusters = clusters;
	}
	
	/**
	 * gets the clusters. The array clusters consists of the clusternumbers (between 0 and noOfCluster-1). 
	 * @return clusters array of clusters where position is the proteinnumber and the value the clusternumber
	 */
	public int[] getClusters(){
		return this.clusters;
	}

	/**
	 * Gets the number objects in the given cluster. 
	 * This method should only be used after the clustering has been performed. If
	 * this is not the case, -1 is returned.
	 * 
	 * @param cluster_no
	 *            The number of the cluster compliant with the numbering within
	 *            the connected component object.
	 * @return The number of objects in the given cluster.
	 */
	public int getClusterMagnitude(int cluster_no) {
		try {
			return clusterDistribution[cluster_no];
		} catch (NullPointerException ex) {
			// TODO print out in log file or handle this exception
			System.err
					.println("ERROR: Either the clusterDistribution variable in the"
							+ "ConnectedComponent object hasn't been initialised, or it doesn't"
							+ "contain the cluster number "
							+ cluster_no
							+ ". == "
							+ ex.getMessage());
			return -1;
		}
	}

	/**
	 * Sets the number of objects in the given cluster. 
	 * This method should only be used after the clustering has been performed.
	 * @param cluster_no The number of the cluster compliant with this instance.
	 * @param magnitude The number of objects in this cluster.
	 */
	public void setClusterMagnitude(int cluster_no, int magnitude) {
		try{
			this.clusterDistribution[cluster_no] = magnitude;
		} catch (NullPointerException ex){
			// TODO print out in log file or handle this exception
			System.err
					.println("ERROR: Either the clusterDistribution variable in the"
							+ "ConnectedComponent object hasn't been initialised, or it doesn't"
							+ "contain this cluster number "
							+ cluster_no
							+ ". == "
							+ ex.getMessage());
			
		}
	}
	

	/**
	 * Gets the cluster number to which the given object number (compliant with this instance) was assigned to.
	 * This method should only be used after the clustering has been performed.
	 * @param object_no The object number compliant with this instance.
	 * @return The cluster number to which this object was assigned to.
	 */
	public int getClusterNoForObject(int object_no) {
			return clusters[object_no];		
	}

	/**\
	 * Sets the cluster number to which the given object number (compliant with this instance) was assigned to.
	 * This method should only be used after the clustering has been performed.
	 * @param object_no The object number compliant with this instance.
	 * @param cluster_no The cluster number to which this object was assigned to.
	 */
	public void setClusterNoForObject(int object_no, int cluster_no) {
			this.clusters[object_no] = cluster_no;
	}

	/**
	 * Gets the total number of clusters for this instance.
	 * This method should only be used after the clustering has been performed.
	 * @return The number of clusters for this instance.
	 */
	public int getNumberOfClusters() {
		return numberOfClusters;
	}


	/**
	 * Gets the score for the clustering performed on this instance.
	 * This method should only be used after the clustering has been performed.
	 * @return The score for the clustering done on this instance.
	 */
	public double getClusteringScore() {
		return clusteringScore;
	}

	/**
	 * Sets the score for the clustering performed on this instance.
	 * This method should only be used after the clustering has been performed.
	 * @param score The score for the clustering done on this instance.
	 */
	public void setClusteringScore(double score) {
		this.clusteringScore = score;
	}
	
	/**
	 * Calculate the score for the clustering performed on this instance.
	 * This method should only be used after the clustering has been performed.
	 * @return score The score for the clustering done on this instance.
	 * @param clusters The clustering obtained e.g. from singleLinkageClustering
	 */
	public double calculateClusteringScore(int[] clusters) {
		double score = 0;
		
		for (int i = 0; i < clusters.length; i++) {
			for (int j = 0; j < i; j++) {
				double edgeCost = this.ccEdges.getEdgeCost(i, j);
				boolean sameCluster = (clusters[i]==clusters[j]);
				if(!sameCluster&&edgeCost>0){   //node_i and node_j are not in the same cluster but there exists an edge between them
					score+=edgeCost;
				}else if(sameCluster&&edgeCost<0){  //node_i and node_j are  in the same cluster but there exists no edge between them
					score-=edgeCost;
				}
			}		
		}
		
		return score;
	}
	
	public void printClusters(){
		System.out.println("Clusters:");
		for (int i = 0; i < clusters.length; i++) {
			System.out.println("Item "+i+" is in Cluster "+clusters[i]);
		}
	}
	
//	//TODO just for testing
//	public void printPositions(){
//		StringBuffer posBuff = new StringBuffer();
//		posBuff.append("\n==== CURRENT POSITIONS ====\n");
//		for(int n=0;n<getNodeNumber();n++){
//			posBuff.append("node ");
//			posBuff.append(n);
//			posBuff.append(": (");
//			double[] posn = getCCPostions(n);
//			for(int i=0;i<posn.length-1;i++){
//				posBuff.append(posn[i]);
//				posBuff.append(", ");
//			}
//			posBuff.append(posn[posn.length-1]);
//			posBuff.append(")\n");			
//		}
//		posBuff.append("====================\n");
//		
//		System.out.println(posBuff.toString());
//	}

	/**
	 * @return the ccPath
	 */
	public String getCcPath() {
		return ccPath;
	}
	
	/**
	 * Copies the connected component with only the information which it had
	 * at the initialisation. Since these properties stay the same, they share
	 * the same storage. The other properties are set to null, so the positions
	 * array still needs to be initialised!!
	 * @return A copy of this ConnectedComponent object.
	 */
	public ConnectedComponent copy(){
		ConnectedComponent newCC = new ConnectedComponent(this.ccEdges,
				this.objectIDs, this.ccPath);
		
		return newCC;
	}
	
	public double[][] copyCCPositions(){
		int dim = TaskConfig.dimension;
		double[][] copiedPos = new double[this.node_no][dim];
		for(int i=0;i<this.node_no;i++){
			for(int j=0;j<dim;j++){
				copiedPos[i][j] = this.ccPositions[i][j];
			}
		}
		
		return copiedPos;
	}
	
	/**
	 * Returns the id of an object.
	 * @param num
	 * @return the id for the specified number
	 */
	public String getObjectID(int num) {
		return objectIDs[num];
	}
	
//	/**
//	 * Returns the boolean if this ConnectedComponent object is reduced
//	 * or not. A reduced matrix has merged all objects into one node that share
//	 * a similarity above a given threshold. This means the object name can consist
//	 * of several single names that are tab delimited.
//	 * @return boolean If the ConnectedComponent is reduced or not.
//	 */
//	public boolean isReduced(){
//		return isReduced;
//	}
//	
//	/** 
//	 * Sets the isReduced tag to the given boolean value.
//	 * @param isReduced If the matrix has been reduced or not.
//	 */
//	public void setIsReducedTag(boolean isReduced){
//		this.isReduced = isReduced;
//	}
	
	/**
	 * This method is to take the given cluster number and create a new ConnectedComponent object
	 * for the nodes of this cluster. This instance shares the {@link ICCEdges} object to save space!
	 * Instead of containing the actual object IDs as a string array, the original node numbers
	 * are saved in objectIDs.
	 * @param clusterNo This is the number of the cluster.
	 * @param intsInCluster The integer values that occur in the cluster.
	 * @return The ConnectedComponent object for the given cluster.
	 */
	public ConnectedComponent createConnectedComponentForCluster(int clusterNo, Vector<Integer> intsInCluster) {
		
		int size = intsInCluster.size();
		
		/* instead of the object IDs, the original node numbers are saved here as strings 
		 *	needed for end clustering */
		String[] subIDs = new String[size];
		for (int i = 0; i < size; i++) {
			subIDs[i] = intsInCluster.get(i).toString();
		}
		
		try {
			ICCEdges subEdges = LayoutFactory.getCCEdgesEnumByClass(TaskConfig.ccEdgesClass).createCCEdges(size);
			for (int i = 0; i < subIDs.length; i++) {
				for (int j = 0; j < i; j++) {
					subEdges.setEdgeCost(i, j, this.ccEdges.getEdgeCost(Integer.parseInt(subIDs[i]), Integer.parseInt(subIDs[j])));
				}			
			}

		ConnectedComponent subCC = new ConnectedComponent(subEdges, subIDs, this.ccPath); 
		
		return subCC;
		} catch (InvalidTypeException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * Gets the cost that accured when merging nodes above the user-given upper bound. 
	 * @return the reductionCost
	 */
	public double getReductionCost() {
		return reductionCost;
	}

	/**
	 * Gets the cost that accured when merging nodes above the user-given upper bound. 
	 * @param reductionCost the reductionCost to set
	 */
	public void setReductionCost(double reductionCost) {
		this.reductionCost = reductionCost;
	}

}