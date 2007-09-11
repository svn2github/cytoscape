package oiler.alg;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

import oiler.Graph;
import oiler.LinkedListGraph;
import oiler.util.IntIntHashMap;

public class BreadthFirstTest
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(BreadthFirstTest.class);
	}

	@Test
	public void testbreadthFirst()
	{
		Graph<String,Integer> g = new LinkedListGraph<String,Integer>();
		int nodeA = g.addNode("A");
		int nodeB = g.addNode("B");
		int nodeC = g.addNode("C");
		int nodeD = g.addNode("D");
		int nodeE = g.addNode("E");
		int nodeF = g.addNode("F");
		int nodeG = g.addNode("G");
		int nodeH = g.addNode("H");
		int nodeI = g.addNode("I");
		int nodeJ = g.addNode("J");
		int nodeK = g.addNode("K");
		int edge0 = g.addEdge(nodeA, nodeC, 0, Graph.UNDIRECTED_EDGE);
		int edge1 = g.addEdge(nodeA, nodeB, 1, Graph.DIRECTED_EDGE);
		int edge2 = g.addEdge(nodeA, nodeE, 2, Graph.DIRECTED_EDGE);
		int edge3 = g.addEdge(nodeE, nodeG, 3, Graph.UNDIRECTED_EDGE);
		int edge4 = g.addEdge(nodeF, nodeC, 4, Graph.DIRECTED_EDGE);
		int edge5 = g.addEdge(nodeA, nodeD, 5, Graph.DIRECTED_EDGE);
		int edge6 = g.addEdge(nodeD, nodeH, 6, Graph.DIRECTED_EDGE);
		int edge7 = g.addEdge(nodeH, nodeJ, 7, Graph.DIRECTED_EDGE);
		int edge8 = g.addEdge(nodeD, nodeI, 8, Graph.DIRECTED_EDGE);
		int edge9 = g.addEdge(nodeI, nodeK, 9, Graph.DIRECTED_EDGE);

		final IntIntHashMap map = new IntIntHashMap();

		BreadthFirst<String,Integer> breadthFirst = new BreadthFirst<String,Integer>()
		{
			public boolean encounteredNode(Graph<String,Integer> graph, int node, int fromNode, int depth)
			{
				map.put(node, depth);
				return true;
			}
		};

		map.clear();
		breadthFirst.search(g, nodeA, Integer.MAX_VALUE, Graph.ANY_EDGE);
		assertTrue(map.get(nodeA) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeB) == 1);
		assertTrue(map.get(nodeC) == 1);
		assertTrue(map.get(nodeD) == 1);
		assertTrue(map.get(nodeE) == 1);
		assertTrue(map.get(nodeG) == 2);
		assertTrue(map.get(nodeF) == 2);
		assertTrue(map.get(nodeH) == 2);
		assertTrue(map.get(nodeI) == 2);
		assertTrue(map.get(nodeJ) == 3);
		assertTrue(map.get(nodeK) == 3);

		map.clear();
		breadthFirst.search(g, nodeA, 1, Graph.ANY_EDGE);
		assertTrue(map.get(nodeA) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeB) == 1);
		assertTrue(map.get(nodeC) == 1);
		assertTrue(map.get(nodeD) == 1);
		assertTrue(map.get(nodeE) == 1);
		assertTrue(map.get(nodeG) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeF) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeH) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeI) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeJ) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeK) == IntIntHashMap.KEY_NOT_FOUND);

		map.clear();
		breadthFirst.search(g, nodeA, 1, Graph.DIRECTED_EDGE);
		assertTrue(map.get(nodeA) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeB) == 1);
		assertTrue(map.get(nodeC) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeD) == 1);
		assertTrue(map.get(nodeE) == 1);
		assertTrue(map.get(nodeG) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeF) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeH) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeI) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeJ) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeK) == IntIntHashMap.KEY_NOT_FOUND);

		map.clear();
		breadthFirst.search(g, nodeA, 2);
		assertTrue(map.get(nodeA) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeB) == 1);
		assertTrue(map.get(nodeC) == 1);
		assertTrue(map.get(nodeD) == 1);
		assertTrue(map.get(nodeE) == 1);
		assertTrue(map.get(nodeG) == 2);
		assertTrue(map.get(nodeF) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeH) == 2);
		assertTrue(map.get(nodeI) == 2);
		assertTrue(map.get(nodeJ) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeK) == IntIntHashMap.KEY_NOT_FOUND);

		map.clear();
		breadthFirst.search(g, nodeA, 2, Graph.ANY_EDGE);
		assertTrue(map.get(nodeA) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeB) == 1);
		assertTrue(map.get(nodeC) == 1);
		assertTrue(map.get(nodeD) == 1);
		assertTrue(map.get(nodeE) == 1);
		assertTrue(map.get(nodeG) == 2);
		assertTrue(map.get(nodeF) == 2);
		assertTrue(map.get(nodeH) == 2);
		assertTrue(map.get(nodeI) == 2);
		assertTrue(map.get(nodeJ) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(map.get(nodeK) == IntIntHashMap.KEY_NOT_FOUND);
	}
}
