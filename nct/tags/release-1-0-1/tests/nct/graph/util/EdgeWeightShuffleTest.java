
//============================================================================
// 
//  file: EdgeWeightShuffleTest.java
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


// A JUnit test class for EdgeWeightShuffle
public class EdgeWeightShuffleTest extends TestCase {
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
	g.addEdge("a","b",2.0);
	g.addEdge("b","c",3.0);
	g.addEdge("b","d",4.0);
	g.addEdge("c","f",5.0);
	g.addEdge("c","d",6.0);
	g.addEdge("d","e",7.0);
	g.addEdge("e","f",8.0);

	deg = new EdgeWeightShuffle<String,Double>(new Random(10));
    }

    public void testRandomize() {
	System.out.println("before shuffle");
	System.out.println(g.toString());

	assertTrue("edge a f expect 1.0: got: " + g.getEdgeWeight("a","f"), g.getEdgeWeight("a","f") == 1.0 );
	assertTrue("edge a b expect 2.0: got: " + g.getEdgeWeight("a","b"), g.getEdgeWeight("a","b") == 2.0 );
	assertTrue("edge b c expect 3.0: got: " + g.getEdgeWeight("b","c"), g.getEdgeWeight("b","c") == 3.0 );
	assertTrue("edge b d expect 4.0: got: " + g.getEdgeWeight("b","d"), g.getEdgeWeight("b","d") == 4.0 );
	assertTrue("edge c f expect 5.0: got: " + g.getEdgeWeight("c","f"), g.getEdgeWeight("c","f") == 5.0 );
	assertTrue("edge c d expect 6.0: got: " + g.getEdgeWeight("c","d"), g.getEdgeWeight("c","d") == 6.0 );
	assertTrue("edge d e expect 7.0: got: " + g.getEdgeWeight("d","e"), g.getEdgeWeight("d","e") == 7.0 );
	assertTrue("edge e f expect 8.0: got: " + g.getEdgeWeight("e","f"), g.getEdgeWeight("e","f") == 8.0 );

    	deg.randomize(g);

	System.out.println("after shuffle");
	System.out.println(g.toString());

	assertTrue("edge a f expect 7.0: got: " + g.getEdgeWeight("a","f"), g.getEdgeWeight("a","f") == 7.0 );
	assertTrue("edge a b expect 6.0: got: " + g.getEdgeWeight("a","b"), g.getEdgeWeight("a","b") == 6.0 );
	assertTrue("edge b c expect 5.0: got: " + g.getEdgeWeight("b","c"), g.getEdgeWeight("b","c") == 5.0 );
	assertTrue("edge b d expect 3.0: got: " + g.getEdgeWeight("b","d"), g.getEdgeWeight("b","d") == 3.0 );
	assertTrue("edge c f expect 4.0: got: " + g.getEdgeWeight("c","f"), g.getEdgeWeight("c","f") == 4.0 );
	assertTrue("edge c d expect 2.0: got: " + g.getEdgeWeight("c","d"), g.getEdgeWeight("c","d") == 2.0 );
	assertTrue("edge d e expect 8.0: got: " + g.getEdgeWeight("d","e"), g.getEdgeWeight("d","e") == 8.0 );
	assertTrue("edge e f expect 1.0: got: " + g.getEdgeWeight("e","f"), g.getEdgeWeight("e","f") == 1.0 );
    }

    public static Test suite() {
	return new TestSuite(EdgeWeightShuffleTest.class);
    }

}
