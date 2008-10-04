
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
import org.cytoscape.event.CyEventListener;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import java.lang.RuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * DOCUMENT ME!
  */
public class CySubNetworkTest extends TestCase {

	private CyRootNetwork root;
	private CyNetwork other;
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
	private CyMetaNode m1;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(CySubNetworkTest.class);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		root = CyNetworkFactory.getRootInstance(); 
		other = CyNetworkFactory.getInstance(); 

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

        m1 = root.createMetaNode(nl);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
		root = null;
		other = null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCopyToNetwork() {

		CySubNetwork sub = m1.getChildNetwork();	

		assertNotNull("subnetwork is not null",sub);
		assertEquals("num nodes",2,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		sub.copyToNetwork(n4);

		assertEquals("num nodes",3,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains node4",sub.containsNode(n4));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		// TODO assuming this will also copy any edges connecting to existing 
		// nodes in the subnetwork
		sub.copyToNetwork(n3);

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

	public void testInvalidCopyToNetwork() {

		CySubNetwork sub = m1.getChildNetwork();	

		checkAdd(sub,nx1);
		checkAdd(sub,null);
    }

	private void checkAdd(CySubNetwork sub, CyNode n) {
		try {
			sub.copyToNetwork(n);
		} catch (Exception e) {
			assertNotNull("subnetwork is not null",sub);
			assertEquals("num nodes",2,sub.getNodeCount());
			assertEquals("num edges",1,sub.getEdgeCount());
			assertTrue("contains node1",sub.containsNode(n1));
			assertTrue("contains node2",sub.containsNode(n2));
			assertTrue("contains edge1",sub.containsEdge(e1));

			return;
		}

		// if we don't get an exception
		fail();
	}


	public void testRemoveFromNetwork() {

		nl.add(n4);

        CyMetaNode m2 = root.createMetaNode(nl);

        assertNotNull("metanode is not null",m2);
	
		CySubNetwork sub = m2.getChildNetwork();	

		assertEquals("num nodes",3,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertTrue("contains node4",sub.containsNode(n4));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		sub.removeFromNetwork(n4);

		assertEquals("num nodes",2,sub.getNodeCount());
		assertEquals("num edges",1,sub.getEdgeCount());
		assertTrue("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertFalse("contains node4",sub.containsNode(n4));
		assertTrue("contains edge1",sub.containsEdge(e1));
		assertTrue("contains edge1",sub.containsEdge(n1,n2));

		sub.removeFromNetwork(n1);

		assertEquals("num nodes",1,sub.getNodeCount());
		assertEquals("num edges",0,sub.getEdgeCount());
		assertFalse("contains node1",sub.containsNode(n1));
		assertTrue("contains node2",sub.containsNode(n2));
		assertFalse("contains edge1",sub.containsEdge(e1));
		assertFalse("contains edge1",sub.containsEdge(n1,n2));
    }

	public void testInvalidRemoveFromNetwork() {

		CySubNetwork sub = m1.getChildNetwork();	

		checkRemove(sub,nx1);
		checkRemove(sub,null);
    }

	private void checkRemove(CySubNetwork sub, CyNode n) {
		try {
			sub.copyToNetwork(n);
		} catch (Exception e) {
			assertNotNull("subnetwork is not null",sub);
			assertEquals("num nodes",2,sub.getNodeCount());
			assertEquals("num edges",1,sub.getEdgeCount());
			assertTrue("contains node1",sub.containsNode(n1));
			assertTrue("contains node2",sub.containsNode(n2));
			assertTrue("contains edge1",sub.containsEdge(e1));

			return;
		}

		// if we don't get an exception
		fail();
	}
}
