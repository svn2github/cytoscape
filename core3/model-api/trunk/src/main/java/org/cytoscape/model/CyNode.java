/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import org.cytoscape.model.events.SetNestedNetworkEvent;
import org.cytoscape.model.events.UnsetNestedNetworkEvent;


/**
 * An object that represents a node (vertex) within a network 
 * of nodes and edges.
 */
public interface CyNode extends CyTableEntry {
	
	/** The column name for the nested network associated with a <code>CyNode</code> in the user table. */
	final static String NESTED_NETWORK_ATTR = "NestedNetwork";

	/** The column name for the attribute that allows testing of whether a <code>CyNode</code>
	    has an associated nested network or not.  (Found in the user table.) */
	final static String HAS_NESTED_NETWORK_ATTR = "hasNestedNetwork";

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
	 *
	 * Note that this if a previous nested network is being replaced or nulled out, an
	 * {@link UnsetNestedNetworkEvent} will be fired and if a new nested network will be set a
	 * {@link SetNestedNetworkEvent} will be fired.  Furthermore the (@link NESTED_NETWORK_ATTR}
	 * and {@link HAS_NESTED_NETWORK_ATTR} columns in the user table will be updated.
	 */
	void setNestedNetwork(CyNetwork nestedNetwork);
}
