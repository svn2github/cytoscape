
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

package org.cytoscape.model;

import java.util.List;


/**
 * An object that represents a node (vertex) within a network 
 * of nodes and edges.
 */
public interface CyNode extends CyTableEntry {
	
	/**
	 * An index of this node within this network.  The index is guaranteed to
	 * be between 0 and (the number of nodes in the network) - 1. This index
	 * can be used as a parameter to {@link CyNetwork#getNode}, however it is not
	 * necessarily an index into {@link CyNetwork#getNodeList}.
	 * @return An index for this node within this network.
	 */
	int getIndex();

	/**
	 * Returns the nested network associated with this node.  If no nested
	 * network has been specified this method will return null.
	 * 
	 * @return A reference to the nested CyNetwork if one exists, 
	 * returns null otherwise.
	 */
	CyNetwork getNestedNetwork();

	/**
	 * Allows the nested network for this network to be set. Only one
	 * nested network may be set for a given node. Any CyNetwork may
	 * be used (the network does not need to be a subnetwork of the 
	 * network that contains this node). To unset a nested network, use
	 * null as an argument. 
	 *
	 * @param nestedNetwork The network that is to be referenced by this node. If
	 * this value is null, any existing reference will be removed.
	 */
	void setNestedNetwork(CyNetwork nestedNetwork);
}
