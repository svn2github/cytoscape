
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
	assertTrue("expect false because no partition is specified",
	           !g.addNode("X")); 
	assertTrue("expect false because no partition is specified",
	           !g.addNode(null)); 

	assertTrue("expect true because partition specified", 
	           g.addNode("X",1));
	assertTrue("expect true because partition specified", 
	           g.addNode("Y",2));

	assertTrue("expect false because node already specified", 
	           !g.addNode("A",1));
	assertTrue("expect false because node already specified", 
	           !g.addNode("e",2));

	assertTrue("expect true because partition specified", 
	           g.addNode("Z",3));

	assertTrue("expect false because partition is null", 
	           !g.addNode("N",null));
	assertTrue("expect false because node is null", 
	           !g.addNode(null,1));
	assertTrue("expect false because node and partition are null", 
	           !g.addNode(null,null));
    }

    public void testaddEdge() {
    	Double dub = new Double(0.5);
    	assertTrue("expect to add edge A-d (diff partitions)", g.addEdge("A","d",dub)); 
    	assertTrue("expect to NOT add edge A-B (same partitions)", !g.addEdge("A","B",dub)); 
    	assertTrue("expect to NOT add edge d-e (same partitions)", !g.addEdge("d","e",dub)); 
    	assertTrue("expect to NOT add edge d-null (null)", !g.addEdge("d",null,dub)); 
    	assertTrue("expect to NOT add edge null-null (null)", !g.addEdge(null,null,dub)); 
    	assertTrue("expect to NOT add edge null-d (null)", !g.addEdge(null,"d",dub)); 
    	assertTrue("expect to NOT add edge d-(non-existant)k ", !g.addEdge("k","d",dub)); 
	System.out.println("hellow world");
    }
    public void testParitions() {
	assertTrue("expect 2 partitions, got: " + g.getNumPartitions(), g.getNumPartitions() == 2);
	assertTrue("expect 2 partitions, got: " + g.getPartitions().size(), 
	           g.getPartitions().size() == 2 );
	assertTrue("expect 1 is a partion", g.isPartition(1));
	assertTrue("expect 2 is a partion", g.isPartition(2));
	assertTrue("expect 3 is a partion", !g.isPartition(3));
	assertTrue("add node with partition 3", g.addNode("1",3));
	assertTrue("expect 3 is now a partion", g.isPartition(3));
    }

    public void testKPartitions() {
    	assertTrue("expect 2, got: " + g.getNumPartitions(), g.getNumPartitions() == 2 );
	assertTrue("add node with partition 3", g.addNode("1",3));
    	assertTrue("expect 3, got: " + g.getNumPartitions(), g.getNumPartitions() == 3 );
	assertTrue("add node with partition 4", g.addNode("2",4));
    	assertTrue("expect 4, got: " + g.getNumPartitions(), g.getNumPartitions() == 4 );

	BasicKPartiteGraph<String,Double,Integer> h = new BasicKPartiteGraph<String,Double,Integer>("test",2);
	
	assertTrue("expect 2, got: " + h.getK() , h.getK() == 2);
	assertTrue("add node with partition 1", h.addNode("A",1));
	assertTrue("add node with partition 2", h.addNode("B",2));
	assertTrue("expect 2, got: " + h.getK() , h.getK() == 2);
	assertTrue("add node with partition 3", !h.addNode("C",3));
	assertTrue("add node with partition null", !h.addNode("C",null));
	assertTrue("expect 2, got: " + h.getK() , h.getK() == 2);

    }

    public static Test suite() {
	return new TestSuite(BasicKPartiteGraphTest.class);
    }

}
