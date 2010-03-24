package clusterMaker.algorithms.AP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

import clusterMaker.algorithms.ClusterStatistics;
import clusterMaker.algorithms.DistanceMatrix;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class RunAP {

	private double lambda; /*lambda value from 0 to 1 dampens messages passed to avoid numberical oscillation*/
	private double pref; //preference parameter determines cluster density. Larger Parameter equals more Clusters. If < 0, automatically set to avg edge_weight threshold
	private int number_iterations; //number of inflation/expansion cycles

	//private double clusteringThresh; Threshold used to remove weak edges between distinct clusters
	//private double maxResidual; The maximum residual to look for

	private List<CyNode> nodes;
	private List<CyEdge> edges;
	private String nodeClusterAttributeName;
	private boolean canceled = false;
	private CyLogger logger;
	public final static String GROUP_ATTRIBUTE = "__APGroups";
	protected int clusterCount = 0;
	private boolean debug = false;
	private boolean createMetaNodes = false;
	private DistanceMatrix distanceMatrix = null;
	private ResponsibilityMatrix r_matrix = null;
	private AvailabilityMatrix a_matrix = null;
	private DoubleMatrix2D matrix = null;
	private DoubleMatrix1D pref_vector = null;

	public RunAP(String nodeClusterAttributeName, DistanceMatrix dMat,
	              double lambdaParameter,double preferenceParameter, int num_iterations, 
	              CyLogger logger )
	{
		this.distanceMatrix = dMat;
		this.nodeClusterAttributeName = nodeClusterAttributeName;
	       
		this.lambda = lambda;
		this.pref = preferenceParameter;

		if(lambda < 0)
			lambda = 0;

		else if(lambda > 1)
			lambda = 1;

		this.number_iterations = num_iterations;
		
		this.logger = logger;
		this.createMetaNodes = false;
		nodes = distanceMatrix.getNodes();
		edges = distanceMatrix.getEdges();
		this.matrix = distanceMatrix.getDistanceMatrix();

		pref_vector = DoubleFactory1D.dense.make(matrix.rows());
		pref_vector.assign(pref);

		r_matrix = new ResponsibilityMatrix(matrix, pref_vector, lambda);
		a_matrix = new AvailabilityMatrix(matrix, lambda);

		// logger.info("Iterations = "+num_iterations);
	}

	public void halt () { canceled = true; }

	public void createMetaNodes() { createMetaNodes = true; }
	

	//return exemplar k for element i => Maximizer of a(i,k) + r(i,k)
	private int get_exemplar(int i) {
	
		double max_value = -1000;
		int exemplar = 0;
		double sum;

		for(int k = 0; k < matrix.rows(); k++) {
			sum = a_matrix.get(i,k) + r_matrix.get(i,k);

			if(sum > max_value){
				max_value = sum;
				exemplar = k;
			}
		}
	  return exemplar;
	}
	    
	//Exchange Messages between Responsibility and Availibility Matrix for Single Iteration of Affinity Propogation
	public void iterate_message_exchange(TaskMonitor monitor, int iteration){

		// Calculate the availability maxima
		a_matrix.updateEvidence();

		// OK, now calculate the responsibility matrix
		r_matrix.update(a_matrix);

		// Get the maximum positive responsibilities
		r_matrix.updateEvidence();

		// Now, update the availability matrix
		a_matrix.update(r_matrix);
	}

	public void run(TaskMonitor monitor)
	{
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Matrix matrix;
		double numClusters;

		// logger.info("Calculating clusters");
		monitor.setPercentCompleted(1);
		
		for (int i=0; i<number_iterations; i++)
		{
			monitor.setStatus("Exchanging messages: iteration "+i);
			iterate_message_exchange(monitor, i);

			if (canceled) {
				monitor.setStatus("canceled");
				return;
			}
		}


		monitor.setStatus("Assigning nodes to clusters");

		HashMap<Integer, Cluster> clusterMap = get_clusterMap();

		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		logger.info("Created "+clusterCount+" clusters");
	       
		if (clusterCount == 0) {
			logger.error("Created 0 clusters!!!!");
			return;
		}

		int clusterNumber = 1;
		HashMap<Cluster,Cluster> cMap = new HashMap();
		for (Cluster cluster: sortMap(clusterMap)) {
			if (cMap.containsKey(cluster))
				continue;

			// for (Integer i: cluster) {
			// 	CyNode node = nodes.get(i.intValue());
			// 	debug(node.getIdentifier()+"\t");
			// }
			// debugln();
			cMap.put(cluster,cluster);

			cluster.setClusterNumber(clusterNumber);
			clusterNumber++;
		}

		Set<Cluster>clusters = cMap.keySet();

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters, createMetaNodes);

		ClusterStatistics stats = new ClusterStatistics(network, nodeClusters);
		monitor.setStatus("Done.  AP results:\n"+stats);
	}	

	

	/**
	 * Debugging routine to print out information about a matrix
	 *
	 * @param matrix the matrix we're going to print out information about
	 */
	private void printMatrixInfo(DoubleMatrix2D matrix) {
		debugln("Matrix("+matrix.rows()+", "+matrix.columns()+")");
		if (matrix instanceof SparseDoubleMatrix2D)
			debugln(" matrix is sparse");
		else
			debugln(" matrix is dense");
		debugln(" cardinality is "+matrix.cardinality());
	}

	/**
	 * Debugging routine to print out information about a matrix
	 *
	 * @param matrix the matrix we're going to print out information about
	 */
	private void printMatrix(DoubleMatrix2D matrix) {
		for (int row = 0; row < matrix.rows(); row++) {
			debug(nodes.get(row).getIdentifier()+":\t");
			for (int col = 0; col < matrix.columns(); col++) {
				debug(""+matrix.get(row,col)+"\t");
			}
			debugln();
		}
		debugln("Matrix("+matrix.rows()+", "+matrix.columns()+")");
		if (matrix instanceof SparseDoubleMatrix2D)
			debugln(" matrix is sparse");
		else
			debugln(" matrix is dense");
		debugln(" cardinality is "+matrix.cardinality());
	}

	private void removeGroups(CyAttributes netAttributes, String networkID) {
		// See if we already have groups defined (from a previous run?)
		if (netAttributes.hasAttribute(networkID, GROUP_ATTRIBUTE)) {
			List<String> groupList = (List<String>)netAttributes.getListAttribute(networkID, GROUP_ATTRIBUTE);
			for (String groupName: groupList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
		}
	}

	private List<List<CyNode>> createGroups(CyAttributes netAttributes, 
	                                        String networkID,
	                                        CyAttributes nodeAttributes, 
	                                        Set<Cluster> cMap, 
	                                        boolean createMetaNodes) {

		List<List<CyNode>> clusterList = new ArrayList(); // List of node lists
		List<String>groupList = new ArrayList(); // keep track of the groups we create
		CyGroup first = null;
		for (Cluster cluster: cMap) {
			int clusterNumber = cluster.getClusterNumber();
			String groupName = nodeClusterAttributeName+"_"+clusterNumber;
			List<CyNode>nodeList = new ArrayList();

			for (Integer nodeIndex: cluster) {
				CyNode node = this.nodes.get(nodeIndex);
				nodeList.add(node);
				nodeAttributes.setAttribute(node.getIdentifier(),
				                            nodeClusterAttributeName, clusterNumber);
			}
			
			if (createMetaNodes) {
				// Create the group
				CyGroup newgroup = CyGroupManager.createGroup(groupName, nodeList, null);
				if (newgroup != null) {
					first = newgroup;
					// Now tell the metanode viewer about it
					CyGroupManager.setGroupViewer(newgroup, "metaNode", 
					                              Cytoscape.getCurrentNetworkView(), false);
				}
			}
			clusterList.add(nodeList);
			groupList.add(groupName);
		}
		if (first != null)
			CyGroupManager.setGroupViewer(first, "metaNode", 
			                              Cytoscape.getCurrentNetworkView(), true);
		
		// Save the network attribute so we remember which groups are ours
		netAttributes.setListAttribute(networkID, GROUP_ATTRIBUTE, groupList);
		return clusterList;
	}

	private void debugln(String message) {
		if (debug) System.out.println(message);
	}

	private void debugln() {
		if (debug) System.out.println();
	}

	private void debug(String message) {
		if (debug) System.out.print(message);
	}

	private List<CyNode> clusterToNodes(Cluster cluster) {
		List<CyNode> nodeList = new ArrayList();
		for (Integer nodeIndex: cluster) {
			CyNode node = nodes.get(nodeIndex);
			nodeList.add(node);
		}
		return nodeList;
	}

	private List<Cluster> sortMap(HashMap<Integer, Cluster> map) {
		Cluster[] clusterArray = map.values().toArray(new Cluster[1]);
		Arrays.sort(clusterArray, new LengthComparator());
		return Arrays.asList(clusterArray);
	}

	//create clusterMap by calculating exemplars for all nodes
	private HashMap<Integer,Cluster> get_clusterMap(){
	    
		HashMap<Integer, Cluster> clusterMap = new HashMap();

		for(int i = 0; i < matrix.rows(); i++){
		
			int exemplar = get_exemplar(i);
		
		    
			if(clusterMap.containsKey(exemplar)){
				if(i == exemplar)
					continue;
				//already seen exemplar
				Cluster exemplarCluster = clusterMap.get(exemplar);
                	
				if(clusterMap.containsKey(i)){
					//We've allready seen i also -- join them
					Cluster iCluster = clusterMap.get(i);
					exemplarCluster.addAll(iCluster);
					clusterCount--;
				} else
					exemplarCluster.add(i);
			 
				//update Clusters
				for (Integer x: exemplarCluster){
					clusterMap.put(x, exemplarCluster);
				}
			} else {
				Cluster iCluster;
		       
				//First time we've seen "exemplar" -- have we already seen "i"?
				if(clusterMap.containsKey(i)){
					if(i == exemplar)
						continue;
					//Yes, just add exemplar to i's cluster
					iCluster = clusterMap.get(i);
					iCluster.add(exemplar);
				} else {
					//No, create new cluster from scratch
					iCluster = new Cluster();
					iCluster.add(i);
					iCluster.add(exemplar);
				}

				//update Clusters
				for (Integer x: iCluster){
					clusterMap.put(x, iCluster);
				}
			}
		}
		return clusterMap;
	}

	private class Cluster extends ArrayList<Integer> {
		int clusterNumber = 0;

		public Cluster() {
			super();
			clusterCount++;
			clusterNumber = clusterCount;
		}

		public int getClusterNumber() { return clusterNumber; }

		public void setClusterNumber(int clusterNumber) { 
			this.clusterNumber = clusterNumber; 
		}

		public void print() {
			debug(clusterNumber+": ");
			for (Integer i: this) {
				debug(i+" ");
			}
			debugln();
		}
	}

	private class LengthComparator implements Comparator {

		public int compare (Object o1, Object o2) {
			Cluster c1 = (Cluster)o1;
			Cluster c2 = (Cluster)o2;
			if (c1.size() > c2.size()) return -1;
			if (c1.size() < c2.size()) return 1;
			return 0;
		}

		public boolean equals(Object obj) { return false; }
	}
}

