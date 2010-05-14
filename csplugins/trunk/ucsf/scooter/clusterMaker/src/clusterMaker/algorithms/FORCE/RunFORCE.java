package clusterMaker.algorithms.FORCE;

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



import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.NodeCluster;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;


import clusterMaker.algorithms.FORCE.Parameters;
import clusterMaker.algorithms.FORCE.ConnectedComponent;
import clusterMaker.algorithms.FORCE.FORCEnDLayoutUtility;
import clusterMaker.algorithms.FORCE.SingleLinkageClusterer;

public class RunFORCE {



	private List<CyNode> nodes;
	private List<CyEdge> edges;
	private boolean canceled = false;
	private CyLogger logger;
	public final static String GROUP_ATTRIBUTE = "__FORCEGroups";
	protected int clusterCount = 0;


       
	private DistanceMatrix distanceMatrix = null;
	private DoubleMatrix2D s_matrix = null;
	private Parameters params = null;
        private int dim;
        private double singleLinkageDistance;
        private ConnectedComponent cc = null;
	private boolean debug;

	public RunFORCE( DistanceMatrix dMat,int dimensions, double singleLinkageDistance, Parameters params, CyLogger logger)
	{
		this.distanceMatrix = dMat;
	       
		this.dim = dimensions;
		this.singleLinkageDistance = singleLinkageDistance;
		this.params = params;
		this.debug = debug;
		this.logger = logger;
	       
	       
		nodes = distanceMatrix.getNodes();
		edges = distanceMatrix.getEdges();
		this.s_matrix = distanceMatrix.getDistanceMatrix();
                
                cc = new ConnectedComponent(s_matrix,dim);

	}

	public void halt () { canceled = true; }

	public List<NodeCluster> run(TaskMonitor monitor)
	{

	   
		// initialize node layout in geometric space
	        cc.init_hsphere_layout();



		// logger.info("Calculating clusters");
		monitor.setPercentCompleted(1);

		if (debug) {
			logger.debug("Input matrix: ");
			distanceMatrix.printMatrix(logger, s_matrix);
		}

		int node_no = this.cc.getNodeNumber();
                double[][] node_pos = this.cc.getCCPositions();

                double[][] allDisplacements = new double[node_no][this.dim];

                /* for each iteration calculate the displacement vectors
                 * and move all nodes by this after calculation in one go */
                for(int it = 0; it<this.params.getIterations();it++){

		    
			monitor.setStatus("Computing Geometry: iteration "+it);
		      

			if (canceled) {
				monitor.setStatus("canceled");
				return null;
			}

			monitor.setPercentCompleted((it*100)/params.getIterations());

                        /* the cooling temperature factor for this iteration */
                        double temperature = FORCEnDLayoutUtility.calculateTemperature(it,
                                        node_no, this.params);

                        FORCEnDLayoutUtility.calculateDisplacementVectors(allDisplacements, this.cc,
                                        this.dim, this.params);

                        FORCEnDLayoutUtility.moveAllNodesByDisplacement(allDisplacements,
                                        node_pos, node_no, this.dim, temperature);

                }

	

		if (debug) {
			for (int i = 0; i < s_matrix.rows(); i++) {
				logger.debug("Node "+nodes.get(i).getIdentifier());
			}
		}

		monitor.setStatus("Assigning nodes to clusters");

		SingleLinkageClusterer s_linkage = new SingleLinkageClusterer(this.cc,this.singleLinkageDistance);
		
		// each array value corresponds to cluster assignment of node index
		int[] cluster_array = s_linkage.run();
		clusterCount = s_linkage.get_clust_num();

		

		Map<Integer, NodeCluster> clusterMap = new HashMap();

		//set clusterMap. TODO: Change this loop to something more efficient
		for(int i = 0; i < clusterCount; i++){

		    NodeCluster iCluster = new NodeCluster();

		    for(int j = 0; j < cluster_array.length; j++)
			if(cluster_array[j] == i)
			    iCluster.add(nodes,i);

		    updateClusters(iCluster,clusterMap);
		}

			
		  

		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		logger.info("Created "+clusterMap.size()+" clusters");
	       
		if (clusterCount == 0) {
			logger.error("Created 0 clusters!!!!");
			return null;
		}

		int clusterNumber = 1;
		Map<NodeCluster,NodeCluster> cMap = new HashMap();
		for (NodeCluster cluster: NodeCluster.sortMap(clusterMap)) {
			if (cMap.containsKey(cluster))
				continue;

			if (debug) {
				logger.debug("Cluster "+clusterNumber);
				String s = "";
				for (CyNode node: cluster) {
			 		s += node.getIdentifier()+"\t";
				}
				logger.debug(s);
			}

			cMap.put(cluster,cluster);

			cluster.setClusterNumber(clusterNumber);
			clusterNumber++;
		}

		Set<NodeCluster>clusters = cMap.keySet();
		return new ArrayList(clusters);
	}	



	private void updateClusters(NodeCluster cluster, Map<Integer, NodeCluster> clusterMap) {
		for (CyNode node: cluster) {
			clusterMap.put(nodes.indexOf(node), cluster);
		}
	}
}

