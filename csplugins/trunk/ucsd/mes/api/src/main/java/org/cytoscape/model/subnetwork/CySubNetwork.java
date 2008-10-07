
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

package org.cytoscape.model.subnetwork;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;


/**
 * A CySubNetwork is a {@link CyNetwork} that is contained within a parent
 * {@link CyNewtork}.  See the description in {@link CyRootNetwork} for
 * a more complete description of Cytoscape's meta-network model.
 */
public interface CySubNetwork extends CyNetwork {
	/**
	 * Return the {@link CyMetaNode} that represents this CySubNetwork in the
	 * parent network.
	 *
	 * @return  the node that represents this CySubNetowrk.
	 */
//	CyMetaNode getParentNode();
//	CyRootNetwork getRootNetwork(); //??

	/**
	 * Adds a node to this {@link CySubNetwork}.  Note that the added node
	 * is not a new node, and must already exist in the {@link CyRootNetwork}.
	 * This method also allows {@link CyMetaNode} to be added to subnetworks.
	 *
	 * @param node  CyNode to add to this subnetwork
	 */
	void addNode(CyNode node);

	/**
	 * A shortcut method that Creates a new {@link CyNode} in both this subnetwork 
	 * <b>AND</b> in the {@link CyRootNetwork}.
	 *
	 * @return A new CyNode that exists in both this subnetwork and the associated
	 * {@link CyRootNetwork}.
	 */
	CyNode addNode();

	/**
	 * Removes a node from this {@link CySubNetwork} but not from the {@link CyRootNetwork}.  
	 * The node is removed from the CySubNetwork, but <i>not</i> deleted
	 * from the {@link CyRootNetwork}.
	 *
	 * @param node  Node to remove from this subnetwork
	 */
	boolean removeNode(CyNode node);
}
