package clusterMaker.algorithms;

import java.util.ArrayList;
import java.util.List;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class DistanceMatrix {
	private String edgeAttributeName = null;
	private double minWeight = Double.MAX_VALUE;
	private double maxWeight = Double.MIN_VALUE;
	private double minAttribute = Double.MAX_VALUE;
	private double maxAttribute = Double.MIN_VALUE;
	private double edgeCutOff = 0.0;
	private boolean unDirectedEdges = false;
	private List<CyNode> nodes = null;
	private List<CyEdge> edges = null;
	private DoubleMatrix2D matrix = null;

	private double[] edgeWeights = null;

	public DistanceMatrix(String edgeAttributeName, boolean selectedOnly, boolean distanceValues,
	                      boolean takeNegLOG) {
		this.edgeAttributeName = edgeAttributeName;

		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();
		if (!selectedOnly) {
			nodes = network.nodesList();
			edges = network.edgesList();
		} else {
			nodes = new ArrayList();
			nodes.addAll(network.getSelectedNodes());
			edges = network.getConnectingEdges(nodes);
		}

		// double[][] graph = new double[this.nodes.size()][this.nodes.size()];
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		edgeWeights = new double[edges.size()];

		// We do a fair amount of massaging the data, so let's just do it once
		for(int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
			CyEdge edge = edges.get(edgeIndex);
			String id = edge.getIdentifier();

			edgeWeights[edgeIndex] = Double.MIN_VALUE;

			if(!edgeAttributes.hasAttribute(id,edgeAttributeName))
				continue;

			double edgeWeight = 0.0;
			if(edgeAttributes.getType(edgeAttributeName) == edgeAttributes.TYPE_FLOATING)
				edgeWeight = edgeAttributes.getDoubleAttribute(id,edgeAttributeName).doubleValue();
			else if(edgeAttributes.getType(edgeAttributeName) == edgeAttributes.TYPE_INTEGER)
				edgeWeight = edgeAttributes.getIntegerAttribute(id,edgeAttributeName).doubleValue();
			else
				continue;

			minAttribute = Math.min(minAttribute, edgeWeight);
			maxAttribute = Math.max(maxAttribute, edgeWeight);
			edgeWeights[edgeIndex] = edgeWeight;
		}

		// We now have two lists, one with the edges, one with the weights, now massage the edgeWeights data as requested
		// Note that we need to go through this again to handle some of the edge cases
		List<Integer> edgeCase = new ArrayList();
		for(int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
			double edgeWeight = edgeWeights[edgeIndex];
			if (edgeWeight == Double.MIN_VALUE) continue;

			if (distanceValues) {
				if (edgeWeight != 0.0)
					edgeWeight = 1/edgeWeight;
				else {
					edgeWeight = Double.MIN_VALUE;
					edgeCase.add(edgeIndex);
				}
			}

			if (takeNegLOG) {
				if (minAttribute < 0.0) 
					edgeWeight += Math.abs(minAttribute);

				if(edgeWeight != 0.0 && edgeWeight != Double.MAX_VALUE)
					edgeWeight = -Math.log10(edgeWeight);
				else
					edgeWeight = 500; // Assume 1e-500 as a reasonble upper bound
			}

			edgeWeights[edgeIndex] = edgeWeight;
			if (edgeWeight != Double.MIN_VALUE) {
				minWeight = Math.min(minWeight, edgeWeight);
				maxWeight = Math.max(maxWeight, edgeWeight);
			}
		}

		// OK, now we have our two arrays with the exception of the edge cases -- we can fix those, now
		for (Integer index: edgeCase) {
			edgeWeights[index] = maxWeight+maxWeight/10.0;
		}
	}

	public double[] getEdgeValues() {
		return edgeWeights;
	}


	public DoubleMatrix2D getDistanceMatrix(Double edgeCutOff, boolean undirectedEdges) {
		setEdgeCutOff(edgeCutOff);
		setUndirectedEdges(undirectedEdges);
		matrix = null;
		return getDistanceMatrix();
	}

	public DoubleMatrix2D getDistanceMatrix() {
		if (matrix != null)
			return matrix;

		matrix = DoubleFactory2D.sparse.make(nodes.size(),nodes.size());
		int sourceIndex;
		int targetIndex;

		for(int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
			CyEdge edge = edges.get(edgeIndex);

			// Is this weight above the cutoff?
			if (edgeWeights[edgeIndex] < edgeCutOff)
				continue; // Nope, don't add it

			/*Add edge to matrix*/
			sourceIndex = nodes.indexOf(edge.getSource());
			targetIndex = nodes.indexOf(edge.getTarget());

			matrix.set(targetIndex,sourceIndex,edgeWeights[edgeIndex]);
			if(unDirectedEdges)
				matrix.set(sourceIndex,targetIndex,edgeWeights[edgeIndex]);
		}

		return matrix;
	}

	public double getMaxAttribute() {return maxAttribute;}
	public double getMaxWeight() {return maxWeight;}
	public double getMinAttribute() {return minAttribute;}
	public double getMinWeight() {return minWeight;}
	public List<CyNode> getNodes() {return nodes;}
	public List<CyEdge> getEdges() {return edges;}

	public void setEdgeCutOff(Double edgeCutOff) { 
		matrix = null;
		this.edgeCutOff = edgeCutOff.doubleValue(); 
	}

	public void	setUndirectedEdges(boolean undirectedEdges) { 
		matrix = null;
		this.unDirectedEdges = undirectedEdges; 
	}

	/**
   * This method handles the loop adjustment, if desired by the user.
   * The basic approach is to go through the diagonal and set the value of the
   * diagonal to the maximum value of the column.  In the von Dongen code, this
   * is handled in separate steps (zero the diagonal, (maybe) preinflate, set diagonal
   * to max).
   *
   * @param matrix the (sparse) data matrix we're going to adjust
   */
	public void adjustLoops()
	{
		if (matrix == null)
			getDistanceMatrix();

		double [] max = new double[matrix.columns()];
		// Calculate the max value for each column
		matrix.forEachNonZero(new MatrixFindMax(max));

		// Set it in the diagonal
		for (int col = 0; col < matrix.columns(); col++) {
			if (max[col] != 0.0)
				matrix.set(col,col,max[col]);
			else
				matrix.set(col,col,1.0);
		}
		
	}

	/**
 	 * MatrixFindMax simply records the maximum value in a column
 	 */
	private class MatrixFindMax implements IntIntDoubleFunction {
		double [] colMax;

		public MatrixFindMax(double[] colMax) {
			this.colMax = colMax;
		}

		public double apply(int row, int column, double value) {
			if (value > colMax[column])
				colMax[column] = value;
			return value;
		}
	}
}
