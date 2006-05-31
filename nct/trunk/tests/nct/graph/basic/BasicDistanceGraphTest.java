
//============================================================================
// 
//  file: BasicDistanceGraphTest.java
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

// A JUnit test class for BasicDistanceGraph.java
public class BasicDistanceGraphTest extends TestCase {
    BasicDistanceGraph<String,Double> g;
    protected void setUp() {
        NetworkBlast.setUpLogging(Level.WARNING);
	g = new BasicDistanceGraph<String,Double>("homer");
    }

    public void testgetDistance() {
	// creates the following network
	//  A -------- B         G
        //  |         /
        //  |        /
        //  C    D-E-
        //       |
        //       F
	//
   
   	assertTrue(g.addNode("A"));
   	assertTrue(g.addNode("B"));
   	assertTrue(g.addNode("C"));
   	assertTrue(g.addNode("D"));
   	assertTrue(g.addNode("E"));
   	assertTrue(g.addNode("F"));
	assertTrue(g.addNode("G"));

	assertTrue(g.addEdge("C", "A", 0.1));
	assertTrue(g.addEdge("B", "A", 0.2));
	assertTrue(g.addEdge("B", "E", 0.3));
	assertTrue(g.addEdge("D", "E", 0.4));
	assertTrue(g.addEdge("D", "F", 0.5));

	assertEquals((byte)-1, g.getDistance(null, "A"));  // check nulls
	assertEquals((byte)-1, g.getDistance("G", null));
	assertEquals((byte)-1, g.getDistance(null, null));		   
	assertEquals((byte)-1, g.getDistance("X", "A")); // check non existant
	assertEquals((byte)-1, g.getDistance("B", "Y")); 
	assertEquals((byte)-1, g.getDistance("X", "Y")); 
	assertEquals((byte)0, g.getDistance("C", "C"));  // check 0
	assertEquals((byte)1, g.getDistance("C", "A"));  // check 1
	assertEquals((byte)1, g.getDistance("A", "C"));  // check bidirectionality	assertEquals(g.getDistance("C", "B"), 2);  // check 2
	assertEquals((byte)2, g.getDistance("B", "C"));  // check bidirectionality
	assertEquals((byte)3, g.getDistance("C", "E"));  // check 3
	assertEquals((byte)3, g.getDistance("E", "C"));  // check bidirectionality
	assertEquals((byte)3, g.getDistance("C", "G"));  // check unconnected (3)
	assertEquals((byte)3, g.getDistance("G", "C"));  // check bidirectionality
	assertEquals((byte)3, g.getDistance("C", "D"));  // check far (3)
	assertEquals((byte)3, g.getDistance("D", "C"));  // check bidirectionality
	assertEquals((byte)3, g.getDistance("C", "F"));  // check distant
	assertEquals((byte)3, g.getDistance("F", "C"));  // check distant bidir
    }

    public void testCopyConstructor() {
        g.addNode("one");
        g.addNode("two");
        g.addNode("three");
        assertEquals(3, g.numberOfNodes());
        g.addEdge("one","two",1.0,"first");
        g.addEdge("two","three",2.0,"second");
        assertEquals(2, g.numberOfEdges());

        g.setScore(25.0);
        assertEquals(25.0, g.getScore().doubleValue(),0.0001);

	assertEquals((byte)1,g.getDistance("one","two"));
	assertEquals((byte)1,g.getDistance("two","three"));
	assertEquals((byte)2,g.getDistance("one","three"));

        DistanceGraph<String,Double> dupe = new BasicDistanceGraph<String,Double>(g);

        assertEquals(3, dupe.numberOfNodes());
        assertEquals(2, dupe.numberOfEdges());

        // edge desc
        assertEquals("edge desc","first", dupe.getEdgeDescription("one","two"));
        assertEquals("edge desc","first", dupe.getEdgeDescription("two","one"));
        assertEquals("edge desc","second", dupe.getEdgeDescription("two","three"));
        assertEquals("edge desc","second", dupe.getEdgeDescription("three","two"));

        // edge weight
        assertEquals("edge weight",1.0,dupe.getEdgeWeight("one","two"));
        assertEquals("edge weight",1.0,dupe.getEdgeWeight("two","one"));
        assertEquals("edge weight",2.0,dupe.getEdgeWeight("two","three"));
        assertEquals("edge weight",2.0,dupe.getEdgeWeight("three","two"));

        assertEquals(25.0, dupe.getScore().doubleValue(),0.0001);
        assertEquals("homer", dupe.getId());

	assertEquals((byte)1,dupe.getDistance("one","two"));
	assertEquals((byte)1,dupe.getDistance("two","three"));
	assertEquals((byte)2,dupe.getDistance("one","three"));
    }

   
    public static Test suite() {
	return new TestSuite(BasicDistanceGraphTest.class);
    }

}
