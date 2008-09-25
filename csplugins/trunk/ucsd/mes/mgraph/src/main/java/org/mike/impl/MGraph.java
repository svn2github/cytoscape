
package org.mike.impl; 

import org.mike.CyNetwork; 
import org.mike.CyNode; 
import org.mike.CyEdge; 
import org.mike.EdgeType; 

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public final class MGraph implements CyNetwork {

	private int m_nodeCount;
	private int m_edgeCount;

	private NodePointer m_firstNode;

	private final List<NodePointer> m_nodes;
	private final List<EdgePointer> m_edges;

	public MGraph() {
		m_nodeCount = 0;
		m_edgeCount = 0;
		m_firstNode = null;
		m_nodes = new ArrayList<NodePointer>();
		m_edges = new ArrayList<EdgePointer>();
	}

	public int getNodeCount() {
		return m_nodeCount;
	}

	public int getEdgeCount() {
		return m_edgeCount;
	}

	public CyEdge getEdge(int e) {
		if ( e >= 0 && e < m_edges.size() )
			return m_edges.get(e).cyEdge;
		else
			return null;
	}

	public CyNode getNode(int n) {
		if ( n >= 0 && n < m_nodes.size() )
			return m_nodes.get(n).cyNode;
		else
			return null;
	}

	public List<CyNode> getNodeList() {
		List<CyNode> ret = new ArrayList<CyNode>(m_nodeCount);
		int numRemaining = m_nodeCount;
		NodePointer node = m_firstNode;

		while ( numRemaining > 0 ) {
			final CyNode toAdd = node.cyNode;
			node = node.nextNode;
			ret.add(toAdd);
			numRemaining--;
		}
		return ret;
	}

	public List<CyEdge> getEdgeList() {
		List<CyEdge> ret = new ArrayList<CyEdge>(m_edgeCount);
		int numRemaining = m_edgeCount;
		NodePointer node = m_firstNode;
		EdgePointer edge = null; 

		while ( numRemaining > 0 ) {
			final CyEdge retEdge;

			if (edge != null) {
				retEdge = edge.cyEdge;
			} else {
				for (edge = node.firstOutEdge; edge == null;
				     node = node.nextNode, edge = node.firstOutEdge) {
				}

				node = node.nextNode;
				retEdge = edge.cyEdge;
			}

			edge = edge.nextOutEdge;
			numRemaining--;
		
			ret.add( retEdge );
		}
		return ret;
	}

	public List<CyNode> getNeighborList(CyNode n, EdgeType e) {

		if ( !containsNode(n) )
			throw new IllegalArgumentException("this node is not contained in the network");

		NodePointer np = getNodePointer(n);
		List<CyNode> ret = new ArrayList<CyNode>( countEdges(np,e) );
		Iterator<EdgePointer> it = edgesAdjacent(np,e);
		while ( it.hasNext() ) {
			final EdgePointer edge = it.next();
			final int neighborIndex = np.index ^ edge.source.index ^ edge.target.index; 
			ret.add( getNode( neighborIndex ) );
		}
		return ret;
	}

	public List<CyEdge> getAdjacentEdgeList(CyNode n, EdgeType e) {

		if ( !containsNode(n) )
			throw new IllegalArgumentException("this node is not contained in the network");

		NodePointer np = getNodePointer(n);
		List<CyEdge> ret = new ArrayList<CyEdge>( countEdges(np,e) );
		Iterator<EdgePointer> it = edgesAdjacent(np,e);
		while ( it.hasNext() )
			ret.add( it.next().cyEdge );	
		return ret;
	}

	public List<CyEdge> getConnectingEdgeList(CyNode src, CyNode trg, EdgeType e) {

		if ( !containsNode(src) )
			throw new IllegalArgumentException("source node is not contained in the network");
		if ( !containsNode(trg) )
			throw new IllegalArgumentException("target node is not contained in the network");

		NodePointer srcP = getNodePointer(src);
		NodePointer trgP = getNodePointer(trg);

		List<CyEdge> ret = new ArrayList<CyEdge>( Math.min(countEdges(srcP,e),countEdges(trgP,e)) );
		Iterator<EdgePointer> it = edgesConnecting(srcP,trgP,e);
		while ( it.hasNext() )
			ret.add( it.next().cyEdge );	
		return ret;
	}


	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final CyNode addNode() {

		final NodePointer n; 

		synchronized (this) {
			n = new NodePointer(m_nodes.size(),this,m_firstNode); 
			m_nodes.add(n);
			m_nodeCount++;
			if (m_firstNode != null) 
				m_firstNode.prevNode = n;
			m_firstNode = n;
		}

		return n.cyNode;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final boolean removeNode(final CyNode n) {

		if ( !containsNode(n) )
			return false;

		// remove adjacent edges
		final List<CyEdge> edges = getAdjacentEdgeList(n, EdgeType.ANY_EDGE);
		for ( final CyEdge e : edges )
			removeEdge(e);

		NodePointer node = getNodePointer(n);

		// now clean up node
		if (node.prevNode != null) 
			node.prevNode.nextNode = node.nextNode;
		else 
			m_firstNode = node.nextNode;


		if (node.nextNode != null) 
			node.nextNode.prevNode = node.prevNode;

		m_nodes.set(node.index, null);

		node.prevNode = null;
		node.firstOutEdge = null;
		node.firstInEdge = null;
		node = null;
		m_nodeCount--;

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param sourceNode DOCUMENT ME!
	 * @param targetNode DOCUMENT ME!
	 * @param directed DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CyEdge addEdge(final CyNode s, final CyNode t, final boolean directed) {

		final EdgePointer e;

		synchronized (this) {
			if ( !containsNode( s ) )
				throw new IllegalArgumentException("source node is not a member of this network");
			if ( !containsNode( t ) )
				throw new IllegalArgumentException("target node is not a member of this network");

			NodePointer source = getNodePointer(s);
			NodePointer target = getNodePointer(t);

			e = new EdgePointer(source,target,directed,m_edges.size()); 
	
			m_edges.add(e);

			m_edgeCount++;

	        e.nextOutEdge = source.firstOutEdge;

			if (source.firstOutEdge != null) {
				source.firstOutEdge.prevOutEdge = e;
			}

			source.firstOutEdge = e;

			e.nextInEdge = target.firstInEdge;

			if (target.firstInEdge != null) {
				target.firstInEdge.prevInEdge = e;
			}

			target.firstInEdge = e;
		}

		return e.cyEdge;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final boolean removeEdge(final CyEdge edge) {
		if ( !containsEdge(edge) )
			return false;

		EdgePointer e = getEdgePointer(edge);

		final NodePointer source = m_nodes.get(e.source.index);
		final NodePointer target = m_nodes.get(e.target.index);

		if (e.prevOutEdge != null) {
			e.prevOutEdge.nextOutEdge = e.nextOutEdge;
		} else {
			source.firstOutEdge = e.nextOutEdge;
		}

		if (e.nextOutEdge != null) {
			e.nextOutEdge.prevOutEdge = e.prevOutEdge;
		}

		if (e.prevInEdge != null) {
			e.prevInEdge.nextInEdge = e.nextInEdge;
		} else {
			target.firstInEdge = e.nextInEdge;
		}

		if (e.nextInEdge != null) {
			e.nextInEdge.prevInEdge = e.prevInEdge;
		}

		if (e.directed) {
			source.outDegree--;
			target.inDegree--;
		} else {
			source.undDegree--;
			target.undDegree--;
		}

		if (source == target) { // Self-edge.

			if (e.directed) {
				source.selfEdges--;
			} else {
				source.undDegree++;
			}
		}

		m_edges.set(e.index,null);
		e.prevOutEdge = null;
		e.nextInEdge = null;
		e.prevInEdge = null;
		e = null;
		m_edgeCount--;

		return true;
	}

	public final boolean containsNode(final CyNode node) {
		if ( node == null )
			return false;	
			//throw new NullPointerException("node is null");

		final int ind = node.getIndex();

		if ( ind < 0 )
			return false;	
			//throw new IllegalArgumentException("node index less than zero");

		if ( ind >= m_nodes.size() )
			return false;

		final NodePointer thisNode = m_nodes.get( ind );

		return ( thisNode != null && thisNode.cyNode.equals( node ) ); 
	}

	public final boolean containsEdge(final CyEdge edge) {
		if ( edge == null )
			return false;	
			//throw new NullPointerException("edge is null");

		final int ind = edge.getIndex();

		if ( ind < 0 )
			return false;	
			//throw new IllegalArgumentException("edge index less than zero");

		if ( ind >= m_edges.size() )
			return false;

		final EdgePointer thisEdge = m_edges.get( ind );

		return ( thisEdge != null && thisEdge.cyEdge.equals( edge ) ); 
	}

	public final boolean containsEdge(final CyNode n1, final CyNode n2) {
		if ( !containsNode( n1 ) )
			return false;
		if ( !containsNode( n2 ) )
			return false;

		Iterator<EdgePointer> it = edgesConnecting(getNodePointer(n1),getNodePointer(n2),EdgeType.ANY_EDGE);

		return it.hasNext();
	}


	private Iterator<EdgePointer> edgesAdjacent(final NodePointer n, final EdgeType edgeType) { 
        if (n == null)
            throw new NullPointerException("node is null");

        final EdgePointer[] edgeLists;

        boolean inc = false;
        boolean out = false;
        boolean und = false;

        if ( edgeType == EdgeType.UNDIRECTED_EDGE ||
             edgeType == EdgeType.ANY_EDGE )
            und = true;

        if ( edgeType == EdgeType.DIRECTED_EDGE ||
             edgeType == EdgeType.ANY_EDGE ||
             edgeType == EdgeType.INCOMING_EDGE )
            inc = true;

        if ( edgeType == EdgeType.DIRECTED_EDGE ||
             edgeType == EdgeType.ANY_EDGE ||
             edgeType == EdgeType.OUTGOING_EDGE )
            out = true;


        final boolean incoming = inc;
        final boolean outgoing = out;
        final boolean undirected = und;

        if (undirected || (outgoing && incoming)) {
            edgeLists = new EdgePointer[] { n.firstOutEdge, n.firstInEdge };
        } else if (outgoing) { // Cannot also be incoming.
            edgeLists = new EdgePointer[] { n.firstOutEdge, null };
        } else if (incoming) { // Cannot also be outgoing.
            edgeLists = new EdgePointer[] { null, n.firstInEdge };
        } else { // All boolean input parameters are false.
            edgeLists = new EdgePointer[] { null, null };
        }

        final int edgeCount = countEdges(n,edgeType);

        return new Iterator<EdgePointer>() {
                private int numRemaining = edgeCount;
                private int edgeListIndex = -1;
                private EdgePointer edge = null;

                public boolean hasNext() {
                    return numRemaining > 0;
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public EdgePointer next() {
                    while (edge == null)
                        edge = edgeLists[++edgeListIndex];

                    int returnIndex = -1;


                    if (edgeListIndex == 0) {
                        while ((edge != null)
                               && !((outgoing && edge.directed) || (undirected && !edge.directed))) {
                            edge = edge.nextOutEdge;

                            if (edge == null) {
                                edge = edgeLists[++edgeListIndex];

                                break;
                            }
                        }

                        if ((edge != null) && (edgeListIndex == 0)) {
                            returnIndex = edge.index;
                            edge = edge.nextOutEdge;
                        }
                    }

                    if (edgeListIndex == 1) {
                        while (((edge.source.index == edge.target.index)
                               && ((outgoing && edge.directed) || (undirected && !edge.directed)))
                               || !((incoming && edge.directed) || (undirected && !edge.directed))) {
                            edge = edge.nextInEdge;
                        }

                        returnIndex = edge.index;
                        edge = edge.nextInEdge;
                    }

                    numRemaining--;

                    return m_edges.get(returnIndex);
                }
            };
	}

	private Iterator<EdgePointer> edgesConnecting(final NodePointer node0, final NodePointer node1, final EdgeType et) {

		if ( node0 == null )
			throw new NullPointerException("node0 is null");
		if ( node1 == null )
			throw new NullPointerException("node1 is null");
		
		final Iterator<EdgePointer> theAdj;
		final int nodeZero;
		final int nodeOne;

		// choose the smaller iterator
		if ( countEdges(node0,et) <= countEdges(node1,et) ) {
			theAdj = edgesAdjacent(node0, et); 
			nodeZero = node0.index;
			nodeOne = node1.index;
		} else {
			theAdj = edgesAdjacent(node1, et); 
			nodeZero = node1.index;
			nodeOne = node0.index;
		}

		return new Iterator<EdgePointer>() {
				private int nextEdge = -1;

				private void ensureComputeNext() {
					if (nextEdge != -1) {
						return;
					}

					while (theAdj.hasNext()) {
						final EdgePointer e = theAdj.next();
						final int edge = e.index;

						if (nodeOne == (nodeZero ^ e.source.index ^ e.target.index) ) {
							nextEdge = edge;
							return;
						}
					}

					nextEdge = -2;
				}

				public void remove() {
					throw new UnsupportedOperationException();	
				}

				public boolean hasNext() {
					ensureComputeNext();
					return ( nextEdge >= 0 );
				}

				public EdgePointer next() {
					ensureComputeNext();

					final int returnIndex = nextEdge;
					nextEdge = -1;

					return m_edges.get(returnIndex);
				}
			};
	}

	private int countEdges(final NodePointer n, final EdgeType edgeType) {

		boolean undirected = false;
		boolean incoming = false;
		boolean outgoing = false;

		if ( edgeType == EdgeType.UNDIRECTED_EDGE || 
		     edgeType == EdgeType.ANY_EDGE )
			undirected = true;
	
		if ( edgeType == EdgeType.DIRECTED_EDGE || 
		     edgeType == EdgeType.ANY_EDGE || 
		     edgeType == EdgeType.INCOMING_EDGE )
			incoming = true;

		if ( edgeType == EdgeType.DIRECTED_EDGE || 
		     edgeType == EdgeType.ANY_EDGE || 
		     edgeType == EdgeType.OUTGOING_EDGE )
			outgoing = true;

		int tentativeEdgeCount = 0;

		if (outgoing) {
			tentativeEdgeCount += n.outDegree;
		}

		if (incoming) {
			tentativeEdgeCount += n.inDegree;
		}

		if (undirected) {
			tentativeEdgeCount += n.undDegree;
		}

		if (outgoing && incoming) {
			tentativeEdgeCount -= n.selfEdges;
		}

		return tentativeEdgeCount;
	}

	private EdgePointer getEdgePointer(CyEdge edge) {
		assert(edge!=null);
		final int e = edge.getIndex();
		if ( e >= 0 && e < m_edges.size() )
			return m_edges.get(e);
		else
			return null;
	}

	private NodePointer getNodePointer(CyNode node) {
		assert(node!=null);
		final int n = node.getIndex();
		if ( n >= 0 && n < m_nodes.size() )
			return m_nodes.get(n);
		else
			return null;
	}
}
