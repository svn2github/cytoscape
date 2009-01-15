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
package clusterMaker.algorithms.hierarchical;

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
	private CyNode rowNodes[];
	private CyNode columnNodes[];
	protected boolean transpose;
	protected boolean symmetrical;
	protected boolean ignoreMissing;
	protected boolean selectedOnly;

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
	public Matrix(String[] weightAttributes, boolean transpose, boolean ignoreMissing, boolean selectedOnly) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		this.transpose = transpose;
		this.ignoreMissing = ignoreMissing;
		this.selectedOnly = selectedOnly;

		// Create our local copy of the weightAtributes array
		String[] attributeArray = new String[weightAttributes.length];
		
		// If our weightAttribute is on edges, we're looking at a symmetrical matrix
		if (weightAttributes.length > 1 && weightAttributes[0].startsWith("node.")) {
			// Get rid of the leading type information
			for (int i = 0; i < weightAttributes.length; i++) {
				attributeArray[i] = weightAttributes[i].substring(5);
			}
			buildGeneArrayMatrix(network, attributeArray, transpose, ignoreMissing, selectedOnly);
			symmetrical = false;
		} else if (weightAttributes.length == 1 && weightAttributes[0].startsWith("edge.")) {
			buildSymmetricalMatrix(network, weightAttributes[0].substring(5), ignoreMissing, selectedOnly);
			symmetrical = true;
		} else {
			// Throw an exception?
			return;
		}
	}

	public Matrix(Matrix duplicate) {
		this.nRows = duplicate.nRows();
		this.nColumns = duplicate.nColumns();
		this.matrix = new Double[nRows][nColumns];
		this.colWeights = new double[nColumns];
		this.rowWeights = new double[nRows];
		this.columnLabels = new String[nColumns];
		this.rowLabels = new String[nRows];
		this.ignoreMissing = duplicate.ignoreMissing;
		this.selectedOnly = duplicate.selectedOnly;

		// Only one of these will actually be used, depending on whether
		// we're transposed or not
		this.rowNodes = null;
		this.columnNodes = null;

		if (duplicate.getRowNode(0) != null)
			this.rowNodes = new CyNode[nRows];
		else
			this.columnNodes = new CyNode[nColumns];

		this.transpose = duplicate.transpose;

		for (int row = 0; row < nRows; row++) {
			rowWeights[row] = duplicate.getRowWeight(row);
			rowLabels[row] = duplicate.getRowLabel(row);
			if (rowNodes != null)
				rowNodes[row] = duplicate.getRowNode(row);
			for (int col = 0; col < nColumns; col++) {
				if (row == 0) {
					colWeights[col] = duplicate.getColWeight(col);
					columnLabels[col] = duplicate.getColLabel(col);
					if (columnNodes != null)
						columnNodes[col] = duplicate.getColNode(col);
				}
				if (duplicate.getValue(row, col) != null)
					this.matrix[row][col] = new Double(duplicate.getValue(row, col));
			}
		}
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
		// Only one of these will actually be used
		this.rowNodes = null;
		this.columnNodes = null;
		this.transpose = false;
		this.ignoreMissing = false;
		this.selectedOnly = false;
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

	public CyNode getRowNode(int row) {
		if (this.rowNodes != null)
			return rowNodes[row];
		return null;
	}

	public CyNode getColNode(int col) {
		if (this.columnNodes != null)
			return columnNodes[col];
		return null;
	}

	public double[] getWeights() {
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
		double[] tData = new double[nColumns];
		int nVals = 0;
		for (int column = 0; column < nColumns; column++) {
			if (hasValue(row,column))
				tData[nVals++] = matrix[row][column].doubleValue();
		}

		if (nVals == 0)
			return null;

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
				// System.out.println("distanceMatrix["+row+"]["+column+"] = "+result[row][column]);
			}
		}
		return result;
	}

	public void printMatrix() {
		for (int col = 0; col < nColumns; col++)
			System.out.print("\t"+columnLabels[col]);
		System.out.println();

		for (int row = 0; row < nRows; row++) {
			System.out.print(rowLabels[row]+"\t");
			for (int col = 0; col < nColumns; col++) {
				if (matrix[row][col] != null)
					System.out.print(matrix[row][col]+"\t");
				else
					System.out.print("\t");
			}
			System.out.println();
		}
	}

	public Integer[] indexSort(double[] tData, int nVals) {
		Integer[] index = new Integer[nVals];
		for (int i = 0; i < nVals; i++) index[i] = new Integer(i);
		IndexComparator iCompare = new IndexComparator(tData);
		Arrays.sort(index, iCompare);
		return index;
	}

	public Integer[] indexSort(int[] tData, int nVals) {
		Integer[] index = new Integer[nVals];
		for (int i = 0; i < nVals; i++) index[i] = new Integer(i);
		IndexComparator iCompare = new IndexComparator(tData);
		Arrays.sort(index, iCompare);
		return index;
	}

	public boolean isTransposed() { return this.transpose; }

	public boolean isSymmetrical() { return this.symmetrical; }

	private void buildSymmetricalMatrix(CyNetwork network, String weight, 
	                                    boolean ignoreMissing, boolean selectedOnly) {

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		// Get the list of edges
		List<CyNode>nodeList = network.nodesList();

		// For debugging purposes, sort the node list by identifier
		nodeList = sortNodeList(nodeList);

		this.nRows = nodeList.size();
		this.nColumns = this.nRows;
		this.matrix = new Double[nRows][nColumns];
		this.rowLabels = new String[nRows];
		this.columnLabels = new String[nColumns];
		this.rowNodes = new CyNode[nRows];
		this.columnNodes = null;

		// For each edge, get the attribute and update the matrix and mask values
		int index = 0;
		int column;
		byte attributeType = edgeAttributes.getType(weight);
		for (CyNode node: nodeList) {
			boolean found = false;
			boolean hasSelectedEdge = false;
			this.rowLabels[index] = node.getIdentifier();
			this.rowNodes[index] = node;
			this.columnLabels[index] = node.getIdentifier();
			// Get the list of adjacent edges
			List<CyEdge> edgeList = network.getAdjacentEdgesList(node, true, true, true);
			for (CyEdge edge: edgeList) {
				if (selectedOnly && !network.isSelected(edge))
				 	continue;
				hasSelectedEdge = true;

				Double val = null;
				if (attributeType == CyAttributes.TYPE_FLOATING) {
					val = edgeAttributes.getDoubleAttribute(edge.getIdentifier(), weight);
				} else {
					Integer v = edgeAttributes.getIntegerAttribute(edge.getIdentifier(), weight);
					if (v != null)
						val = Double.valueOf(v.toString());
				}
				if (val != null) found = true;
				if (edge.getSource() == node) {
					column = nodeList.indexOf(edge.getTarget());
					matrix[index][column] = val;
				} else {
					column = nodeList.indexOf(edge.getSource());
					matrix[index][column] = val;
				}
			}
			if ((!ignoreMissing || found) && (!selectedOnly || hasSelectedEdge))
				index++;
		}
	}

	private void buildGeneArrayMatrix(CyNetwork network, String[] weightAttributes, 
	                                  boolean transpose, boolean ignoreMissing,
	                                  boolean selectedOnly) {
		// Get the list of nodes
		List<CyNode>nodeList = network.nodesList();

		if (selectedOnly) nodeList = new ArrayList(network.getSelectedNodes());

		// For debugging purposes, sort the node list by identifier
		nodeList = sortNodeList(nodeList);

		// Make a map of the conditions, indexed by CyNode
		HashMap<CyNode,HashMap<String,Double>>nodeCondMap = new HashMap();

		// Make a map of the conditions, by name
		List<String>condList = Arrays.asList(weightAttributes);

		// Get our node attribute list
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Iterate over all of our nodes, getting the conditions attributes
		for (CyNode node: nodeList) {
			// Create the map for this node
			HashMap<String,Double>thisCondMap = new HashMap();

			for (int attrIndex = 0; attrIndex < weightAttributes.length; attrIndex++) {
				String attr = weightAttributes[attrIndex];
				Double value = null;
				// Get the attribute type
				if (nodeAttributes.getType(attr) == CyAttributes.TYPE_INTEGER) {
					Integer intVal = nodeAttributes.getIntegerAttribute(node.getIdentifier(), attr);
					if (intVal != null)
						value = Double.valueOf(intVal.doubleValue());
				} else if (nodeAttributes.getType(attr) == CyAttributes.TYPE_FLOATING) {
					value = nodeAttributes.getDoubleAttribute(node.getIdentifier(), attr);
				} else {
					continue; // At some point, handle lists?
				}
				if (!ignoreMissing || value != null) {
					// Set it
					thisCondMap.put(attr, value);
				}
			}
			if (!ignoreMissing || thisCondMap.size() > 0)
				nodeCondMap.put(node, thisCondMap);
		}

		// We've got all of the information, get our counts and create the
		// matrix
		if (transpose) {
			this.nRows = condList.size();
			this.nColumns = nodeCondMap.size();
			this.matrix = new Double[nRows][nColumns];
			this.rowLabels = new String[nRows];
			this.columnLabels = new String[nColumns];
			this.columnNodes = new CyNode[nColumns];
			assignRowLabels(condList);

			int column = 0;
			for (CyNode node: nodeList) {
				if (!nodeCondMap.containsKey(node))
					continue;

				HashMap<String,Double>thisCondMap = nodeCondMap.get(node);
				this.columnLabels[column] = node.getIdentifier();
				this.columnNodes[column] = node;
				for (int row=0; row < this.nRows; row++) {
					String rowLabel = this.rowLabels[row];
					if (thisCondMap.containsKey(rowLabel)) {
						matrix[row][column] = thisCondMap.get(rowLabel);
					}
				}
				column++;
			}
		} else {
			this.nRows = nodeCondMap.size();
			this.nColumns = condList.size();
			this.rowLabels = new String[nRows];
			this.rowNodes = new CyNode[nRows];
			this.columnLabels = new String[nColumns];
			this.matrix = new Double[nRows][nColumns];
			assignColumnLabels(condList);

			int row = 0;
			for (CyNode node: nodeList) {
				if (!nodeCondMap.containsKey(node))
					continue;
				this.rowLabels[row] = node.getIdentifier();
				this.rowNodes[row] = node;
				HashMap<String,Double>thisCondMap = nodeCondMap.get(node);
				for (int column=0; column < this.nColumns; column++) {
					String columnLabel = this.columnLabels[column];
					if (thisCondMap.containsKey(columnLabel)) {
						// System.out.println("Setting matrix["+rowLabels[row]+"]["+columnLabel+"] to "+thisCondMap.get(columnLabel));
						matrix[row][column] = thisCondMap.get(columnLabel);
					}
				}
				row++;
			}
		}
	}

	private void assignRowLabels(List<String>labelList) {
		int index = 0;
		for (String label: labelList){
			this.rowLabels[index++] = label;
		}
	}

	private void assignColumnLabels(List<String>labelList) {
		int index = 0;
		for (String label: labelList){
			this.columnLabels[index++] = label;
		}
	}

	// sortNodeList does an alphabetical sort on the names of the nodes.
	private List<CyNode>sortNodeList(List<CyNode>nodeList) {
		HashMap<String,CyNode>nodeMap = new HashMap();
		// First build a string array
		String nodeNames[] = new String[nodeList.size()];
		int index = 0;
		for (CyNode node: nodeList) {
			nodeNames[index++] = node.getIdentifier();
			nodeMap.put(node.getIdentifier(), node);
		}
		// Sort it
		Arrays.sort(nodeNames);
		// Build the node list again
		ArrayList<CyNode>newList = new ArrayList(nodeList.size());
		for (index = 0; index < nodeNames.length; index++) {
			newList.add(nodeMap.get(nodeNames[index]));
		}
		return newList;
	}

	private class IndexComparator implements Comparator<Integer> {
		double[] data = null;
		int[] intData = null;

		public IndexComparator(double[] data) { this.data = data; }

		public IndexComparator(int[] data) { this.intData = data; }

		public int compare(Integer o1, Integer o2) {
			if (data != null) {
				if (data[o1.intValue()] < data[o2.intValue()]) return -1;
				if (data[o1.intValue()] > data[o2.intValue()]) return 1;
				return 0;
			} else if (intData != null) {
				if (intData[o1.intValue()] < intData[o2.intValue()]) return -1;
				if (intData[o1.intValue()] > intData[o2.intValue()]) return 1;
				return 0;
			}
			return 0;
		}

		boolean equals() { return false; };
	}
}
