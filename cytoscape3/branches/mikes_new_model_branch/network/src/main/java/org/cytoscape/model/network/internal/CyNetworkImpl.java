
package org.cytoscape.model.internal;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.DynamicGraphFactory;

import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntEnumerator;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.EdgeType;

import org.cytoscape.model.events.AboutToRemoveEdgeEvent;     
import org.cytoscape.model.events.AboutToRemoveEdgeListener;  
import org.cytoscape.model.events.AddedEdgeEvent;     
import org.cytoscape.model.events.AddedEdgeListener;  
import org.cytoscape.model.events.AboutToRemoveNodeEvent;     
import org.cytoscape.model.events.AboutToRemoveNodeListener;  
import org.cytoscape.model.events.AddedNodeEvent;     
import org.cytoscape.model.events.AddedNodeListener;  
import org.cytoscape.model.events.RemovedNodeListener;
import org.cytoscape.model.events.RemovedNodeEvent;
import org.cytoscape.model.events.RemovedEdgeEvent;     
import org.cytoscape.model.events.RemovedEdgeListener;

import org.cytoscape.model.events.internal.NodeEvent;
import org.cytoscape.model.events.internal.EdgeEvent;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.attributes.InitialCyAttributesManager;

public class CyNetworkImpl implements CyNetwork {

	final private long suid;
	final private DynamicGraph dg;
	final private ArrayList<CyNode> nodeList; 
	final private ArrayList<CyEdge> edgeList; 
	final private AtomicInteger nodeCount;
	final private AtomicInteger edgeCount;

	final static private int OUT = 0;
	final static private int IN = 1;
	final static private int UN = 2;

	final private Map<String,CyAttributesManager> netAttrMgr;
	final private Map<String,CyAttributesManager> nodeAttrMgr;
	final private Map<String,CyAttributesManager> edgeAttrMgr;

	final private CyEventHelper eventHelper;

	public CyNetworkImpl(CyEventHelper eh) {
		suid = IdFactory.getNextSUID();
		dg = DynamicGraphFactory.instantiateDynamicGraph();
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeCount = new AtomicInteger(0);
		edgeCount = new AtomicInteger(0);

		netAttrMgr = new HashMap<String,CyAttributesManager>();
        netAttrMgr.put("USER",new CyAttributesManagerImpl(InitialCyAttributesManager.getInstance().getTypeMap()));

		nodeAttrMgr = new HashMap<String,CyAttributesManager>();
        nodeAttrMgr.put("USER",new CyAttributesManagerImpl(InitialCyAttributesManager.getInstance().getTypeMap()));

		edgeAttrMgr = new HashMap<String,CyAttributesManager>();
        edgeAttrMgr.put("USER",new CyAttributesManagerImpl(InitialCyAttributesManager.getInstance().getTypeMap()));

		eventHelper = eh;
	}

	public long getSUID() {
		return suid;
	}

	public CyNode addNode() {

		CyNode newNode;

		synchronized (this) {
			int newNodeInd = dg.nodeCreate();	
			newNode = new CyNodeImpl(this,newNodeInd,nodeAttrMgr);
			if ( newNodeInd == nodeList.size() )
				nodeList.add( newNode );
			else if ( newNodeInd < nodeList.size() && newNodeInd >= 0 )
				nodeList.set( newNodeInd, newNode );
			else
				throw new IllegalStateException("bad new int index: " + newNodeInd + " max size: " + nodeList.size());
			nodeCount.incrementAndGet();
		}

		eventHelper.fireSynchronousEvent( (AddedNodeEvent) new NodeEvent(newNode, this), 
		                                  AddedNodeListener.class );

		return newNode;
	}

	public boolean removeNode(final CyNode node) {

		eventHelper.fireSynchronousEvent( (AboutToRemoveNodeEvent) new NodeEvent(node, this), 
		                                  AboutToRemoveNodeListener.class );

		boolean rem = false;

		synchronized (this) {
			if ( !containsNode(node) ) 
				return false;

			List<CyEdge> edgesToRemove = getAdjacentEdgeList(node,CyEdge.Type.ANY);
			for ( CyEdge etr : edgesToRemove ) {
				boolean removeSuccess = removeEdge(etr);
				if ( !removeSuccess )
					throw new IllegalStateException("couldn't remove edge in preparation for node removal: " + etr);	
			}

			int remInd = node.getIndex();
			rem = dg.nodeRemove(remInd);
			if ( rem ) {
				nodeList.set(remInd, null);
				nodeCount.decrementAndGet();
			}
		}

		eventHelper.fireSynchronousEvent( (RemovedNodeEvent)new NodeEvent(node, this), 
		                                  RemovedNodeListener.class );

		return rem;
	}

	public CyEdge addEdge(final CyNode source, final CyNode target, final boolean isDirected) {

		CyEdge newEdge;

		synchronized (this) {
			if ( !containsNode(source) || !containsNode(target) ) 
				throw new IllegalArgumentException("invalid input nodes");

			int newEdgeInd = dg.edgeCreate(source.getIndex(),target.getIndex(),isDirected);

			newEdge = new CyEdgeImpl(source,target,isDirected,newEdgeInd,edgeAttrMgr);
			if ( newEdgeInd == edgeList.size() )
				edgeList.add( newEdge );
			else if ( newEdgeInd < edgeList.size() && newEdgeInd > 0 )
				edgeList.set( newEdgeInd, newEdge );
			else
				throw new IllegalStateException("bad new int index: " + newEdgeInd + " max size: " + edgeList.size());

			edgeCount.incrementAndGet();
		}

		eventHelper.fireSynchronousEvent( (AddedEdgeEvent) new EdgeEvent(newEdge, this), 
		                                  AddedEdgeListener.class );

		return newEdge;
	}


	public boolean removeEdge(final CyEdge edge) {
		eventHelper.fireSynchronousEvent( (AboutToRemoveEdgeEvent) new EdgeEvent(edge, this), 
		                                  AboutToRemoveEdgeListener.class );

		boolean rem = false;

		synchronized (this) {
			if ( !containsEdge(edge) ) 
				return false;

			int remInd = edge.getIndex();
			rem = dg.edgeRemove(remInd);
			if ( rem ) {
				edgeList.set(remInd, null);
				edgeCount.decrementAndGet();
			}
		}

		eventHelper.fireSynchronousEvent( (RemovedEdgeEvent) new EdgeEvent(edge, this), 
		                                  RemovedEdgeListener.class );
		
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

	public boolean containsNode( final CyNode node ) {
		if ( node == null )
			return false;

		return dg.nodeExists( node.getIndex() );
	}

	public boolean containsEdge( final CyEdge edge ) {
		if ( edge == null )
			return false;

		if ( dg.edgeType( edge.getIndex() ) == -1 )
			return false;
		else
			return true;
	}

	public boolean containsEdge( final CyNode from, final CyNode to ) {
		if ( !containsNode(from) || !containsNode(to) )
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

		if ( !containsNode(node) )
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

		if ( !containsNode(node) )
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

		if ( !containsNode(source) || !containsNode(target) )
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

	public CyAttributes getCyAttributes(String namespace) {
		if ( namespace == null )
			throw new NullPointerException("namespace is null");

		CyAttributesManager mgr = netAttrMgr.get(namespace);
		if ( mgr == null )
			throw new NullPointerException("attribute manager is null for namespace: " + namespace);

		return mgr.getCyAttributes(suid);
	}

	public Map<String,CyAttributesManager> getNetworkCyAttributesManagers() {
		return netAttrMgr;	
	}

	public Map<String,CyAttributesManager> getNodeCyAttributesManagers() {
		return nodeAttrMgr;
	}

	public Map<String,CyAttributesManager> getEdgeCyAttributesManagers() {
		return edgeAttrMgr;
	}
}
