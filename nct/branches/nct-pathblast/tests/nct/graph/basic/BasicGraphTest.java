
//============================================================================
// 
//  file: BasicGraphTest.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.graph.basic; 

import junit.framework.*;
import java.util.*;
import java.util.logging.Level;

import nct.networkblast.NetworkBlast;
import nct.graph.*;


// A JUnit test class for Graph.java
public class BasicGraphTest extends TestCase {
    BasicGraph<String,Double> g;
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);
	g = new BasicGraph<String,Double>();
    }

    public void testaddNode() {
	assertTrue(g.addNode("testNode")); // add new node should return true
	assertTrue(!g.addNode(null)); // add null node should return false
    }

    public void testaddEdge() {
	g.addNode("testNode");
	g.addNode("testNode2");  // dummy nodes, we just want them to be in there
	assertTrue(!g.addEdge(null, "testNode", .94)); // null tests
	assertTrue(!g.addEdge(null, null, .94));
	assertTrue(!g.addEdge("testNode2", null, .94));
	assertTrue(g.addEdge("testNode", "testNode2", .94)); // true test
	//assertTrue(!g.addEdge("testNode2", "testNode", .12)); // should return false
	//assertTrue(!g.addEdge("blah", "testNode", .35)); // test nonexistant node
	assertTrue(g.addEdge("testNode", "testNode", .3)); // test self edge
    }

    public void testisNode() {
	g.addNode("testNode");
	assertTrue(!g.isNode("b"));
	assertTrue(!g.isNode(null));
	assertTrue(g.isNode("testNode"));
    }

    public void testgetNodes() {
	g.addNode("b");
	g.addNode("a");
	Set<String> a = g.getNodes();
	assertTrue(g.numberOfNodes() == 2);
	assertTrue(a.size() == 2);
    }

    public void testgetNeighbors() {

    	g.addNode("a");
    	g.addNode("b");
    	g.addNode("c");
    	g.addNode("d");

	g.addEdge("a", "b", .5);
	g.addEdge("b", "c", .2);
	g.addEdge("c", "d", .1);	

	assertTrue(g.getNeighbors("e") == null);  // test nonexistant
	assertTrue(g.getNeighbors(null) == null); // test null
	Set<String> s = g.getNeighbors("a");
	assertTrue(s.size() == 1);
	assertTrue(s.contains("b"));
	s = g.getNeighbors("b");
	assertTrue(s.size() == 2);
	assertTrue(s.contains("c"));
	assertTrue(s.contains("a"));
    }

    public void testdegreeOfNode() {
	
	assertTrue(g.addNode("a"));
	assertTrue(g.addNode("b"));
	assertTrue(g.addNode("c"));
	assertTrue(g.addNode("d"));
	assertTrue(g.addNode("e"));

	assertTrue(g.addEdge("a", "b", .5));
	assertTrue(g.addEdge("b", "c", .5));
	assertTrue(g.addEdge("c", "d", .5));
	assertTrue(g.addEdge("d", "e", .5));

	assertTrue("expected degree 1, got: " + g.degreeOfNode("a"), g.degreeOfNode("a") == 1);
	assertTrue("expected degree 2, got: " + g.degreeOfNode("b"), g.degreeOfNode("b") == 2);
	assertTrue("expected degree 1, got: " + g.degreeOfNode("e"), g.degreeOfNode("e") == 1);
	assertTrue("expected degree 2, got: " + g.degreeOfNode("c"), g.degreeOfNode("c") == 2);
    }


    public void testisEdge() {
	assertTrue(g.addNode("1"));
	assertTrue(g.addNode("2"));
	assertTrue(g.addNode("3"));

	assertTrue(g.addEdge("1", "2", .35));

	assertTrue("expect true, got: " + g.isEdge("1", "2"), g.isEdge("1", "2"));
	assertTrue("expect false, got: " + g.isEdge("1", "3"), !g.isEdge("1", "3"));
	assertTrue("expect false, got: " + g.isEdge("1", null), !g.isEdge("1", null));
	assertTrue("expect false, got: " + g.isEdge(null, "2"), !g.isEdge(null, "2"));
    }

    public void testnumberOfNodes() {
	assertTrue(g.numberOfNodes() == 0);
	assertTrue(g.addNode("4"));
	assertTrue(g.numberOfNodes() == 1);
	assertTrue(g.addNode("5"));
	assertTrue(g.numberOfNodes() == 2);
	assertTrue(!g.addEdge("4", "6", .21));
	assertTrue(g.numberOfNodes() == 2);
	assertTrue(g.addNode("6"));
	assertTrue(g.addEdge("4", "6", .21));
	assertTrue(g.numberOfNodes() == 3);
    }
    public void testnumberOfEdges() {
	assertTrue(g.numberOfEdges() == 0);
	assertTrue(g.addNode("4"));
	assertTrue(g.numberOfEdges() == 0);
	assertTrue(g.addNode("5"));
	assertTrue(g.addEdge("4", "5", .21));
	System.out.println("num edge " + g.numberOfEdges());
	assertTrue(g.numberOfEdges() == 1);
    }

    public void testgetEdgeWeight() {

	assertTrue(g.addNode("A"));
	assertTrue(g.addNode("B"));
	assertTrue(g.addNode("C"));
	assertTrue(g.addNode("D"));
	assertTrue(g.addNode("E"));
	assertTrue(g.addNode("F"));

	assertTrue(g.addEdge("C", "A", 0.5));
	assertTrue(g.addEdge("B", "A", 0.5));
	assertTrue(g.addEdge("B", "E", 0.5));
	assertTrue(g.addEdge("D", "E", 0.5));
	assertTrue(g.addEdge("D", "F", 0.5));
	assertTrue(g.addEdge("A", "A", 0.5));

	assertTrue(g.getEdgeWeight(null, "A") == null);  // check nulls
	assertTrue(g.getEdgeWeight("G", null) == null);
	assertTrue(g.getEdgeWeight(null, null) == null);		   
	assertTrue(g.getEdgeWeight("C", "C") == null);  // non-existant self edge
	assertTrue(g.getEdgeWeight("A", "A") == 0.5);  // self edge
	assertTrue(g.getEdgeWeight("C", "A") == .5);  // check 1
	assertTrue(g.getEdgeWeight("A", "C") == .5);  // check bidirectionality
	assertTrue(g.getEdgeWeight("A", "B") == .5);  // check bidirectionality

	// if an edge does not exist between two nodes, we return null.
	assertTrue(g.getEdgeWeight("C", "B") == null);  // check 2
	assertTrue(g.getEdgeWeight("B", "C") == null);  // check bidirectionality
	assertTrue(g.getEdgeWeight("C", "E") == null);  // check 3
	assertTrue(g.getEdgeWeight("E", "C") == null);  // check bidirectionality
	assertTrue(g.getEdgeWeight("C", "G") == null);  // check unconnected (3)
	assertTrue(g.getEdgeWeight("G", "C") == null);  // check bidirectionality
	assertTrue(g.getEdgeWeight("C", "D") == null);  // check far (3)
	assertTrue(g.getEdgeWeight("D", "C") == null);  // check bidirectionality
    }

    public void testgetEdges() {
	assertTrue(g.addNode("A"));
	assertTrue(g.addNode("B"));
	assertTrue(g.addNode("C"));
	assertTrue(g.addNode("D"));
	assertTrue(g.addNode("E"));
	assertTrue(g.addNode("F"));

	assertTrue(g.addEdge("C", "A", 0.9));
	assertTrue(g.addEdge("B", "A", 0.8));
	assertTrue(g.addEdge("B", "E", 0.7));
	assertTrue(g.addEdge("D", "E", 0.6));
	assertTrue(g.addEdge("D", "F", 0.5));

	Edge<String,Double> first = new BasicEdge<String,Double>("D","F",0.5); 
	Edge<String,Double> second = new BasicEdge<String,Double>("D","E",0.6); 
	Edge<String,Double> third = new BasicEdge<String,Double>("B","E",0.7); 
	Edge<String,Double> forth = new BasicEdge<String,Double>("A","B",0.8); 
	Edge<String,Double> fifth = new BasicEdge<String,Double>("A","C",0.9); 

	SortedSet<Edge<String,Double>> edges = new TreeSet<Edge<String,Double>>( g.getEdges() );

	for (Edge e : edges) {
		System.out.print("src node: " + e.getSourceNode());
		System.out.print("  target node: " + e.getTargetNode());
		System.out.println("  weight: " + e.getWeight());
	}

	assertTrue("expected num edges: 5, got: " + g.numberOfEdges(), g.numberOfEdges() == 5);

	Edge<String,Double> e = edges.first();
	assertTrue("expect edge D-F 0.5, got: " + e.toString(), e.equals(first) ); 
	edges.remove(edges.first());

	e = edges.first();
	assertTrue("expect edge D-E 0.6, got: " + e.toString(), e.equals(second) ); 
	edges.remove(edges.first());

	e = edges.first();
	assertTrue("expect edge B-E 0.7, got: " + e.toString(), e.equals(third) ); 
	edges.remove(edges.first());

	e = edges.first();
	assertTrue("expect edge A-B 0.8, got: " + e.toString(), e.equals(forth) ); 
	edges.remove(edges.first());

	e = edges.first();
	assertTrue("expect edge A-C 0.9, got: " + e.toString(), e.equals(fifth) ); 
	edges.remove(edges.first());
    }

    public void testremoveNode() {
    	g.addNode("one");
    	g.addNode("two");
    	g.addNode("three");
	assertTrue("expect 3, got: " + g.numberOfNodes(), g.numberOfNodes() == 3);
	g.addEdge("one","two",1.0);
	g.addEdge("two","three",1.0);
	g.addEdge("three","one",1.0);
	assertTrue("expect 3, got: " + g.numberOfEdges(), g.numberOfEdges() == 3);

	Set<String> nodes = g.getNodes();
	assertTrue("expect 3, got: " + nodes.size(), nodes.size() == 3);

	Set<Edge<String,Double>> edges = g.getEdges();
	assertTrue("expect 3, got: " + edges.size(), edges.size() == 3);

	g.removeNode("one");

	assertTrue("expect 2, got: " + g.numberOfNodes(), g.numberOfNodes() == 2);
	assertTrue("expect 1, got: " + g.numberOfEdges(), g.numberOfEdges() == 1);
	assertTrue("expect one is not a node ", !g.isNode("one") );
	assertTrue("expect two is still a node: ", g.isNode("two") );
	assertTrue("expect three is still a node: ", g.isNode("three") );
	assertTrue("expect edge one-two is not an edge ", !g.isEdge("one","two") );
	assertTrue("expect edge two-one is not an edge ", !g.isEdge("two","one") );
	assertTrue("expect edge one-three is not an edge ", !g.isEdge("one","three") );
	assertTrue("expect edge three-one is not an edge ", !g.isEdge("three","one") );
	assertTrue("expect edge two-three is an edge ", g.isEdge("two","three") );
	assertTrue("expect edge three-two is an edge ", g.isEdge("three","two") );
	assertTrue("expect edge weight null" , g.getEdgeWeight("one","two") == null );
	assertTrue("expect edge weight null" , g.getEdgeWeight("one","three") == null );
	assertTrue("expect edge weight not null" , g.getEdgeWeight("two","three") != null );

	Set<String> nodes2 = g.getNodes();
	assertTrue("expect 2, got: " + nodes2.size(), nodes2.size() == 2);

	Set<Edge<String,Double>> edges2 = g.getEdges();
	assertTrue("expect 1, got: " + edges2.size(), edges2.size() == 1);
    }

    public void testremoveEdge() {
    	g.addNode("one");
    	g.addNode("two");
    	g.addNode("three");
	assertTrue("expect 3, got: " + g.numberOfNodes(), g.numberOfNodes() == 3);
	g.addEdge("one","two",1.0);
	g.addEdge("two","three",1.0);
	g.addEdge("three","one",1.0);
	assertTrue("expect 3, got: " + g.numberOfEdges(), g.numberOfEdges() == 3);

	Set<String> nodes = g.getNodes();
	assertTrue("expect 3, got: " + nodes.size(), nodes.size() == 3);

	Set<Edge<String,Double>> edges = g.getEdges();
	assertTrue("expect 3, got: " + edges.size(), edges.size() == 3);

	g.removeEdge("one","two");

	assertTrue("expect 3, got: " + g.numberOfNodes(), g.numberOfNodes() == 3);
	assertTrue("expect 2, got: " + g.numberOfEdges(), g.numberOfEdges() == 2);
	assertTrue("expect one is still a node ", g.isNode("one") );
	assertTrue("expect two is still a node: ", g.isNode("two") );
	assertTrue("expect three is still a node: ", g.isNode("three") );
	assertTrue("expect edge one-two is not an edge ", !g.isEdge("one","two") );
	assertTrue("expect edge two-one is not an edge ", !g.isEdge("two","one") );
	assertTrue("expect edge one-three is not an edge ", g.isEdge("one","three") );
	assertTrue("expect edge three-one is not an edge ", g.isEdge("three","one") );
	assertTrue("expect edge two-three is an edge ", g.isEdge("two","three") );
	assertTrue("expect edge three-two is an edge ", g.isEdge("three","two") );
	assertTrue("expect edge weight null" , g.getEdgeWeight("one","two") == null );
	assertTrue("expect edge weight null" , g.getEdgeWeight("one","three") != null );
	assertTrue("expect edge weight not null" , g.getEdgeWeight("two","three") != null );

	Set<String> nodes2 = g.getNodes();
	assertTrue("expect 3, got: " + nodes2.size(), nodes2.size() == 3);

	Set<Edge<String,Double>> edges2 = g.getEdges();
	assertTrue("expect 2, got: " + edges2.size(), edges2.size() == 2);
    }

    public void testDescription() {
    	g.addNode("one");
    	g.addNode("two");
    	g.addNode("three");
	assertTrue("expect 3, got: " + g.numberOfNodes(), g.numberOfNodes() == 3);
	g.addEdge("one","two",1.0,"first");
	g.addEdge("two","three",1.0,"second");
	g.addEdge("three","one",1.0);
	assertTrue("expect 3, got: " + g.numberOfEdges(), g.numberOfEdges() == 3);

	assertTrue("expect 'first', got: " + g.getEdgeDescription("one","two"), g.getEdgeDescription("one","two").equals("first"));
	assertTrue("expect 'first', got: " + g.getEdgeDescription("two","one"), g.getEdgeDescription("two","one").equals("first"));
	assertTrue("expect 'second', got: " + g.getEdgeDescription("two","three"), g.getEdgeDescription("two","three").equals("second"));
	assertTrue("expect 'second', got: " + g.getEdgeDescription("three","two"), g.getEdgeDescription("three","two").equals("second"));
	assertTrue("expect '1.0', got: " + g.getEdgeDescription("one","three"), g.getEdgeDescription("one","three").equals("1.0") );
	assertTrue("expect '1.0', got: " + g.getEdgeDescription("three","one"), g.getEdgeDescription("three","one").equals("1.0") );

	g.setEdgeDescription("one","three","homer");

	assertTrue("expect 'homer', got: " + g.getEdgeDescription("one","three"), g.getEdgeDescription("one","three").equals("homer"));
	assertTrue("expect 'homer', got: " + g.getEdgeDescription("three","one"), g.getEdgeDescription("three","one").equals("homer"));

	assertTrue("expect 'null', got: " + g.getEdgeDescription("four","one"), g.getEdgeDescription("four","one") == null );
    }

    public static Test suite() {
	return new TestSuite(BasicGraphTest.class);
    }

}
