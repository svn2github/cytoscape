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

import clusterMaker.algorithms.Cluster;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.NodeCluster;

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
	private boolean canceled = false;
	private CyLogger logger;
	public final static String GROUP_ATTRIBUTE = "__APGroups";
	protected int clusterCount = 0;
	private DistanceMatrix distanceMatrix = null;
	private ResponsibilityMatrix r_matrix = null;
	private AvailabilityMatrix a_matrix = null;
	private DoubleMatrix2D s_matrix = null;
	private DoubleMatrix1D pref_vector = null;
	private boolean debug;

	public RunAP( DistanceMatrix dMat,
	              double lambdaParameter, double preferenceParameter, int num_iterations, 
	              CyLogger logger, boolean debug)
	{
		this.distanceMatrix = dMat;
	       
		this.lambda = lambdaParameter;
		this.pref = preferenceParameter;
		this.debug = debug;

		if(lambda < 0)
			lambda = 0;

		else if(lambda > 1)
			lambda = 1;

		this.number_iterations = num_iterations;
		
		this.logger = logger;
		nodes = distanceMatrix.getNodes();
		edges = distanceMatrix.getEdges();
		this.s_matrix = distanceMatrix.getDistanceMatrix();

		// Assign the preference vector to the diagonal
		for (int row = 0; row < s_matrix.rows(); row++) {
			s_matrix.set(row, row, pref);
		}

		// System.out.println("lambda = "+lambda);
		r_matrix = new ResponsibilityMatrix(s_matrix, lambda);
		a_matrix = new AvailabilityMatrix(s_matrix, lambda);

		// logger.info("Iterations = "+num_iterations);
	}

	public void halt () { canceled = true; }

	public List<NodeCluster> run(TaskMonitor monitor)
	{
		// initialize the Cluster class
		Cluster.init();

		// Matrix matrix;
		double numClusters;

		// logger.info("Calculating clusters");
		monitor.setPercentCompleted(1);

		if (debug) {
			logger.debug("Input matrix: ");
			distanceMatrix.printMatrix(logger, s_matrix);
		}
		
		for (int i=0; i<number_iterations; i++)
		{
			monitor.setStatus("Exchanging messages: iteration "+i);
			iterate_message_exchange(monitor, i);

			if (canceled) {
				monitor.setStatus("canceled");
				return null;
			}
			monitor.setPercentCompleted((i*100)/number_iterations);
		}

		if (debug) {
			for (int i = 0; i < s_matrix.rows(); i++) {
				logger.debug("Node "+nodes.get(i).getIdentifier()+" has exemplar "+get_exemplar(i));
			}
		}

		monitor.setStatus("Assigning nodes to clusters");

		Map<Integer, NodeCluster> clusterMap = getClusterMap();

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

	//Exchange Messages between Responsibility and Availibility Matrix for Single Iteration of Affinity Propogation
	public void iterate_message_exchange(TaskMonitor monitor, int iteration){

		logger.debug("Iteration "+iteration);

		// Calculate the availability maxima
		a_matrix.updateEvidence();

		// OK, now calculate the responsibility matrix
		r_matrix.update(a_matrix);

		if (debug) {
			logger.debug("Responsibility matrix: ");
			distanceMatrix.printMatrix(logger, r_matrix.getMatrix());
		}

		// Get the maximum positive responsibilities
		r_matrix.updateEvidence();

		// Now, update the availability matrix
		a_matrix.update(r_matrix);

		if (debug) {
			logger.debug("Availability matrix: ");
			distanceMatrix.printMatrix(logger, a_matrix.getMatrix());
		}
	}

	
	//return exemplar k for element i => Maximizer of a(i,k) + r(i,k)
	private int get_exemplar(int i) {
	
		double max_value = -1000;
		int exemplar = 0;
		double sum;

		for(int k = 0; k < s_matrix.rows(); k++) {
			sum = a_matrix.get(i,k) + r_matrix.get(i,k);

			if(sum > max_value){
				max_value = sum;
				exemplar = k;
			}
		}
		logger.debug("Exemplar for "+i+" is "+exemplar);
	  return exemplar;
	}

	private Map<Integer, NodeCluster> getClusterMap(){
	    
		HashMap<Integer, NodeCluster> clusterMap = new HashMap();

		for(int i = 0; i < s_matrix.rows(); i++){
		
			int exemplar = get_exemplar(i);
			// System.out.println("Examplar for node "+i+" is "+exemplar);
		    
			if (clusterMap.containsKey(exemplar)) {
				if (i == exemplar)
					continue;

				// Already seen exemplar
				NodeCluster exemplarCluster = clusterMap.get(exemplar);

				if (clusterMap.containsKey(i)) {
					// We've already seen i also -- join them
					NodeCluster iCluster = clusterMap.get(i);
					if (iCluster != exemplarCluster) {
						exemplarCluster.addAll(iCluster);
						// System.out.println("Combining "+i+"["+iCluster+"] and "+exemplar+" ["+exemplarCluster+"]");
						clusterCount--;
						clusterMap.remove(i);
					}
				} else {
					exemplarCluster.add(nodes, i);
					// System.out.println("Adding "+i+" to ["+exemplarCluster+"]");
				}

				// Update Clusters
				for (CyNode node: exemplarCluster) {
					clusterMap.put(nodes.indexOf(node), exemplarCluster);
				}
			} else {
				NodeCluster iCluster;

				// First time we've seen this "exemplar" -- have we already seen "i"?
				if (clusterMap.containsKey(i)) {
					if (i == exemplar)
						continue;
					// Yes, just add exemplar to i's cluster
					iCluster = clusterMap.get(i);
					iCluster.add(nodes, exemplar);
					// System.out.println("Adding "+exemplar+" to ["+iCluster+"]");
				} else {
					// No create new cluster from scratch
					iCluster = new NodeCluster();
					iCluster.add(nodes, i);
					if (exemplar != i)
						iCluster.add(nodes, exemplar);
					// System.out.println("New cluster ["+iCluster+"]");
				}
				// Update Clusters
				for (CyNode node: iCluster) {
					clusterMap.put(nodes.indexOf(node), iCluster);
				}
			}
		}
		return clusterMap;
	}
}

