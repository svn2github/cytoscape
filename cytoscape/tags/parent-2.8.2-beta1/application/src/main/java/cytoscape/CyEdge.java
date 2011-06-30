/*
  File: CyEdge.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape;

import cytoscape.giny.CytoscapeFingRootGraph;

import giny.model.*;


/**
 *
 */
public class CyEdge implements giny.model.Edge {
	// Variables specific to public get/set methods.
	CytoscapeFingRootGraph m_rootGraph = null;
	int m_rootGraphIndex = 0;
	String m_identifier = null;

	/**
	 * Creates a new CyEdge object.
	 *
	 * @param root  the RootGraph to put the edge into
	 * @param rootGraphIndex  the index to assign to this edge
	 */
	public CyEdge(RootGraph root, int rootGraphIndex) {
		this.m_rootGraph = (CytoscapeFingRootGraph) root;
		this.m_rootGraphIndex = rootGraphIndex;
		this.m_identifier = new Integer(m_rootGraphIndex).toString();
	}

	/**
	 *  Return the source node for this edge.  All edges have a source
	 *  Node and a target Node.  For directed edges, the edge points from
	 *  the source node to the target node, but for undirected edges, the
	 *  definition of source and target is defined by the edge but
	 *  which node is the source and which node is the target does not
	 *  impact the behavior.
	 *
	 * @return  The source Node for this edge.  Note that this is returned as a Node
	 * rather than a CyNode.  Generally, the Node may be safely cast to a CyNode.
	 */
	public giny.model.Node getSource() {
		return m_rootGraph.getNode(m_rootGraph.getEdgeSourceIndex(m_rootGraphIndex));
	}

	/**
	 *  Return the target node for this edge.  All edges have a source
	 *  Node and a target Node.  For directed edges, the edge points from
	 *  the source node to the target node, but for undirected edges, the
	 *  definition of source and target is defined by the edge but
	 *  which node is the source and which node is the target does not
	 *  impact the behavior.
	 *
	 * @return  The target Node for this edge.  Note that this is returned as a Node
	 * rather than a CyNode.  Generally, the Node may be safely cast to a CyNode.
	 */
	public giny.model.Node getTarget() {
		return m_rootGraph.getNode(m_rootGraph.getEdgeTargetIndex(m_rootGraphIndex));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isDirected() {
		return m_rootGraph.isEdgeDirected(m_rootGraphIndex);
	}

	/**
	 *  Get the root graph for this edge.
	 *
	 * @return  the root graph
	 */
	public RootGraph getRootGraph() {
		return m_rootGraph;
	}

	/**
	 *  Get the root graph index for this edge
	 *
	 * @return  the root graph index
	 */
	public int getRootGraphIndex() {
		return m_rootGraphIndex;
	}

	/**
	 * Return the "name" of an edge
	 *
	 * @return string representation of the edge
	 */
	public String toString() {
		return getIdentifier();
	}

	/**
	 *  Return the edge identifier.  Usually this is something of the
	 *  form: "SourceNode (interaction) TargetNode".
	 *
	 * @return  edge identifier
	 */
	public String getIdentifier() {
		return m_identifier;
	}

	/**
	 *  Set the identifier for this edge
	 *
	 * @param new_id The new identifier
	 *
	 * @return  always returns true
	 */
	public boolean setIdentifier(String new_id) {
		if (new_id == null) {
			m_rootGraph.setEdgeIdentifier(m_identifier, 0);
		} else {
			m_rootGraph.setEdgeIdentifier(new_id, m_rootGraphIndex);
		}

		m_identifier = new_id;

		return true;
	}

	/**
	 * A static method used to create edge identifiers.
	 */
	public static String createIdentifier(String source, String attribute_value, String target) {
		return source + " (" + attribute_value + ") " + target;
	}
}
