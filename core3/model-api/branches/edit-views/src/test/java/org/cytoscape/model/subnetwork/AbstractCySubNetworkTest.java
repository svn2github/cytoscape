
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

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import java.lang.RuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


/**
 * DOCUMENT ME!
 */
public abstract class AbstractCySubNetworkTest extends TestCase {

	protected CyRootNetwork root;
	protected CyRootNetwork root2;

	private CyNode n1;
	private CyNode n2;
	private CyNode n3;
	private CyNode n4;
	private CyNode n5;
	private CyNode nx1;
	private CyEdge e1;
	private CyEdge e2;
	private CyEdge e3;
	private List<CyNode> nl; 
	private CySubNetwork sub;


	/**
	 *  DOCUMENT ME!
	 */
	private void defaultSetup() {
        n1 = root.addNode();
        n2 = root.addNode();
        n3 = root.addNode();
        n4 = root.addNode();
        n5 = root.addNode();

		e1 = root.addEdge(n1,n2,true);
		e2 = root.addEdge(n3,n2,true);
		e3 = root.addEdge(n1,n3,false);

        nl = new ArrayList<CyNode>(2);
        nl.add(n1);
        nl.add(n2);

        sub = root.addSubNetwork(nl);
	}


	/**
	 *  DOCUMENT ME!
	 */
	public void testAddNode() {

		defaultSetup();

		assertNotNull("subnetwork is not null",sub);
		assertEquals("num nodes",2,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		sub.addNode(n4);

		assertEquals("num nodes",3,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains node4",sub.containsNode(n4));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		// TODO assuming this will also copy any edges connecting to existing 
		// nodes in the subnetwork
		sub.addNode(n3);

		assertEquals("num nodes",4,sub.getNodeCount());
		assertEquals("num edges",3,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains node3",sub.containsNode(n3));
		assertTrue("contains node4",sub.containsNode(n4));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge2",sub.containsEdge(e2));
		assertTrue("contains edge3",sub.containsEdge(e3));

    }

	public void testInvalidAddNode() {
		defaultSetup();

		checkInvalidAdd(sub,nx1);
		checkInvalidAdd(sub,null);
    }

	private void checkInvalidAdd(CySubNetwork s, CyNode n) {
		try {
			s.addNode(n);
		} catch (Exception e) {
			assertNotNull("subnetwork is not null",s);
			assertEquals("num nodes",2,s.getNodeCount());
			assertEquals("num edges",1,s.getEdgeCount());
			assertTrue("contains node1",s.containsNode(n1));
			assertTrue("contains node2",s.containsNode(n2));
			assertTrue("contains edge1",s.containsEdge(e1));

			return;
		}

		// if we don't get an exception
		fail();
	}


	public void testRemoveNode() {
		defaultSetup();

		nl.add(n4);

        CySubNetwork sub2 = root.addSubNetwork(nl);

        assertNotNull("metanode is not null",sub2);
		assertEquals("num nodes",3,sub2.getNodeCount());
		assertEquals("num edges",1,sub2.getEdgeCount());
		assertTrue("contains node1",sub2.containsNode(n1));
		assertTrue("contains node2",sub2.containsNode(n2));
		assertTrue("contains node4",sub2.containsNode(n4));
		assertTrue("contains edge1",sub2.containsEdge(e1));
		assertTrue("contains edge1",sub2.containsEdge(n1,n2));

		sub2.removeNode(n4);

		assertEquals("num nodes",2,sub2.getNodeCount());
		assertEquals("num edges",1,sub2.getEdgeCount());
		assertTrue("contains node1",sub2.containsNode(n1));
		assertTrue("contains node2",sub2.containsNode(n2));
		assertFalse("contains node4",sub2.containsNode(n4));
		assertTrue("contains edge1",sub2.containsEdge(e1));
		assertTrue("contains edge1",sub2.containsEdge(n1,n2));

		sub2.removeNode(n1);

		assertEquals("num nodes",1,sub2.getNodeCount());
		assertEquals("num edges",0,sub2.getEdgeCount());
		assertFalse("contains node1",sub2.containsNode(n1));
		assertTrue("contains node2",sub2.containsNode(n2));
		assertFalse("contains edge1",sub2.containsEdge(e1));
		assertFalse("contains edge1",sub2.containsEdge(n1,n2));
    }

	public void testInvalidRemoveFromNetwork() {
		defaultSetup();

		checkInvalidRemove(sub,nx1);
		checkInvalidRemove(sub,null);
    }

	private void checkInvalidRemove(CySubNetwork s, CyNode n) {
		try {
			s.addNode(n);
		} catch (Exception e) {
			assertNotNull("subnetwork is not null",s);
			assertEquals("num nodes",2,s.getNodeCount());
			assertEquals("num edges",1,s.getEdgeCount());
			assertTrue("contains node1",s.containsNode(n1));
			assertTrue("contains node2",s.containsNode(n2));
			assertTrue("contains edge1",s.containsEdge(e1));

			return;
		}

		// if we don't get an exception
		fail();
	}

	public void testGetRootNetwork() {
		defaultSetup();
		CyRootNetwork r2 = sub.getRootNetwork();
		assertNotNull("root is not null",r2);
		assertTrue("r2 equals root",r2.equals(root));
		assertEquals("node list size",r2.getNodeList().size(),root.getNodeList().size());
		assertEquals("edge list size",r2.getEdgeList().size(),root.getEdgeList().size());
	}

	public void testGetExternalNeighborSet() {
		defaultSetup();

		Set<CyNode> ex = sub.getExternalNeighborSet();

		assertNotNull(ex);
		assertEquals("set size",1,ex.size());
		assertTrue("contains n3",ex.contains(n3));

		// now modify the root network
		CyEdge e4 = root.addEdge(n1,n4,true);
		CyEdge e5 = root.addEdge(n5,n2,true);
		
		Set<CyNode> ex1 = sub.getExternalNeighborSet();
	
		assertNotNull(ex1);
		assertEquals("set size",3,ex1.size());
		assertTrue("contains n3",ex1.contains(n3));
		assertTrue("contains n4",ex1.contains(n4));
		assertTrue("contains n5",ex1.contains(n5));

		// now modify the sub network
		sub.addNode(n4);

		Set<CyNode> ex3 = sub.getExternalNeighborSet();
	
		assertNotNull(ex3);
		assertEquals("set size",2,ex3.size());
		assertTrue("contains n3",ex3.contains(n3));
		assertTrue("contains n5",ex3.contains(n5));

		// note switch to root2

        CyNode nn1 = root2.addNode();
        CyNode nn2 = root2.addNode();
        CyNode nn3 = root2.addNode();

        List<CyNode> nnl = new ArrayList<CyNode>(2);
        nnl.add(nn1);
        nnl.add(nn2);

        CySubNetwork sub2 = root2.addSubNetwork(nnl);

		Set<CyNode> ex2 = sub2.getExternalNeighborSet();

		assertNotNull(ex2);
		assertEquals("set size",0,ex2.size());
	}
}
