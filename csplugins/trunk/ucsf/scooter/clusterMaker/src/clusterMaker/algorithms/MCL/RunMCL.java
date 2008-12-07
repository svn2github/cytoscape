package clusterMaker.algorithms.MCL;

import java.util.ArrayList;
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
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class RunMCL {

	private double inflationParameter; //density parameter 
	private int number_iterations; //number of inflation/expansion cycles
	private double clusteringThresh; //Threshold used to remove weak edges between distinct clusters
	private double maxResidual; //The maximum residual to look for
	private List<CyNode> nodes;
	private List<CyEdge> edges;
	private String nodeClusterAttributeName;
	private String edgeAttributeName;
	private boolean takeNegLOG;
	private boolean canceled = false;
	private CyLogger logger;
	final static String GROUP_ATTRIBUTE = "__MCLGroups";
	protected int clusterCount = 0;
	private boolean debug = false;
	private boolean createNewNetwork = false;
	
	public RunMCL(String nodeClusterAttributeName, String edgeAttributeName, 
	              double inflationParameter, int num_iterations, 
	              double clusteringThresh, double maxResidual,
	              boolean takeNegLOG, boolean createNewNetwork, CyLogger logger )
	{
		
		this.nodeClusterAttributeName = nodeClusterAttributeName;
		this.edgeAttributeName = edgeAttributeName;
		this.inflationParameter = inflationParameter;
		this.number_iterations = num_iterations;
		this.clusteringThresh = clusteringThresh;
		this.maxResidual = maxResidual;
		this.takeNegLOG = takeNegLOG;
		this.logger = logger;
		this.createNewNetwork = createNewNetwork;
		// logger.info("InflationParameter = "+inflationParameter);
		// logger.info("Iterations = "+num_iterations);
		// logger.info("Clustering Threshold = "+clusteringThresh);
	}

	public void halt () { canceled = true; }
	
	public void run(TaskMonitor monitor)
	{
		edges = Cytoscape.getCurrentNetwork().edgesList();
		nodes = Cytoscape.getCurrentNetwork().nodesList();
		// double[][] graph = new double[this.nodes.size()][this.nodes.size()];
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		String networkID = Cytoscape.getCurrentNetwork().getIdentifier();
		// Matrix matrix;
		double numClusters;
		double edgeWeight;
		double minEdgeWeight = 0;
		int sourceIndex;
		int targetIndex;

		monitor.setStatus("Setting up distance matrix");

		DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(nodes.size(),nodes.size());

		// logger.info("Getting edge weights from network");
		//Get Edge Weights From Network
		for(CyEdge edge: edges) {
			String id = edge.getIdentifier();

			if(!edgeAttributes.hasAttribute(id,edgeAttributeName))
				continue;
		
			if(edgeAttributes.getType(edgeAttributeName) == edgeAttributes.TYPE_FLOATING)
				edgeWeight = edgeAttributes.getDoubleAttribute(id,edgeAttributeName).doubleValue();
		
			else if(edgeAttributes.getType(edgeAttributeName) == edgeAttributes.TYPE_INTEGER)
				edgeWeight = edgeAttributes.getIntegerAttribute(id,edgeAttributeName).doubleValue();
		
			else
				continue;
		    
		  /*Take -LOG of edge weight (E-Value) if so specified*/
			if(takeNegLOG)
				if(edgeWeight != 0.0)
					edgeWeight = -1*Math.log(edgeWeight);

			if(edgeWeight < minEdgeWeight)
				minEdgeWeight = edgeWeight;
		      
			/*Add edge to graph*/
			sourceIndex = nodes.indexOf(edge.getSource());
			targetIndex = nodes.indexOf(edge.getTarget());
			//graph[targetIndex][sourceIndex] = edgeWeight;
			matrix.set(targetIndex,sourceIndex,edgeWeight);

			if(!edge.isDirected())
				matrix.set(sourceIndex,targetIndex,edgeWeight);
				// graph[sourceIndex][targetIndex] = edgeWeight;
			if (canceled) {
				monitor.setStatus("canceled");
				return;
			}
		}

		if(minEdgeWeight < 0) {
			for (CyEdge edge: edges) {
				sourceIndex = nodes.indexOf(edge.getSource());
				targetIndex = nodes.indexOf(edge.getTarget());
				matrix.set(sourceIndex, targetIndex, matrix.get(sourceIndex,targetIndex) - minEdgeWeight);
				if(!edge.isDirected())
					matrix.set(targetIndex, sourceIndex, matrix.get(targetIndex,sourceIndex) - minEdgeWeight);
			}
		}

		// debugln("Initial matrix:");
		// printMatrix(matrix);

		// Normalize
		normalize(matrix, clusteringThresh);

		// logger.info("Calculating clusters");

		double residual = 1.0;
		IntIntDoubleFunction myPow = new MatrixPow(inflationParameter);
		debugln("residual = "+residual+" maxResidual = "+maxResidual);
		for (int i=0; (i<number_iterations)&&(residual>maxResidual); i++)
		{
			// Expand
			{
				monitor.setStatus("Iteration: "+(i+1)+" expanding ");
				debugln("Iteration: "+(i+1)+" expanding ");
				printMatrixInfo(matrix);
				// We really, really want to make sure this is sparse!
				DoubleMatrix2D newMatrix = DoubleFactory2D.sparse.make(nodes.size(),nodes.size());
				matrix = matrix.zMult(matrix, newMatrix, 1.0, 0.0, false, false);
			}

			// Inflate
			{
				monitor.setStatus("Iteration: "+(i+1)+" inflating");
				debugln("Iteration: "+(i+1)+" inflating");
				printMatrixInfo(matrix);
				matrix.forEachNonZero(myPow);
			}

			// Normalize
			normalize(matrix, clusteringThresh);

			matrix.trimToSize();
			residual = calculateResiduals(matrix);
			debugln("Iteration: "+(i+1)+" residual: "+residual);

			if (canceled) {
				monitor.setStatus("canceled");
				return;
			}
		}

		// If we're in debug mode, output the matrix
		printMatrixInfo(matrix);
		// printMatrix(matrix);

		monitor.setStatus("Assigning nodes to clusters");

		HashMap<Integer, Cluster> clusterMap = new HashMap();
		matrix.forEachNonZero(new ClusterMatrix(clusterMap));

		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		logger.info("Created "+clusterCount+" clusters");
		debugln("Created "+clusterCount+" clusters:");

		HashMap<Cluster,Cluster> cMap = new HashMap();
		for (Cluster cluster: clusterMap.values()) {
			if (cMap.containsKey(cluster))
				continue;

			for (Integer i: cluster) {
				CyNode node = nodes.get(i.intValue());
				debug(node.getIdentifier()+"\t");
			}
			debugln();
			cMap.put(cluster,cluster);
		}

		Set<Cluster>clusters = cMap.keySet();

		//Assign the node attributes
		assignAttributes(nodeAttributes, clusters);

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");

		// Now, create the groups
		List<String> groupList = createGroups(clusters);

		// Finally, if we're supposed to, create the new network
		if (createNewNetwork)
		 	createClusteredNetwork(clusters);
		
		// Now notify the metanode viewer
		CyGroup group = CyGroupManager.findGroup(groupList.get(0));
		CyGroupManager.setGroupViewer(group, "metaNode", Cytoscape.getCurrentNetworkView(), true);

		// Save the network attribute so we remember which groups are ours
		netAttributes.setListAttribute(networkID, GROUP_ATTRIBUTE, groupList);
	}	

	/**
	 * This method does threshold and normalization.  First, we get rid of
	 * any cells that have a value beneath our threshold and in the same pass
	 * calculate all of the row sums.  Then we use the row sums to normalize
	 * each row such that all of the cells in the row sum to 1.
	 *
	 * @param matrix the (sparse) data matrix we're operating on
	 * @param clusteringThresh the maximum value that we will take as a "zero" value
	 */
	private void normalize(DoubleMatrix2D matrix, double clusteringThresh)
	{
		// Remove any really low values and create the sums array
		double [] sums = new double[matrix.rows()];
		matrix.forEachNonZero(new MatrixZeroAndSum(clusteringThresh, sums));

		// Finally, adjust the values
		matrix.forEachNonZero(new MatrixNormalize(sums));

		// Last step -- find any rows that summed to zero and set the diagonal to 1
		for (int row = 0; row < sums.length; row++) {
			if (sums[row] == 0.0) {
				debugln("Row "+row+" sums to 0");
				matrix.set(row,row,1.0);
			}
		}
	}

	/**
	 * This method calculates the residuals.  Calculate the sum and
	 * sum of squares for each row, then return the maximum residual.
	 *
	 * @param matrix the (sparse) data matrix we're operating on
	 * @return residual value
	 */
	private double calculateResiduals(DoubleMatrix2D matrix) {
		// Calculate and return the residuals
		double[] sums = new double[matrix.rows()];
		double [] sumSquares = new double[matrix.rows()];
		matrix.forEachNonZero(new MatrixSumAndSumSq(sums, sumSquares));
		double residual = 0.0;
		for (int i = 0; i < sums.length; i++) {
			residual = Math.max(residual, sums[i] - sumSquares[i]);
		}
		return residual;
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


	private void assignAttributes(CyAttributes nodeAttributes, 
	                              Set<Cluster> cMap) {
		for (Cluster cluster: cMap) {
			int clusterNumber = cluster.getClusterNumber();
			for (Integer nodeIndex: cluster) {
				CyNode node = this.nodes.get(nodeIndex);
				nodeAttributes.setAttribute(node.getIdentifier(),nodeClusterAttributeName,clusterNumber);
			}
		}
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

	private List<String> createGroups(Set<Cluster> cMap) {
		List<String>groupList = new ArrayList(); // keep track of the groups we create
		for (Cluster cluster: cMap) {

			String groupName = nodeClusterAttributeName+"_"+cluster.getClusterNumber();
			List<CyNode>nodeList = new ArrayList();
			for (Integer nodeIndex: cluster) {
				CyNode node = this.nodes.get(nodeIndex);
				nodeList.add(node);
			}
			// logger.info("Group: "+clusterNumber+": "+groupName);
			// Create the group
			CyGroup newgroup = CyGroupManager.createGroup(groupName, nodeList, null);
			if (newgroup != null) {
				// Now tell the metanode viewer about it
				CyGroupManager.setGroupViewer(newgroup, "metaNode", Cytoscape.getCurrentNetworkView(), false);
				groupList.add(groupName);
			}
		}
		return groupList;
	}

	private void createClusteredNetwork(Set<Cluster> cMap) {
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

		// Create the new network
		CyNetwork net = Cytoscape.createNetwork(currentNetwork.getTitle()+"--clustered",currentNetwork,false);

		for (Cluster cluster: cMap) {
			// Get the list of nodes
			List<CyNode> nodeList = clusterToNodes(cluster);
			// Get the list of edges
			List<CyEdge> edgeList = currentNetwork.getConnectingEdges(nodeList);
			for (CyNode node: nodeList) { net.addNode(node); }
			for (CyEdge edge: edgeList) { net.addEdge(edge); }
		}

		// Create the network view
		CyNetworkView view = Cytoscape.createNetworkView(net);

		// Get the current visual mapper
		VisualStyle vm = Cytoscape.getVisualMappingManager().getVisualStyle();
		view.applyVizmapper(vm);

		// If available, do a force-directed layout
		CyLayoutAlgorithm alg = CyLayouts.getLayout("force-directed");
		if (alg != null)
			view.applyLayout(alg);

		Cytoscape.setCurrentNetwork(net.getIdentifier());
		Cytoscape.setCurrentNetworkView(view.getIdentifier());
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


	/**
	 * The MatrixPow class raises the value of each non-zero cell of the matrix
	 * to the power passed in it's constructor.
	 */
	private class MatrixPow implements IntIntDoubleFunction {
		double pow;

		public MatrixPow(double power) {
			this.pow = power;
		}
		
		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }
			return Math.pow(value,pow);
		}
	}

	/**
	 * The MatrixZeroAndSum looks through all non-zero cells in a matrix
	 * and if the value of the cell is beneath "threshold" it is set to
	 * zero.  All non-zero cells in a row are added together to return
	 * the sum of each row.
	 */
	private class MatrixZeroAndSum implements IntIntDoubleFunction {
		double threshold;
		double [] rowSums;

		public MatrixZeroAndSum (double threshold, double[] rowSums) {
			this.threshold = threshold;
			this.rowSums = rowSums;
		}

		public double apply(int row, int column, double value) {
			if (value < threshold)
				return 0.0;
			rowSums[row] += value;
			return value;
		}
	}

	/**
	 * The MatrixSumAndSumSq looks through all non-zero cells in a matrix
	 * and calculates the sums and sum of squares for each row.
	 */
	private class MatrixSumAndSumSq implements IntIntDoubleFunction {
		double [] sumSquares;
		double [] rowSums;

		public MatrixSumAndSumSq (double[] rowSums, double[] sumSquares) {
			this.sumSquares = sumSquares;
			this.rowSums = rowSums;
		}
		public double apply(int row, int column, double value) {
			rowSums[row] += value;
			sumSquares[row] += value*value;
			return value;
		}
	}

	/**
	 * The MatrixNormalize class takes as input an array of sums for
	 * each row in the matrix and uses that to normalize the sum of the
	 * row to 1.  If the sum of the row is 0, the diagonal is set to 1.
	 */
	private class MatrixNormalize implements IntIntDoubleFunction {
		double [] rowSums;

		public MatrixNormalize(double[] rowSums) {
			this.rowSums = rowSums;
		}
		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }
			return value/rowSums[row];
		}
	}

	private class ClusterMatrix implements IntIntDoubleFunction {
		Map<Integer, Cluster> clusterMap;

		public ClusterMatrix(Map<Integer,Cluster> clusterMap) {
			this.clusterMap = clusterMap;
		}

		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }

			if (row == column) 
				return value;

			if (clusterMap.containsKey(row)) {
				// Already seen "row" -- get the cluster and add column
				Cluster rowCluster = clusterMap.get(row);
				if (clusterMap.containsKey(column)) {
					// We've already seen column also -- join them
					Cluster columnCluster = clusterMap.get(column);
					if (rowCluster == columnCluster) 
						return value;
					// debugln("Joining cluster "+rowCluster.getClusterNumber()+" and "+columnCluster.getClusterNumber());
					rowCluster.addAll(columnCluster);
					clusterCount--;
				} else {
					// debugln("Adding "+column+" to "+rowCluster.getClusterNumber());
					rowCluster.add(column);
				}
				updateClusters(rowCluster);
			} else {
				Cluster colCluster;
				// First time we've seen "row" -- have we already seen "column"
				if (clusterMap.containsKey(column)) {
					// Yes, just add row to column's cluster
					colCluster = clusterMap.get(column);
					// debugln("Adding "+row+" to "+colCluster.getClusterNumber());
					colCluster.add(row);
				} else {
					colCluster = new Cluster();
					// debugln("Created new cluster "+colCluster.getClusterNumber()+" with "+column+" and "+row);
					colCluster.add(column);
					colCluster.add(row);
				}
				updateClusters(colCluster);
			}
			return value;
		}

		private void updateClusters(Cluster cl) {
			for (Integer i: cl) {
				clusterMap.put(i, cl);
			}
		}
	}

	private class Cluster extends ArrayList<Integer> {
		int clusterNumber = 0;

		public Cluster() {
			super();
			clusterCount++;
			clusterNumber = clusterCount;
		}

		public int getClusterNumber() { return clusterNumber; }

		public void print() {
			debug(clusterNumber+": ");
			for (Integer i: this) {
				debug(i+" ");
			}
			debugln();
		}
	}
}

