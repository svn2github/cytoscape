
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

package org.cytoscape.model.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.event.CyEventHelper;

import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyMetaNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
A linked list implmentation of CyNetwork and CyRootNetwork.
The fundamental idea here is instead of having NodePointer (and EdgePointer) contain
single references to other NodePointers, it instead contains arrays of references to NodePointers.  
The arrays are indexed by an internal subnetwork id where the root network has an id of 0.
All nodes/edges would be created with the root id and subnetworks would add new elements 
to the arrays with new internal network ids.  Queries to the subnetwork thus include the
internal network id to identify which particular subnetwork is being considered.  
The benefit is that we only have one graph object, which all root
and subnetworks share.  The goal is to reduce redundancy and eliminate the need for
synchronizing between rootnetwork and subnetwork.
<p>
This approach is much faster and more memory efficient than the SlowFatGraph approach of using 
maps instead of arrays, however it is still slower and fatter than MGraph (in cases, by about
a factor of 2).
<p>
The difficulty is keeping proper track of the various linked lists and debugging the related
code.  
 */
public class ArrayGraph implements CyRootNetwork {
	private static final int ROOT = 0;

	private final long suid;

	private int numSubNetworks;
	private int nodeCount;
	private int edgeCount;
	private NodePointer firstNode;
	private final List<NodePointer> nodePointers;
	private final List<EdgePointer> edgePointers;
    private final Map<String, CyDataTable> netAttrMgr;
    private final Map<String, CyDataTable> nodeAttrMgr;
    private final Map<String, CyDataTable> edgeAttrMgr;
    private final CyEventHelper eventHelper;
	private final List<CySubNetwork> subNets;
	private final List<CyMetaNode> metaNodes;
	private CySubNetwork base; 

	/**
	 * Creates a new ArrayGraph object.
	 * @param eh The CyEventHelper used for firing events.
	 */
	public ArrayGraph(final CyEventHelper eh) {
		suid = IdFactory.getNextSUID();
		//System.out.println("new ArrayGraph out " + suid);
		numSubNetworks = 0;
		nodeCount = 0;
		edgeCount = 0;
		firstNode = null; 
		nodePointers = new ArrayList<NodePointer>();
		edgePointers = new ArrayList<EdgePointer>();

        netAttrMgr = new HashMap<String, CyDataTable>();
        netAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " network", true));

        nodeAttrMgr = new HashMap<String, CyDataTable>();
        nodeAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " node", true));

        edgeAttrMgr = new HashMap<String, CyDataTable>();
        edgeAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " edge", true));

        eventHelper = eh;

		subNets = new ArrayList<CySubNetwork>();
		metaNodes = new ArrayList<CyMetaNode>();

		base = addSubNetwork(new ArrayList<CyNode>());
	}

	/**
	 * {@inheritDoc}
	 */
	public long getSUID() {
		return suid;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNodeCount() {
		return nodeCount; 
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEdgeCount() {
		return edgeCount; 
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge getEdge(final int e) {
		if ((e >= 0) && (e < edgePointers.size()))
			return edgePointers.get(e).cyEdge;
		else

			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNode getNode(final int n) {
		if ((n >= 0) && (n < nodePointers.size()))
			return nodePointers.get(n).cyNode;
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNodeList() {
		return getNodeList(firstNode,ROOT,nodeCount);
	}

	List<CyNode> getNodeList(final NodePointer first, final int inId, final int numNodes) {
		//System.out.println("private getNodeList " + inId);
		final List<CyNode> ret = new ArrayList<CyNode>(numNodes);
		int numRemaining = numNodes;
		NodePointer node = first;

		synchronized (this) {
		while (numRemaining > 0) {
			// possible NPE here if the linked list isn't constructed correctly
			// this is the correct behavior
			final CyNode toAdd = node.cyNode;
			node = node.nextNode[inId];
			ret.add(toAdd);
			numRemaining--;
		}
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getEdgeList() {
		return getEdgeList(firstNode,ROOT,edgeCount);
	}

	List<CyEdge> getEdgeList(final NodePointer first, final int inId, final int numEdges) {
		final List<CyEdge> ret = new ArrayList<CyEdge>(numEdges);
		int numRemaining = numEdges;
		EdgePointer edge = null;

		synchronized (this) {
		NodePointer node = first;
		while (numRemaining > 0) {
			final CyEdge retEdge;

			if (edge != null) {
				retEdge = edge.cyEdge;
			} else {
				for (edge = node.firstOutEdge[inId]; edge == null; node = node.nextNode[inId], edge = node.firstOutEdge[inId]);

				node = node.nextNode[inId];
				retEdge = edge.cyEdge;
			}

			edge = edge.nextOutEdge[inId];
			numRemaining--;

			ret.add(retEdge);
		}
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNeighborList(final CyNode n, final CyEdge.Type e) {
		return getNeighborList(n,e,ROOT);	
	}

	synchronized List<CyNode> getNeighborList(final CyNode n, final CyEdge.Type e, final int inId) {
		if (!containsNode(n))
			throw new IllegalArgumentException("this node is not contained in the network");

		final NodePointer np = getNodePointer(n);
		final List<CyNode> ret = new ArrayList<CyNode>(countEdges(np, e, inId));
		final Iterator<EdgePointer> it = edgesAdjacent(np, e, inId);
		while (it.hasNext()) {
			final EdgePointer edge = it.next();
			final int neighborIndex = np.index ^ edge.source.index ^ edge.target.index;
			ret.add(getNode(neighborIndex));
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getAdjacentEdgeList(final CyNode n, final CyEdge.Type e) {
		return getAdjacentEdgeList(n,e,ROOT);
	}

	synchronized List<CyEdge> getAdjacentEdgeList(final CyNode n, final CyEdge.Type e, final int inId) {
		if (!containsNode(n))
			throw new IllegalArgumentException("this node is not contained in the network");

		final NodePointer np = getNodePointer(n);
		final List<CyEdge> ret = new ArrayList<CyEdge>(countEdges(np, e, inId));
		final Iterator<EdgePointer> it = edgesAdjacent(np, e, inId);

		while (it.hasNext()) 
			ret.add(it.next().cyEdge);

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getConnectingEdgeList(final CyNode src, final CyNode trg, final CyEdge.Type e) {
		return getConnectingEdgeList(src,trg,e,ROOT);
	}

	synchronized List<CyEdge> getConnectingEdgeList(final CyNode src, final CyNode trg, final CyEdge.Type e, final int inId) {
		if (!containsNode(src))
			throw new IllegalArgumentException("source node is not contained in the network");

		if (!containsNode(trg))
			throw new IllegalArgumentException("target node is not contained in the network");

		final NodePointer srcP = getNodePointer(src);
		final NodePointer trgP = getNodePointer(trg);

		final List<CyEdge> ret = new ArrayList<CyEdge>(Math.min(countEdges(srcP, e, inId), 
		                                                        countEdges(trgP, e, inId)));
		final Iterator<EdgePointer> it = edgesConnecting(srcP, trgP, e, inId);

		while (it.hasNext())
			ret.add(it.next().cyEdge);

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNode addNode() {
		return base.addNode();
	}

	CyMetaNode addNode(final CySubNetwork sub) {
		final NodePointer n;

		//System.out.println("addNode");
		synchronized (this) {
			final int index = nodePointers.size();
			n = new NodePointer(index, new CyNodeImpl(this, index, nodeAttrMgr, sub));
			nodePointers.add(n);
			nodeCount++;
			firstNode = n.insert(firstNode,ROOT);
		}

		return n.cyNode;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeNode(final CyNode n) {
		synchronized (this) {
		//System.out.println("removeNode root");
		if (!containsNode(n))
			return false;
		//System.out.println(" attempting removeNode root");

		// clean up subnetwork pointers
		// this will remove the node from base	
		for ( CySubNetwork sub : subNets )
			sub.removeNode(n);

		// remove adjacent edges from ROOT network
		final List<CyEdge> edges = getAdjacentEdgeList(n, CyEdge.Type.ANY, ROOT);

		for (final CyEdge e : edges)
			removeEdge(e);

		final NodePointer node = getNodePointer(n);
		firstNode = node.remove(firstNode,ROOT);

		nodePointers.set(n.getIndex(), null);

		nodeCount--;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge addEdge(final CyNode s, final CyNode t, final boolean directed) {
		// by calling base.addEdge() we'll update both the base network AND
		// the root network.  base.addEdge() calls edgeAdd().
		return base.addEdge(s,t,directed);
	}

	// will be called from base.addEdge()!
	CyEdge edgeAdd(final CyNode s, final CyNode t, final boolean directed) {

		final EdgePointer e;

		synchronized (this) {
			if (!containsNode(s))
				throw new IllegalArgumentException("source node is not a member of this network");

			if (!containsNode(t))
				throw new IllegalArgumentException("target node is not a member of this network");

			final NodePointer source = getNodePointer(s);
			final NodePointer target = getNodePointer(t);

			final int index = edgePointers.size();
			e = new EdgePointer(source, target, directed, index, 
			                    new CyEdgeImpl(s, t, directed, index, edgeAttrMgr));

			e.insert(ROOT);

			edgePointers.add(e);

			edgeCount++;
		}

		return e.cyEdge;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(final CyEdge edge) {
		synchronized (this) {
		if (!containsEdge(edge))
			return false;

		// clean up subnetwork pointers
		// this will remove the edge from base	
		for ( CySubNetwork sub : subNets )
			sub.removeEdge(edge);

		final EdgePointer e = getEdgePointer(edge);

		e.remove(ROOT);

		edgePointers.set(e.index, null);
		edgeCount--;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsNode(final CyNode node) {
		if (node == null)
			return false;
			//throw new NullPointerException("node is null");

		final int ind = node.getIndex();

		if (ind < 0)
			return false;
			//throw new IllegalArgumentException("node index less than zero");

		final NodePointer thisNode; 

		synchronized (this) {
			if (ind >= nodePointers.size())
				return false;

			thisNode = nodePointers.get(ind);
		}

		if ( thisNode == null )
			return false;	

		return thisNode.cyNode.equals(node);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyEdge edge) {
		if (edge == null)
			return false;

		final int ind = edge.getIndex();

		if (ind < 0)
			return false;

		final EdgePointer thisEdge; 
		synchronized (this) {
			if (ind >= edgePointers.size())
				return false;

			thisEdge = edgePointers.get(ind);
		}

		if ( thisEdge == null )
			return false;

		return thisEdge.cyEdge.equals(edge);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyNode n1, final CyNode n2) {
		return containsEdge(n1,n2,ROOT);
	}

	synchronized boolean containsEdge(final CyNode n1, final CyNode n2, final int inId) {
		//System.out.println("private containsEdge");
		if (!containsNode(n1)) {
			//System.out.println("private containsEdge doesn't contain node1 " + inId);
			return false;
		}

		if (!containsNode(n2)) {
			//System.out.println("private containsEdge doesn't contain node2 " + inId);
			return false;
		}

		final Iterator<EdgePointer> it = edgesConnecting(getNodePointer(n1), getNodePointer(n2), 
		                                                 CyEdge.Type.ANY,inId);

		return it.hasNext();
	}


	/**
	 * {@inheritDoc}
	 */
	public CyRow getCyRow(final String namespace) {
		if (namespace == null)
			throw new NullPointerException("namespace is null");

		final CyRow ret; 
		final CyDataTable mgr;
	
		synchronized (this) {
			mgr = netAttrMgr.get(namespace);

			if (mgr == null)
				throw new NullPointerException("attribute manager is null for namespace: " + namespace);

			ret = mgr.getRow(suid);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyRow attrs() {
		return getCyRow(CyNetwork.DEFAULT_ATTRS);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, CyDataTable> getNetworkCyDataTables() {
		return netAttrMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, CyDataTable> getNodeCyDataTables() {
		return nodeAttrMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, CyDataTable> getEdgeCyDataTables() {
		return edgeAttrMgr;
	}


	private Iterator<EdgePointer> edgesAdjacent(final NodePointer n, final CyEdge.Type edgeType, final int inId) {
		assert(n!=null);

		final EdgePointer[] edgeLists;

		final boolean incoming = assessIncoming(edgeType);
		final boolean outgoing = assessOutgoing(edgeType);
		final boolean undirected = assessUndirected(edgeType);

		if (undirected || (outgoing && incoming)) 
			edgeLists = new EdgePointer[] { n.firstOutEdge[inId], n.firstInEdge[inId] };
		else if (outgoing) // Cannot also be incoming.
			edgeLists = new EdgePointer[] { n.firstOutEdge[inId], null };
		else if (incoming) // Cannot also be outgoing.
			edgeLists = new EdgePointer[] { null, n.firstInEdge[inId] };
		else // All boolean input parameters are false.
			edgeLists = new EdgePointer[] { null, null };

		final int inEdgeCount = countEdges(n, edgeType, inId);
		//System.out.println("edgesAdjacent edgeCount: " + inEdgeCount);

		return new Iterator<EdgePointer>() {
				private int numRemaining = inEdgeCount;
				private int edgeListIndex = -1;
				private EdgePointer edge;

				public boolean hasNext() {
					return numRemaining > 0;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}

				public EdgePointer next() {
					// get the first non-null edgePointer
					while (edge == null)
						edge = edgeLists[++edgeListIndex];

					int returnIndex = -1;

					// look at outgoing edges
					if (edgeListIndex == 0) {
						// go to the next edge if the current edge is NOT either
						// directed when we want outgoing or undirected when we
						// want undirected
						while ((edge != null) && 
						       !((outgoing && edge.directed) || (undirected && !edge.directed))) {
							edge = edge.nextOutEdge[inId];

							// we've hit the last edge in the list
							// so increment edgeListIndex so we go to 
							// incoming, set edge, and break
							if (edge == null) {
								edge = edgeLists[++edgeListIndex];
								break;
							}
						}
					
						// if we have a non-null outgoing edge set the 
						// edge and return values
						// since edgeListIndex is still for outgoing we'll
						// just directly to the return
						if ((edge != null) && (edgeListIndex == 0)) {
							returnIndex = edge.index;
							edge = edge.nextOutEdge[inId];
						}
					}
	
					// look at incoming edges
					if (edgeListIndex == 1) {
						
						// Important NOTE!!!
						// Possible null pointer exception here if numRemaining, 
						// i.e. edgeCount is wrong. However, this is probably the
						// correct behavior since it means the linked lists are
						// messed up and there isn't a graceful way to deal.


						// go to the next edge if the edge is a self edge AND 
						// either directed when but we're looking for outgoing or
						// undirected when we're looking for undirected 
						// OR 
						// go to the next edge if the current edge is NOT either
						// directed when we want incoming or undirected when we
						// want undirected
						while (((edge.source.index == edge.target.index)
						       && ((outgoing && edge.directed) || (undirected && !edge.directed)))
						       || !((incoming && edge.directed) || (undirected && !edge.directed))) {
							edge = edge.nextInEdge[inId];
						}

						returnIndex = edge.index;
						edge = edge.nextInEdge[inId];
					}

					numRemaining--;
					return edgePointers.get(returnIndex);
				}
			};
	}

	private Iterator<EdgePointer> edgesConnecting(final NodePointer node0, final NodePointer node1,
	                                              final CyEdge.Type et, final int inId) {
		assert(node0!=null);
		assert(node1!=null);

		final Iterator<EdgePointer> theAdj;
		final int nodeZero;
		final int nodeOne;

		// choose the smaller iterator
		if (countEdges(node0, et, inId) <= countEdges(node1, et, inId)) {
			//System.out.println("edgesConnecting fewer edges node0: " + node0.index);
			theAdj = edgesAdjacent(node0, et, inId);
			nodeZero = node0.index;
			nodeOne = node1.index;
		} else {
			//System.out.println("edgesConnecting fewer edges node1: " + node1.index);
			theAdj = edgesAdjacent(node1, et, inId);
			nodeZero = node1.index;
			nodeOne = node0.index;
		}

		return new Iterator<EdgePointer>() {
				private int nextIndex = -1;

				private void ensureComputeNext() {
					if (nextIndex != -1) {
						return;
					}

					while (theAdj.hasNext()) {
						final EdgePointer e = theAdj.next();

						if (nodeOne == (nodeZero ^ e.source.index ^ e.target.index)) {
							nextIndex = e.index;

							return;
						}
					}

					nextIndex = -2;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}

				public boolean hasNext() {
					ensureComputeNext();

					return (nextIndex >= 0);
				}

				public EdgePointer next() {
					ensureComputeNext();

					final int returnIndex = nextIndex;
					nextIndex = -1;

					return edgePointers.get(returnIndex);
				}
			};
	}

	private boolean assessUndirected(final CyEdge.Type e) {
		return ((e == CyEdge.Type.UNDIRECTED) || (e == CyEdge.Type.ANY));
	}

	private boolean assessIncoming(final CyEdge.Type e) {
		return ((e == CyEdge.Type.DIRECTED) || (e == CyEdge.Type.ANY) || (e == CyEdge.Type.INCOMING));
	}

	private boolean assessOutgoing(final CyEdge.Type e) {
		return ((e == CyEdge.Type.DIRECTED) || (e == CyEdge.Type.ANY) || (e == CyEdge.Type.OUTGOING));
	}

	private int countEdges(final NodePointer n, final CyEdge.Type edgeType, final int inId) {
		assert(n!=null);
		final boolean undirected = assessUndirected(edgeType);
		final boolean incoming = assessIncoming(edgeType);
		final boolean outgoing = assessOutgoing(edgeType);

		//System.out.println("countEdges un: " + undirected + " in: " + incoming + " out: " + outgoing);

		int tentativeEdgeCount = 0;

		if (outgoing) { 
			//System.out.println("  countEdges outgoing: " + n.outDegree[inId]);
			tentativeEdgeCount += n.outDegree[inId];
		}

		if (incoming) { 
			//System.out.println("  countEdges incoming: " + n.inDegree[inId]);
			tentativeEdgeCount += n.inDegree[inId];
		}

		if (undirected) {
			//System.out.println("  countEdges undirected: " + n.undDegree[inId]);
			tentativeEdgeCount += n.undDegree[inId];
		}

		if (outgoing && incoming) {
			//System.out.println("  countEdges out+in MINUS: " + n.selfEdges[inId]);
			tentativeEdgeCount -= n.selfEdges[inId];
		}

		//System.out.println("  countEdges final: " + tentativeEdgeCount);
		return tentativeEdgeCount;
	}

	EdgePointer getEdgePointer(final CyEdge edge) {
		assert(edge != null);
		assert(edge.getIndex()>=0);
		assert(edge.getIndex()<edgePointers.size());

		return edgePointers.get(edge.getIndex());
	}

	NodePointer getNodePointer(final CyNode node) {
		assert(node != null);
		assert(node.getIndex()>=0);
		assert(node.getIndex()<nodePointers.size());

		return nodePointers.get(node.getIndex());
	}

	/**
	 * Tests object for equality with this object.
	 * @param o The object to test for equality.
	 * @return True if the object is an ArrayGraph and the SUID matches, false otherwise.
	 */
	@Override 
   	public boolean equals(final Object o) {
   		if (!(o instanceof ArrayGraph))
			return false;

		final ArrayGraph ag = (ArrayGraph) o;

		return ag.suid == this.suid;
	}

	/**
	 * Returns a hashcode for this object. 
	 * @return A mangled version of the SUID. 
	 */
   	@Override
   	public int hashCode() {
		return (int) (suid ^ (suid >>> 32));
	}

	/**
	 * {@inheritDoc}
	 */
	public CySubNetwork addSubNetwork(final List<CyNode> nodes) {
		return addSubNetwork(nodes, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public CySubNetwork addSubNetwork(final List<CyNode> nodes, final List<CyEdge> edges) {
		final int newId = ++numSubNetworks;
		final ArraySubGraph sub = new ArraySubGraph(this,nodes,edges,newId);
		subNets.add(sub);
		return sub;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubNetwork(final CySubNetwork sub) {
		if ( sub == null )
			throw new NullPointerException("subnetwork is null");

		if ( sub.equals( base ) )
			throw new IllegalArgumentException("can't remove base subnetwork");

		if ( !subNets.contains( sub ) )
			throw new IllegalArgumentException("subnetwork not contained in root network");

		final List<CyNode> subNodes = sub.getNodeList();
		for ( CyNode node : subNodes )
			sub.removeNode(node);

		subNets.remove( sub );
	}

	/**
	 * {@inheritDoc}
	 */
	public CyMetaNode addMetaNode(final CySubNetwork sub) {
		
		//System.out.println("meta addNode sub");
		final CyMetaNode newNode = addNode( sub );

		// TODO do we need to preserve directedness?
		for ( CyNode exNode : sub.getExternalNeighborSet() )
			// important to call edgeAdd so as not to add this
			// metanode edge to the base network
			edgeAdd(newNode, exNode, false); 

		metaNodes.add(newNode);

		return newNode; 
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMetaNode(final CyMetaNode n) {
		if (!containsNode(n))
			return;

		// first clean up the node pointer information for the nodes
		// in the subnetwork
		final CySubNetwork sub = n.getSubNetwork();
		if ( subNets.contains( sub ) )
			removeSubNetwork( sub );
		// else assume it's already been done

		metaNodes.remove(n);

		removeNode(n);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CySubNetwork> getSubNetworkList() {
		return new ArrayList<CySubNetwork>(subNets);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyMetaNode> getMetaNodeList() {
		return new ArrayList<CyMetaNode>(metaNodes);
	}

	/**
	 * {@inheritDoc}
	 */
	public CySubNetwork getBaseNetwork() {
		return base;
	}
}
