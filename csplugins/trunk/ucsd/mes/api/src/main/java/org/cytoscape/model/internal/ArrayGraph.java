
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
import java.util.Set;
import java.util.HashSet;


/**
 * hmmm.
 */
public class ArrayGraph implements CyRootNetwork {
	private final long suid;
	private static final int ROOT = 0;
	private int original = 0;

	private int numSubNetworks = 0;
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
	private CySubNetwork base = null;

	/**
	 * Creates a new ArrayGraph object.
	 * @param eh The CyEventHelper used for firing events.
	 */
	public ArrayGraph(final CyEventHelper eh) {
		suid = IdFactory.getNextSUID();
		System.out.println("new ArrayGraph out " + suid);
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
		if ( base == null )
			return nodeCount;
		else
			return base.getNodeCount();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEdgeCount() {
		if ( base == null )
			return edgeCount;
		else
			return base.getEdgeCount();
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
		if ( base == null ) {
			System.out.println("public getNodeList root");
			return getNodeList(firstNode,original,nodeCount);
		} else {
			System.out.println("public getNodeList base");
			return base.getNodeList();
		}
	}

	private List<CyNode> getNodeList(final NodePointer first, final int inId, final int numNodes) {
		System.out.println("private getNodeList " + inId);
		final List<CyNode> ret = new ArrayList<CyNode>(nodeCount);
		int numRemaining = numNodes;
		NodePointer node = first;

		while (numRemaining > 0) {
			System.out.println(" ++ " + numRemaining);
			if ( node == null )
				System.out.println("node == null");
			final CyNode toAdd = node.cyNode;
			node = node.nextNode[inId];
			ret.add(toAdd);
			numRemaining--;
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getEdgeList() {
		if ( base == null ) {
			System.out.println("public getEdgeList root");
			return getEdgeList(firstNode,original,edgeCount);
		} else  {
			System.out.println("public getEdgeList base");
			return base.getEdgeList();
		}
	}

	private List<CyEdge> getEdgeList(final NodePointer first, final int inId, final int numEdges) {
		final List<CyEdge> ret = new ArrayList<CyEdge>(edgeCount);
		int numRemaining = numEdges;
		NodePointer node = first;
		EdgePointer edge = null;
		System.out.println("private getEdgeList numEdges: " + numEdges );
		System.out.println("private getEdgeList inId: " + inId );

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

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNeighborList(final CyNode n, final CyEdge.Type e) {
		if ( base == null )
			return getNeighborList(n,e,original);	
		else
			return base.getNeighborList(n,e);	
	}

	private List<CyNode> getNeighborList(final CyNode n, final CyEdge.Type e, final int inId) {
		if (!containsNode(n))
			throw new IllegalArgumentException("this node is not contained in the network");

		final NodePointer np = getNodePointer(n);
		final List<CyNode> ret = new ArrayList<CyNode>(countEdges(np, e, inId));
		final Iterator<EdgePointer> it = edgesAdjacent(np, e, inId);

		while (it.hasNext()) {
			final EdgePointer edge = it.next();
			final int neighborIndex = np.index ^ edge.source.index ^ edge.target.index;
			final NodePointer nnp = nodePointers.get(neighborIndex);
			ret.add(getNode(neighborIndex));
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getAdjacentEdgeList(final CyNode n, final CyEdge.Type e) {
		if ( base == null )
			return getAdjacentEdgeList(n,e,original);
		else 
			return base.getAdjacentEdgeList(n,e);
	}

	private List<CyEdge> getAdjacentEdgeList(final CyNode n, final CyEdge.Type e, final int inId) {
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
		if ( base == null )
			return getConnectingEdgeList(src,trg,e,original);
		else
			return base.getConnectingEdgeList(src,trg,e);
	}

	private List<CyEdge> getConnectingEdgeList(final CyNode src, final CyNode trg, final CyEdge.Type e, final int inId) {
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
		return addNode(null);
	}

	private CyMetaNode addNode(CySubNetwork sub) {
		final NodePointer n;

		synchronized (this) {
			n = new NodePointer(nodePointers.size(), this, firstNode, sub);
			nodePointers.add(n);
			nodeCount++;
			firstNode = n;
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
		if (node.prevNode[ROOT] != null)
			node.prevNode[ROOT].nextNode[ROOT] = node.nextNode[ROOT];
		else
			firstNode = node.nextNode[ROOT];

		if (node.nextNode[ROOT] != null)
			node.nextNode[ROOT].prevNode[ROOT] = node.prevNode[ROOT];

		nodePointers.set(node.index, null);

		node.prevNode[ROOT] = null;
		node.firstOutEdge[ROOT] = null;
		node.firstInEdge[ROOT] = null;
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

			e.nextOutEdge[ROOT] = source.firstOutEdge[ROOT];

			if (source.firstOutEdge[ROOT] != null) 
				source.firstOutEdge[ROOT].prevOutEdge[ROOT] = e;

			source.firstOutEdge[ROOT] = e;

			e.nextInEdge[ROOT] = target.firstInEdge[ROOT];

			if (target.firstInEdge[ROOT] != null) 
				target.firstInEdge[ROOT].prevInEdge[ROOT] = e;

			target.firstInEdge[ROOT] = e;
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

		if (e.prevOutEdge[ROOT] != null) 
			e.prevOutEdge[ROOT].nextOutEdge[ROOT] = e.nextOutEdge[ROOT];
		else 
			source.firstOutEdge[ROOT] = e.nextOutEdge[ROOT];
		

		if (e.nextOutEdge[ROOT] != null) 
			e.nextOutEdge[ROOT].prevOutEdge[ROOT] = e.prevOutEdge[ROOT];

		if (e.prevInEdge[ROOT] != null) 
			e.prevInEdge[ROOT].nextInEdge[ROOT] = e.nextInEdge[ROOT];
		else 
			target.firstInEdge[ROOT] = e.nextInEdge[ROOT];

		if (e.nextInEdge[ROOT] != null) 
			e.nextInEdge[ROOT].prevInEdge[ROOT] = e.prevInEdge[ROOT];

		if (e.directed) {
			source.outDegree[ROOT]--;
			target.inDegree[ROOT]--;
		} else {
			source.undDegree[ROOT]--;
			target.undDegree[ROOT]--;
		}

		if (source == target) { // Self-edge.

			if (e.directed) {
				source.selfEdges[ROOT]++;
			} else {
				source.undDegree[ROOT]++;
			}
		}

		edgePointers.set(e.index, null);
		e.nextOutEdge[ROOT] =  null; // ?? wasn't here in DynamicGraph
		e.prevOutEdge[ROOT] =  null;
		e.nextInEdge[ROOT] =  null;
		e.prevInEdge[ROOT] =  null;
		edgeCount--;

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

		if (ind >= nodePointers.size())
			return false;

		final NodePointer thisNode = nodePointers.get(ind);

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

		return thisEdge.cyEdge.equals(edge);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyNode n1, final CyNode n2) {
		// TODO should this be original instead of ROOT?
		return containsEdge(n1,n2,ROOT);
	}

	private boolean containsEdge(final CyNode n1, final CyNode n2, final int inId) {
		System.out.println("private containsEdge");
		if (!containsNode(n1)) {
			System.out.println("private containsEdge doesn't contain node1 " + inId);
			return false;
		}

		if (!containsNode(n2)) {
			System.out.println("private containsEdge doesn't contain node2 " + inId);
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
		System.out.println("edgesAdjacent edgeCount: " + inEdgeCount);

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
			System.out.println("edgesConnecting fewer edges node0: " + node0.index);
			theAdj = edgesAdjacent(node0, et, inId);
			nodeZero = node0.index;
			nodeOne = node1.index;
		} else {
			System.out.println("edgesConnecting fewer edges node1: " + node1.index);
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

		System.out.println("countEdges un: " + undirected + " in: " + incoming + " out: " + outgoing);

		int tentativeEdgeCount = 0;

		if (outgoing) { 
			System.out.println("  countEdges outgoing: " + n.outDegree[inId]);
			tentativeEdgeCount += n.outDegree[inId];
		}

		if (incoming) { 
			System.out.println("  countEdges incoming: " + n.inDegree[inId]);
			tentativeEdgeCount += n.inDegree[inId];
		}

		if (undirected) {
			System.out.println("  countEdges undirected: " + n.undDegree[inId]);
			tentativeEdgeCount += n.undDegree[inId];
		}

		if (outgoing && incoming) {
			System.out.println("  countEdges out+in MINUS: " + n.selfEdges[inId]);
			tentativeEdgeCount -= n.selfEdges[inId];
		}

		System.out.println("  countEdges final: " + tentativeEdgeCount);
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
		EdgePointer[] nextOutEdge = new EdgePointer[1];
		EdgePointer[] prevOutEdge = new EdgePointer[1];
		EdgePointer[] nextInEdge = new EdgePointer[1];
		EdgePointer[] prevInEdge = new EdgePointer[1];
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
				source.outDegree[ROOT]++;
				target.inDegree[ROOT] ++;
			} else {
				source.undDegree[ROOT]++; 
				target.undDegree[ROOT]++; 
			}
	
			// Self-edge
			if (source == target) {
				if (directed) {
					source.selfEdges[ROOT]++; 
				} else {
					source.undDegree[ROOT]--; 
				}
			}

			nextOutEdge[ROOT] = null;
			prevOutEdge[ROOT] = null;

			nextInEdge[ROOT] = null;
			prevInEdge[ROOT] = null;
		}

		void expandTo(int x) {
			x++;
			if ( nextOutEdge.length > x )
				return;

			nextOutEdge = expandEdgePointerArray(nextOutEdge,x); 
			prevOutEdge = expandEdgePointerArray(prevOutEdge,x);
			nextInEdge = expandEdgePointerArray(nextInEdge,x);
			prevInEdge = expandEdgePointerArray(prevInEdge,x);
		}
	}
	

	/**
	 * Element of the node linked list.
	 */
	private class NodePointer {
		final CyMetaNode cyNode;
		final int index;

		NodePointer[] nextNode = new NodePointer[1];
		NodePointer[] prevNode = new NodePointer[1];
		EdgePointer[] firstOutEdge = new EdgePointer[1];
		EdgePointer[] firstInEdge = new EdgePointer[1];
	
		// The number of directed edges whose source is this node.
		int[] outDegree = new int[1];
	
		// The number of directed edges whose target is this node.
		int[] inDegree = new int[1];

		// The number of undirected edges which touch this node.
		int[] undDegree = new int[1];

		// The number of directed self-edges on this node.
		int[] selfEdges = new int[1]; 

		NodePointer(final int nodeIndex, final CyNetwork n, final NodePointer next, final CySubNetwork sub) {
			index = nodeIndex;
			cyNode = new CyNodeImpl(n, index, nodeAttrMgr, sub);
			nextNode[ROOT] =  next;
			if ( next != null )
				next.prevNode[ROOT] = this;
			outDegree[ROOT] = 0;
			inDegree[ROOT] = 0;
			undDegree[ROOT] = 0;
			selfEdges[ROOT] = 0;

			firstOutEdge[ROOT] = null;
			firstInEdge[ROOT] = null;
		}

		void expandTo(int x) {
			x++;
			if ( nextNode.length > x )
				return;
			
			nextNode = expandNodePointerArray(nextNode,x); 
			prevNode =  expandNodePointerArray(prevNode,x);
			firstOutEdge =  expandEdgePointerArray(firstOutEdge,x);
			firstInEdge =  expandEdgePointerArray(firstInEdge,x);
			outDegree = expandIntArray(outDegree,x); 
			inDegree = expandIntArray(inDegree,x);
			undDegree = expandIntArray(undDegree,x);
			selfEdges = expandIntArray(selfEdges,x);
		}
	}

	private static NodePointer[] expandNodePointerArray(final NodePointer[] np, final int n) {
		NodePointer[] nnp = new NodePointer[n+1];
		System.arraycopy(np,0,nnp,0,np.length);
		return nnp;
	}

	private static EdgePointer[] expandEdgePointerArray(final EdgePointer[] np, final int n) {
		EdgePointer[] nnp = new EdgePointer[n+1];
		System.arraycopy(np,0,nnp,0,np.length);
		return nnp;
	}

	private static int[] expandIntArray(final int[] np, final int n) {
		int[] nnp = new int[n+1];
		System.arraycopy(np,0,nnp,0,np.length);
		return nnp;
	}

	public List<CyNode> getAllNodes() {
		return getNodeList(firstNode, ROOT, nodeCount);
	}

	public List<CyEdge> getAllEdges() {
		return getEdgeList(firstNode,ROOT,edgeCount);
	}

	public CyMetaNode createMetaNode(List<CyNode> nodes) {
		

		// only hit this once
		// this will create a subnetwork of the original network 
		// so that we can continue to see the original
		if ( ROOT == original ) {
			final int originalId = ++numSubNetworks;
			base = new InternalNetwork(this,getNodeList(),originalId);
			original++;  // must happen after internal network is created
		}

		final int newId;
		synchronized (this) {
			newId = ++numSubNetworks;
		}

		CySubNetwork sub = new InternalNetwork(this,nodes,newId);
		subNets.add(sub);

		return addNode( sub );
	}

	public void removeMetaNode(CyMetaNode mn) {
	}

	public List<CySubNetwork> getAllSubNetworks() {
		return new ArrayList<CySubNetwork>(subNets);
	}


	private class InternalNetwork implements CySubNetwork {
		private final int internalId;
		private final long internalSUID;
		private final ArrayGraph parent;
		private int internalNodeCount;
		private int internalEdgeCount;
		private NodePointer inFirstNode;
		private Set<CyNode> nodeSet; 
		private Set<CyEdge> edgeSet; 

		InternalNetwork(final ArrayGraph par, final List<CyNode> nodes, final int inId) {
			internalId = inId; 
			internalSUID = IdFactory.getNextSUID();
			System.out.println("new InternalNetwork " + internalSUID + "  " + inId);

			if ( par == null )
				throw new NullPointerException("parent network is null");

			parent = par;

			if ( nodes == null )
				throw new NullPointerException("node list is null");
			if ( nodes.size() <= 0 )
				throw new IllegalArgumentException("node list has size zero");
			nodeSet = new HashSet<CyNode>(nodes);
		
			internalNodeCount = nodeSet.size();
			System.out.println("node size: " + internalNodeCount);

			// add this network's internal id to each NodePointer 
			inFirstNode = null; 
			for ( CyNode n : nodeSet ) {
				if ( !parent.containsNode(n) )
					throw new IllegalArgumentException("node is not contained in parent network");
			
				updateNode(n);
			}


			// find all adjacent edges of the nodes, determine if the 
			// target node is in the subnetwork, and if so, add it 
			// to the edge set
			edgeSet = new HashSet<CyEdge>();
			for ( CyNode n : nodeSet ) {
				System.out.println(" checking edges for NODE: " + n.getIndex());
				// note that we're getting the adj edges from the ROOT network
				List<CyEdge> adjEdges = parent.getAdjacentEdgeList(n,CyEdge.Type.ANY,ROOT);
				for ( CyEdge edge : adjEdges ) {
				System.out.println("  evaluating: " + edge.getIndex());

					// check the nodeSet because containsNode won't yet be updated
					if ( nodeSet.contains( edge.getSource() ) && nodeSet.contains( edge.getTarget() ) ) {
						System.out.println("    adding edge: " + edge.getIndex());
						edgeSet.add( edge );	
					}
				}		
			}		

			internalEdgeCount = edgeSet.size();

			// now add this network's internal id to each EdgePointer	
			for ( CyEdge edge : edgeSet ) 
				updateEdge(edge);

			if ( inFirstNode == null )
				System.out.println("infirstnode == null  " + internalId);
			else
				System.out.println("infirstnode looks good  " + internalId);

			System.out.println("internalEdgeCount  " + internalEdgeCount);
		}

		private void updateNode(CyNode n) {
			NodePointer node = getNodePointer(n);
			node.expandTo(internalId);

			// set up internal linked list
			node.nextNode[internalId] = inFirstNode;
			if ( inFirstNode != null )
				inFirstNode.prevNode[internalId] = node;
			inFirstNode = node;
		}

		private void updateEdge(CyEdge edge) {
			NodePointer source = getNodePointer(edge.getSource());
			source.expandTo(internalId);

			NodePointer target = getNodePointer(edge.getTarget());
			target.expandTo(internalId);

			EdgePointer e = getEdgePointer( edge );
			e.expandTo(internalId);

			e.nextOutEdge[internalId] = source.firstOutEdge[internalId];

			if (source.firstOutEdge[internalId] != null) 
				source.firstOutEdge[internalId].prevOutEdge[internalId] = e;

			source.firstOutEdge[internalId] = e;

			e.nextInEdge[internalId] = target.firstInEdge[internalId];

			if (target.firstInEdge[internalId] != null) 
				target.firstInEdge[internalId].prevInEdge[internalId] = e;
		
			target.firstInEdge[internalId] = e;

			if (edge.isDirected()) {
				source.outDegree[internalId]++;
				target.inDegree[internalId] ++;
			} else {
				source.undDegree[internalId]++; 
				target.undDegree[internalId]++; 
			}
	
			// Self-edge
			if (source == target) {
				if (edge.isDirected()) {
					source.selfEdges[internalId]++; 
				} else {
					source.undDegree[internalId]--; 
				}
			}
		}


		public long getSUID() {
			return internalSUID;
		}

		public CyNode addNode() {
			CyNode ret = parent.addNode();
			updateNode(ret);
			return ret;
		}

		public boolean removeNode(final CyNode node) {
			return false;
		}

		public CyEdge addEdge(final CyNode source, final CyNode target, final boolean isDirected) {
			CyEdge ret = parent.addEdge(source,target,isDirected);
			updateEdge(ret);
			return ret;
		}

		public boolean removeEdge(final CyEdge edge) {
			return false;
		}

		public int getNodeCount() {
			return internalNodeCount;
		}

		public int getEdgeCount() {
			return internalEdgeCount;
		}

		public List<CyNode> getNodeList() {
			return parent.getNodeList(inFirstNode,internalId,internalNodeCount);
		}

		public List<CyEdge> getEdgeList() {
			return parent.getEdgeList(inFirstNode,internalId,internalEdgeCount);
		}

		public boolean containsNode(final CyNode node) {
			return parent.containsNode(node) && nodeSet.contains(node);
		}

		public boolean containsEdge(final CyEdge edge) {
			return parent.containsEdge(edge) && edgeSet.contains(edge);
		}

		public boolean containsEdge(final CyNode from, final CyNode to) {
			return containsNode(from) && containsNode(to) && parent.containsEdge(from,to,internalId);
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
		public CyRow getCyRow(final String namespace) {
			return parent.getCyRow(namespace);
		}
		public CyRow attrs() {
			return parent.attrs();
		}
		public void copyToNetwork(CyNode node) {
			if ( node == null )
				throw new NullPointerException("node is null");	
			if ( containsNode(node) )
				throw new IllegalArgumentException("node is already contained in network!");
			if ( !parent.containsNode(node) )
				throw new IllegalArgumentException("node is not contained in parent network!");
			
			// add node 
			internalNodeCount++;
			nodeSet.add(node);
			updateNode(node);

			// add any adjacent edges	
			List<CyEdge> adjEdges = parent.getAdjacentEdgeList(node,CyEdge.Type.ANY,ROOT);
			Set<CyEdge> tmpSet = new HashSet<CyEdge>();
			for ( CyEdge edge : adjEdges ) {
				System.out.println(" copy adjEdge: " + edge.getIndex());
				// check the nodeSet because containsNode won't yet be updated
				if ( nodeSet.contains( edge.getSource() ) && nodeSet.contains( edge.getTarget() ) ) {
					System.out.println(" copy ADDING adjEdge: " + edge.getIndex());
					edgeSet.add(edge);
					tmpSet.add(edge);
				}
			}

			for ( CyEdge edge : tmpSet ) {
				internalEdgeCount++;
				updateEdge(edge);
			}
		}

		public void removeFromNetwork(CyNode n) {
			if (!containsNode(n))
				throw new IllegalArgumentException("node not contained in subnetwork");

			// remove adjacent edges
			final List<CyEdge> edges = getAdjacentEdgeList(n, CyEdge.Type.ANY);

			for (final CyEdge e : edges)
				removeInternalEdge(e);

			final NodePointer node = getNodePointer(n);

			// now clean up node
			if (node.prevNode[internalId] != null)
				node.prevNode[internalId].nextNode[internalId] = node.nextNode[internalId];
			else
				firstNode = node.nextNode[internalId];

			if (node.nextNode[internalId] != null)
				node.nextNode[internalId].prevNode[internalId] = node.prevNode[internalId];

			node.prevNode[internalId] = null;
			node.firstOutEdge[internalId] = null;
			node.firstInEdge[internalId] = null;
			internalNodeCount--;
			nodeSet.remove(n);
		}

		private void removeInternalEdge(final CyEdge edge) {
			if (!containsEdge(edge))
				return;

			final EdgePointer e = getEdgePointer(edge);

			final NodePointer source = nodePointers.get(e.source.index);
			final NodePointer target = nodePointers.get(e.target.index);

			if (e.prevOutEdge[internalId] != null) 
				e.prevOutEdge[internalId].nextOutEdge[internalId] = e.nextOutEdge[internalId];
			else 
				source.firstOutEdge[internalId] = e.nextOutEdge[internalId];
		

			if (e.nextOutEdge[internalId] != null) 
				e.nextOutEdge[internalId].prevOutEdge[internalId] = e.prevOutEdge[internalId];

			if (e.prevInEdge[internalId] != null) 
				e.prevInEdge[internalId].nextInEdge[internalId] = e.nextInEdge[internalId];
			else 
				target.firstInEdge[internalId] = e.nextInEdge[internalId];

			if (e.nextInEdge[internalId] != null) 
				e.nextInEdge[internalId].prevInEdge[internalId] = e.prevInEdge[internalId];

			if (e.directed) {
				source.outDegree[internalId]--;
				target.inDegree[internalId]--;
			} else {
				source.undDegree[internalId]--;
				target.undDegree[internalId]--;
			}

			if (source == target) { // Self-edge.

				if (e.directed) {
					source.selfEdges[internalId]++;
				} else {
					source.undDegree[internalId]++;
				}
			}

			e.nextOutEdge[internalId] =  null; // ?? wasn't here in DynamicGraph
			e.prevOutEdge[internalId] =  null;
			e.nextInEdge[internalId] =  null;
			e.prevInEdge[internalId] =  null;
			internalEdgeCount--;
			edgeSet.remove(edge);
		}
	}
}
