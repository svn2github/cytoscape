
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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;

import java.lang.RuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * DOCUMENT ME!
  */
public class CyRootNetworkTest extends TestCase {
	private CyRootNetwork root;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(CyRootNetworkTest.class);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		System.out.println("");
		System.out.println("setUp ---------------");
		root = CyNetworkFactory.getRootInstance(); 
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
		root = null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testCreateMetaNode() {
		System.out.println("---> testCreateMetaNode");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);

		List<CyNode> nl = new ArrayList<CyNode>(2);
		nl.add(n1);
		nl.add(n2);

		assertEquals("num nodes", 4, root.getNodeList().size());
		assertEquals("base nodes", 4, root.getBaseNetwork().getNodeList().size());

		assertEquals("num edges", 3, root.getEdgeList().size());
		assertEquals("base edges", 3, root.getBaseNetwork().getEdgeList().size());

		CySubNetwork s1 = root.addSubNetwork(nl);
		CyMetaNode m1 = root.addMetaNode(s1);

		assertNotNull("metanode is not null",m1);
		assertEquals("num nodes", 5, root.getNodeList().size());
		assertEquals("base nodes", 4, root.getBaseNetwork().getNodeList().size());

		assertEquals("num edges", 5, root.getEdgeList().size());
		assertEquals("base edges", 3, root.getBaseNetwork().getEdgeList().size());
	}

	public void testRemoveMetaNode() {
		System.out.println("---> testRemoveMetaNode");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);

		List<CyNode> nl = new ArrayList<CyNode>(2);
		nl.add(n1);
		nl.add(n2);

		CySubNetwork s1 = root.addSubNetwork(nl);
		CyMetaNode m1 = root.addMetaNode(s1);

		assertNotNull("metanode is not null",m1);
		assertEquals("node list size",4,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list does contain meta node",root.getNodeList().contains(m1));
		assertFalse("base node list does not contain meta node",root.getBaseNetwork().getNodeList().contains(m1));
		assertEquals("edge list size",3,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

		List<CyNode> nl2 = new ArrayList<CyNode>(2);
		nl2.add(n3);
		nl2.add(n2);

		CySubNetwork s2 = root.addSubNetwork(nl2);
		CyMetaNode m2 = root.addMetaNode(s2);

		assertNotNull("metanode is not null",m2);
		assertEquals("node list size",5,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("edge list size",5,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

		root.removeMetaNode(m1);

		assertEquals("node list size",4,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		// m1 now gone
		assertFalse("node list doesn't contain meta node",root.getNodeList().contains(m1));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m1));
		// m2 still present
		assertTrue("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("edge list size",3,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

		root.removeMetaNode(m2);

		assertEquals("node list size",3,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		// m2 now gone
		assertFalse("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("edge list size",2,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

	}

	public void testGetAllNodes() {
		System.out.println("---> testGetAllNodes");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);

		assertEquals("node list size",3,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertEquals("edge list size",2,root.getEdgeList().size());

		List<CyNode> nl = new ArrayList<CyNode>(2);
		nl.add(n1);
		nl.add(n2);

		CySubNetwork s1 = root.addSubNetwork(nl);
		CyMetaNode m1 = root.addMetaNode(s1);

		assertNotNull("metanode is not null",m1);
		assertEquals("node list size",4,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list contains meta node",root.getNodeList().contains(m1));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m1));

		List<CyNode> nl2 = new ArrayList<CyNode>(2);
		nl2.add(n3);
		nl2.add(n2);

		CySubNetwork s2 = root.addSubNetwork(nl2);
		CyMetaNode m2 = root.addMetaNode(s2);

		assertNotNull("metanode is not null",m2);
		assertEquals("node list size",5,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
	}

	public void testGetAllEdges() {
		System.out.println("---> testGetAllEdges");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);

		assertEquals("edge list size",3,root.getEdgeList().size());
		assertEquals("base edge list size",3,root.getBaseNetwork().getEdgeList().size());

		List<CyNode> nl = new ArrayList<CyNode>(2);
		nl.add(n1);
		nl.add(n2);

		CySubNetwork s1 = root.addSubNetwork(nl);
		CyMetaNode m1 = root.addMetaNode(s1);

		assertNotNull("metanode is not null",m1);
		assertEquals("edge list size",5,root.getEdgeList().size());
		assertEquals("base edge list size",3,root.getBaseNetwork().getEdgeList().size());
	}

	public void testGetSubNetworkList() {
		System.out.println("---> testGetSubNetworkList");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);

		List<CyNode> nl = new ArrayList<CyNode>(2);
		nl.add(n1);
		nl.add(n2);

		CySubNetwork s1 = root.addSubNetwork(nl);
		CyMetaNode m1 = root.addMetaNode(s1);

		List<CyNode> nl2 = new ArrayList<CyNode>(2);
		nl2.add(n3);
		nl2.add(n2);

		CySubNetwork s2 = root.addSubNetwork(nl2);
		CyMetaNode m2 = root.addMetaNode(s2);

		CySubNetwork sn1 = m1.getSubNetwork();
		CySubNetwork sn2 = m2.getSubNetwork();

		List<CySubNetwork> snl = root.getSubNetworkList();
		assertNotNull("subnetwork list not null",snl);
		assertEquals("subnetwork list size",3,snl.size());
		assertTrue("contains metanode 1 child net", snl.contains(sn1));
		assertTrue("contains metanode 2 child net", snl.contains(sn2));

		root.removeMetaNode(m1);

		List<CySubNetwork> snl2 = root.getSubNetworkList();
		assertNotNull("subnetwork list not null",snl2);
		assertEquals("subnetwork list size",2,snl2.size());
		assertFalse("contains metanode 1", snl2.contains(sn1));
		assertTrue("contains metanode 2", snl2.contains(sn2));
	}

	public void testAddNode() {
		System.out.println("---> testAddNode");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);

		assertEquals("node list size",4,root.getNodeList().size());

		List<CyNode> nl = new ArrayList<CyNode>(2);
		nl.add(n1);
		nl.add(n2);

		//System.out.println("###### 4 nodes");

		CySubNetwork s1 = root.addSubNetwork(nl);
		CyMetaNode m1 = root.addMetaNode(s1);

		//System.out.println("###### 4 nodes, 1 metanode");

		assertEquals("base node list size",4,root.getBaseNetwork().getNodeList().size());
		assertEquals("node list size",5,root.getNodeList().size());

		CyNode n5 = root.addNode();

		//System.out.println("###### 5 nodes, 1 metanode");

		assertEquals("node list size",6,root.getNodeList().size());
		assertEquals("base node list size",5,root.getBaseNetwork().getNodeList().size());

		CyNode n6 = root.addNode();

		//System.out.println("###### 6 nodes, 1 metanode");

		assertEquals("node list size",7,root.getNodeList().size());
		assertEquals("base node list size",6,root.getBaseNetwork().getNodeList().size());
		assertTrue("root does contain node5",root.containsNode(n5));

		boolean ret = root.removeNode(n5);

		//System.out.println("###### 5 nodes, 1 metanode");
		assertTrue("removed node5", ret);
		assertFalse("root doesn't contain node5",root.containsNode(n5));

		//for ( CyNode nn : root.getNodeList() )
			//System.out.println("~~~ " + nn.getIndex());
		assertEquals("node list size",6,root.getNodeList().size());
		assertEquals("base node list size",5,root.getBaseNetwork().getNodeList().size());

		ret = root.removeNode(n6);
		assertTrue("removed node6", ret);

		//System.out.println("###### 4 nodes, 1 metanode");
		assertEquals("node list size",5,root.getNodeList().size());
		assertEquals("base node list size",4,root.getBaseNetwork().getNodeList().size());
	}
}
