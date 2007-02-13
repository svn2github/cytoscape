
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package legacy.layout.algorithm.util;

import legacy.layout.algorithm.MutablePolyEdgeGraphLayout;


/**
 * This class provides an implementation of
 * <code>MutablePolyEdgeGraphLayout</code> whose only purpose is to represent
 * a mutable poly edge graph layout based on structure defined in arrays of
 * integers and floating-point numbers.  Methods on an instance of this class
 * have no hooks into outside code.
 */
public class MutablePolyEdgeGraphLayoutRepresentation extends MutableGraphLayoutRepresentation
    implements MutablePolyEdgeGraphLayout {
	private final double[][] m_edgeAnchorXPositions;
	private final double[][] m_edgeAnchorYPositions;
	private final int[] m_edgeAnchorCount;

	/**
	 * Copies are made of all the array input parameters; modifying
	 * the arrays after this constructor is called will have no effect on
	 * an instance of this class.  An instance of this class
	 * never modifies any of the arrays passed into the constructor.<p>
	 * This constructor calls<blockquote><pre>
	 * super(numNodes,
	 *      directedEdgeSourceNodeIndices,
	 *      directedEdgeTargetNodeIndices,
	 *      undirectedEdgeNode0Indices,
	 *      undirectedEdgeNode1Indices,
	 *      maxWidth,
	 *      maxHeight,
	 *      nodeXPositions,
	 *      nodeYPositions,
	 *      isMovableNode);</pre></blockquote>
	 * - for the sake of preventing the same documentation from existing in two
	 * different source code files, please refer to
	 * <code>MutableGraphLayoutRepresentation</code> for a definition of these
	 * first ten input parameters.
	 *
	 * @param edgeAnchorXPositions <blockquote>an array of length equal to the number of edges in
	 *                             this graph; the <code>double[]</code> array
	 *                             <code>edgeAnchorXPositions[edgeIndex]</code> defines, in anchor point
	 *                             index order, the X positions of anchor points belonging to edge at index
	 *                             <code>edgeIndex</code>.</blockquote>
	 * @param edgeAnchorYPositions <blockquote>an array of length equal to the number of edges in
	 *                             this graph; the <code>double[]</code> array
	 *                             <code>edgeAnchorYPositions[edgeIndex]</code> defines, in anchor point
	 *                             index order, the Y positions of anchor points belonging to edge at index
	 *                             <code>edgeIndex</code>.</blockquote>
	 * @throws IllegalArgumentException if parameters are passed which
	 *                                  don't agree with a possible graph definition.
	 * @see MutableGraphLayoutRepresentation#MutableGraphLayoutRepresentation(int, int[], int[], int[], int[], double, double, double[], double[], boolean[])
	 * @see cytoscape.graph.util.GraphTopologyRepresentation#GraphTopologyRepresentation(int, int[], int[], int[], int[])
	 */
	public MutablePolyEdgeGraphLayoutRepresentation(int numNodes,
	                                                int[] directedEdgeSourceNodeIndices,
	                                                int[] directedEdgeTargetNodeIndices,
	                                                int[] undirectedEdgeNode0Indices,
	                                                int[] undirectedEdgeNode1Indices,
	                                                double maxWidth, double maxHeight,
	                                                double[] nodeXPositions,
	                                                double[] nodeYPositions,
	                                                boolean[] isMovableNode,
	                                                double[][] edgeAnchorXPositions,
	                                                double[][] edgeAnchorYPositions) {
		super(numNodes, directedEdgeSourceNodeIndices, directedEdgeTargetNodeIndices,
		      undirectedEdgeNode0Indices, undirectedEdgeNode1Indices, maxWidth, maxHeight,
		      nodeXPositions, nodeYPositions, isMovableNode);

		// Let's be anal and prove to ourselves that we no longer need any
		// of the parameters that are passed to our superclass' constructor.
		numNodes = -1;
		directedEdgeSourceNodeIndices = null;
		directedEdgeTargetNodeIndices = null;
		undirectedEdgeNode0Indices = null;
		undirectedEdgeNode1Indices = null;
		maxWidth = -1.0d;
		maxHeight = -1.0d;
		nodeXPositions = null;
		nodeYPositions = null;
		isMovableNode = null;

		// Preliminary error checking.
		if (edgeAnchorXPositions == null) {
			edgeAnchorXPositions = new double[getNumEdges()][];
		}

		if (edgeAnchorYPositions == null) {
			edgeAnchorYPositions = new double[getNumEdges()][];
		}

		// Real parameter checking.  Set member variables;
		final int numEdges = getNumEdges();

		if (edgeAnchorXPositions.length != numEdges) {
			throw new IllegalArgumentException("edge anchor points X array does not have length numEdges");
		}

		if (edgeAnchorYPositions.length != numEdges) {
			throw new IllegalArgumentException("edge anchor points Y array does not have length numEdges");
		}

		m_edgeAnchorXPositions = new double[edgeAnchorXPositions.length][];
		m_edgeAnchorYPositions = new double[edgeAnchorYPositions.length][];
		m_edgeAnchorCount = new int[numEdges];

		for (int i = 0; i < numEdges; i++) {
			m_edgeAnchorXPositions[i] = new double[((edgeAnchorXPositions[i] == null) ? 0
			                                                                          : edgeAnchorXPositions[i].length)];
			m_edgeAnchorYPositions[i] = new double[((edgeAnchorYPositions[i] == null) ? 0
			                                                                          : edgeAnchorYPositions[i].length)];

			if (m_edgeAnchorXPositions[i].length != m_edgeAnchorYPositions[i].length) {
				throw new IllegalArgumentException("for anchor points belonging to edge at index "
				                                   + i
				                                   + ", the number of X positions is not the same as the number of "
				                                   + "Y positions");
			}

			if (edgeAnchorXPositions[i] != null) {
				System.arraycopy(edgeAnchorXPositions[i], 0, m_edgeAnchorXPositions[i], 0,
				                 edgeAnchorXPositions[i].length);
			}

			if (edgeAnchorYPositions[i] != null) {
				System.arraycopy(edgeAnchorYPositions[i], 0, m_edgeAnchorYPositions[i], 0,
				                 edgeAnchorYPositions[i].length);
			}

			m_edgeAnchorCount[i] = m_edgeAnchorXPositions[i].length;

			for (int j = 0; j < m_edgeAnchorXPositions[i].length; j++) {
				if ((m_edgeAnchorXPositions[i][j] < 0.0d)
				    || (m_edgeAnchorXPositions[i][j] > getMaxWidth())
				    || (m_edgeAnchorYPositions[i][j] < 0.0d)
				    || (m_edgeAnchorYPositions[i][j] > getMaxHeight())) {
					throw new IllegalArgumentException("an anchor position falls outside of allowable rectangle");
				}
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeIndex DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int getNumAnchors(int edgeIndex) {
		return m_edgeAnchorCount[edgeIndex];
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeIndex DOCUMENT ME!
	 * @param anchorIndex DOCUMENT ME!
	 * @param xPosition DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final double getAnchorPosition(int edgeIndex, int anchorIndex, boolean xPosition) {
		// Parameter check.  We need to check this because arrays may be longer
		// than the number of anchor points.
		if (anchorIndex >= getNumAnchors(edgeIndex)) {
			throw new IndexOutOfBoundsException("anchor index out of bounds");
		}

		return (xPosition ? m_edgeAnchorXPositions[edgeIndex] : m_edgeAnchorYPositions[edgeIndex])[anchorIndex];
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeIndex DOCUMENT ME!
	 * @param anchorIndex DOCUMENT ME!
	 */
	public void deleteAnchor(int edgeIndex, int anchorIndex) {
		// Parameter check.
		if ((anchorIndex < 0) || (anchorIndex >= getNumAnchors(edgeIndex))) {
			throw new IndexOutOfBoundsException("anchor index out of bounds");
		}

		if ((!isMovableNode(getEdgeNodeIndex(edgeIndex, true)))
		    && (!isMovableNode(getEdgeNodeIndex(edgeIndex, false)))) {
			throw new UnsupportedOperationException("specified edge's anchor points cannot change");
		}

		// Collapse remaining anchor position entries.
		double[] xArr = m_edgeAnchorXPositions[edgeIndex];
		double[] yArr = m_edgeAnchorYPositions[edgeIndex];

		for (int i = anchorIndex; i < (getNumAnchors(edgeIndex) - 1); i++) {
			xArr[i] = xArr[i + 1];
			yArr[i] = yArr[i + 1];
		}

		m_edgeAnchorCount[edgeIndex] = m_edgeAnchorCount[edgeIndex] - 1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeIndex DOCUMENT ME!
	 * @param anchorIndex DOCUMENT ME!
	 */
	public void createAnchor(int edgeIndex, int anchorIndex) {
		// Parameter check.
		if ((anchorIndex < 0) || (anchorIndex > getNumAnchors(edgeIndex))) {
			throw new IndexOutOfBoundsException("anchor index out of bounds");
		}

		if ((!isMovableNode(getEdgeNodeIndex(edgeIndex, true)))
		    && (!isMovableNode(getEdgeNodeIndex(edgeIndex, false)))) {
			throw new UnsupportedOperationException("specified edge's anchor points cannot change");
		}

		// Expand existing anchor position entries; make room for new entry.
		double[] xArr = m_edgeAnchorXPositions[edgeIndex];
		double[] yArr = m_edgeAnchorYPositions[edgeIndex];

		if (xArr.length == getNumAnchors(edgeIndex)) {
			xArr = biggerArray(xArr);
			yArr = biggerArray(yArr);
			m_edgeAnchorXPositions[edgeIndex] = xArr;
			m_edgeAnchorYPositions[edgeIndex] = yArr;
		}

		for (int i = getNumAnchors(edgeIndex); i > anchorIndex; i--) {
			xArr[i] = xArr[i - 1];
			yArr[i] = yArr[i - 1];
		}

		m_edgeAnchorCount[edgeIndex] = m_edgeAnchorCount[edgeIndex] + 1;

		// Calculate midpoint between new anchor's neighbors.
		final double srcX;

		// Calculate midpoint between new anchor's neighbors.
		final double srcY;

		// Calculate midpoint between new anchor's neighbors.
		final double trgX;

		// Calculate midpoint between new anchor's neighbors.
		final double trgY;

		if (anchorIndex == 0) {
			srcX = getNodePosition(getEdgeNodeIndex(edgeIndex, true), true);
			srcY = getNodePosition(getEdgeNodeIndex(edgeIndex, true), false);
		} else {
			srcX = getAnchorPosition(edgeIndex, anchorIndex - 1, true);
			srcY = getAnchorPosition(edgeIndex, anchorIndex - 1, false);
		}

		if (anchorIndex == (getNumAnchors(edgeIndex) - 1)) {
			trgX = getNodePosition(getEdgeNodeIndex(edgeIndex, false), true);
			trgY = getNodePosition(getEdgeNodeIndex(edgeIndex, false), false);
		} else {
			trgX = getAnchorPosition(edgeIndex, anchorIndex + 1, true);
			trgY = getAnchorPosition(edgeIndex, anchorIndex + 1, false);
		}

		xArr[anchorIndex] = (srcX + trgX) / 2.0d;
		yArr[anchorIndex] = (srcY + trgY) / 2.0d;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeIndex DOCUMENT ME!
	 * @param anchorIndex DOCUMENT ME!
	 * @param xPosition DOCUMENT ME!
	 * @param yPosition DOCUMENT ME!
	 */
	public void setAnchorPosition(int edgeIndex, int anchorIndex, double xPosition, double yPosition) {
		// Parameter check.
		if ((anchorIndex < 0) || (anchorIndex >= getNumAnchors(edgeIndex))) {
			throw new IndexOutOfBoundsException("anchor index out of bounds");
		}

		if ((!isMovableNode(getEdgeNodeIndex(edgeIndex, true)))
		    && (!isMovableNode(getEdgeNodeIndex(edgeIndex, false)))) {
			throw new UnsupportedOperationException("specified edge's anchor points cannot change");
		}

		if ((xPosition < 0.0d) || (xPosition > getMaxWidth()) || (yPosition < 0.0d)
		    || (yPosition > getMaxHeight())) {
			throw new IllegalArgumentException("trying to set anchor position outside of allowable rectangle");
		}

		m_edgeAnchorXPositions[edgeIndex][anchorIndex] = xPosition;
		m_edgeAnchorYPositions[edgeIndex][anchorIndex] = yPosition;
	}

	private final static double[] biggerArray(double[] oldArray) {
		double[] tempBuff = new double[(oldArray.length + 1) * 2];
		System.arraycopy(oldArray, 0, tempBuff, 0, oldArray.length);

		return tempBuff;
	}
}
