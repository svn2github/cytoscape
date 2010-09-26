package de.mpg.mpi_inf.bioinf.netanalyzer.data;

import giny.model.Node;

/**
 * Immutable storage of information on a connected component.
 * 
 * @author Yassen Assenov
 */
public class CCInfo {

	/**
	 * Initializes a new instance of <code>CCInfo</code>.
	 * 
	 * @param aSize Size of the connected component (number of nodes).
	 * @param aNode One of the nodes in the component.
	 */
	public CCInfo(int aSize, Node aNode) {
		size = aSize;
		node = aNode;
	}

	/**
	 * Gets the size of the connected component.
	 * 
	 * @return Number of nodes in the connected component.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets a node from the connected component.
	 * 
	 * @return Node belonging to this connected component.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Number of nodes in the connected component.
	 */
	private int size;

	/**
	 * One of the nodes in the connected component.
	 */
	private Node node;
}
