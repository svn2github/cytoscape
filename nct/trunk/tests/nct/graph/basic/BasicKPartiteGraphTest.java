
//============================================================================
// 
//  file: BasicKPartiteGraphTest.java
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


// A JUnit test class for KPartiteGraph.java
public class BasicKPartiteGraphTest extends TestCase {
    KPartiteGraph<String,Double,Integer> g;
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);
	g = new BasicKPartiteGraph<String,Double,Integer>();
	g.addNode("A",1);
	g.addNode("B",1);
	g.addNode("C",1);
	g.addNode("d",2);
	g.addNode("e",2);
	g.addNode("f",2);
    }

    public void testaddNode() {
	assertFalse("expect false because no partition is specified",
	           g.addNode("X")); 
	assertFalse("expect false because no partition is specified",
	           g.addNode(null)); 

	assertTrue("expect true because partition specified", 
	           g.addNode("X",1));
	assertTrue("expect true because partition specified", 
	           g.addNode("Y",2));

	assertFalse("expect false because node already specified", 
	           g.addNode("A",1));
	assertFalse("expect false because node already specified", 
	           g.addNode("e",2));

	assertTrue("expect true because partition specified", 
	           g.addNode("Z",3));

	assertFalse("expect false because partition is null", 
	           g.addNode("N",null));
	assertFalse("expect false because node is null", 
	           g.addNode(null,1));
	assertFalse("expect false because node and partition are null", 
	           g.addNode(null,null));
    }

    public void testaddEdge() {
    	Double dub = new Double(0.5);
    	assertTrue("expect to add edge A-d (diff partitions)", g.addEdge("A","d",dub)); 
    	assertFalse("expect to NOT add edge A-B (same partitions)", g.addEdge("A","B",dub)); 
    	assertFalse("expect to NOT add edge d-e (same partitions)", g.addEdge("d","e",dub)); 
    	assertFalse("expect to NOT add edge d-null (null)", g.addEdge("d",null,dub)); 
    	assertFalse("expect to NOT add edge null-null (null)", g.addEdge(null,null,dub)); 
    	assertFalse("expect to NOT add edge null-d (null)", g.addEdge(null,"d",dub)); 
    	assertFalse("expect to NOT add edge d-(non-existant)k ", g.addEdge("k","d",dub)); 
	System.out.println("hellow world");
    }
    public void testParitions() {
	assertEquals("expect 2 partitions, got: " + g.getNumPartitions(), 2, g.getNumPartitions());
	assertEquals("expect 2 partitions, got: " + g.getPartitions().size(), 
	           2, g.getPartitions().size() );
	assertTrue("expect 1 is a partition", g.isPartition(1));
	assertTrue("expect 2 is a partition", g.isPartition(2));
	assertFalse("expect 3 is not a partition", g.isPartition(3));
	assertTrue("add node with partition 3", g.addNode("1",3));
	assertTrue("expect 3 is now a partition", g.isPartition(3));
    }

    public void testKPartitions() {
    	assertEquals(2, g.getNumPartitions());
	assertTrue("add node with partition 3", g.addNode("1",3));
    	assertEquals(3, g.getNumPartitions());
	assertTrue("add node with partition 4", g.addNode("2",4));
    	assertEquals(4, g.getNumPartitions());

	BasicKPartiteGraph<String,Double,Integer> h = new BasicKPartiteGraph<String,Double,Integer>("test",2);
	
	assertEquals(2, h.getK());
	assertTrue("add node with partition 1", h.addNode("A",1));
	assertTrue("add node with partition 2", h.addNode("B",2));
	assertEquals(2, h.getK());
	assertFalse("add node with partition 3", h.addNode("C",3));
	assertFalse("add node with partition null", h.addNode("C",null));
	assertEquals(2, h.getK());

    }

    public static Test suite() {
	return new TestSuite(BasicKPartiteGraphTest.class);
    }

}
