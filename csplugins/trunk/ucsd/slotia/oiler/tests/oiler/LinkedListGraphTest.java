package oiler;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

import oiler.util.IntIterator;

public class LinkedListGraphTest
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(LinkedListGraphTest.class);
	}

	Graph<NamedNode,WeightedEdge> graph;

	@Before
	public void setUp()
	{
		graph = new LinkedListGraph<NamedNode,WeightedEdge>();
	}

	private Set<Integer> captureIteratorToSet(IntIterator iterator)
	{
		Set<Integer> set = new TreeSet<Integer>();
		while (iterator.hasNext())
		{
			Integer item = iterator.next();
			set.add(item);
		}
		return set;
	}

	private List<Integer> captureIteratorToList(IntIterator iterator)
	{
		List<Integer> list = new ArrayList<Integer>();
		while (iterator.hasNext())
			list.add(iterator.next());
		return list;
	}

	@Test
	public void testconstructor()
	{
		assertFalse(graph.edgeExists(0));
		assertFalse(graph.nodeExists(0));
		assertFalse(graph.edgeExists(1));
		assertFalse(graph.nodeExists(1));
		assertFalse(graph.edgeExists(2));
		assertFalse(graph.nodeExists(2));
		
		assertTrue(graph.edgeCount() == 0);
		assertTrue(graph.nodeCount() == 0);
	}

	@Test
	public void testconstructor2()
	{
		Graph<NamedNode,WeightedEdge> graph2 = new LinkedListGraph<NamedNode,WeightedEdge>();

		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C
			A --(4.0)--- B
			C --(5.0)--- C
		*/
		NamedNode nodeObjA = new BasicNamedNode("a");
		NamedNode nodeObjB = new BasicNamedNode("b");
		NamedNode nodeObjC = new BasicNamedNode("c");

		WeightedEdge edgeObj0 = new BasicWeightedEdge(0.0);
		WeightedEdge edgeObj1 = new BasicWeightedEdge(1.0);
		WeightedEdge edgeObj2 = new BasicWeightedEdge(2.0);
		WeightedEdge edgeObj3 = new BasicWeightedEdge(3.0);
		WeightedEdge edgeObj4 = new BasicWeightedEdge(4.0);
		WeightedEdge edgeObj5 = new BasicWeightedEdge(5.0);

		// add nodes and edges to graph2
		int nodeA = graph2.addNode(nodeObjA);
		int nodeB = graph2.addNode(nodeObjB);
		int nodeC = graph2.addNode(nodeObjC);

		int edge0 = graph2.addEdge(nodeA, nodeB, edgeObj0, Graph.DIRECTED_EDGE);
		int edge1 = graph2.addEdge(nodeA, nodeB, edgeObj1, Graph.DIRECTED_EDGE);
		int edge2 = graph2.addEdge(nodeA, nodeC, edgeObj2, Graph.DIRECTED_EDGE);
		int edge3 = graph2.addEdge(nodeB, nodeC, edgeObj3, Graph.DIRECTED_EDGE);
		int edge4 = graph2.addEdge(nodeA, nodeB, edgeObj4, Graph.UNDIRECTED_EDGE);
		int edge5 = graph2.addEdge(nodeC, nodeC, edgeObj5, Graph.UNDIRECTED_EDGE);

		// copy graph2 to graph
		graph = new LinkedListGraph<NamedNode,WeightedEdge>(graph2);

		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);

		// begin testing the copy constructor
		assertTrue(graph.nodeCount() == 3);
		assertTrue(graph.edgeCount() == 6);

		nodeA = graph.nodeIndex(nodeObjA);
		assertFalse(nodeA < 0);
		nodeB = graph.nodeIndex(nodeObjB);
		assertFalse(nodeB < 0);
		nodeC = graph.nodeIndex(nodeObjC);
		assertFalse(nodeC < 0);

		edge0 = graph.edgeIndex(edgeObj0);
		assertFalse(edge0 < 0);
		edge1 = graph.edgeIndex(edgeObj1);
		assertFalse(edge1 < 0);
		edge2 = graph.edgeIndex(edgeObj2);
		assertFalse(edge2 < 0);
		edge3 = graph.edgeIndex(edgeObj3);
		assertFalse(edge3 < 0);
		edge4 = graph.edgeIndex(edgeObj4);
		assertFalse(edge4 < 0);
		edge5 = graph.edgeIndex(edgeObj5);
		assertFalse(edge5 < 0);

		Set<Integer> nodeANeighborsIncoming = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.INCOMING_EDGE));
		assertTrue(nodeANeighborsIncoming.size() == 0);
		Set<Integer> nodeANeighborsOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(nodeANeighborsOutgoing.size() == 2);
		assertTrue(nodeANeighborsOutgoing.contains(new Integer(nodeB)));
		assertTrue(nodeANeighborsOutgoing.contains(new Integer(nodeC)));
		Set<Integer> nodeANeighborsUndirected = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.UNDIRECTED_EDGE));
		assertTrue(nodeANeighborsUndirected.size() == 1);
		assertTrue(nodeANeighborsUndirected.contains(new Integer(nodeB)));

		List<Integer> nodeAEdgesIncoming = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.INCOMING_EDGE));
		assertTrue(nodeAEdgesIncoming.size() == 0);
		List<Integer> nodeAEdgesOutgoing = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(nodeAEdgesOutgoing.size() == 3);
		assertTrue(nodeAEdgesOutgoing.contains(new Integer(edge0)));
		assertTrue(nodeAEdgesOutgoing.contains(new Integer(edge1)));
		assertTrue(nodeAEdgesOutgoing.contains(new Integer(edge2)));
		List<Integer> nodeAEdgesUndirected = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.UNDIRECTED_EDGE));
		assertTrue(nodeAEdgesUndirected.size() == 1);
		assertTrue(nodeAEdgesUndirected.contains(new Integer(edge4)));

		Set<Integer> nodeBNeighborsIncoming = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.INCOMING_EDGE));
		assertTrue(nodeBNeighborsIncoming.size() == 1);
		assertTrue(nodeBNeighborsIncoming.contains(new Integer(nodeA)));
		Set<Integer> nodeBNeighborsOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(nodeBNeighborsOutgoing.size() == 1);
		assertTrue(nodeBNeighborsOutgoing.contains(new Integer(nodeC)));
		Set<Integer> nodeBNeighborsUndirected = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.UNDIRECTED_EDGE));
		assertTrue(nodeBNeighborsUndirected.size() == 1);
		assertTrue(nodeBNeighborsUndirected.contains(new Integer(nodeA)));

		List<Integer> nodeBEdgesIncoming = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.INCOMING_EDGE));
		assertTrue(nodeBEdgesIncoming.size() == 2);
		assertTrue(nodeBEdgesIncoming.contains(new Integer(edge0)));
		assertTrue(nodeBEdgesIncoming.contains(new Integer(edge1)));
		List<Integer> nodeBEdgesOutgoing = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(nodeBEdgesOutgoing.size() == 1);
		assertTrue(nodeBEdgesOutgoing.contains(new Integer(edge3)));
		List<Integer> nodeBEdgesUndirected = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.UNDIRECTED_EDGE));
		assertTrue(nodeBEdgesUndirected.size() == 1);
		assertTrue(nodeBEdgesUndirected.contains(new Integer(edge4)));

		Set<Integer> nodeCNeighborsIncoming = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.INCOMING_EDGE));
		assertTrue(nodeCNeighborsIncoming.size() == 2);
		assertTrue(nodeCNeighborsIncoming.contains(new Integer(nodeA)));
		assertTrue(nodeCNeighborsIncoming.contains(new Integer(nodeB)));
		Set<Integer> nodeCNeighborsOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(nodeCNeighborsOutgoing.size() == 0);
		Set<Integer> nodeCNeighborsUndirected = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.UNDIRECTED_EDGE));
		assertTrue(nodeCNeighborsUndirected.size() == 1);
		assertTrue(nodeCNeighborsUndirected.contains(new Integer(nodeC)));

		List<Integer> nodeCEdgesIncoming = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.INCOMING_EDGE));
		assertTrue(nodeCEdgesIncoming.size() == 2);
		assertTrue(nodeCEdgesIncoming.contains(new Integer(edge2)));
		assertTrue(nodeCEdgesIncoming.contains(new Integer(edge3)));
		List<Integer> nodeCEdgesOutgoing = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(nodeCEdgesOutgoing.size() == 0);
		List<Integer> nodeCEdgesUndirected = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.UNDIRECTED_EDGE));
		assertTrue(nodeCEdgesUndirected.size() == 1);
		assertTrue(nodeCEdgesUndirected.contains(new Integer(edge5)));
	}

	@Test
	public void testaddNodes()
	{
		NamedNode nodeObj0 = new BasicNamedNode("node0");
		NamedNode nodeObj1 = new BasicNamedNode("node1");
		NamedNode nodeObj2 = new BasicNamedNode("node2");

		int node0 = graph.addNode(nodeObj0);
		int node1 = graph.addNode(nodeObj1);
		int node2 = graph.addNode(nodeObj2);

		assertTrue(graph.nodeExists(node0));
		assertTrue(graph.nodeExists(node1));
		assertTrue(graph.nodeExists(node2));

		assertTrue(graph.nodeObject(node0) == nodeObj0);
		assertTrue(graph.nodeObject(node1) == nodeObj1);
		assertTrue(graph.nodeObject(node2) == nodeObj2);
		
		Set<Integer> nodeSet = captureIteratorToSet(graph.nodes());
		assertTrue(nodeSet.contains(new Integer(node0)));
		assertTrue(nodeSet.contains(new Integer(node1)));
		assertTrue(nodeSet.contains(new Integer(node2)));

		assertTrue(graph.nodeCount() == 3);
	}

	@Test
	public void testnodeIterator()
	{
		int nodeCount = 5;
		
		int nodes[] = new int[nodeCount];
		for (int i = 0; i < nodeCount; i++)
			nodes[i] = graph.addNode(null);

		// has each node been visited by the iterator?
		boolean hasBeenVisited[] = new boolean[nodeCount];
		
		for (IntIterator iterator = graph.nodes(); iterator.hasNext();)
		{
			Integer node = iterator.next();
			
			// find the index in nodes[] that has node
			int index = -1;
			for (int i = 0; i < nodeCount; i++)
				if (nodes[i] == node)
					index = i;

			// check to see if we found an index
			assertTrue(index != -1);
			// check to see if we haven't visited the same node twice
			assertFalse(hasBeenVisited[index]);
			// mark the node as has been visited
			hasBeenVisited[index] = true;
		}

		// check to see each node has been visited
		for (int i = 0; i < nodeCount; i++)
			assertTrue(hasBeenVisited[i]);
	}

	@Test
	public void testaddEdges()
	{
		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		
		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C		*/
		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);
		int edge3 = graph.addEdge(nodeB, nodeC, new BasicWeightedEdge(3.0), Graph.DIRECTED_EDGE);

		assertTrue(graph.edgeCount() == 4);

		assertTrue(graph.edgeExists(edge0));
		assertTrue(graph.edgeExists(edge1));
		assertTrue(graph.edgeExists(edge2));
		assertTrue(graph.edgeExists(edge3));

		assertTrue(graph.edgeObject(edge0).weight() == 0.0);
		assertTrue(graph.edgeObject(edge1).weight() == 1.0);
		assertTrue(graph.edgeObject(edge2).weight() == 2.0);
		assertTrue(graph.edgeObject(edge3).weight() == 3.0);

		assertTrue(graph.edgeType(edge0) == Graph.DIRECTED_EDGE);
		assertTrue(graph.edgeType(edge1) == Graph.DIRECTED_EDGE);
		assertTrue(graph.edgeType(edge2) == Graph.DIRECTED_EDGE);
		assertTrue(graph.edgeType(edge3) == Graph.DIRECTED_EDGE);
	}

	@Test
	public void testedgeIterator()
	{
		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);

		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C		*/

		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);
		int edge3 = graph.addEdge(nodeB, nodeC, new BasicWeightedEdge(3.0), Graph.DIRECTED_EDGE);

		Set<Integer> edgeSet = captureIteratorToSet(graph.edges());
		assertTrue(edgeSet.size() == 4);
		assertTrue(edgeSet.contains(edge0));
		assertTrue(edgeSet.contains(edge1));
		assertTrue(edgeSet.contains(edge2));
		assertTrue(edgeSet.contains(edge3));
	}

	@Test
	public void testedgeSourceTarget()
	{
		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C		*/
	
		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);
		int edge3 = graph.addEdge(nodeB, nodeC, new BasicWeightedEdge(3.0), Graph.DIRECTED_EDGE);

		assertTrue(graph.edgeSource(edge0) == nodeA && graph.edgeTarget(edge0) == nodeB);
		assertTrue(graph.edgeSource(edge1) == nodeA && graph.edgeTarget(edge1) == nodeB);
		assertTrue(graph.edgeSource(edge2) == nodeA && graph.edgeTarget(edge2) == nodeC);
		assertTrue(graph.edgeSource(edge3) == nodeB && graph.edgeTarget(edge3) == nodeC);
	}

	@Test
	public void testedgeDegree()
	{
		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C		*/

		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);
		int edge3 = graph.addEdge(nodeB, nodeC, new BasicWeightedEdge(3.0), Graph.DIRECTED_EDGE);
		
		assertTrue(graph.degree(nodeA, Graph.INCOMING_EDGE) == 0);
		assertTrue(graph.degree(nodeA, Graph.OUTGOING_EDGE) == 3);
		assertTrue(graph.degree(nodeB, Graph.INCOMING_EDGE) == 2);
		assertTrue(graph.degree(nodeB, Graph.OUTGOING_EDGE) == 1);
		assertTrue(graph.degree(nodeC, Graph.INCOMING_EDGE) == 2);
		assertTrue(graph.degree(nodeC, Graph.OUTGOING_EDGE) == 0);
	}

	@Test
	public void testadjacentNodes()
	{
		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);

		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C		*/
		
		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);
		int edge3 = graph.addEdge(nodeB, nodeC, new BasicWeightedEdge(3.0), Graph.DIRECTED_EDGE);

		Set<Integer> neighborsAIncoming = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.INCOMING_EDGE));
		assertTrue(neighborsAIncoming.size() == 0);
		Set<Integer> neighborsAOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(neighborsAOutgoing.size() == 2);
		assertTrue(neighborsAOutgoing.contains(new Integer(nodeB)));
		assertTrue(neighborsAOutgoing.contains(new Integer(nodeC)));

		Set<Integer> neighborsBIncoming = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.INCOMING_EDGE));
		assertTrue(neighborsBIncoming.size() == 1);
		assertTrue(neighborsBIncoming.contains(new Integer(nodeA)));
		Set<Integer> neighborsBOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(neighborsBOutgoing.size() == 1);
		assertTrue(neighborsBOutgoing.contains(new Integer(nodeC)));

		Set<Integer> neighborsCIncoming = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.INCOMING_EDGE));
		assertTrue(neighborsCIncoming.size() == 2);
		assertTrue(neighborsCIncoming.contains(new Integer(nodeA)));
		assertTrue(neighborsCIncoming.contains(new Integer(nodeB)));
		Set<Integer> neighborsCOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(neighborsCOutgoing.size() == 0);
	}

	@Test
	public void testadjacentEdges()
	{
		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);

		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C
			B --(3.0)--> C		*/
		
		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);
		int edge3 = graph.addEdge(nodeB, nodeC, new BasicWeightedEdge(3.0), Graph.DIRECTED_EDGE);

		List<Integer> connectingEdgesAIncoming = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesAIncoming.size() == 0);
		List<Integer> connectingEdgesAOutgoing = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesAOutgoing.size() == 3);
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge0)));
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge1)));
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge2)));
		
		List<Integer> connectingEdgesBIncoming = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesBIncoming.size() == 2);
		assertTrue(connectingEdgesBIncoming.contains(new Integer(edge0)));
		assertTrue(connectingEdgesBIncoming.contains(new Integer(edge1)));
		List<Integer> connectingEdgesBOutgoing = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesBOutgoing.size() == 1);
		assertTrue(connectingEdgesBOutgoing.contains(new Integer(edge3)));

		List<Integer> connectingEdgesCIncoming = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesCIncoming.size() == 2);
		assertTrue(connectingEdgesCIncoming.contains(new Integer(edge2)));
		assertTrue(connectingEdgesCIncoming.contains(new Integer(edge3)));
		List<Integer> connectingEdgesCOutgoing = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesCOutgoing.size() == 0);
	}

	@Test
	public void testsetNodeEdgeObject()
	{
		int node0 = graph.addNode(new BasicNamedNode("a"));
		int edge0 = graph.addEdge(node0, node0, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		
		assertTrue(graph.nodeObject(node0).name().equals("a"));
		assertFalse(graph.nodeObject(node0).name().equals("a*"));
		assertTrue(graph.edgeObject(edge0).weight() == 1.0);
		assertFalse(graph.edgeObject(edge0).weight() == -1.0);

		graph.setNodeObject(node0, new BasicNamedNode("a*"));
		graph.setEdgeObject(edge0, new BasicWeightedEdge(-1.0));

		assertFalse(graph.nodeObject(node0).name().equals("a"));
		assertTrue(graph.nodeObject(node0).name().equals("a*"));
		assertFalse(graph.edgeObject(edge0).weight() == 1.0);
		assertTrue(graph.edgeObject(edge0).weight() == -1.0);
	}

	@Test
	public void testremoveEdge()
	{
		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);

		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C

			A's incoming neighbors: { }
			A's incoming edges:     { }
			A's outgoing neighbors: { B, C }
			A's outgoing edges:     { 0, 1, 2 }

			B's incoming neighbors: { A }
			B's incoming edges:     { 0, 1 }
			B's outgoing neighbors: { }
			B's outgoing edges:     { }

			C's incoming neighbors: { A }
			C's incoming edges:     { 2 }
			C's outgoing neighbors: { }
			C's outgoing edges:     { }
		*/

		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);

		/*
			Remove edge0:
			
			A's incoming neighbors: { }
			A's incoming edges:     { }
			A's outgoing neighbors: { B, C }
			A's outgoing edges:     { 1, 2 }

			B's incoming neighbors: { A }
			B's incoming edges:     { 1 }
			B's outgoing neighbors: { }
			B's outgoing edges:     { }

			C's incoming neighbors: { A }
			C's incoming edges:     { 2 }
			C's outgoing neighbors: { }
			C's outgoing edges:     { }
		*/

		graph.removeEdge(edge0);
		assertFalse(graph.edgeExists(edge0));

		assertTrue(graph.degree(nodeA, Graph.INCOMING_EDGE) == 0);
		assertTrue(graph.degree(nodeA, Graph.OUTGOING_EDGE) == 2);
		assertTrue(graph.degree(nodeB, Graph.INCOMING_EDGE) == 1);
		assertTrue(graph.degree(nodeB, Graph.OUTGOING_EDGE) == 0);
		assertTrue(graph.degree(nodeC, Graph.INCOMING_EDGE) == 1);
		assertTrue(graph.degree(nodeC, Graph.OUTGOING_EDGE) == 0);

		Set<Integer> neighborsAIncoming = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.INCOMING_EDGE));
		assertTrue(neighborsAIncoming.size() == 0);
		Set<Integer> neighborsAOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(neighborsAOutgoing.size() == 2);
		assertTrue(neighborsAOutgoing.contains(new Integer(nodeB)));
		assertTrue(neighborsAOutgoing.contains(new Integer(nodeC)));
		
		List<Integer> connectingEdgesAIncoming = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesAIncoming.size() == 0);
		List<Integer> connectingEdgesAOutgoing = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesAOutgoing.size() == 2);
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge1)));
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge2)));

		Set<Integer> neighborsBIncoming = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.INCOMING_EDGE));
		assertTrue(neighborsBIncoming.size() == 1);
		assertTrue(neighborsBIncoming.contains(new Integer(nodeA)));
		Set<Integer> neighborsBOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(neighborsBOutgoing.size() == 0);

		List<Integer> connectingEdgesBIncoming = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesBIncoming.size() == 1);
		assertTrue(connectingEdgesBIncoming.contains(new Integer(edge1)));
		List<Integer> connectingEdgesBOutgoing = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesBOutgoing.size() == 0);

		Set<Integer> neighborsCIncoming = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.INCOMING_EDGE));
		assertTrue(neighborsCIncoming.size() == 1);
		assertTrue(neighborsCIncoming.contains(new Integer(nodeA)));
		Set<Integer> neighborsCOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(neighborsCOutgoing.size() == 0);

		List<Integer> connectingEdgesCIncoming = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesCIncoming.size() == 1);
		assertTrue(connectingEdgesCIncoming.contains(new Integer(edge2)));
		List<Integer> connectingEdgesCOutgoing = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesCOutgoing.size() == 0);

		/*	Remove edge2:

			A's incoming neighbors: { }
			A's incoming edges:     { }
			A's outgoing neighbors: { B }
			A's outgoing edges:     { 1 }

			B's incoming neighbors: { A }
			B's incoming edges:     { 1 }
			B's outgoing neighbors: { }
			B's outgoing edges:     { }

			C's incoming neighbors: { }
			C's incoming edges:     { }
			C's outgoing neighbors: { }
			C's outgoing edges:     { }
		*/

		graph.removeEdge(edge2);
		assertFalse(graph.edgeExists(edge2));

		assertTrue(graph.degree(nodeA, Graph.INCOMING_EDGE) == 0);
		assertTrue(graph.degree(nodeA, Graph.OUTGOING_EDGE) == 1);
		assertTrue(graph.degree(nodeB, Graph.INCOMING_EDGE) == 1);
		assertTrue(graph.degree(nodeB, Graph.OUTGOING_EDGE) == 0);
		assertTrue(graph.degree(nodeC, Graph.INCOMING_EDGE) == 0);
		assertTrue(graph.degree(nodeC, Graph.OUTGOING_EDGE) == 0);

		neighborsAIncoming = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.INCOMING_EDGE));
		assertTrue(neighborsAIncoming.size() == 0);
		neighborsAOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(neighborsAOutgoing.size() == 1);
		assertTrue(neighborsAOutgoing.contains(new Integer(nodeB)));
		
		connectingEdgesAIncoming = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesAIncoming.size() == 0);
		connectingEdgesAOutgoing = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesAOutgoing.size() == 1);
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge1)));

		neighborsBIncoming = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.INCOMING_EDGE));
		assertTrue(neighborsBIncoming.size() == 1);
		assertTrue(neighborsBIncoming.contains(new Integer(nodeA)));
		neighborsBOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(neighborsBOutgoing.size() == 0);

		connectingEdgesBIncoming = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesBIncoming.size() == 1);
		assertTrue(connectingEdgesBIncoming.contains(new Integer(edge1)));
		connectingEdgesBOutgoing = captureIteratorToList(graph.adjacentEdges(nodeB, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesBOutgoing.size() == 0);

		neighborsCIncoming = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.INCOMING_EDGE));
		assertTrue(neighborsCIncoming.size() == 0);
		neighborsCOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(neighborsCOutgoing.size() == 0);

		connectingEdgesCIncoming = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesCIncoming.size() == 0);
		connectingEdgesCOutgoing = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesCOutgoing.size() == 0);	
	}

	@Test
	public void testremoveNode()
	{
		// add and remove some nodes so the node indices don't overlap with edge indices
		int nullNode;
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);
		nullNode = graph.addNode(null); graph.removeNode(nullNode);

		/*	A --(0.0)--> B
			A --(1.0)--> B
			A --(2.0)--> C

			A's incoming neighbors: { }
			A's incoming edges:     { }
			A's outgoing neighbors: { B, C }
			A's outgoing edges:     { 0, 1, 2 }

			B's incoming neighbors: { A }
			B's incoming edges:     { 0, 1 }
			B's outgoing neighbors: { }
			B's outgoing edges:     { }

			C's incoming neighbors: { A }
			C's incoming edges:     { 2 }
			C's outgoing neighbors: { }
			C's outgoing edges:     { }
		*/	

		int nodeA = graph.addNode(new BasicNamedNode("a"));
		int nodeB = graph.addNode(new BasicNamedNode("b"));
		int nodeC = graph.addNode(new BasicNamedNode("c"));

		int edge0 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(0.0), Graph.DIRECTED_EDGE);
		int edge1 = graph.addEdge(nodeA, nodeB, new BasicWeightedEdge(1.0), Graph.DIRECTED_EDGE);
		int edge2 = graph.addEdge(nodeA, nodeC, new BasicWeightedEdge(2.0), Graph.DIRECTED_EDGE);

		/*	Remove nodeB:
			A --(2.0)--> C

			A's incoming neighbors: { }
			A's incoming edges:     { }
			A's outgoing neighbors: { C }
			A's outgoing edges:     { 2 }

			C's incoming neighbors: { A }
			C's incoming edges:     { 2 }
			C's outgoing neighbors: { }
			C's outgoing edges:     { }
		*/	

		graph.removeNode(nodeB);
		assertTrue(graph.nodeExists(nodeA));
		assertFalse(graph.nodeExists(nodeB));
		assertTrue(graph.nodeExists(nodeC));
		assertFalse(graph.edgeExists(edge0));
		assertFalse(graph.edgeExists(edge1));
		assertTrue(graph.edgeExists(edge2));

		assertTrue(graph.degree(nodeA, Graph.INCOMING_EDGE) == 0);
		assertTrue(graph.degree(nodeA, Graph.OUTGOING_EDGE) == 1);
		assertTrue(graph.degree(nodeC, Graph.INCOMING_EDGE) == 1);
		assertTrue(graph.degree(nodeC, Graph.OUTGOING_EDGE) == 0);

		Set<Integer> neighborsAIncoming = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.INCOMING_EDGE));
		assertTrue(neighborsAIncoming.size() == 0);
		Set<Integer> neighborsAOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(neighborsAOutgoing.size() == 1);
		assertTrue(neighborsAOutgoing.contains(new Integer(nodeC)));

		List<Integer> connectingEdgesAIncoming = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesAIncoming.size() == 0);
		List<Integer> connectingEdgesAOutgoing = captureIteratorToList(graph.adjacentEdges(nodeA, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesAOutgoing.size() == 1);
		assertTrue(connectingEdgesAOutgoing.contains(new Integer(edge2)));

		Set<Integer> neighborsCIncoming = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.INCOMING_EDGE));
		assertTrue(neighborsCIncoming.size() == 1);
		assertTrue(neighborsCIncoming.contains(new Integer(nodeA)));
		Set<Integer> neighborsCOutgoing = captureIteratorToSet(graph.adjacentNodes(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(neighborsCOutgoing.size() == 0);

		List<Integer> connectingEdgesCIncoming = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.INCOMING_EDGE));
		assertTrue(connectingEdgesCIncoming.size() == 1);
		assertTrue(connectingEdgesCIncoming.contains(new Integer(edge2)));
		List<Integer> connectingEdgesCOutgoing = captureIteratorToList(graph.adjacentEdges(nodeC, Graph.OUTGOING_EDGE));
		assertTrue(connectingEdgesCOutgoing.size() == 0);
	}
}
