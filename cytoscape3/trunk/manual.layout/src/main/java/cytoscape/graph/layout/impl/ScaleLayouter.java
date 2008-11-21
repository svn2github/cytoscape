
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

package cytoscape.graph.layout.impl;

import com.nerius.math.xform.AffineTransform3D;
import com.nerius.math.xform.Scale3D;
import com.nerius.math.xform.Translation3D;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.util.intr.IntEnumerator;


/**
 *
 */
public final class ScaleLayouter {
	private final MutablePolyEdgeGraphLayout m_graph;
	private final Translation3D m_translationToOrig;
	private final Translation3D m_translationFromOrig;

	/**
	 * This operation does not affect edge anchor points which belong to edges
	 * containing at least one non-movable node.
	 **/
	public ScaleLayouter(MutablePolyEdgeGraphLayout graph) {
		m_graph = graph;

		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;
		IntEnumerator edges = m_graph.edges();

		while (edges.numRemaining() > 0) {
			int edge = edges.nextInt();

			if (!(m_graph.isMovableNode(m_graph.edgeSource(edge))
			    && m_graph.isMovableNode(m_graph.edgeTarget(edge))))
				continue;

			final int numAnchors = m_graph.getNumAnchors(edge);

			for (int j = 0; j < numAnchors; j++) {
				double anchXPosition = m_graph.getAnchorPosition(edge, j, true);
				double anchYPosition = m_graph.getAnchorPosition(edge, j, false);
				xMin = Math.min(xMin, anchXPosition);
				xMax = Math.max(xMax, anchXPosition);
				yMin = Math.min(yMin, anchYPosition);
				yMax = Math.max(yMax, anchYPosition);
			}
		}

		IntEnumerator nodes = m_graph.nodes();

		while (nodes.numRemaining() > 0) {
			int node = nodes.nextInt();

			if (!m_graph.isMovableNode(node))
				continue;

			double nodeXPosition = m_graph.getNodePosition(node, true);
			double nodeYPosition = m_graph.getNodePosition(node, false);
			xMin = Math.min(xMin, nodeXPosition);
			xMax = Math.max(xMax, nodeXPosition);
			yMin = Math.min(yMin, nodeYPosition);
			yMax = Math.max(yMax, nodeYPosition);
		}

		if (xMax < 0) // Nothing is movable.
		 {
			m_translationToOrig = null;
			m_translationFromOrig = null;
		} else {
			final double xRectCenter = (xMin + xMax) / 2.0d;
			final double yRectCenter = (yMin + yMax) / 2.0d;
			m_translationToOrig = new Translation3D(-xRectCenter, -yRectCenter, 0.0d);
			m_translationFromOrig = new Translation3D(xRectCenter, yRectCenter, 0.0d);
		}
	}

	private final double[] m_pointBuff = new double[3];

	/**
	 * A scaleFactor of 1.0 does not move anything.
	 *
	 * @exception IllegalArgumentException if
	 *   scaleFactor < 0.001 or if scaleFactor > 1000.0.
	 **/
	public void scaleGraph(double scaleFactor) {
		if ((scaleFactor < 0.001d) || (scaleFactor > 1000.0d))
			throw new IllegalArgumentException("scaleFactor is outside allowable range [0.001, 1000.0]");

		if (m_translationToOrig == null)
			return;

		final AffineTransform3D xform = m_translationToOrig.concatenatePost((new Scale3D(scaleFactor,
		                                                                                 scaleFactor,
		                                                                                 1.0d))
		                                                                                                                              .concatenatePost(m_translationFromOrig));
		IntEnumerator nodes = m_graph.nodes();

		while (nodes.numRemaining() > 0) {
			int node = nodes.nextInt();

			if (!m_graph.isMovableNode(node))
				continue;

			m_pointBuff[0] = m_graph.getNodePosition(node, true);
			m_pointBuff[1] = m_graph.getNodePosition(node, false);
			m_pointBuff[2] = 0.0d;
			xform.transformArr(m_pointBuff);
			m_graph.setNodePosition(node, m_pointBuff[0], m_pointBuff[1]);
		}

		IntEnumerator edges = m_graph.edges();

		while (edges.numRemaining() > 0) {
			int edge = edges.nextInt();

			if (!(m_graph.isMovableNode(m_graph.edgeSource(edge))
			    && m_graph.isMovableNode(m_graph.edgeTarget(edge))))
				continue;

			final int numAnchors = m_graph.getNumAnchors(edge);

			for (int j = 0; j < numAnchors; j++) {
				m_pointBuff[0] = m_graph.getAnchorPosition(edge, j, true);
				m_pointBuff[1] = m_graph.getAnchorPosition(edge, j, false);
				m_pointBuff[2] = 0.0d;
				xform.transformArr(m_pointBuff);
				m_graph.setAnchorPosition(edge, j, m_pointBuff[0], m_pointBuff[1]);
			}
		}
	}
}
