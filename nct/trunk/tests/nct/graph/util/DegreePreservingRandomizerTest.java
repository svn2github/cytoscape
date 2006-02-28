
//============================================================================
// 
//  file: DegreePreservingRandomizerTest.java
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



package nct.graph.util; 

import junit.framework.*;
import java.util.*;
import java.util.logging.Level;

import nct.networkblast.NetworkBlast;
import nct.graph.*;
import nct.graph.basic.*;


// A JUnit test class DegreePreservingRandomizer 
public class DegreePreservingRandomizerTest extends TestCase {
    BasicGraph<String,Double> g;
    GraphRandomizer<String,Double> deg; 
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);
	g = new BasicGraph<String,Double>();

	g.addNode("a");
	g.addNode("b");
	g.addNode("c");
	g.addNode("d");
	g.addNode("e");
	g.addNode("f");

	g.addEdge("a","f",1.0);
	g.addEdge("a","b",1.0);
	g.addEdge("b","c",1.0);
	g.addEdge("b","d",1.0);
	g.addEdge("c","f",1.0);
	g.addEdge("c","d",1.0);
	g.addEdge("d","e",1.0);
	g.addEdge("e","f",1.0);

	deg = new DegreePreservingRandomizer<String,Double>(new Random(10),false);
    }

    public void testRandomize() {
	System.out.println(g.toString());

	// initial state of graph
    	assertTrue("expect 6, got: " + g.numberOfNodes(), g.numberOfNodes() == 6);
    	assertTrue("expect 8, got: " + g.numberOfEdges(), g.numberOfEdges() == 8);

	assertTrue("edge a f", g.isEdge("a","f"));
	assertTrue("edge a b", g.isEdge("a","b"));
	assertTrue("edge b c", g.isEdge("b","c"));
	assertTrue("edge b d", g.isEdge("b","d"));
	assertTrue("edge c f", g.isEdge("c","f"));
	assertTrue("edge c d", g.isEdge("c","d"));
	assertTrue("edge d e", g.isEdge("d","e"));
	assertTrue("edge e f", g.isEdge("e","f"));

	assertTrue("expect 2, got: " + g.degreeOfNode("a"), g.degreeOfNode("a") == 2);
	assertTrue("expect 3, got: " + g.degreeOfNode("b"), g.degreeOfNode("b") == 3);
	assertTrue("expect 3, got: " + g.degreeOfNode("c"), g.degreeOfNode("c") == 3);
	assertTrue("expect 3, got: " + g.degreeOfNode("d"), g.degreeOfNode("d") == 3);
	assertTrue("expect 2, got: " + g.degreeOfNode("e"), g.degreeOfNode("e") == 2);
	assertTrue("expect 3, got: " + g.degreeOfNode("f"), g.degreeOfNode("f") == 3);

    	deg.randomize(g);

	System.out.println("after randomizing...");
	System.out.println(g.toString());

	// verify degrees are the same
	assertTrue("expect 2, got: " + g.degreeOfNode("a"), g.degreeOfNode("a") == 2);
	assertTrue("expect 3, got: " + g.degreeOfNode("b"), g.degreeOfNode("b") == 3);
	assertTrue("expect 3, got: " + g.degreeOfNode("c"), g.degreeOfNode("c") == 3);
	assertTrue("expect 3, got: " + g.degreeOfNode("d"), g.degreeOfNode("d") == 3);
	assertTrue("expect 2, got: " + g.degreeOfNode("e"), g.degreeOfNode("e") == 2);
	assertTrue("expect 3, got: " + g.degreeOfNode("f"), g.degreeOfNode("f") == 3);

	// check new edges
	assertTrue("edge a c", g.isEdge("a","c"));
	assertTrue("edge a e", g.isEdge("a","e"));
	assertTrue("edge b d", g.isEdge("b","d"));
	assertTrue("edge b e", g.isEdge("b","e"));
	assertTrue("edge b f", g.isEdge("b","f"));
	assertTrue("edge c d", g.isEdge("c","d"));
	assertTrue("edge c f", g.isEdge("c","f"));
	assertTrue("edge d f", g.isEdge("d","f"));

	// check old edges
	assertTrue("edge a f", !g.isEdge("a","f"));
	assertTrue("edge a b", !g.isEdge("a","b"));
	assertTrue("edge b c", !g.isEdge("b","c"));
	assertTrue("edge d e", !g.isEdge("d","e"));
	assertTrue("edge e f", !g.isEdge("e","f"));
    }

    public static Test suite() {
	return new TestSuite(DegreePreservingRandomizerTest.class);
    }

}
