
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
 * complex meta-network structure that can used for a variety
 * of other use cases, including implementing subnetworks
 * and shared nodes between networks.  Beyond the concepts
 * and methods provided by {@link CyNetwork} a meta-network
 * adds three new concepts:
 * <ul><li>A <b>CyRootNetwork</b> is a {@link CyNetwork} that
 * adds methods for maintaining the meta-network.
 * All {@link CyNode}s and {@link CyEdge}s in all {@link CyNetwork}s
 * that are part of this CyRootNetwork, including all {@link CyMetaNoes}.
 * <li>A {@link CySubNetwork} is a group of nodes and edges
 * that are a sub-network of another {@link CyNetwork}.  The
 * {@link CySubNetwork} is represented in a {@link CyNetwork} by
 * a {@link CyMetaNode}.  A {@link CySubNetwork} may be thought
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

 * <b>QUESTIONS:</b>
 * <ul>
 * <li><i>In general, I think the root/cy/sub network relationship is
 * a bit confusing right now and the current implementation leaves
 * something to be desired.</i></li>
 * <li>How do we get the flattened network?  I'm assuming
 * that the flattened network is what the CyRootNetwork returns
 * when you call getNodeList() and getEdgeList().  That way, the simplest
 * implementation would always be looking at the flattened network,
 * which is what I think we want.</li>
 * <li><i>The flattened network will be from getAllNodes() and getAllEdges().
 * At the moment, getNodeList() and getEdgeList() only ever return 
 * the network as created before any metanodes were added.  I'm seeing
 * a bug where a normal node added *after* a metanode was created might
 * not be captured correctly.</i></li>
 * <li>If that is the case, how do we extract the top group of
 * CySubNetworks?  Do we need to add a getRootSubNetwork() method?
 * I'm going to assume we do, recognizing that this may be something
 * we might need to re-evaluate</li>
 * <li><i>No explicit support for this right now, other than the fact that
 * you have a reference to the original CyRootNetwork that you created
 * the metanode on. When calling getNodeList and getEdgeList, you'll see
 * the original network. </i></li>
 * <li>How do I add a CyMetaNode to a particular sub-network?  CyNetwork
 * does not have an addNode(CyNode) method.  Should this be part of the
 * CyRootNetwork interface?</li>
 * <li><i>Add the metanode using {@link CySubNetwork#copyToNetwork(CyNode)}
 * since CyMetaNode extends CyNode, this should work.</i></li>
 * </ul>
 */
public interface CyRootNetwork extends CyNetwork {

	/**
	 * Return <i>all</i> the {@link CyNode}s that are part of
	 * this meta-network, including all {@link CyMetaNode}s.
	 *
	 * @return a list of {@link CyNode}s that are part of this network
	 */
	List<CyNode> getAllNodes();

	/**
	 * Return <i>all</i> the {@link CyEdge}s that are part of
	 * this meta-network, including all @{link CyEdge}s that
	 * link {@link CyMetaNode}s.
	 *
	 * @return a list of {@link CyEdge}s that are part of this network
	 */
	List<CyEdge> getAllEdges();

	/**
	 * Create a {@link CyMetaNode} and its associated {@link CySubNetwork}.
	 * The {@link CySubNetwork} contains all of the nodes provided and all
	 * of the edges connecting those nodes.  For all edges between those nodes
	 * and other nodes not part of the subnetwork duplicate edges will be
	 * created between the created {@link CyMetaNode} and the external nodes.
	 *
	 * @param network  the {@link CyNetwork} this metanode should be added to
	 * @param nodes  the list of nodes to add to the {@link CySubNetwork} created.
	 *
	 * @return  The created {@link CyMetaNode}.
	 */
	CyMetaNode addMetaNode(List<CyNode> nodes);
//	CyMetaNode addMetaNode(CyNetwork network, List<CyNode> nodes);

	/**
	 * Create a {@link CyMetaNode} and its associated {@link CySubNetwork}.
	 * The {@link CySubNetwork} contains all of the nodes and the edges provided
	 * that connect those nodes.  For provided edges that connect those nodes
	 * to nodes outside of the created {@link CySubNetwork} 
	 * duplicate edges will be
	 * created between the created {@link CyMetaNode} and the external nodes.
	 *
	 * @param network  the {@link CyNetwork} this metanode should be added to
	 * @param nodes  the list of nodes to add to the {@link CySubNetwork} created.
	 * @param edges  the list of edges to add to the {@link CySubNetwork} created.
	 *
	 * @return  The created {@link CyMetaNode}.
	 */
//	CyMetaNode addMetaNode(CyNetwork network, List<CyNode> nodes, List<CyEdge> edges);

	/**
	 * Removes the metanode and the CySubNetwork, not the nodes and edges contained in the
	 * subnetwork.
	 *
	 * @param node  the {@link CyMetaNode} that represents the {@link CySubNetwork} to remove.
	 */
	void removeMetaNode(CyMetaNode node);

	/**
	 * Return all of the subnetworks that are part of this meta-network.
	 *
	 * @return the list of {@link CySubNetwork}s.
	 */
	List<CySubNetwork> getAllSubNetworks();

	/**
	 * Return the {@link CyNetwork} that represents the top containing {@link CyNetwork}
	 * that is <i>not</i> a {@link CyRootNetwork}.  In the case of a {@link CyRootNetwork}
	 * with no {@link CySubNetwork}s, this contains exactly the same nodes and edges
	 * as the "flattened" {@link CyNetwork}, otherwise, it contains the top network, including
	 * any contained {@link CyMetaNode}s that are in the network, but not expanded.
	 *
	 * @return the top network
	 */
//	CyNetwork getTopNetwork();
}
