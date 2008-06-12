
package org.cytoscape.network.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.DynamicGraphFactory;

import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntEnumerator;

import org.cytoscape.network.CyNetwork;
import org.cytoscape.network.CyNode;
import org.cytoscape.network.CyEdge;
import org.cytoscape.network.EdgeType;

import org.cytoscape.attrs.CyAttributes;
import org.cytoscape.attrs.impl.CyAttributesImpl;

public class CyNetworkImpl implements CyNetwork {

	final private String id;
	final private DynamicGraph dg;
	final private ArrayList<CyNode> nodeList; 
	final private ArrayList<CyEdge> edgeList; 
	final private AtomicInteger nodeCount;
	final private AtomicInteger edgeCount;
	final private CyAttributes attrs;

	final static private int OUT = 0;
	final static private int IN = 1;
	final static private int UN = 2;

	public CyNetworkImpl(final String ident) {
		id = ident;
		dg = DynamicGraphFactory.instantiateDynamicGraph();
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeCount = new AtomicInteger(0);
		edgeCount = new AtomicInteger(0);
		attrs = new CyAttributesImpl();
	}
		
	public String getIdentifier() {
		return id;
	}

	public synchronized CyNode addNode() {

		int newNodeInd = dg.nodeCreate();	
		CyNode newNode = new CyNodeImpl(this,newNodeInd);
		if ( newNodeInd == nodeList.size() )
			nodeList.add( newNode );
		else if ( newNodeInd < nodeList.size() && newNodeInd >= 0 )
			nodeList.set( newNodeInd, newNode );
		else
			throw new IllegalStateException("bad new int index: " + newNodeInd + " max size: " + nodeList.size());

		nodeCount.incrementAndGet();

		return newNode;
	}

	public synchronized boolean removeNode(final CyNode node) {

		if ( !contains(node) ) 
			return false;

		List<CyEdge> edgesToRemove = getAdjacentEdgeList(node,EdgeType.ANY_EDGE);
		for ( CyEdge etr : edgesToRemove ) {
			boolean removeSuccess = removeEdge(etr);
			if ( !removeSuccess )
				throw new IllegalStateException("couldn't remove edge in preparation for node removal: " + etr);	
		}

		int remInd = node.getIndex();
		boolean rem = dg.nodeRemove(remInd);
		if ( rem ) {
			nodeList.set(remInd, null);
			nodeCount.decrementAndGet();
		}

		return rem;
	}

	public synchronized CyEdge addEdge(final CyNode source, final CyNode target, final boolean isDirected) {
		if ( !contains(source) || !contains(target) ) 
			throw new IllegalArgumentException("invalid input nodes");

		int newEdgeInd = dg.edgeCreate(source.getIndex(),target.getIndex(),isDirected);

		CyEdge newEdge = new CyEdgeImpl(source,target,isDirected,newEdgeInd);
		if ( newEdgeInd == edgeList.size() )
			edgeList.add( newEdge );
		else if ( newEdgeInd < edgeList.size() && newEdgeInd > 0 )
			edgeList.set( newEdgeInd, newEdge );
		else
			throw new IllegalStateException("bad new int index: " + newEdgeInd + " max size: " + edgeList.size());

		edgeCount.incrementAndGet();
		return newEdge;
	}


	public synchronized boolean removeEdge(final CyEdge edge) {
		if ( !contains(edge) ) 
			return false;

		int remInd = edge.getIndex();
		boolean rem = dg.edgeRemove(remInd);
		if ( rem ) {
			edgeList.set(remInd, null);
			edgeCount.decrementAndGet();
		}
		
		return rem;
	}

	public int getNodeCount() {
		return nodeCount.get();
	}

	public int getEdgeCount() {
		return edgeCount.get();
	}

	public List<CyNode> getNodeList() {
		final ArrayList<CyNode> nl = new ArrayList<CyNode>();
		final IntEnumerator it = dg.nodes();

		while ( it.numRemaining() > 0 ) {
			CyNode n = nodeList.get( it.nextInt() );
			if ( n == null )
				throw new IllegalStateException("Iterator and List out of sync");
			nl.add( n );
		}
	
		return nl;
	}

	public List<CyEdge> getEdgeList() {
		final ArrayList<CyEdge> el = new ArrayList<CyEdge>();
		final IntEnumerator it = dg.edges();

		while ( it.numRemaining() > 0 ) {
			CyEdge e = edgeList.get( it.nextInt() );
			if ( e == null )
				throw new IllegalStateException("Iterator and List out of sync");
			el.add( e );
		}
	
		return el;
	}

	public boolean contains( final CyNode node ) {
		if ( node == null )
			return false;

		return dg.nodeExists( node.getIndex() );
	}

	public boolean contains( final CyEdge edge ) {
		if ( edge == null )
			return false;

		if ( dg.edgeType( edge.getIndex() ) == -1 )
			return false;
		else
			return true;
	}

	public boolean contains( final CyNode from, final CyNode to ) {
		if ( !contains(from) || !contains(to) )
			return false;
	
		final IntIterator it = dg.edgesConnecting(from.getIndex(),to.getIndex(),true,true,true);

		if ( it == null )
			return false;

		return it.hasNext();
	}

	public List<CyNode> getNeighborList( final CyNode node, final EdgeType edgeType ) {

		boolean[] et = convertEdgeType(edgeType);

		final IntEnumerator it; 
		ArrayList<CyNode> nodes = new ArrayList<CyNode>();

		if ( !contains(node) )
			throw new IllegalArgumentException("bad node");
		
		it = dg.edgesAdjacent(node.getIndex(),et[OUT],et[IN],et[UN]);

		while ( it != null && it.numRemaining() > 0 ) {
			int edgeInd = it.nextInt();
			int neighbor = node.getIndex() ^ dg.edgeSource(edgeInd) ^ dg.edgeTarget(edgeInd);
			if ( neighbor < 0 || neighbor >= nodeList.size() )
				throw new IllegalStateException("bad neighbor");
			CyNode n = nodeList.get(neighbor);
			if ( n == null )
				throw new IllegalStateException("null neighbor");

			nodes.add(n);
		}

		return nodes;
	}

	public List<CyEdge> getAdjacentEdgeList( final CyNode node, final EdgeType edgeType ) {
		boolean[] et = convertEdgeType(edgeType);

		ArrayList<CyEdge> edges = new ArrayList<CyEdge>();
		final IntEnumerator it; 

		if ( !contains(node) )
			throw new IllegalArgumentException("bad nodes");

		it = dg.edgesAdjacent(node.getIndex(),et[OUT],et[IN],et[UN]);

		while ( it != null && it.numRemaining() > 0 ) {
			int edgeInd = it.nextInt();
			CyEdge e = edgeList.get(edgeInd);
			if ( e == null )
				throw new IllegalStateException("Iterator and List out of sync");
			edges.add(e);
		}

		return edges;
	}

	public List<CyEdge> getConnectingEdgeList( final CyNode source, final CyNode target, 
	                                           final EdgeType edgeType ) {
		boolean[] et = convertEdgeType(edgeType);

		ArrayList<CyEdge> edges = new ArrayList<CyEdge>();
		final IntIterator it;

		if ( !contains(source) || !contains(target) )
			throw new IllegalArgumentException("bad nodes");

		it = dg.edgesConnecting(source.getIndex(),target.getIndex(),et[OUT],et[IN],et[UN]);

		while ( it != null && it.hasNext() ) {
			int edgeInd = it.nextInt();
			CyEdge e = edgeList.get(edgeInd);
			if ( e == null )
				throw new IllegalStateException("Iterator and List out of sync");
			edges.add(e);
		}

		return edges;
	}

	public CyNode getNode(final int index) {
		if ( index < 0 || index >= nodeList.size() )
			return null;
		else 
			return nodeList.get(index);
	}

	public CyEdge getEdge(final int index) {
		if ( index < 0 || index >= edgeList.size() )
			return null;
		else 
			return edgeList.get(index);
	}

	private boolean[] convertEdgeType(final EdgeType e) {

		if ( e == EdgeType.UNDIRECTED_EDGE )
			return new boolean[] { false, false, true }; 
		else if ( e == EdgeType.DIRECTED_EDGE )
			return new boolean[] { true, true, false }; 
		else if ( e == EdgeType.INCOMING_EDGE )
			return new boolean[] { false, true, false }; 
		else if ( e == EdgeType.OUTGOING_EDGE )
			return new boolean[] { true, false, false }; 
		else // ANY_EDGE
			return new boolean[] { true, true, true }; 
	}

	public CyAttributes getAttributes() {
		return attrs;
	}
}
