
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.model.subnetwork;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyListener;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTempNode;

import java.lang.RuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * DOCUMENT ME!
  */
public abstract class AbstractCyMetaNodeTest extends TestCase {
	protected CyRootNetwork root;

    /**
     *  DOCUMENT ME!
     */
    public void testCreateMetaNode() {
		System.out.println("testCreateMetaNode begin");
        CyTempNode tn1 = root.createNode();
        CyTempNode tn2 = root.createNode();
        CyTempNode tn3 = root.createNode();
		List<CyNode> ln = root.addNodes(tn1,tn2,tn3);
        CyNode n1 = ln.get(0);
        CyNode n2 = ln.get(1);
        CyNode n3 = ln.get(2);

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n3,n2,true);
		CyEdge e3 = root.addEdge(n1,n3,false);

        List<CyNode> nl = new ArrayList<CyNode>(2);
        nl.add(n1);
        nl.add(n2);

		assertEquals(3,root.getEdgeCount());

		System.out.println("testCreateMetaNode add subnetwork");

        CySubNetwork s1 = root.addSubNetwork(nl);

		assertEquals(3,root.getEdgeCount());

		System.out.println("testCreateMetaNode add metanode");
        CyMetaNode m1 = root.addMetaNode(s1);

        assertNotNull("metanode is not null",m1);
	
		CySubNetwork sub = m1.getSubNetwork();	
		System.out.println("testCreateMetaNode get subnetwork: " + sub.toString());
		assertNotNull("subnetwork is not null",sub);
		assertTrue("sub == s1",sub.equals(s1));
		assertEquals("num nodes",2,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		List<CyNode> mnls = sub.getNodeList();
		assertEquals("node list size",2,sub.getNodeList().size());
		assertEquals("edge list size",1,sub.getEdgeList().size());

		// this accounts for the extra edge added from the 
		// metanode to n3
		assertEquals(4,root.getEdgeCount());
		System.out.println("testCreateMetaNode end");
    }

    public void testMetaNodeEdgeAdding() {
		System.out.println("testMetaNodeEdgeAdding begin");
		CyTempNode tn1 = root.createNode();
		CyTempNode tn2 = root.createNode();
		CyTempNode tn3 = root.createNode();
		CyTempNode tn4 = root.createNode();
		CyTempNode tn5 = root.createNode();
		CyTempNode tn6 = root.createNode();
		CyTempNode tn7 = root.createNode();
		CyTempNode tn8 = root.createNode();

		List<CyNode> ln = root.addNodes(tn1,tn2,tn3,tn4,tn5,tn6,tn7,tn8);

		// just in base
        CyNode n1 = ln.get(0);
        CyNode n2 = ln.get(1);
        CyNode n3 = ln.get(2);

		// in metanode 2
        CyNode n4 = ln.get(3);
        CyNode n5 = ln.get(4);
        CyNode n6 = ln.get(5);

		// in metanode 1
        CyNode n7 = ln.get(6);
        CyNode n8 = ln.get(7);

		// between base and m1
		CyEdge e1 = root.addEdge(n1,n7,true);

		// between base and m2
		CyEdge e2 = root.addEdge(n2,n4,true);
		CyEdge e3 = root.addEdge(n2,n5,true);

		// within m1
		CyEdge e4 = root.addEdge(n7,n8,false);

		// within m2
		CyEdge e5 = root.addEdge(n4,n5,false);
		CyEdge e6 = root.addEdge(n5,n6,false);

		// just in base
		CyEdge e7 = root.addEdge(n2,n3,false);

		assertEquals( "inital node count",8,root.getNodeCount());  
		assertEquals( "inital edge count",7,root.getEdgeCount());  

		// create metanode 1
		List<CyNode> m1sl = new ArrayList<CyNode>(2);
		m1sl.add(n7);
		m1sl.add(n8);

		CySubNetwork m1s = root.addSubNetwork(m1sl);
		CyMetaNode m1 = root.addMetaNode(m1s);

		assertEquals( "root after m1 node count",9,root.getNodeCount());  
		assertEquals( "base after m1 node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "root after m1 edge count",8,root.getEdgeCount());  
		assertEquals( "base after m1 edge count",7,root.getBaseNetwork().getEdgeCount());  

		// create metanode 2
		List<CyNode> m2sl = new ArrayList<CyNode>(2);
		m2sl.add(n4);
		m2sl.add(n5);
		m2sl.add(n6);

		CySubNetwork m2s = root.addSubNetwork(m2sl);
		CyMetaNode m2 = root.addMetaNode(m2s);

		assertEquals( "root after m2 node count",10,root.getNodeCount());  
		assertEquals( "base after m2 node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "root after m2 edge count",9,root.getEdgeCount());  
		assertEquals( "base after m2 edge count",7,root.getBaseNetwork().getEdgeCount());  

		// add edge between n4 n6 (within metanode)

		// add edge between n3 n1 (just in base)

		// add edge between n6 n2 (between meta and base, but with existing meta edge)

		// add edge between n6 n3 (between meta and base, but new meta edge)

		// add edge between n6 n8 (between two meta nodes)

		// add edge between m1 n3 (between meta node in root and base)

		// add edge between m1 m2 (just in root)

		// what about metanodes within metanodes?
		System.out.println("testMetaNodeEdgeAdding end");
	}
}
