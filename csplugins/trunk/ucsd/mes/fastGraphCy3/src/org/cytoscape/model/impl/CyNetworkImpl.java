
package org.cytoscape.model.impl;

import java.util.List;
import java.util.ArrayList;
import java.lang.RuntimeException;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;

import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntEnumerator;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

public class CyNetworkImpl implements CyNetwork {

	private String id;
	private DynamicGraph dg;
	private ArrayList<CyNode> nodeList; 
	private ArrayList<CyEdge> edgeList; 
	private int nodeCount;
	private int edgeCount;

	public CyNetworkImpl(String ident) {
		id = ident;
		dg = DynamicGraphFactory.instantiateDynamicGraph();
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeCount = 0;
		edgeCount = 0;
	}
		
	public String getIdentifier() {
		return id;
	}

	public CyNode addNode() {
		int newNodeInd = dg.nodeCreate();	
		CyNode newNode = new CyNodeImpl(newNodeInd);
		if ( newNodeInd == nodeList.size() )
			nodeList.add( newNode );
		else if ( newNodeInd < nodeList.size() && newNodeInd >= 0 )
			nodeList.set( newNodeInd, newNode );
		else
			throw new RuntimeException("bad new int index: " + newNodeInd + " max size: " + nodeList.size());

		nodeCount++;
		return newNode;
	}

	public boolean removeNode(CyNode node) {
		if ( !contains(node) ) 
			return false;

		List<CyEdge> edgesToRemove = getAdjacentEdgeList(node,CyNetwork.ANY_EDGE);
		for ( CyEdge etr : edgesToRemove ) {
			boolean removeSuccess = removeEdge(etr);
			if ( !removeSuccess )
				throw new RuntimeException("couldn't remove edge in preparation for node removal: " + etr);	
		}

		int remInd = node.getIndex();
		boolean rem = dg.nodeRemove(remInd);
		if ( rem ) {
			nodeList.set(remInd, null);
			nodeCount--;
		}
		
		return rem;
	}

	public CyEdge addEdge(CyNode source, CyNode target, boolean isDirected) {
		if ( !contains(source) || !contains(target) ) 
			throw new RuntimeException("invalid input nodes");

		int newEdgeInd = dg.edgeCreate(source.getIndex(),target.getIndex(),isDirected);

		CyEdge newEdge = new CyEdgeImpl(source,target,isDirected,newEdgeInd);
		if ( newEdgeInd == edgeList.size() )
			edgeList.add( newEdge );
		else if ( newEdgeInd < edgeList.size() && newEdgeInd > 0 )
			edgeList.set( newEdgeInd, newEdge );
		else
			throw new RuntimeException("bad new int index: " + newEdgeInd + " max size: " + edgeList.size());

		edgeCount++;
		return newEdge;
	}


	public boolean removeEdge(CyEdge edge) {
		if ( !contains(edge) ) 
			return false;

		int remInd = edge.getIndex();
		boolean rem = dg.edgeRemove(remInd);
		if ( rem ) {
			edgeList.set(remInd, null);
			edgeCount--;
		}
		
		return rem;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public List<CyNode> getNodeList() {
		ArrayList<CyNode> nl = new ArrayList<CyNode>();
		IntEnumerator it = dg.nodes();

		while ( it.numRemaining() > 0 ) {
			CyNode n = nodeList.get( it.nextInt() );
			if ( n == null )
				throw new RuntimeException("Iterator and List out of sync");
			nl.add( n );
		}
	
		return nl;
	}

	public List<CyEdge> getEdgeList() {
		ArrayList<CyEdge> el = new ArrayList<CyEdge>();
		IntEnumerator it = dg.edges();

		while ( it.numRemaining() > 0 ) {
			CyEdge e = edgeList.get( it.nextInt() );
			if ( e == null )
				throw new RuntimeException("Iterator and List out of sync");
			el.add( e );
		}
	
		return el;
	}

	public boolean contains( CyNode node ) {
		if ( node == null )
			return false;

		return dg.nodeExists( node.getIndex() );
	}

	public boolean contains( CyEdge edge ) {
		if ( edge == null )
			return false;

		if ( dg.edgeType( edge.getIndex() ) == -1 )
			return false;
		else
			return true;
	}

	public boolean contains( CyNode from, CyNode to ) {
		if ( !contains(from) || !contains(to) )
			return false;
	
		IntIterator it = dg.edgesConnecting(from.getIndex(),to.getIndex(),true,true,true);

		if ( it == null )
			return false;

		return it.hasNext();
	}

	public List<CyNode> getNeighborList( CyNode node, byte edgeType ) {
		if ( !contains(node) )
			throw new RuntimeException("bad node");
		
		ArrayList<CyNode> nodes = new ArrayList<CyNode>();

		boolean incoming = (edgeType & CyNetwork.INCOMING_EDGE) == CyNetwork.INCOMING_EDGE;
		boolean outgoing = (edgeType & CyNetwork.OUTGOING_EDGE) == CyNetwork.OUTGOING_EDGE;
		boolean undirected = (edgeType & CyNetwork.UNDIRECTED_EDGE) == CyNetwork.UNDIRECTED_EDGE;

		IntEnumerator it = dg.edgesAdjacent(node.getIndex(),outgoing,incoming,undirected);

		while ( it != null && it.numRemaining() > 0 ) {
			int edgeInd = it.nextInt();
			int neighbor = node.getIndex() ^ dg.edgeSource(edgeInd) ^ dg.edgeTarget(edgeInd);
			if ( neighbor < 0 || neighbor >= nodeList.size() )
				throw new RuntimeException("bad neighbor");
			CyNode n = nodeList.get(neighbor);
			if ( n == null )
				throw new RuntimeException("null neighbor");

			nodes.add(n);
		}

		return nodes;
	}

	public List<CyEdge> getAdjacentEdgeList( CyNode node, byte edgeType ) {
		if ( !contains(node) )
			throw new RuntimeException("bad nodes");

		ArrayList<CyEdge> edges = new ArrayList<CyEdge>();

		boolean incoming = (edgeType & CyNetwork.INCOMING_EDGE) == CyNetwork.INCOMING_EDGE;
		boolean outgoing = (edgeType & CyNetwork.OUTGOING_EDGE) == CyNetwork.OUTGOING_EDGE;
		boolean undirected = (edgeType & CyNetwork.UNDIRECTED_EDGE) == CyNetwork.UNDIRECTED_EDGE;

		IntEnumerator it = dg.edgesAdjacent(node.getIndex(),outgoing,incoming,undirected);

		while ( it != null && it.numRemaining() > 0 ) {
			int edgeInd = it.nextInt();
			CyEdge e = edgeList.get(edgeInd);
			if ( e == null )
				throw new RuntimeException("Iterator and List out of sync");
			edges.add(e);
		}

		return edges;
	}

	public List<CyEdge> getConnectingEdgeList( CyNode source, CyNode target, byte edgeType ) {
		if ( !contains(source) || !contains(target) )
			throw new RuntimeException("bad nodes");

		ArrayList<CyEdge> edges = new ArrayList<CyEdge>();

		boolean incoming = (edgeType & CyNetwork.INCOMING_EDGE) == CyNetwork.INCOMING_EDGE;
		boolean outgoing = (edgeType & CyNetwork.OUTGOING_EDGE) == CyNetwork.OUTGOING_EDGE;
		boolean undirected = (edgeType & CyNetwork.UNDIRECTED_EDGE) == CyNetwork.UNDIRECTED_EDGE;

		IntIterator it = dg.edgesConnecting(source.getIndex(),target.getIndex(),
		                                       outgoing,incoming,undirected);

		while ( it != null && it.hasNext() ) {
			int edgeInd = it.nextInt();
			CyEdge e = edgeList.get(edgeInd);
			if ( e == null )
				throw new RuntimeException("Iterator and List out of sync");
			edges.add(e);
		}

		return edges;
	}

	public CyNode getNode(int index) {
		if ( index < 0 || index >= nodeList.size() )
			return null;
		else 
			return nodeList.get(index);
	}

	public CyEdge getEdge(int index) {
		if ( index < 0 || index >= edgeList.size() )
			return null;
		else 
			return edgeList.get(index);
	}
}
