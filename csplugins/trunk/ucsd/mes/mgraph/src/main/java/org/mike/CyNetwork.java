
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.mike;

import java.util.List;


/**
 * DOCUMENT ME!
  */
public interface CyNetwork {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	CyNode addNode();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	boolean removeNode(CyNode node);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 * @param isDirected DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	CyEdge addEdge(CyNode source, CyNode target, boolean isDirected);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	boolean removeEdge(CyEdge edge);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	int getNodeCount();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	int getEdgeCount();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	List<CyNode> getNodeList();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	List<CyEdge> getEdgeList();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	boolean containsNode(CyNode node);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	boolean containsEdge(CyEdge edge);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	boolean containsEdge(CyNode from, CyNode to);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param index DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	CyNode getNode(int index);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param index DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	CyEdge getEdge(int index);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param edgeType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	List<CyNode> getNeighborList(CyNode node, EdgeType edgeType);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param edgeType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	List<CyEdge> getAdjacentEdgeList(CyNode node, EdgeType edgeType);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 * @param edgeType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	List<CyEdge> getConnectingEdgeList(CyNode source, CyNode target, EdgeType edgeType);
}
