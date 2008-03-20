/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package clusterMaker.algorithms;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.Exception;

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;

// clusterMaker imports

public class Matrix {
	private int nRows;
	private int nColumns;
	private Double matrix[][];
	private double colWeights[];
	private double rowWeights[];
	private String rowLabels[];
	private String columnLabels[];
	protected boolean transpose;

	/**
	 * Create a data matrix from the current nodes in the network.  There are two ways
	 * we construct the matrix, depending on the type.  If we are looking at expression
	 * profiles, for example, each node will represent a gene, and the expression results
	 * for each condition will be encoded in the indicated node attributes.  For our purposes,
	 * we don't pay any attention to edges when creating the matrix (there are obviously
	 * reasons why we might want to derive edges from the resulting data, but this can be
	 * done after the clustering is complete.
	 *
	 * On the other hand, if we are looking at genetic interactions, the resulting matrix will
	 * be symmetrical around the diagonal and the weightAttribute will be an edge attribute
	 * on the edges between the nodes.
	 *
	 * @param weightAttribute the edge attribute we use to get the weight (size of effect)
	 * @param transpose true if we are transposing this matrix 
	 *                  (clustering columns instead of rows)
	 */
	public Matrix(String[] weightAttributes, boolean transpose) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		this.transpose = transpose;
		
		// If our weightAttribute is on edges, we're looking at a symmetrical matrix
		if (weightAttributes.length > 1 || weightAttributes[0].startsWith("node.")) {
			// Get rid of the leading type information
			for (int i = 0; i < weightAttributes.length; i++) {
				weightAttributes[i] = weightAttributes[i].substring(5);
			}
			buildGeneArrayMatrix(network, weightAttributes, transpose);
		} else if (weightAttributes.length == 1 && weightAttributes[0].startsWith("edge.")) {
			buildSymmetricalMatrix(network, weightAttributes[0].substring(5));
		} else {
			// Throw an exception?
			return;
		}
		System.out.println("New Matrix with "+nRows+" rows and "+nColumns+" columns");
	}

	public Matrix(Matrix duplicate) {
		this.nRows = duplicate.nRows();
		this.nColumns = duplicate.nColumns();
		this.matrix = new Double[nRows][nColumns];
		this.colWeights = new double[nColumns];
		this.rowWeights = new double[nRows];
		this.columnLabels = new String[nColumns];
		this.rowLabels = new String[nRows];
		this.transpose = duplicate.transpose;

		for (int row = 0; row < nRows; row++) {
			rowWeights[row] = duplicate.getRowWeight(row);
			rowLabels[row] = duplicate.getRowLabel(row);
			for (int col = 0; col < nColumns; col++) {
				if (row == 0) {
					colWeights[col] = duplicate.getColWeight(col);
					columnLabels[col] = duplicate.getColLabel(col);
				}
				if (duplicate.getValue(row, col) != null)
					this.matrix[row][col] = new Double(duplicate.getValue(row, col));
			}
		}
		System.out.println("New Matrix with "+nRows+" rows and "+nColumns+" columns");
	}

	public Matrix(int rows, int cols) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		this.nRows = rows;
		this.nColumns = cols;
		this.matrix = new Double[rows][cols];
		this.colWeights = new double[cols];
		this.rowWeights = new double[rows];
		this.columnLabels = new String[cols];
		this.rowLabels = new String[rows];
		this.transpose = false;
		System.out.println("New Matrix with "+nRows+" rows and "+nColumns+" columns");
	}

	public int nRows() { return this.nRows; }

	public int nColumns() { return this.nColumns; }

	public Double getValue(int row, int column) {
		return matrix[row][column];
	}

	public double doubleValue(int row, int column) {
		if (matrix[row][column] != null)
			return matrix[row][column].doubleValue();
		return Double.NaN;
	}

	public void setValue(int row, int column, double value) {
		matrix[row][column] = new Double(value);
	}

	public void setValue(int row, int column, Double value) {
		matrix[row][column] = value;
	}

	public boolean hasValue(int row, int column) {
		if (matrix[row][column] != null)
			return true;
		return false;
	}

	public void setUniformWeights() {
		if (colWeights == null || rowWeights == null) {
			colWeights = new double[nColumns];
			rowWeights = new double[nRows];
		}
		Arrays.fill(this.colWeights,1.0);
		Arrays.fill(this.rowWeights,1.0);
	}

	public double[] getRowWeights() {
		return this.rowWeights;
	}

	public double getRowWeight(int row) {
		return this.rowWeights[row];
	}

	public double[] getColWeights() {
		return this.colWeights;
	}

	public double getColWeight(int col) {
		return this.colWeights[col];
	}

	public double[] getWeights() {
		if (transpose)
			return rowWeights;
		else
			return colWeights;
	}

	public void setRowWeight(int row, double value) {
		if (rowWeights == null) {
			rowWeights = new double[nRows];
		}
		rowWeights[row] = value;
	}

	public void setColWeight(int col, double value) {
		if (colWeights == null) {
			colWeights = new double[nColumns];
		}
		colWeights[col] = value;
	}

	public String[] getColLabels() {
		return this.columnLabels;
	}

	public String getColLabel(int col) {
		return this.columnLabels[col];
	}

	public void setColLabel(int col, String label) {
		this.columnLabels[col] = label;
	}

	public String[] getRowLabels() {
		return this.rowLabels;
	}

	public String getRowLabel(int row) {
		return this.rowLabels[row];
	}

	public void setRowLabel(int row, String label) {
		this.rowLabels[row] = label;
	}

	public double[] getRank(int row) {

		// Get the masked row
		double[] tData = new double[nRows];
		int nVals = 0;
		for (int column = 0; column < nColumns; column++) {
			if (hasValue(row,column))
				tData[nVals++] = matrix[row][column].doubleValue();
		}

		// Sort the data
		Integer index[] = indexSort(tData,nVals);

		// Build a rank table
		double[] rank = new double[nVals];
		for (int i = 0; i < nVals; i++) rank[index[i].intValue()] = i;

		// Fix for equal ranks
		int i = 0;
		while (i < nVals) {
			int m = 0;
			double value = tData[index[i].intValue()];
			int j = i+1;
			while (j < nVals && tData[index[j].intValue()] == value) j++;
			m = j - i; // Number of equal ranks found
			value = rank[index[i].intValue()] + (m-1)/2.0;
			for (j = i; j < i+m; j++) rank[index[j].intValue()] = value;
			i += m;
		}
		return rank;
	}

	public double[][] getDistanceMatrix(DistanceMetric metric) {
		double[][] result = new double[this.nRows][this.nRows];
		for (int row = 1; row < this.nRows; row++) {
			for (int column = 0; column < row; column++) {
				result[row][column] = 
				   metric.getMetric(this, this, this.getWeights(), row, column);
			}
		}
		return result;
	}

	private Integer[] indexSort(double[] tData, int nVals) {
		Integer[] index = new Integer[nVals];
		for (int i = 0; i < nVals; i++) index[i] = new Integer(i);
		IndexComparator iCompare = new IndexComparator(tData);
		Arrays.sort(index, iCompare);
		return index;
	}

	private void buildSymmetricalMatrix(CyNetwork network, String weight) {

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		// Get the list of edges
		List<CyNode>nodeList = network.nodesList();
		this.nRows = nodeList.size();
		this.nColumns = this.nRows;
		this.matrix = new Double[nRows][nColumns];
		this.rowLabels = new String[nRows];
		this.columnLabels = new String[nColumns];

		// For each edge, get the attribute and update the matrix and mask values
		int index = 0;
		int column;
		for (CyNode node: nodeList) {
			this.rowLabels[index] = node.getIdentifier();
			this.columnLabels[index] = node.getIdentifier();
			// Get the list of adjacent edges
				List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
			for (CyEdge edge: edgeList) {
				Double val = edgeAttributes.getDoubleAttribute(edge.getIdentifier(), weight);
				if (edge.getSource() == node) {
					column = nodeList.indexOf(edge.getTarget());
					matrix[index][column] = val;
				} else {
					column = nodeList.indexOf(edge.getSource());
					matrix[index][column] = val;
				}
			}
		}
	}

	private void buildGeneArrayMatrix(CyNetwork network, String[] weightAttributes, boolean transpose) {
		// Get the list of nodes
		List<CyNode>nodeList = network.nodesList();

		// Make a map of the conditions, indexed by CyNode
		HashMap<CyNode,HashMap<String,Double>>nodeCondMap = new HashMap();

		// Make a map of the conditions, by name
		HashMap<String,String>condMap = new HashMap();

		// Get our node attribute list
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Iterate over all of our nodes, getting the conditions attributes
		for (CyNode node: nodeList) {
			// Create the map for this node
			HashMap<String,Double>thisCondMap = new HashMap();

			for (int attrIndex = 0; attrIndex < weightAttributes.length; attrIndex++) {
				String attr = weightAttributes[attrIndex];
				Double value = null;
				// Remember the condition name
				condMap.put(attr,attr);
				// Get the attribute type
				if (nodeAttributes.getType(attr) == CyAttributes.TYPE_INTEGER) {
					Integer intVal = nodeAttributes.getIntegerAttribute(node.getIdentifier(), attr);
					value = Double.valueOf(intVal.doubleValue());
				} else if (nodeAttributes.getType(attr) == CyAttributes.TYPE_FLOATING) {
					value = nodeAttributes.getDoubleAttribute(node.getIdentifier(), attr);
				} else {
					continue; // At some point, handle lists?
				}
				// Set it
				thisCondMap.put(attr, value);
			}
			nodeCondMap.put(node, thisCondMap);
		}

		// We've got all of the information, get our counts and create the
		// matrix
		if (transpose) {
			this.nRows = condMap.keySet().size();
			this.nColumns = nodeList.size();
			this.matrix = new Double[nRows][nColumns];
			this.rowLabels = new String[nRows];
			this.columnLabels = new String[nColumns];
			assignRowLabels(condMap.keySet());

			int column = 0;
			for (CyNode node: nodeList) {
				this.columnLabels[column] = node.getIdentifier();
				HashMap<String,Double>thisCondMap = nodeCondMap.get(node);
				for (int row=0; row < this.nRows; row++) {
					String rowLabel = this.rowLabels[row];
					if (thisCondMap.containsKey(rowLabel)) {
						matrix[row][column] = thisCondMap.get(rowLabel);
					}
				}
			}
		} else {
			this.nRows = nodeList.size();
			this.nColumns = condMap.keySet().size();
			this.rowLabels = new String[nRows];
			this.columnLabels = new String[nColumns];
			this.matrix = new Double[nRows][nColumns];
			assignColumnLabels(condMap.keySet());

			int row = 0;
			for (CyNode node: nodeList) {
				this.rowLabels[row] = node.getIdentifier();
				HashMap<String,Double>thisCondMap = nodeCondMap.get(node);
				for (int column=0; column < this.nColumns; column++) {
					String columnLabel = this.columnLabels[column];
					if (thisCondMap.containsKey(columnLabel)) {
						matrix[row][column] = thisCondMap.get(columnLabel);
					}
				}
			}
		}
	}

	private void assignRowLabels(Set<String>labelList) {
		int index = 0;
		for (String label: labelList){
			this.rowLabels[index++] = label;
		}
	}

	private void assignColumnLabels(Set<String>labelList) {
		int index = 0;
		for (String label: labelList){
			System.out.println("Column label: "+label);
			this.columnLabels[index++] = label;
		}
	}

	private class IndexComparator implements Comparator<Integer> {
		double[] data;

		public IndexComparator(double[] data) { this.data = data; }

		public int compare(Integer o1, Integer o2) {
			if (data[o1.intValue()] < data[o2.intValue()]) return -1;
			if (data[o1.intValue()] > data[o2.intValue()]) return 1;
			return 0;
		}

		boolean equals() { return false; };
	}
}
