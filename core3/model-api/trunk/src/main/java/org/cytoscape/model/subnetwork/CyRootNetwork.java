
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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import java.util.List;


/**
 * CyRootNetwork is an interface for managing Cytoscape's 
 * meta-network implementation.  While most applications (and users!)
 * will treat each {@link CyNetwork} created within Cytoscape
 * as an independent network, beginning with Cytoscape 3.0, 
 * Cytoscape has provided a mechanism for implementing a more
 * complex meta-network structure that can be used for a variety
 * of other use cases, including implementing subnetworks
 * and shared nodes between subnetworks.  Beyond the concepts
 * and methods provided by {@link CyNetwork} a meta-network
 * adds three new concepts:
 * <ul><li>A <b>CyRootNetwork</b> is a {@link CyNetwork} that
 * adds methods for maintaining the meta-network.
 * All {@link CyNode}s and {@link CyEdge}s in all {@link CySubNetwork}s
 * that are part of this CyRootNetwork, including all {@link CyMetaNode}s.
 * <li>A {@link CySubNetwork} is a group of nodes and edges
 * that are a sub-network of a {@link CyRootNetwork}.  The
 * {@link CySubNetwork} can be represented within another {@link CySubNetwork}
 * by a {@link CyMetaNode}.  A {@link CySubNetwork} may be thought
 * of as a projection of the graph implemented by the <b>CyRootNetwork</b>.
 * <li>A {@link CyMetaNode} is a {@link CyNode} that represents
 * a {@link CySubNetwork} in a {@link CyNetwork}.
 * </ul>
 * These three additional interfaces can be used to create and
 * maintain a complex graph structure.  In order to avoid requiring
 * all plugins and other uses of this package to test for
 * and support the presence of a meta-network, Cytoscape provides
 * a {@link CyNetwork} that is a "flattened" version of the
 * meta-network (essentially all {@link CyNode}s and {@link CyEdge}s
 * except {@link CyMetaNode}s and their associated {@link CyEdge}s.
 */
public interface CyRootNetwork extends CyNetwork {

	/**
	 * Create a {@link CyMetaNode} which will contain an empty {@link CySubNetwork}.
	 *
	 * @return  The created {@link CyMetaNode}.
	 */
	CyMetaNode addMetaNode();

	/**
	 * Removes the metanode and and its {@link CySubNetwork}, but not the nodes 
	 * and edges contained in the subnetwork.
	 *
	 * @param node  the {@link CyMetaNode} to remove.
	 */
	void removeMetaNode(CyMetaNode node);

	/**
	 * Will return A list of all {@link CyMetaNode}s contained in this root network.
	 * @return A list of all {@link CyMetaNode}s contained in this root network.
	 */
	List<CyMetaNode> getMetaNodeList();

	/**
	 * The initial network of {@link CyNode}s and {@link CyEdge}s, that excludes any
	 * {@link CyMetaNode}s.
	 */
	CySubNetwork getBaseNetwork();

	/**
	 * Will convert any {@link CyNode} object into a {@link CyMetaNode}. If the
	 * node is already metanode, this method will return the same object. 
	 */
	CyMetaNode convert(CyNode node);
}
