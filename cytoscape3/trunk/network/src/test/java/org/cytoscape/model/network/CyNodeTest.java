package org.cytoscape.model.network;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.internal.CyNetworkImpl;
import org.cytoscape.model.CyDataTable;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.model.CyRow;

public class CyNodeTest extends TestCase {

	private CyNetwork net;

	public static Test suite() {
		return new TestSuite(CyNodeTest.class);
	}

	public void setUp() {
		net = new CyNetworkImpl( new DummyCyEventHelper() );	
	}

	public void tearDown() {
		net = null;
	}

	public void testGetIndex() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		assertTrue("index >= 0",n1.getIndex() >= 0 );
		assertTrue("index >= 0",n2.getIndex() >= 0 );
	}

	public void testBasicGetNeighborList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,false);
		CyEdge e2 = net.addEdge(n2,n3,false);

		// one neighbor
		List<CyNode> l = n1.getNeighborList(CyEdge.Type.ANY);
		assertEquals("one neighbor",1,l.size());
		assertTrue("contains node 2",l.contains(n2));

		// two neighbors
		l = n2.getNeighborList(CyEdge.Type.ANY);
		assertEquals("two neighbors",2,l.size());
		assertTrue("contains node 1",l.contains(n1));
		assertTrue("contains node 3",l.contains(n3));
		assertFalse("contains node 4",l.contains(n4));

		// no neighbors
		l = n4.getNeighborList(CyEdge.Type.ANY);
		assertEquals("no neighbors",0,l.size());

		// whoa!  what about self edges?
		// TODO
		CyEdge e3 = net.addEdge(n4,n4,false);
		l = n4.getNeighborList(CyEdge.Type.ANY);
		assertEquals("one neighbor?",1,l.size());

		CyEdge e4 = net.addEdge(n4,n4,true);
		l = n4.getNeighborList(CyEdge.Type.ANY);
		assertEquals("two neighbors",2,l.size());
	}

	public void testUndirectedGetNeighborList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,false);
		CyEdge e2 = net.addEdge(n2,n3,false);
		CyEdge e3 = net.addEdge(n4,n2,false);
		CyEdge e4 = net.addEdge(n4,n1,false);

		List<CyNode> l = n1.getNeighborList(EdgeType.UNDIRECTED_EDGE);
		assertEquals("node 1 neighbors",2,l.size());
		assertTrue("contains node 2",l.contains(n2));
		assertTrue("contains node 4",l.contains(n4));
	
		l = n2.getNeighborList(EdgeType.UNDIRECTED_EDGE);
		assertEquals("node 2 neighbors",3,l.size());
		assertTrue("contains node 1",l.contains(n1));
		assertTrue("contains node 3",l.contains(n3));
		assertTrue("contains node 4",l.contains(n4));

		l = n2.getNeighborList(CyEdge.Type.ANY);
		assertEquals("node 2 neighbors",3,l.size());
		assertTrue("contains node 1",l.contains(n1));
		assertTrue("contains node 3",l.contains(n3));
		assertTrue("contains node 4",l.contains(n4));

		l = n2.getNeighborList(EdgeType.INCOMING_EDGE);
		assertEquals("node 2 neighbors",0,l.size());

		l = n2.getNeighborList(EdgeType.OUTGOING_EDGE);
		assertEquals("node 2 neighbors",0,l.size());

		l = n2.getNeighborList(EdgeType.DIRECTED_EDGE);
		assertEquals("node 2 neighbors",0,l.size());
	}

	public void testDirectedGetNeighborList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,true);
		CyEdge e2 = net.addEdge(n2,n3,true);
		CyEdge e3 = net.addEdge(n4,n2,true);
		CyEdge e4 = net.addEdge(n4,n1,true);
		CyEdge e5 = net.addEdge(n5,n2,false);

		List<CyNode> l = n1.getNeighborList(EdgeType.DIRECTED_EDGE);
		assertEquals("node 1 neighbors directed",2,l.size());
		assertTrue("contains node 2",l.contains(n2));
		assertTrue("contains node 4",l.contains(n4));
	
		l = n1.getNeighborList(EdgeType.INCOMING_EDGE);
		assertEquals("node 1 neighbors incoming",1,l.size());
		assertTrue("contains node 4",l.contains(n4));

		l = n1.getNeighborList(EdgeType.OUTGOING_EDGE);
		assertEquals("node 1 neighbors outgoing",1,l.size());
		assertTrue("contains node 2",l.contains(n2));

		l = n2.getNeighborList(EdgeType.UNDIRECTED_EDGE);
		assertEquals("node 2 neighbors undirected",1,l.size());
		assertTrue("contains node 5",l.contains(n5));

		l = n2.getNeighborList(EdgeType.DIRECTED_EDGE);
		assertEquals("node 2 neighbors directed",3,l.size());
		assertTrue("contains node 1",l.contains(n1));
		assertTrue("contains node 3",l.contains(n3));
		assertTrue("contains node 4",l.contains(n4));

		l = n2.getNeighborList(EdgeType.INCOMING_EDGE);
		assertEquals("node 2 neighbors incoming",2,l.size());
		assertTrue("contains node 1",l.contains(n1));
		assertTrue("contains node 4",l.contains(n4));

		l = n2.getNeighborList(EdgeType.OUTGOING_EDGE);
		assertEquals("node 2 neighbors outgoing",1,l.size());
		assertTrue("contains node 3",l.contains(n3));
	}

	public void testBasicGetAdjacentEdgeList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,false);
		CyEdge e2 = net.addEdge(n2,n3,false);

		// one edge
		List<CyEdge> l = n1.getAdjacentEdgeList(CyEdge.Type.ANY);
		assertEquals("one adjacent edge",1,l.size());
		assertTrue("contains edge 1",l.contains(e1));

		// two edge
		l = n2.getAdjacentEdgeList(CyEdge.Type.ANY);
		assertEquals("two adjacent edges",2,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 2",l.contains(e2));

		// no adjacent edges
		l = n4.getAdjacentEdgeList(CyEdge.Type.ANY);
		assertEquals("no edges",0,l.size());

		// whoa!  what about self edges?
		// TODO
		CyEdge e3 = net.addEdge(n4,n4,false);
		l = n4.getAdjacentEdgeList(CyEdge.Type.ANY);
		assertEquals("one adjacent edge?",1,l.size());
	}

	public void testUndirectedGetAdjacentEdgeList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,false);
		CyEdge e2 = net.addEdge(n2,n3,false);
		CyEdge e3 = net.addEdge(n4,n2,false);
		CyEdge e4 = net.addEdge(n4,n1,false);

		List<CyEdge> l = n1.getAdjacentEdgeList(EdgeType.UNDIRECTED_EDGE);
		assertEquals("node 1 adjacent edges",2,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 4",l.contains(e4));
	
		l = n2.getAdjacentEdgeList(EdgeType.UNDIRECTED_EDGE);
		assertEquals("node 2 adjacent edges",3,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 2",l.contains(e2));
		assertTrue("contains edge 3",l.contains(e3));

		l = n2.getAdjacentEdgeList(CyEdge.Type.ANY);
		assertEquals("node 2 adjacent edges",3,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 2",l.contains(e2));
		assertTrue("contains edge 3",l.contains(e3));

		l = n2.getAdjacentEdgeList(EdgeType.INCOMING_EDGE);
		assertEquals("node 2 adjacent edges",0,l.size());

		l = n2.getAdjacentEdgeList(EdgeType.OUTGOING_EDGE);
		assertEquals("node 2 adjacent edges",0,l.size());

		l = n2.getAdjacentEdgeList(EdgeType.DIRECTED_EDGE);
		assertEquals("node 2 adjacent edges",0,l.size());
	}

	public void testDirectedGetAdjacentEdgeList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,true);
		CyEdge e2 = net.addEdge(n2,n3,true);
		CyEdge e3 = net.addEdge(n4,n2,true);
		CyEdge e4 = net.addEdge(n4,n1,true);
		CyEdge e5 = net.addEdge(n5,n2,false);

		List<CyEdge> l = n1.getAdjacentEdgeList(EdgeType.DIRECTED_EDGE);
		assertEquals("node 1 adjacent edges directed",2,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 4",l.contains(e4));
	
		l = n1.getAdjacentEdgeList(EdgeType.INCOMING_EDGE);
		assertEquals("node 1 adjacent edges incoming",1,l.size());
		assertTrue("contains edge 4",l.contains(e4));

		l = n1.getAdjacentEdgeList(EdgeType.OUTGOING_EDGE);
		assertEquals("node 1 adjacent edges outgoing",1,l.size());
		assertTrue("contains edge 1",l.contains(e1));

		l = n2.getAdjacentEdgeList(EdgeType.UNDIRECTED_EDGE);
		assertEquals("node 2 adjacent edges undirected",1,l.size());
		assertTrue("contains edge 5",l.contains(e5));

		l = n2.getAdjacentEdgeList(EdgeType.DIRECTED_EDGE);
		assertEquals("node 2 adjacent edges directed",3,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 2",l.contains(e2));
		assertTrue("contains edge 3",l.contains(e3));

		l = n2.getAdjacentEdgeList(EdgeType.INCOMING_EDGE);
		assertEquals("node 2 adjacent edges incoming",2,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 3",l.contains(e3));

		l = n2.getAdjacentEdgeList(EdgeType.OUTGOING_EDGE);
		assertEquals("node 2 adjacent edges outgoing",1,l.size());
		assertTrue("contains edge 2",l.contains(e2));
	}

	public void testBasicGetConnectingEdgeList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,false);
		CyEdge e2 = net.addEdge(n2,n3,false);
		CyEdge e3 = net.addEdge(n1,n2,false);
		CyEdge e4 = net.addEdge(n1,n2,false);

		// between node 1 and 2
		List<CyEdge> l = n1.getConnectingEdgeList(n2,CyEdge.Type.ANY);
		assertEquals("connecting edges",3,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 3",l.contains(e1));
		assertTrue("contains edge 4",l.contains(e4));

		// between node 2 and 3
		l = n3.getConnectingEdgeList(n2,CyEdge.Type.ANY);
		assertEquals("connecting edges",1,l.size());
		assertTrue("contains edge 2",l.contains(e2));

		// between node 2 and 3 after adding an edge
		CyEdge e5 = net.addEdge(n3,n2,false);
		l = n2.getConnectingEdgeList(n3,CyEdge.Type.ANY);
		assertEquals("connecting edges",2,l.size());
		assertTrue("contains edge 2",l.contains(e2));
		assertTrue("contains edge 5",l.contains(e5));

		// between node 2 and 3 after deleting an edge
		boolean rem5 = net.removeEdge(e5);
		assertTrue("removed successfully",rem5);
		l = n2.getConnectingEdgeList(n3,CyEdge.Type.ANY);
		assertEquals("connecting edges",1,l.size());
		assertTrue("contains edge 2",l.contains(e2));
	}

	public void testUndirectedBasicGetConnectingEdgeList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,false);
		CyEdge e2 = net.addEdge(n2,n3,false);
		CyEdge e3 = net.addEdge(n1,n2,false);
		CyEdge e4 = net.addEdge(n1,n2,true);

		// between node 1 and 2
		List<CyEdge> l = n1.getConnectingEdgeList(n2,EdgeType.UNDIRECTED_EDGE);
		assertEquals("connecting edges",2,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 3",l.contains(e3));
	}

	public void testDirectedBasicGetConnectingEdgeList() {
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();

		CyEdge e1 = net.addEdge(n1,n2,true);
		CyEdge e2 = net.addEdge(n2,n3,true);
		CyEdge e3 = net.addEdge(n1,n2,true);
		CyEdge e4 = net.addEdge(n2,n1,true);
		CyEdge e5 = net.addEdge(n2,n1,false);

		// between node 1 and 2
		List<CyEdge> l = n1.getConnectingEdgeList(n2,EdgeType.DIRECTED_EDGE);
		assertEquals("connecting edges",3,l.size());
		assertTrue("contains edge 1",l.contains(e1));
		assertTrue("contains edge 3",l.contains(e3));
		assertTrue("contains edge 4",l.contains(e4));
	}

}
