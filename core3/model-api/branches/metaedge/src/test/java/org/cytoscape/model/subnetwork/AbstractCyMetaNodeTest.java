
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

	// just in base
	protected CyNode n1; 
	protected CyNode n2;
	protected CyNode n3;

	// in metanode 2
	protected CyNode n4;
	protected CyNode n5;
	protected CyNode n6;

	// in metanode 1
	protected CyNode n7;
	protected CyNode n8;

	// between base and m1
	protected CyEdge e1;

	// between base and m2
	protected CyEdge e2;
	protected CyEdge e3;

	// within m1
	protected CyEdge e4;

	// within m2
	protected CyEdge e5;
	protected CyEdge e6;

	// just in base
	protected CyEdge e7;

	// metanode 1
	protected List<CyNode> m1sl; 
	protected CySubNetwork m1s;
	protected CyMetaNode m1;

	// metanode 2
	protected List<CyNode> m2sl;
	protected CySubNetwork m2s;
	protected CyMetaNode m2;


	protected void optionalSetup() {

		// just in base
		n1 = root.addNode();
		n2 = root.addNode();
		n3 = root.addNode();

		// in metanode 2
		n4 = root.addNode();
		n5 = root.addNode();
		n6 = root.addNode();

		// in metanode 1
		n7 = root.addNode();
		n8 = root.addNode();

		// between base and m1
		e1 = root.addEdge(n1,n7,true);

		// between base and m2
		e2 = root.addEdge(n2,n4,true);
		e3 = root.addEdge(n2,n5,true);

		// within m1
		e4 = root.addEdge(n7,n8,false);

		// within m2
		e5 = root.addEdge(n4,n5,false);
		e6 = root.addEdge(n5,n6,false);

		// just in base
		e7 = root.addEdge(n2,n3,false);

		assertEquals( "inital node count",8,root.getNodeCount());  
		assertEquals( "inital edge count",7,root.getEdgeCount());  
	}

	protected void createMetaNode1() {
		// create metanode 1
//		List<CyNode> m1sl = new ArrayList<CyNode>(2);
//		m1sl.add(n7);
//		m1sl.add(n8);

//		m1s = root.addSubNetwork(m1sl);
//		m1 = root.addMetaNode(m1s);

		m1 = root.addMetaNode();
		m1s = m1.getSubNetwork();

		m1s.addNode(n7);
		m1s.addNode(n8);

		m1s.addEdge(e4);

		assertEquals( "initial metanode 1 node count",2,m1s.getNodeCount());
		assertEquals( "initial metanode 1 edge count",1,m1s.getEdgeCount());
	}

	protected void createMetaNode2() {
//		List<CyNode> m2sl = new ArrayList<CyNode>(3);
//		m2sl.add(n4);
//		m2sl.add(n5);
//		m2sl.add(n6);

//		m2s = root.addSubNetwork(m2sl);
//		m2 = root.addMetaNode(m2s);

		m2 = root.addMetaNode();
		m2s = m2.getSubNetwork();

		m2s.addNode(n4);
		m2s.addNode(n5);
		m2s.addNode(n6);

		m2s.addEdge(e5);
		m2s.addEdge(e6);

		assertEquals( "initial metanode 2 node count",3,m2s.getNodeCount());
		assertEquals( "initial metanode 2 edge count",2,m2s.getEdgeCount());
	}


    /**
     * tests basic metanode creation 
     */
    public void testCreateMetaNode() {
        n1 = root.addNode();
        n2 = root.addNode();
        n3 = root.addNode();

		e1 = root.addEdge(n1,n2,true);
		e2 = root.addEdge(n3,n2,true);
		e3 = root.addEdge(n1,n3,false);

		assertEquals(3,root.getEdgeCount());

//        List<CyNode> nl = new ArrayList<CyNode>(2);
 //       nl.add(n1);
  //      nl.add(n2);


//        CySubNetwork s1 = root.addSubNetwork(nl);


        CyMetaNode m1 = root.addMetaNode();
        CySubNetwork sub = m1.getSubNetwork(); 
		sub.addNode(n1);
		sub.addNode(n2);
		sub.addEdge(e1);

		assertEquals(3,root.getEdgeCount());
        assertNotNull("metanode is not null",m1);
		assertNotNull("subnetwork is not null",sub);
		assertEquals("num nodes",2,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));
		assertEquals("node list size",2,sub.getNodeList().size());
		assertEquals("edge list size",1,sub.getEdgeList().size());
		assertEquals(3,root.getEdgeCount());
    }

	/**
	 * verify edges are added correctly
	 */
    public void testMetaNodeEdgeAdding() {
		optionalSetup();
		createMetaNode1();

		assertEquals( "root after m1 node count",9,root.getNodeCount());  
		assertEquals( "base after m1 node count",8,root.getBaseNetwork().getNodeCount());  

		assertEquals( "root after m1 edge count",7,root.getEdgeCount());  
		assertEquals( "base after m1 edge count",7,root.getBaseNetwork().getEdgeCount());  

		createMetaNode2();

		assertEquals( "root after m2 node count",10,root.getNodeCount());  
		assertEquals( "base after m2 node count",8,root.getBaseNetwork().getNodeCount());  

		assertEquals( "root after m2 edge count",7,root.getEdgeCount());  
		assertEquals( "base after m2 edge count",7,root.getBaseNetwork().getEdgeCount());  
	}


	/**
	 * add edge between n4 n6 (within metanode)
	 */
    public void testAddEdgeWithinMetaNode() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();

		CyEdge ex1 = m2s.addEdge(n4,n6,false);

		// no change
		assertEquals( "root after m2 edge add node count",10,root.getNodeCount());  
		assertEquals( "base after m2 edge add node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "base after m2 edge add edge count",7,root.getBaseNetwork().getEdgeCount());  

		// increase by one
		assertEquals( "root after m2 edge add edge count",8,root.getEdgeCount());  
		assertEquals( "root after m2 edge add edge count",3,m2s.getEdgeCount());  
	}

	/** 
	 * add edge between n3 n1 (just in base)
	 */
    public void testAddEdgeJustInBase() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();

		CyEdge ex1 = root.getBaseNetwork().addEdge(n3,n1,true);

		// no change
		assertEquals( "root after base edge add node count",10,root.getNodeCount());  
		assertEquals( "base after base edge add node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "m1 after base edge add edge count",1,m1.getSubNetwork().getEdgeCount());  
		assertEquals( "m2 after base edge add edge count",2,m2.getSubNetwork().getEdgeCount());  

		// increase by one
		assertEquals( "root after base edge add edge count",8,root.getEdgeCount());  
		assertEquals( "base after base edge add edge count",8,root.getBaseNetwork().getEdgeCount());  
	}

	/** 
	 * add edge between n6 n2 (between meta and base)
	 */
    public void testEdgeBetweenMetaAndBase() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();

		CyEdge ex1 = root.getBaseNetwork().addEdge(n6,n2,true);

		// no change
		assertEquals( "root after base-meta edge add node count",10,root.getNodeCount());  
		assertEquals( "base after base-meta edge add node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "m2 after base-meta edge add edge count",2,m2.getSubNetwork().getEdgeCount());  

		// increase by one
		assertEquals( "root after base-meta edge add edge count",8,root.getEdgeCount());  
		assertEquals( "base after base-meta edge add edge count",8,root.getBaseNetwork().getEdgeCount());  
	}


	/** 
	 * add edge between n6 n8 (between two meta nodes)
	 */
    public void testEdgeBetweenNodesInMetaNodes() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();

		CyEdge ex1 = root.getBaseNetwork().addEdge(n6,n8,true);

		// no change
		assertEquals( "root after base-meta edge add node count",10,root.getNodeCount());  
		assertEquals( "base after base-meta edge add node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "m2 after base-meta edge add edge count",2,m2.getSubNetwork().getEdgeCount());  

		// increase by one
		assertEquals( "root after base-meta edge add edge count",8,root.getEdgeCount());  
		assertEquals( "base after base-meta edge add edge count",8,root.getBaseNetwork().getEdgeCount());  
	}

	/** 
	 * add edge between m1 n3 (between meta node in root and base)
    public void testMetaNodeToNormalNode() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();

		CyEdge ex1 = root.addEdge(m1,n3,true);

		// no change
		assertEquals( "root after base-meta edge add node count",10,root.getNodeCount());  
		assertEquals( "base after base-meta edge add node count",8,root.getBaseNetwork().getNodeCount());  
		assertEquals( "m1 after base-meta edge add edge count",1,m1.getSubNetwork().getEdgeCount());  
		assertEquals( "base after base-meta edge add edge count",7,root.getBaseNetwork().getEdgeCount());  

		// increase by one
		assertEquals( "root after base-meta edge add edge count",8,root.getEdgeCount());  
	}
	 */

	/** 
	 * add edge between m1 m2 (just in root)
    public void testMetaNodeToMetaNodeEdge() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();

		CyEdge ex1 = root.addEdge(m1,m2,true);

		// no change
		assertEquals( "base after base-meta edge add edge count",7,root.getBaseNetwork().getEdgeCount());  
		// increase by one
		assertEquals( "root after base-meta edge add edge count",8,root.getEdgeCount());  
	}
	 */

	/** 
	 * Adding an edge between two nodes in the same subnetwork,
	 * but doing it from the root network.
    public void testAddingEdgeToSubNetworkFromRoot() {
		optionalSetup();
		createMetaNode1();
		createMetaNode2();
		CyEdge ex1 = root.addEdge(n4,n6,true);

		// no change
		assertEquals( "base after base-meta edge add edge count",7,root.getBaseNetwork().getEdgeCount());  
		assertEquals( "base after base-meta edge add edge count",2,m2s.getEdgeCount());  

		// increase by one
		assertEquals( "root after base-meta edge add edge count",8,root.getEdgeCount());  
	}
	 */

}
