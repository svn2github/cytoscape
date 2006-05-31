
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
	g = new BasicGraph<String,Double>("homer");
    }

    public void testaddNode() {
	assertTrue(g.addNode("testNode")); // add new node should return true
	assertFalse(g.addNode(null)); // add null node should return false
    }

    public void testaddEdge() {
	g.addNode("testNode");
	g.addNode("testNode2");  // dummy nodes, we just want them to be in there
	assertFalse(g.addEdge(null, "testNode", .94)); // null tests
	assertFalse(g.addEdge(null, null, .94));
	assertFalse(g.addEdge("testNode2", null, .94));
	assertTrue(g.addEdge("testNode", "testNode2", .94)); // true test
	//assertTrue(!g.addEdge("testNode2", "testNode", .12)); // should return false
	//assertTrue(!g.addEdge("blah", "testNode", .35)); // test nonexistant node
	assertTrue(g.addEdge("testNode", "testNode", .3)); // test self edge
    }

    public void testisNode() {
	g.addNode("testNode");
	assertFalse(g.isNode("b"));
	assertFalse(g.isNode(null));
	assertTrue(g.isNode("testNode"));
    }

    public void testgetNodes() {
	g.addNode("b");
	g.addNode("a");
	Set<String> a = g.getNodes();
	assertEquals(2, g.numberOfNodes());
	assertEquals(2, a.size());
    }

    public void testgetNeighbors() {

    	g.addNode("a");
    	g.addNode("b");
    	g.addNode("c");
    	g.addNode("d");

	g.addEdge("a", "b", .5);
	g.addEdge("b", "c", .2);
	g.addEdge("c", "d", .1);	

	assertNull(g.getNeighbors("e"));  // test nonexistant
	assertNull(g.getNeighbors(null)); // test null
	Set<String> s = g.getNeighbors("a");
	assertEquals(1, s.size());
	assertTrue(s.contains("b"));
	s = g.getNeighbors("b");
	assertEquals(2, s.size());
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

	assertEquals("expected degree 1, got: " + g.degreeOfNode("a"), 1, g.degreeOfNode("a"));
	assertEquals("expected degree 2, got: " + g.degreeOfNode("b"), 2, g.degreeOfNode("b"));
	assertEquals("expected degree 1, got: " + g.degreeOfNode("e"), 1, g.degreeOfNode("e"));
	assertEquals("expected degree 2, got: " + g.degreeOfNode("c"), 2, g.degreeOfNode("c"));
    }


    public void testisEdge() {
	assertTrue(g.addNode("1"));
	assertTrue(g.addNode("2"));
	assertTrue(g.addNode("3"));

	assertTrue(g.addEdge("1", "2", .35));

	assertTrue("expected true, got: " + g.isEdge("1", "2"), g.isEdge("1", "2"));
	assertTrue("expected false, got: " + g.isEdge("1", "3"), !g.isEdge("1", "3"));
	assertTrue("expected false, got: " + g.isEdge("1", null), !g.isEdge("1", null));
	assertTrue("expected false, got: " + g.isEdge(null, "2"), !g.isEdge(null, "2"));
    }

    public void testnumberOfNodes() {
	assertEquals(0, g.numberOfNodes());
	assertTrue(g.addNode("4"));
	assertEquals(1, g.numberOfNodes());
	assertTrue(g.addNode("5"));
	assertEquals(2, g.numberOfNodes());
	assertFalse(g.addEdge("4", "6", .21));
	assertEquals(2, g.numberOfNodes());
	assertTrue(g.addNode("6"));
	assertTrue(g.addEdge("4", "6", .21));
	assertEquals(3, g.numberOfNodes());
    }
    public void testnumberOfEdges() {
	assertEquals(0, g.numberOfEdges());
	assertTrue(g.addNode("4"));
	assertEquals(0, g.numberOfEdges());
	assertTrue(g.addNode("5"));
	assertTrue(g.addEdge("4", "5", .21));
	System.out.println("num edge " + g.numberOfEdges());
	assertEquals(1, g.numberOfEdges());
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

	assertNull(g.getEdgeWeight(null, "A"));  // check nulls
	assertNull(g.getEdgeWeight("G", null));
	assertNull(g.getEdgeWeight(null, null));		   
	assertNull(g.getEdgeWeight("C", "C"));  // non-existant self edge
	assertEquals(0.5, g.getEdgeWeight("A", "A"));  // self edge
	assertEquals(0.5, g.getEdgeWeight("C", "A"));  // check 1
	assertEquals(0.5, g.getEdgeWeight("A", "C"));  // check bidirectionality
	assertEquals(0.5, g.getEdgeWeight("A", "B"));  // check bidirectionality

	// if an edge does not exist between two nodes, we return null.
	assertNull(g.getEdgeWeight("C", "B"));  // check 2
	assertNull(g.getEdgeWeight("B", "C"));  // check bidirectionality
	assertNull(g.getEdgeWeight("C", "E"));  // check 3
	assertNull(g.getEdgeWeight("E", "C"));  // check bidirectionality
	assertNull(g.getEdgeWeight("C", "G"));  // check unconnected (3)
	assertNull(g.getEdgeWeight("G", "C"));  // check bidirectionality
	assertNull(g.getEdgeWeight("C", "D"));  // check far (3)
	assertNull(g.getEdgeWeight("D", "C"));  // check bidirectionality
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

	assertEquals("expected num edges: 5, got: " + g.numberOfEdges(), 5, g.numberOfEdges());

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
	assertEquals("expect 3, got: " + g.numberOfNodes(), 3, g.numberOfNodes());
	g.addEdge("one","two",1.0);
	g.addEdge("two","three",1.0);
	g.addEdge("three","one",1.0);
	assertEquals("expect 3, got: " + g.numberOfEdges(), 3, g.numberOfEdges());

	Set<String> nodes = g.getNodes();
	assertEquals("expect 3, got: " + nodes.size(), 3, nodes.size());

	Set<Edge<String,Double>> edges = g.getEdges();
	assertEquals("expect 3, got: " + edges.size(), 3, edges.size());

	g.removeNode("one");

	assertEquals(2, g.numberOfNodes());
	assertEquals(1, g.numberOfEdges());
	assertFalse("expect one is not a node ", g.isNode("one") );
	assertTrue("expect two is still a node: ", g.isNode("two") );
	assertTrue("expect three is still a node: ", g.isNode("three") );
	assertFalse("expect edge one-two is not an edge ", g.isEdge("one","two") );
	assertFalse("expect edge two-one is not an edge ", g.isEdge("two","one") );
	assertFalse("expect edge one-three is not an edge ", g.isEdge("one","three") );
	assertFalse("expect edge three-one is not an edge ", g.isEdge("three","one") );
	assertTrue("expect edge two-three is an edge ", g.isEdge("two","three") );
	assertTrue("expect edge three-two is an edge ", g.isEdge("three","two") );
	assertNull("expect edge weight null" , g.getEdgeWeight("one","two"));
	assertNull("expect edge weight null" , g.getEdgeWeight("one","three"));
	assertNotNull("expect edge weight not null" , g.getEdgeWeight("two","three") );

	Set<String> nodes2 = g.getNodes();
	assertEquals(2, nodes2.size());

	Set<Edge<String,Double>> edges2 = g.getEdges();
	assertEquals(1, edges2.size());
    }

    public void testremoveEdge() {
    	g.addNode("one");
    	g.addNode("two");
    	g.addNode("three");
	assertEquals(3, g.numberOfNodes());
	g.addEdge("one","two",1.0);
	g.addEdge("two","three",1.0);
	g.addEdge("three","one",1.0);
	assertEquals(3, g.numberOfEdges());

	Set<String> nodes = g.getNodes();
	assertEquals(3, nodes.size());

	Set<Edge<String,Double>> edges = g.getEdges();
	assertEquals(3, edges.size());

	g.removeEdge("one","two");

	assertEquals(3, g.numberOfNodes());
	assertEquals(2, g.numberOfEdges());
	assertTrue("expect one is still a node ", g.isNode("one") );
	assertTrue("expect two is still a node: ", g.isNode("two") );
	assertTrue("expect three is still a node: ", g.isNode("three") );
	assertFalse("expect edge one-two is not an edge ", g.isEdge("one","two") );
	assertFalse("expect edge two-one is not an edge ", g.isEdge("two","one") );
	assertTrue("expect edge one-three is still an edge ", g.isEdge("one","three") );
	assertTrue("expect edge three-one is still an edge ", g.isEdge("three","one") );
	assertTrue("expect edge two-three is an edge ", g.isEdge("two","three") );
	assertTrue("expect edge three-two is an edge ", g.isEdge("three","two") );
	assertNull("expect edge weight null" , g.getEdgeWeight("one","two") );
	assertNotNull("expect edge weight null" , g.getEdgeWeight("one","three") );
	assertNotNull("expect edge weight not null" , g.getEdgeWeight("two","three") );

	Set<String> nodes2 = g.getNodes();
	assertEquals(3, nodes2.size());

	Set<Edge<String,Double>> edges2 = g.getEdges();
	assertEquals(2, edges2.size());
    }

    public void testDescription() {
    	g.addNode("one");
    	g.addNode("two");
    	g.addNode("three");
	assertEquals(3, g.numberOfNodes());
	g.addEdge("one","two",1.0,"first");
	g.addEdge("two","three",1.0,"second");
	g.addEdge("three","one",1.0);
	assertEquals(3, g.numberOfEdges());

	assertEquals("edge desc","first", g.getEdgeDescription("one","two"));
	assertEquals("edge desc","first", g.getEdgeDescription("two","one"));
	assertEquals("edge desc","second", g.getEdgeDescription("two","three"));
	assertEquals("edge desc","second", g.getEdgeDescription("three","two"));
	assertEquals("edge desc","1.0", g.getEdgeDescription("one","three"));
	assertEquals("edge desc","1.0", g.getEdgeDescription("three","one"));

	g.setEdgeDescription("one","three","homer");

	assertEquals("edge desc for new desc", "homer", g.getEdgeDescription("one","three"));
	assertEquals("edge desc for new desc", "homer", g.getEdgeDescription("three","one"));

	assertNull("edge description for non-existant edge", g.getEdgeDescription("four","one") );
    }

    public void testCopyConstructor() {
    	g.addNode("one");
    	g.addNode("two");
    	g.addNode("three");
	assertEquals(3, g.numberOfNodes());
	g.addEdge("one","two",1.0,"first");
	g.addEdge("two","three",2.0,"second");
	g.addEdge("three","one",4.0);
	assertEquals(3, g.numberOfEdges());

	g.setScore(25.0);
	assertEquals(25.0, g.getScore().doubleValue(),0.0001);

	Graph<String,Double> dupe = new BasicGraph<String,Double>(g);

	assertEquals(3, dupe.numberOfNodes());
	assertEquals(3, dupe.numberOfEdges());

	// edge desc
	assertEquals("edge desc","first", dupe.getEdgeDescription("one","two"));
	assertEquals("edge desc","first", dupe.getEdgeDescription("two","one"));
	assertEquals("edge desc","second", dupe.getEdgeDescription("two","three"));
	assertEquals("edge desc","second", dupe.getEdgeDescription("three","two"));
	assertEquals("edge desc","4.0", dupe.getEdgeDescription("one","three"));
	assertEquals("edge desc","4.0", dupe.getEdgeDescription("three","one"));

	// edge weight
	assertEquals("edge weight",1.0,dupe.getEdgeWeight("one","two"));
	assertEquals("edge weight",1.0,dupe.getEdgeWeight("two","one"));
	assertEquals("edge weight",2.0,dupe.getEdgeWeight("two","three"));
	assertEquals("edge weight",2.0,dupe.getEdgeWeight("three","two"));
	assertEquals("edge weight",4.0,dupe.getEdgeWeight("one","three"));
	assertEquals("edge weight",4.0,dupe.getEdgeWeight("three","one"));

	assertEquals(25.0, dupe.getScore().doubleValue(),0.0001);
	assertEquals("homer", dupe.getId());

	assertEquals(0,g.compareTo(dupe));
    }

    public static Test suite() {
	return new TestSuite(BasicGraphTest.class);
    }

}
