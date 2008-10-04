
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


/**
A linked list implmentation of CyNetwork in anticipation of support for subnetworks.
The fundamental idea here is instead of having NodePointer (and EdgePointer) contain
references to other NodePointers, it instead contains a map of NodePointers.  The
key to the maps is the network suid.  All nodes/edges would be created with the root
suid and subnetworks would add new elements to the map with new internal suids.
Queries to the network then include an suid to identify which particular subnetwork
is being considered.  The benefit is that we only have one graph object, which all root 
and subnetworks share, which makes the implementation (relatively) straightforward.
<p>
However, there appear to be two significant defects.  First, each node/edge now contains
several map objects, which substantially increases the memory footprint of the objects 
by something like a factor of 5 relative to MGraph.  Second, the indirection of getting 
references from the maps seems to slow things down substantially, apparently by a factor 
of 5-7 relative to MGraph.
 */
public class SlowFatGraph implements CyNetwork {
	private final long suid;
	private int nodeCount;
	private int edgeCount;
	private Map<Long,NodePointer> firstNode;
	private final List<NodePointer> nodePointers;
	private final List<EdgePointer> edgePointers;
    private final Map<String, CyDataTable> netAttrMgr;
    private final Map<String, CyDataTable> nodeAttrMgr;
    private final Map<String, CyDataTable> edgeAttrMgr;
    private final CyEventHelper eventHelper;

	/**
	 * Creates a new SlowFatGraph object.
	 * @param eh The CyEventHelper used for firing events.
	 */
	public SlowFatGraph(final CyEventHelper eh) {
		suid = IdFactory.getNextSUID();
		nodeCount = 0;
		edgeCount = 0;
		firstNode = new HashMap<Long,NodePointer>();
		firstNode.put(suid,null);
		nodePointers = new ArrayList<NodePointer>();
		edgePointers = new ArrayList<EdgePointer>();

        netAttrMgr = new HashMap<String, CyDataTable>();
        netAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " network", true));

        nodeAttrMgr = new HashMap<String, CyDataTable>();
        nodeAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " node", true));

        edgeAttrMgr = new HashMap<String, CyDataTable>();
        edgeAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " edge", true));

        eventHelper = eh;
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
		return getNodeList(suid);
	}

	private List<CyNode> getNodeList(final long inId) {
		final List<CyNode> ret = new ArrayList<CyNode>(nodeCount);
		int numRemaining = nodeCount;
		NodePointer node = firstNode.get(inId);

		while (numRemaining > 0) {
			final CyNode toAdd = node.cyNode;
			node = node.nextNode.get(inId);
			ret.add(toAdd);
			numRemaining--;
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getEdgeList() {
		return getEdgeList(suid);
	}

	private List<CyEdge> getEdgeList(final long inId) {
		final List<CyEdge> ret = new ArrayList<CyEdge>(edgeCount);
		int numRemaining = edgeCount;
		NodePointer node = firstNode.get(inId);
		EdgePointer edge = null;

		while (numRemaining > 0) {
			final CyEdge retEdge;

			if (edge != null) {
				retEdge = edge.cyEdge;
			} else {
				for (edge = node.firstOutEdge.get(inId); edge == null; node = node.nextNode.get(inId), edge = node.firstOutEdge.get(inId));

				node = node.nextNode.get(inId);
				retEdge = edge.cyEdge;
			}

			edge = edge.nextOutEdge.get(inId);
			numRemaining--;

			ret.add(retEdge);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNeighborList(final CyNode n, final CyEdge.Type e) {
		return getNeighborList(n,e,suid);	
	}

	private List<CyNode> getNeighborList(final CyNode n, final CyEdge.Type e, final long inId) {
		if (!containsNode(n,inId))
			throw new IllegalArgumentException("this node is not contained in the network");

		final NodePointer np = getNodePointer(n);
		final List<CyNode> ret = new ArrayList<CyNode>(countEdges(np, e, inId));
		final Iterator<EdgePointer> it = edgesAdjacent(np, e, inId);

		while (it.hasNext()) {
			final EdgePointer edge = it.next();
			final int neighborIndex = np.index ^ edge.source.index ^ edge.target.index;
			final NodePointer nnp = nodePointers.get(neighborIndex);
			if ( nnp.knownNetworks.contains(inId) )
				ret.add(getNode(neighborIndex));
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getAdjacentEdgeList(final CyNode n, final CyEdge.Type e) {
		return getAdjacentEdgeList(n,e,suid);
	}

	private List<CyEdge> getAdjacentEdgeList(final CyNode n, final CyEdge.Type e, final long inId) {
		if (!containsNode(n,inId))
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
		return getConnectingEdgeList(src,trg,e,suid);
	}

	private List<CyEdge> getConnectingEdgeList(final CyNode src, final CyNode trg, final CyEdge.Type e, final long inId) {
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
		final NodePointer n;

		synchronized (this) {
			n = new NodePointer(nodePointers.size(), this, firstNode.get(suid));
			nodePointers.add(n);
			nodeCount++;
			firstNode.put(suid,n);
		}

		return n.cyNode;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeNode(final CyNode n) {
		if (!containsNode(n))
			return false;

		// remove adjacent edges
		final List<CyEdge> edges = getAdjacentEdgeList(n, CyEdge.Type.ANY);

		for (final CyEdge e : edges)
			removeEdge(e);

		final NodePointer node = getNodePointer(n);

		// now clean up node
		if (node.prevNode.get(suid) != null)
			node.prevNode.get(suid).nextNode.put(suid,node.nextNode.get(suid));
		else
			firstNode.put(suid, node.nextNode.get(suid));

		if (node.nextNode.get(suid) != null)
			node.nextNode.get(suid).prevNode.put(suid, node.prevNode.get(suid));

		nodePointers.set(node.index, null);

		node.prevNode.put(suid, null);
		//node.firstOutEdge = null;
		node.firstOutEdge.clear();
		//node.firstInEdge = null;
		node.firstInEdge.clear();
		nodeCount--;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge addEdge(final CyNode s, final CyNode t, final boolean directed) {
		final EdgePointer e;

		synchronized (this) {
			if (!containsNode(s))
				throw new IllegalArgumentException("source node is not a member of this network");

			if (!containsNode(t))
				throw new IllegalArgumentException("target node is not a member of this network");

			final NodePointer source = getNodePointer(s);
			final NodePointer target = getNodePointer(t);

			e = new EdgePointer(source, target, directed, edgePointers.size());

			edgePointers.add(e);

			edgeCount++;

			e.nextOutEdge.put(suid, source.firstOutEdge.get(suid));

			if (source.firstOutEdge.get(suid) != null) 
				source.firstOutEdge.get(suid).prevOutEdge.put(suid,e);

			source.firstOutEdge.put(suid,e);

			e.nextInEdge.put(suid,target.firstInEdge.get(suid));

			if (target.firstInEdge.get(suid) != null) 
				target.firstInEdge.get(suid).prevInEdge.put(suid,e);

			target.firstInEdge.put(suid,e);
		}

		return e.cyEdge;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(final CyEdge edge) {
		if (!containsEdge(edge))
			return false;

		final EdgePointer e = getEdgePointer(edge);

		final NodePointer source = nodePointers.get(e.source.index);
		final NodePointer target = nodePointers.get(e.target.index);

		if (e.prevOutEdge.get(suid) != null) 
			e.prevOutEdge.get(suid).nextOutEdge.put(suid,e.nextOutEdge.get(suid));
		else 
			source.firstOutEdge.put(suid,e.nextOutEdge.get(suid));
		

		if (e.nextOutEdge.get(suid) != null) 
			e.nextOutEdge.get(suid).prevOutEdge.put(suid,e.prevOutEdge.get(suid));

		if (e.prevInEdge.get(suid) != null) 
			e.prevInEdge.get(suid).nextInEdge.put(suid,e.nextInEdge.get(suid));
		else 
			target.firstInEdge.put(suid,e.nextInEdge.get(suid));

		if (e.nextInEdge.get(suid) != null) 
			e.nextInEdge.get(suid).prevInEdge.put(suid,e.prevInEdge.get(suid));

		if (e.directed) {
			source.outDegree.put(suid, source.outDegree.get(suid) - 1);
			target.inDegree.put(suid, target.inDegree.get(suid) - 1);
		} else {
			source.undDegree.put(suid, source.undDegree.get(suid) - 1);
			target.undDegree.put(suid, target.undDegree.get(suid) - 1);
		}

		if (source == target) { // Self-edge.

			if (e.directed) {
				source.selfEdges.put(suid, source.selfEdges.get(suid) - 1);
			} else {
				source.undDegree.put(suid, source.undDegree.get(suid) + 1);
			}
		}

		edgePointers.set(e.index, null);
		e.nextOutEdge.put(suid, null); // ?? wasn't here in DynamicGraph
		e.prevOutEdge.put(suid, null);
		e.nextInEdge.put(suid, null);
		e.prevInEdge.put(suid, null);
		edgeCount--;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsNode(final CyNode node) {
		return containsNode(node,suid);
	}

	private boolean containsNode(final CyNode node,final long inId) {
		if (node == null)
			return false;
			//throw new NullPointerException("node is null");

		final int ind = node.getIndex();

		if (ind < 0)
			return false;
			//throw new IllegalArgumentException("node index less than zero");

		if (ind >= nodePointers.size())
			return false;

		final NodePointer thisNode = nodePointers.get(ind);

		if ( thisNode == null )
			return false;	

		if ( !thisNode.knownNetworks.contains(inId) )	
			return false;	

		return thisNode.cyNode.equals(node);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyEdge edge) {
		return containsEdge(edge,suid);
	}

	private boolean containsEdge(final CyEdge edge, final long inId) {
		if (edge == null)
			return false;

		//throw new NullPointerException("edge is null");
		final int ind = edge.getIndex();

		if (ind < 0)
			return false;

		//throw new IllegalArgumentException("edge index less than zero");
		if (ind >= edgePointers.size())
			return false;

		final EdgePointer thisEdge = edgePointers.get(ind);

		if ( thisEdge == null )
			return false;

		if ( ! ( thisEdge.source.knownNetworks.contains(inId) &&
		         thisEdge.target.knownNetworks.contains(inId) ) ) 
			return false;

		return thisEdge.cyEdge.equals(edge);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyNode n1, final CyNode n2) {
		return containsEdge(n1,n2,suid);
	}

	private boolean containsEdge(final CyNode n1, final CyNode n2, final long inId) {
		if (!containsNode(n1))
			return false;

		if (!containsNode(n2))
			return false;

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

		final CyDataTable mgr = netAttrMgr.get(namespace);

		if (mgr == null)
			throw new NullPointerException("attribute manager is null for namespace: " + namespace);

		return mgr.getRow(suid);
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


	private Iterator<EdgePointer> edgesAdjacent(final NodePointer n, final CyEdge.Type edgeType, final long inId) {
		assert(n!=null);

		final EdgePointer[] edgeLists;

		final boolean incoming = assessIncoming(edgeType);
		final boolean outgoing = assessOutgoing(edgeType);
		final boolean undirected = assessUndirected(edgeType);

		if (undirected || (outgoing && incoming)) 
			edgeLists = new EdgePointer[] { n.firstOutEdge.get(inId), n.firstInEdge.get(inId) };
		else if (outgoing) // Cannot also be incoming.
			edgeLists = new EdgePointer[] { n.firstOutEdge.get(inId), null };
		else if (incoming) // Cannot also be outgoing.
			edgeLists = new EdgePointer[] { null, n.firstInEdge.get(inId) };
		else // All boolean input parameters are false.
			edgeLists = new EdgePointer[] { null, null };

		final int edgeCount = countEdges(n, edgeType, inId);

		return new Iterator<EdgePointer>() {
				private int numRemaining = edgeCount;
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
							edge = edge.nextOutEdge.get(inId);

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
							edge = edge.nextOutEdge.get(inId);
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
							edge = edge.nextInEdge.get(inId);
						}

						returnIndex = edge.index;
						edge = edge.nextInEdge.get(inId);
					}

					numRemaining--;
					return edgePointers.get(returnIndex);
				}
			};
	}

	private Iterator<EdgePointer> edgesConnecting(final NodePointer node0, final NodePointer node1,
	                                              final CyEdge.Type et, final long inId) {
		assert(node0!=null);
		assert(node1!=null);

		final Iterator<EdgePointer> theAdj;
		final int nodeZero;
		final int nodeOne;

		// choose the smaller iterator
		if (countEdges(node0, et, inId) <= countEdges(node1, et, inId)) {
			theAdj = edgesAdjacent(node0, et, inId);
			nodeZero = node0.index;
			nodeOne = node1.index;
		} else {
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

	private int countEdges(final NodePointer n, final CyEdge.Type edgeType, final long inId) {
		assert(n!=null);
		final boolean undirected = assessUndirected(edgeType);
		final boolean incoming = assessIncoming(edgeType);
		final boolean outgoing = assessOutgoing(edgeType);

		int tentativeEdgeCount = 0;

		if (outgoing) 
			tentativeEdgeCount += n.outDegree.get(inId);

		if (incoming) 
			tentativeEdgeCount += n.inDegree.get(inId);

		if (undirected) 
			tentativeEdgeCount += n.undDegree.get(inId);

		if (outgoing && incoming) 
			tentativeEdgeCount -= n.selfEdges.get(inId);

		return tentativeEdgeCount;
	}

	private EdgePointer getEdgePointer(final CyEdge edge) {
		assert(edge != null);
		assert(edge.getIndex()>=0);
		assert(edge.getIndex()<edgePointers.size());

		return edgePointers.get(edge.getIndex());
	}

	private NodePointer getNodePointer(final CyNode node) {
		assert(node != null);
		assert(node.getIndex()>=0);
		assert(node.getIndex()<nodePointers.size());

		return nodePointers.get(node.getIndex());
	}


	/**
	 * Element of the edge linked list.
	 */
	private class EdgePointer {
		final CyEdge cyEdge;
		final int index;
		Map<Long,EdgePointer> nextOutEdge = new HashMap<Long,EdgePointer>();
		Map<Long,EdgePointer> prevOutEdge = new HashMap<Long,EdgePointer>();
		Map<Long,EdgePointer> nextInEdge = new HashMap<Long,EdgePointer>();
		Map<Long,EdgePointer> prevInEdge = new HashMap<Long,EdgePointer>();
		boolean directed;
		NodePointer source;
		NodePointer target;
	
		EdgePointer(final NodePointer s, final NodePointer t, final boolean dir, final int ind) {
			index = ind;
			source = s;
			target = t;
			directed = dir;
			cyEdge = new CyEdgeImpl(source.cyNode, target.cyNode, directed, index, edgeAttrMgr);
	
			if (directed) {
				source.outDegree.put(suid, source.outDegree.get(suid) + 1);
				target.inDegree.put(suid, target.inDegree.get(suid) + 1);
			} else {
				source.undDegree.put(suid, source.undDegree.get(suid) + 1);
				target.undDegree.put(suid, target.undDegree.get(suid) + 1);
			}
	
			// Self-edge
			if (source == target) {
				if (directed) {
					source.selfEdges.put(suid, source.selfEdges.get(suid) + 1);
				} else {
					source.undDegree.put(suid, source.undDegree.get(suid) - 1);
				}
			}

			nextOutEdge.put(suid,null);
			prevOutEdge.put(suid,null);

			nextInEdge.put(suid,null);
			prevInEdge.put(suid,null);
		}
	}
	

	/**
	 * Element of the node linked list.
	 */
	private class NodePointer {
		final CyNode cyNode;
		final int index;
		final Map<Long,NodePointer> nextNode = new HashMap<Long,NodePointer>();
		final Map<Long,NodePointer> prevNode = new HashMap<Long,NodePointer>();
		final Set<Long> knownNetworks = new HashSet<Long>();
		final Map<Long,EdgePointer> firstOutEdge = new HashMap<Long,EdgePointer>();
		final Map<Long,EdgePointer> firstInEdge = new HashMap<Long,EdgePointer>();
	
		// The number of directed edges whose source is this node.
		final Map<Long,Integer> outDegree = new HashMap<Long,Integer>();
	
		// The number of directed edges whose target is this node.
		final Map<Long,Integer> inDegree = new HashMap<Long,Integer>();

		// The number of undirected edges which touch this node.
		final Map<Long,Integer> undDegree = new HashMap<Long,Integer>();

		// The number of directed self-edges on this node.
		final Map<Long,Integer> selfEdges = new HashMap<Long,Integer>();

		NodePointer(final int nodeIndex, final CyNetwork n, final NodePointer next) {
			index = nodeIndex;
			cyNode = new CyNodeImpl(n, index, nodeAttrMgr);
			nextNode.put(suid, next);
			if ( next != null )
				next.prevNode.put(suid,this);
			outDegree.put(suid,0);
			inDegree.put(suid,0);
			undDegree.put(suid,0);
			selfEdges.put(suid,0);
			knownNetworks.add(suid);

			firstOutEdge.put(suid,null);
			firstInEdge.put(suid,null);
		}
	}

/*
	private class InternalNetwork implements CyNetwork {
		private final long internalId;
		private final CyNetwork parent;
		private int internalNodeCount;
		private int internalEdgeCount;

		IntenalNetwork(final CyNetwork par, final List<CyNode> nodes) {
			internalId = IdFactory.getNextSUID(); 

			if ( par == null )
				throw new NullPointerException("parent network is null");

			parent = par;

			if ( nodes == null )
				throw new NullPointerException("node list is null");
			if ( nodes.size() <= 0 )
				throw new IllegalArgumentException("node list has size zero");

			internalNodeCount = nodes.size();

			// add this network's internal id to each NodePointer 
			NodePointer next = null;
			for ( CyNode n : nodes ) {
				if ( !parent.containsNode(n) )
					throw new IllegalArgumentException("node is not contained in parent network");
				
				updateNode(n);
			}

			// now add this network's internal id to each EdgePointer	
			for ( CyNode n : nodes ) {
				List<CyEdge> adjEdges = getAdjacentEdgeList(n,CyEdge.Type.ANY);
				for ( CyEdge edge : adjEdges ) {

					// See if the subnetwork contains the target node and only
					// update the edge if it does.
					if ( !containsNode( edge.getTarget(), internalId) )
						return;
			
					internalEdgeCount++;
					updateEdge(edge);
				}
			}
		}

		private void updateNode(CyNode n) {
			NodePointer node = getNodePointer(n);

			// add this network to the node's known networks
			node.knownNetworks.add( internalId );

			// set up internal linked list
			node.nextNode.put(internalId,next);
			if ( next != null )
				next.prevNode.put(internalId,node);
			next = node;
		}

		private void updateEdge(CyEdge e) {
			NodePointer source = edge.getSource();
			NodePointer target = edge.getTarget();

			EdgePointer e = getEdgePointer( edge );

			e.nextOutEdge.put(internalId, source.firstOutEdge.get(internalId));

			if (source.firstOutEdge.get(internalId) != null) 
				source.firstOutEdge.get(internalId).prevOutEdge.put(internalId,e);

			source.firstOutEdge.put(internalId,e);

			e.nextInEdge.put(internalId,target.firstInEdge);

			if (target.firstInEdge.get(internalId) != null) 
				target.firstInEdge.get(internalId).prevInEdge.put(internalId,e);
		
			target.firstInEdge.put(internalId,e);
		}


		public long getSUID() {
			return internalId;
		}

		public CyNode addNode() {
			CyNode ret = parent.addNode();
			updateNode(ret);
			return ret;
		}

		public boolean removeNode(final CyNode node) {
		}

		public CyEdge addEdge(final CyNode source, final CyNode target, final boolean isDirected) {
			CyEdge ret = parent.addEdge(source,target,isDirected);
			updateEdge(ret);
			return ret;
		}

		public boolean removeEdge(final CyEdge edge) {
		}

		public int getNodeCount() {
			return internalNodeCount;
		}

		public int getEdgeCount() {
			return internalEdgeCount;
		}

		public List<CyNode> getNodeList() {
			return parent.getNodeList(internalId);
		}

		public List<CyEdge> getEdgeList() {
			return parent.getEdgeList(internalId);
		}

		public boolean containsNode(final CyNode node) {
			return parent.containsNode(node,internalId);
		}

		public boolean containsEdge(final CyEdge edge) {
			return parent.containsEdge(edge,internalId);
		}

		public boolean containsEdge(final CyNode from, final CyNode to) {
			return parent.containsEdge(from,to,internalId);
		}

		public CyNode getNode(final int index) {
			return parent.getNode(index);
		}

		public CyEdge getEdge(final int index) {
			return parent.getEdge(index);
		}

		public List<CyNode> getNeighborList(final CyNode node, final CyEdge.Type edgeType) {
			return parent.getNeighborList(node,edgeType,internalId);
		}

		public List<CyEdge> getAdjacentEdgeList(final CyNode node, final CyEdge.Type edgeType) {
			return parent.getAdjacentEdgeList(node,edgeType,internalId);
		}

		public List<CyEdge> getConnectingEdgeList(final CyNode source, final CyNode target, final CyEdge.Type edgeType) {
			return parent.getConnectingEdgeList(source,target,edgeType,internalId);
		}

		public Map<String, ?extends CyDataTable> getNetworkCyDataTables() {
			return parent.getNetworkCyDataTables();
		}

		public Map<String, ?extends CyDataTable> getNodeCyDataTables() {
			return parent.getNodeCyDataTables();
		}

		public Map<String, ?extends CyDataTable> getEdgeCyDataTables() {
			return parent.getEdgeCyDataTables();
		}
	}
	*/
}
