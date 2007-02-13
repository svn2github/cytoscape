
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

import legacy.layout.algorithm.MutableGraphLayout;

import legacy.layout.util.GraphLayoutRepresentation;


/**
 * This class provides an implementation of
 * <code>MutableGraphLayout</code> whose only purpose is to
 * represent a mutable graph layout based on structure defined in arrays of
 * integers and floating-point numbers.
 * Methods on an instance of this class have no hooks into outside code.
 */
public class MutableGraphLayoutRepresentation extends GraphLayoutRepresentation
    implements MutableGraphLayout {
	/**
	 * Mobility of nodes for subclasses that implement mutable functionality.
	 */
	protected final boolean[] m_isMovableNode;

	/**
	 * Member variable defining <code>areAllNodesMovable()</code> to be
	 * used by subclasses if necessary.  Subclasses should take care to keep
	 * <code>m_areAllNodesMovable</code> consistent with
	 * the values in <code>m_isMovableNode</code>.
	 */
	protected boolean m_areAllNodesMovable;

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
	 *      nodeYPositions);</pre></blockquote>
	 * - for the sake of preventing the same documentation from existing in two
	 * different source code files, please refer to
	 * <code>GraphLayoutRepresentation</code> for a definition of these first
	 * nine input parameters.
	 *
	 * @param isMovableNode <blockquote>an array of length <code>numNodes</code> such that
	 *                      <code>isMovableNode[nodeIndex]</code> defines
	 *                      <code>isMovableNode(nodeIndex)</code>; if <code>isMovableNode</code>
	 *                      is <code>null</code>, all
	 *                      nodes in this graph are defined to be movable.</blockquote>
	 * @throws IllegalArgumentException if parameters are passed which
	 *                                  don't agree with a possible graph definition.
	 * @see GraphLayoutRepresentation#GraphLayoutRepresentation(int, int[], int[], int[], int[], double, double, double[], double[])
	 */
	public MutableGraphLayoutRepresentation(int numNodes, int[] directedEdgeSourceNodeIndices,
	                                        int[] directedEdgeTargetNodeIndices,
	                                        int[] undirectedEdgeNode0Indices,
	                                        int[] undirectedEdgeNode1Indices, double maxWidth,
	                                        double maxHeight, double[] nodeXPositions,
	                                        double[] nodeYPositions, boolean[] isMovableNode) {
		super(numNodes, directedEdgeSourceNodeIndices, directedEdgeTargetNodeIndices,
		      undirectedEdgeNode0Indices, undirectedEdgeNode1Indices, maxWidth, maxHeight,
		      nodeXPositions, nodeYPositions);

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

		// Preliminary error checking.
		if (isMovableNode == null) {
			isMovableNode = new boolean[getNumNodes()];

			for (int i = 0; i < isMovableNode.length; i++) {
				isMovableNode[i] = true;
			}
		}

		// Real parameter checking.  Set member variables.
		if (isMovableNode.length != getNumNodes()) {
			throw new IllegalArgumentException("is movable node array does not have length numNodes");
		}

		m_isMovableNode = new boolean[isMovableNode.length];
		System.arraycopy(isMovableNode, 0, m_isMovableNode, 0, isMovableNode.length);
		m_areAllNodesMovable = true;

		for (int i = 0; i < m_isMovableNode.length; i++) {
			if (!m_isMovableNode[i]) {
				m_areAllNodesMovable = false;

				break;
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final boolean areAllNodesMovable() {
		return m_areAllNodesMovable;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeIndex DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final boolean isMovableNode(int nodeIndex) {
		// This will automatically throw an ArrayIndexOutOfBoundsException,
		// which is a subclass of IndexOutOfBoundsException, if nodeIndex
		// is not a valid index.
		return m_isMovableNode[nodeIndex];
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeIndex DOCUMENT ME!
	 * @param xPos DOCUMENT ME!
	 * @param yPos DOCUMENT ME!
	 */
	public final void setNodePosition(int nodeIndex, double xPos, double yPos) {
		if (!isMovableNode(nodeIndex)) // Will throw IndexOutOfBoundsException
		                               // if nodeIndex is out of bounds.
		 {
			throw new UnsupportedOperationException("trying to move node at index " + nodeIndex
			                                        + " - non-movable node");
		}

		if ((xPos < 0.0d) || (xPos > getMaxWidth()) || (yPos < 0.0d) || (yPos > getMaxHeight())) {
			throw new IllegalArgumentException("trying to set node position outside of allowable rectangle");
		}

		m_nodeXPositions[nodeIndex] = xPos;
		m_nodeYPositions[nodeIndex] = yPos;
	}
}
