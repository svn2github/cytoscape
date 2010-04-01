package clusterMaker.algorithms.MCL;

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

import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.DistanceMatrix;

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
	private boolean canceled = false;
	private CyLogger logger;
	public final static String GROUP_ATTRIBUTE = "__MCLGroups";
	protected int clusterCount = 0;
	private boolean createMetaNodes = false;
	private DistanceMatrix distanceMatrix = null;
	private DoubleMatrix2D matrix = null;
	private boolean debug = false;
	
	public RunMCL(DistanceMatrix dMat, double inflationParameter, int num_iterations, 
	              double clusteringThresh, double maxResidual, CyLogger logger )
	{
		this.distanceMatrix = dMat;
		this.inflationParameter = inflationParameter;
		this.number_iterations = num_iterations;
		this.clusteringThresh = clusteringThresh;
		this.maxResidual = maxResidual;
		this.logger = logger;
		nodes = distanceMatrix.getNodes();
		edges = distanceMatrix.getEdges();
		this.matrix = distanceMatrix.getDistanceMatrix();
		// logger.info("InflationParameter = "+inflationParameter);
		// logger.info("Iterations = "+num_iterations);
		// logger.info("Clustering Threshold = "+clusteringThresh);
	}

	public void halt () { canceled = true; }

	public void setDebug(boolean debug) { this.debug = debug; }
	
	public List<NodeCluster> run(TaskMonitor monitor)
	{
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Matrix matrix;
		double numClusters;

		debugln("Initial matrix:");
		printMatrix(matrix);

		// Normalize
		normalize(matrix, clusteringThresh, false);

		debugln("Normalized matrix:");
		printMatrix(matrix);

		// logger.info("Calculating clusters");

		double residual = 1.0;
		IntIntDoubleFunction myPow = new MatrixPow(inflationParameter);
		// debugln("residual = "+residual+" maxResidual = "+maxResidual);
		for (int i=0; (i<number_iterations)&&(residual>maxResidual); i++)
		{
			// Expand
			{
				monitor.setStatus("Iteration: "+(i+1)+" expanding ");
				// debugln("Iteration: "+(i+1)+" expanding ");
				// printMatrixInfo(matrix);
				// We really, really want to make sure this is sparse!
				DoubleMatrix2D newMatrix = DoubleFactory2D.sparse.make(nodes.size(),nodes.size());
				matrix = matrix.zMult(matrix, newMatrix, 1.0, 0.0, false, false);
				// Normalize
				normalize(matrix, clusteringThresh, false);
			}

			// printMatrix(matrix);
			// debugln("^ "+(i+1)+" after expansion");

			// Inflate
			{
				monitor.setStatus("Iteration: "+(i+1)+" inflating");
				// debugln("Iteration: "+(i+1)+" inflating");
				// printMatrixInfo(matrix);
				matrix.forEachNonZero(myPow);
				// Normalize
				normalize(matrix, clusteringThresh, true);
			}

			// printMatrix(matrix);
			// debugln("^ "+(i+1)+" after inflation");

			matrix.trimToSize();
			residual = calculateResiduals(matrix);
			// debugln("Iteration: "+(i+1)+" residual: "+residual);

			if (canceled) {
				monitor.setStatus("canceled");
				return null;
			}
		}

		// If we're in debug mode, output the matrix
		// printMatrixInfo(matrix);
		// printMatrix(matrix);

		monitor.setStatus("Assigning nodes to clusters");

		HashMap<Integer, NodeCluster> clusterMap = new HashMap();
		matrix.forEachNonZero(new ClusterMatrix(clusterMap));

		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		logger.info("Created "+clusterCount+" clusters");
		// debugln("Created "+clusterCount+" clusters:");
		//
		if (clusterCount == 0) {
			logger.error("Created 0 clusters!!!!");
			return null;
		}

		int clusterNumber = 1;
		HashMap<NodeCluster,NodeCluster> cMap = new HashMap();
		for (NodeCluster cluster: NodeCluster.sortMap(clusterMap)) {

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

		Set<NodeCluster>clusters = cMap.keySet();
		return new ArrayList(clusters);
	}	

	/**
	 * This method does threshold and normalization.  First, we get rid of
	 * any cells that have a value beneath our threshold and in the same pass
	 * calculate all of the column sums.  Then we use the column sums to normalize
	 * each column such that all of the cells in the column sum to 1.
	 *
	 * @param matrix the (sparse) data matrix we're operating on
	 * @param clusteringThresh the maximum value that we will take as a "zero" value
	 * @param prune if 'false', don't prune this pass
	 */
	private void normalize(DoubleMatrix2D matrix, double clusteringThresh, boolean prune)
	{
		// Remove any really low values and create the sums array
		double [] sums = new double[matrix.columns()];
		matrix.forEachNonZero(new MatrixZeroAndSum(prune, clusteringThresh, sums));

		// Finally, adjust the values
		matrix.forEachNonZero(new MatrixNormalize(sums));

		// Last step -- find any columns that summed to zero and set the diagonal to 1
		for (int col = 0; col < sums.length; col++) {
			if (sums[col] == 0.0) {
				// debugln("Column "+col+" sums to 0");
				matrix.set(col,col,1.0);
			}
		}
	}

	/**
	 * This method normalizes the weights to between 0 and 1.
	 *
	 * @param matrix the (sparse) data matrix we're operating on
	 * @param min the minimum weight
	 * @param max the maximum weight
	 */
	private void normalizeWeights(DoubleMatrix2D matrix, double min, double max) {
		matrix.forEachNonZero(new MatrixNormalizeWeights(min, max));
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
		double[] sums = new double[matrix.columns()];
		double [] sumSquares = new double[matrix.columns()];
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

	private void debugln(String message) {
		if (debug) System.out.println(message);
	}

	private void debugln() {
		if (debug) System.out.println();
	}

	private void debug(String message) {
		if (debug) System.out.print(message);
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
	 * zero.  All non-zero cells in a column are added together to return
	 * the sum of each column.
	 */
	private class MatrixZeroAndSum implements IntIntDoubleFunction {
		double threshold;
		double [] colSums;
		boolean prune;

		public MatrixZeroAndSum (boolean prune, double threshold, double[] colSums) {
			this.threshold = threshold;
			this.colSums = colSums;
			this.prune = prune;
		}

		public double apply(int row, int column, double value) {
			if (prune && (value < threshold))
				return 0.0;
			colSums[column] += value;
			return value;
		}
	}

	/**
	 * The MatrixSumAndSumSq looks through all non-zero cells in a matrix
	 * and calculates the sums and sum of squares for each column.
	 */
	private class MatrixSumAndSumSq implements IntIntDoubleFunction {
		double [] sumSquares;
		double [] colSums;

		public MatrixSumAndSumSq (double[] colSums, double[] sumSquares) {
			this.sumSquares = sumSquares;
			this.colSums = colSums;
		}
		public double apply(int row, int column, double value) {
			colSums[column] += value;
			sumSquares[column] += value*value;
			return value;
		}
	}

	/**
	 * The MatrixNormalize class takes as input an array of sums for
	 * each column in the matrix and uses that to normalize the sum of the
	 * column to 1.  If the sum of the column is 0, the diagonal is set to 1.
	 */
	private class MatrixNormalize implements IntIntDoubleFunction {
		double [] colSums;

		public MatrixNormalize(double[] colSums) {
			this.colSums = colSums;
		}

		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }
			return value/colSums[column];
		}
	}

	private class MatrixNormalizeWeights implements IntIntDoubleFunction {
		double min;
		double max;

		public MatrixNormalizeWeights(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public double apply(int row, int column, double value) {
			return (value - min) / (max - min);
		}
	}

	private class ClusterMatrix implements IntIntDoubleFunction {
		Map<Integer, NodeCluster> clusterMap;

		public ClusterMatrix(Map<Integer,NodeCluster> clusterMap) {
			this.clusterMap = clusterMap;
		}

		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }

			if (row == column) 
				return value;

			if (clusterMap.containsKey(column)) {
				// Already seen "column" -- get the cluster and add column
				NodeCluster columnCluster = clusterMap.get(column);
				if (clusterMap.containsKey(row)) {
					// We've already seen row also -- join them
					NodeCluster rowCluster = clusterMap.get(row);
					if (rowCluster == columnCluster) 
						return value;
					// debugln("Joining cluster "+columnCluster.getClusterNumber()+" and "+rowCluster.getClusterNumber());
					columnCluster.addAll(rowCluster);
					clusterCount--;
				} else {
					// debugln("Adding "+row+" to "+columnCluster.getClusterNumber());
					columnCluster.add(nodes, row);
				}
				updateClusters(columnCluster);
			} else {
				NodeCluster rowCluster;
				// First time we've seen "column" -- have we already seen "row"
				if (clusterMap.containsKey(row)) {
					// Yes, just add column to row's cluster
					rowCluster = clusterMap.get(row);
					// debugln("Adding "+column+" to "+rowCluster.getClusterNumber());
					rowCluster.add(nodes, column);
				} else {
					rowCluster = new NodeCluster();
					// debugln("Created new cluster "+rowCluster.getClusterNumber()+" with "+row+" and "+column);
					rowCluster.add(nodes, column);
					rowCluster.add(nodes, row);
				}
				updateClusters(rowCluster);
			}
			return value;
		}

		private void updateClusters(NodeCluster cl) {
			for (CyNode node: cl) {
				clusterMap.put(nodes.indexOf(node), cl);
			}
		}
	}
}

