
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
 * that are part of this CyRootNetwork, including all {@link CyMetaNodes}.
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
	 * Create a {@link CyMetaNode} and its associated {@link CySubNetwork}.
	 * The {@link CySubNetwork} contains all of the nodes provided and all
	 * of the edges connecting those nodes.  For all edges between those nodes
	 * and other nodes not part of the subnetwork duplicate edges will be
	 * created between the created {@link CyMetaNode} and the external nodes.
	 *
	 * @param sub  the {@link CySubNetwork} this metanode should be added to
	 *
	 * @return  The created {@link CyMetaNode}.
	 */
	CyMetaNode addMetaNode(CySubNetwork sub);

	/**
	 * Removes the metanode and the CySubNetwork, not the nodes and edges contained in the
	 * subnetwork.
	 *
	 * @param node  the {@link CyMetaNode} that represents the {@link CySubNetwork} to remove.
	 */
	void removeMetaNode(CyMetaNode node);

	/**
	 * Will return A list of all {@link CyMetaNode}s contained in this root network.
	 * @return A list of all {@link CyMetaNode}s contained in this root network.
	 */
	List<CyMetaNode> getMetaNodeList();


	/**
	 * Return the {@link CyNetwork} that represents the base 
	 * or "flattened" {@link CyNetwork} that contains all nodes and edges 
	 * <b>exclusive</b> of any {@link CyMetaNode}s  or {@link CyEdge}s 
	 * connecting metanodes.  In the case of a {@link CyRootNetwork}
	 * with no {@link CySubNetwork}s, this contains exactly the same nodes and edges
	 * as if it were a normal {@link CyNetwork}. 
	 *
	 * @return the base network
	 */
	CySubNetwork getBaseNetwork();


	/**
	 * Create a subnetwork to be used independently or for creation
	 * of a {@link CyMetaNode}. The network will include all edges
	 * from the {@link CyRootNetwork} where both the source <b>AND</b>
	 * target are contained in the specified list.
	 *
	 * @param nodes The nodes contained in this {@link CyRootNetwork}
	 * that represent the nodes to be included in the {@link CySubNetwork}.
	 * @return A {@link CySubNetwork} created from the specified nodes.
	 */
	CySubNetwork addSubNetwork(List<CyNode> nodes);

	/**
	 * Create a subnetwork to be used independently or for creation
	 * of a {@link CyMetaNode}. The network will include only the
	 * edges specified in the list.
	 *
	 * @param nodes The nodes contained in this {@link CyRootNetwork}
	 * that represent the nodes to be included in the {@link CySubNetwork}.
	 * @param edges The edges contained in this {@link CyRootNetwork}
	 * <b>AND</b> that connect specified nodes. Any edges that do not
	 * connect specified nodes will result in an {@link IllegalArgumentException}.
	 * @return A {@link CySubNetwork} created from the specified nodes and edges.
	 */
	CySubNetwork addSubNetwork(List<CyNode> nodes, List<CyEdge> edges);

	/**
	 * Will remove the specified {@link CySubNetwork} from this root network.
	 * @param sub The subnetwork to be removed from this root network.
	 */
	void removeSubNetwork(CySubNetwork sub);

	/**
	 * Return all of the subnetworks that are part of this meta-network.
	 *
	 * @return the list of {@link CySubNetwork}s.
	 */
	List<CySubNetwork> getSubNetworkList();
}
