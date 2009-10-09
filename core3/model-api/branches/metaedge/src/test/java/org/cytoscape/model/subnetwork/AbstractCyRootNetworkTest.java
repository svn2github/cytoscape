
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
public abstract class AbstractCyRootNetworkTest extends TestCase {
	protected CyRootNetwork root;
	protected CyRootNetwork root2;


	/**
	 *  DOCUMENT ME!
	 */
	public void testAddMetaNode() {
		System.out.println("---> testAddMetaNode");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);

//		List<CyNode> nl = new ArrayList<CyNode>(2);
//		nl.add(n1);
//		nl.add(n2);

		assertEquals("num nodes", 4, root.getNodeList().size());
		assertEquals("base nodes", 4, root.getBaseNetwork().getNodeList().size());

		assertEquals("num edges", 3, root.getEdgeList().size());
		assertEquals("base edges", 3, root.getBaseNetwork().getEdgeList().size());

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

		assertNotNull("metanode is not null",m1);
		assertEquals("num nodes", 5, root.getNodeList().size());
		assertEquals("base nodes", 4, root.getBaseNetwork().getNodeList().size());

		assertEquals("num edges", 3, root.getEdgeList().size());
		assertEquals("base edges", 3, root.getBaseNetwork().getEdgeList().size());
	}

	public void testRemoveMetaNode() {
		System.out.println("---> testRemoveMetaNode");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);

//		List<CyNode> nl = new ArrayList<CyNode>(2);
//		nl.add(n1);
//		nl.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

		assertNotNull("metanode is not null",m1);
		assertEquals("node list size",4,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list does contain meta node",root.getNodeList().contains(m1));
		assertFalse("base node list does not contain meta node",root.getBaseNetwork().getNodeList().contains(m1));
		assertEquals("edge list size",2,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

//		List<CyNode> nl2 = new ArrayList<CyNode>(2);
//		nl2.add(n3);
//		nl2.add(n2);

		CyMetaNode m2 = root.addMetaNode();
		CySubNetwork s2 = m2.getSubNetwork();
		s2.addNode(n3);
		s2.addNode(n2);
		s2.addEdge(e2);

		assertNotNull("metanode is not null",m2);
		assertEquals("node list size",5,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("edge list size",2,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

		assertEquals("node count",5,root.getNodeCount());
		assertEquals("base node count",3,root.getBaseNetwork().getNodeCount());
		assertEquals("edge count",2,root.getEdgeCount());
		assertEquals("base edge count",2,root.getBaseNetwork().getEdgeCount());

		root.removeMetaNode(m1);

		assertEquals("node list size",4,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		// m1 now gone
		assertFalse("node list doesn't contain meta node",root.getNodeList().contains(m1));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m1));
		// m2 still present
		assertTrue("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("edge list size",2,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

		assertEquals("node count",4,root.getNodeCount());
		assertEquals("base node count",3,root.getBaseNetwork().getNodeCount());
		assertEquals("edge count",2,root.getEdgeCount());
		assertEquals("base edge count",2,root.getBaseNetwork().getEdgeCount());

		root.removeMetaNode(m2);

		assertEquals("node list size",3,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		// m2 now gone
		assertFalse("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("edge list size",2,root.getEdgeList().size());
		assertEquals("base edge list size",2,root.getBaseNetwork().getEdgeList().size());

		assertEquals("node count",3,root.getNodeCount());
		assertEquals("base node count",3,root.getBaseNetwork().getNodeCount());
		assertEquals("edge count",2,root.getEdgeCount());
		assertEquals("base edge count",2,root.getBaseNetwork().getEdgeCount());

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
		assertEquals("node count",3,root.getNodeCount());
		assertEquals("base node count",3,root.getBaseNetwork().getNodeCount());

//		List<CyNode> nl = new ArrayList<CyNode>(2);
//		nl.add(n1);
//		nl.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

		assertNotNull("metanode is not null",m1);
		assertEquals("node list size",4,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list contains meta node",root.getNodeList().contains(m1));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m1));
		assertEquals("node count",4,root.getNodeCount());
		assertEquals("base node count",3,root.getBaseNetwork().getNodeCount());

//		List<CyNode> nl2 = new ArrayList<CyNode>(2);
//		nl2.add(n3);
//		nl2.add(n2);

		CyMetaNode m2 = root.addMetaNode();
		CySubNetwork s2 = m2.getSubNetwork();
		s2.addNode(n3);
		s2.addNode(n2);
		s2.addEdge(e2);

		assertNotNull("metanode is not null",m2);
		assertEquals("node list size",5,root.getNodeList().size());
		assertEquals("base node list size",3,root.getBaseNetwork().getNodeList().size());
		assertTrue("node list contains meta node",root.getNodeList().contains(m2));
		assertFalse("base node list doesn't contain meta node",root.getBaseNetwork().getNodeList().contains(m2));
		assertEquals("node count",5,root.getNodeCount());
		assertEquals("base node count",3,root.getBaseNetwork().getNodeCount());
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
		assertEquals("edge count",3,root.getEdgeCount());
		assertEquals("base edge count",3,root.getBaseNetwork().getEdgeCount());

//		List<CyNode> nl = new ArrayList<CyNode>(2);
//		nl.add(n1);
//		nl.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

		assertNotNull("metanode is not null",m1);
		assertEquals("edge list size",3,root.getEdgeList().size());
		assertEquals("base edge list size",3,root.getBaseNetwork().getEdgeList().size());
		assertEquals("edge count",3,root.getEdgeCount());
		assertEquals("base edge count",3,root.getBaseNetwork().getEdgeCount());
	}

	public void testGetSubNetworkList() {
		System.out.println("---> testGetSubNetworkList");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);

//		List<CyNode> nl = new ArrayList<CyNode>(2);
//		nl.add(n1);
//		nl.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

//		List<CyNode> nl2 = new ArrayList<CyNode>(2);
//		nl2.add(n3);
//		nl2.add(n2);

		CyMetaNode m2 = root.addMetaNode();
		CySubNetwork s2 = m2.getSubNetwork();
		s2.addNode(n3);
		s2.addNode(n2);
		s2.addEdge(e2);

//		CySubNetwork sn1 = m1.getSubNetwork();
//		CySubNetwork sn2 = m2.getSubNetwork();

//		List<CySubNetwork> snl = root.getSubNetworkList();
//		assertNotNull("subnetwork list not null",snl);
//		assertEquals("subnetwork list size",3,snl.size());
//		assertTrue("contains metanode 1 child net", snl.contains(sn1));
//		assertTrue("contains metanode 2 child net", snl.contains(sn2));

		root.removeMetaNode(m1);

// TODO should be some check here
//		List<CySubNetwork> snl2 = root.getSubNetworkList();
//		assertNotNull("subnetwork list not null",snl2);
//		assertEquals("subnetwork list size",2,snl2.size());
//		assertFalse("contains metanode 1", snl2.contains(sn1));
//		assertTrue("contains metanode 2", snl2.contains(sn2));
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

//		List<CyNode> nl = new ArrayList<CyNode>(2);
//		nl.add(n1);
//		nl.add(n2);

		//System.out.println("###### 4 nodes");

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

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

	public void testGetMetaNodeList() {
		System.out.println("---> testGetMetaNodeList");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);

		// list of size 0
		List<CyMetaNode> mnodes = root.getMetaNodeList();
		assertNotNull(mnodes);
		assertEquals("meta node list size",0,mnodes.size());
//		List<CySubNetwork> subsY = root.getSubNetworkList();
//		assertEquals("subnetwork list size",1,subsY.size()); // contains base network


//		List<CyNode> nl1 = new ArrayList<CyNode>(2);
//		nl1.add(n1);
//		nl1.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();

		// after creating a metanode/subnetwork

		List<CyMetaNode> mnodesX = root.getMetaNodeList();
		assertNotNull(mnodesX);
		assertEquals("meta node list size",1,mnodesX.size());
		assertTrue("contains m1",mnodesX.contains(m1));
//		List<CySubNetwork> subsX = root.getSubNetworkList();
//		assertNotNull(subsX);
//		assertEquals("subnetwork list size",2,subsX.size());


//		List<CyNode> nl2 = new ArrayList<CyNode>(2);
//		nl2.add(n1);
//		nl2.add(n3);

		CyMetaNode m2 = root.addMetaNode();
		CySubNetwork s2 = m2.getSubNetwork();

		// list of size 2 
		List<CyMetaNode> mnodes2 = root.getMetaNodeList();
		assertNotNull(mnodes2);
		assertEquals("meta node list size",2,mnodes2.size());
		assertTrue("contains m1",mnodes2.contains(m1));
		assertTrue("contains m2",mnodes2.contains(m2));

		root.removeMetaNode(m1);

		// list of size 1 after removing a metanode
		List<CyMetaNode> mnodes3 = root.getMetaNodeList();
		assertNotNull(mnodes3);
		assertEquals("meta node list size",1,mnodes3.size());
		assertTrue("contains m2",mnodes3.contains(m2));
		assertFalse("not contains m1",mnodes3.contains(m1));
	}

	public void testGetBaseNetwork() {
		System.out.println("---> testGetBaseNetwork");

		// should already contain a base network
		CySubNetwork base1 = root.getBaseNetwork();
		assertNotNull(base1);
		assertEquals("num nodes",0,base1.getNodeCount());
		assertEquals("num edges",0,base1.getEdgeCount());

//		List<CySubNetwork> subs1 = root.getSubNetworkList();
//		assertNotNull(subs1);
//		assertEquals("subnetwork list size",1,subs1.size()); 

//		CySubNetwork sub1 = subs1.get(0);
//		assertNotNull(sub1);
//		assertEquals("num nodes",0,sub1.getNodeCount());
//		assertEquals("num edges",0,sub1.getEdgeCount());
	
//		assertTrue("sub1 equals base1",sub1.equals(base1));

		// now modify root
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);

//		// still only the base
//		List<CySubNetwork> subs2 = root.getSubNetworkList();
//		assertEquals("subnetwork list size",1,subs2.size()); 

		CySubNetwork base2 = root.getBaseNetwork();
		assertNotNull(base2);
		assertEquals("num nodes",4,base2.getNodeCount());
		assertEquals("num edges",3,base2.getEdgeCount());

		// now add a subnetwork
//		List<CyNode> nl1 = new ArrayList<CyNode>(2);
//		nl1.add(n1);
//		nl1.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();

//		List<CySubNetwork> subs3 = root.getSubNetworkList();
//		assertEquals("subnetwork list size",2,subs3.size()); 

		CySubNetwork base3 = root.getBaseNetwork();
		assertNotNull(base3);
		assertEquals("num nodes",4,base3.getNodeCount());
		assertEquals("num edges",3,base3.getEdgeCount());

		// now add a metanode
//		CyMetaNode m1 = root.addMetaNode(s1);

//		List<CySubNetwork> subs4 = root.getSubNetworkList();
//		assertEquals("subnetwork list size",2,subs4.size()); 

		CySubNetwork base4 = root.getBaseNetwork();
		assertNotNull(base4);
		assertEquals("num nodes",4,base4.getNodeCount());
		assertEquals("num edges",3,base4.getEdgeCount());
	}

	public void testAddSubNetworkNodes() {
		System.out.println("---> testAddSubNetworkNodes");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();
		CyNode n5 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);
		CyEdge e4 = root.addEdge(n3,n4,false);
		CyEdge e5 = root.addEdge(n1,n4,false);

//		List<CyNode> nl1 = new ArrayList<CyNode>(2);
//		nl1.add(n1);
//		nl1.add(n2);

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n1);
		s1.addNode(n2);
		s1.addEdge(e1);

		assertNotNull(s1);
		assertEquals("s1 num nodes",2,s1.getNodeCount());
		assertEquals("s1 node list size",2,s1.getNodeList().size());
		assertTrue("s1 contains n1",s1.containsNode(n1));
		assertTrue("s1 contains n2",s1.containsNode(n2));
		assertFalse("s1 contains n3",s1.containsNode(n3));
		assertFalse("s1 contains n4",s1.containsNode(n4));
		assertFalse("s1 contains n5",s1.containsNode(n5));

		assertEquals("s1 num edges",1,s1.getEdgeCount());
		assertEquals("s1 edge list size",1,s1.getEdgeList().size());
		assertTrue("s1 contains e1",s1.containsEdge(e1));
		assertFalse("s1 contains e2",s1.containsEdge(e2));
		assertFalse("s1 contains e3",s1.containsEdge(e3));
		assertFalse("s1 contains e4",s1.containsEdge(e4));
		assertFalse("s1 contains e5",s1.containsEdge(e5));

//		List<CyNode> nl2 = new ArrayList<CyNode>(4);
//		nl2.add(n2);
//		nl2.add(n3);
//		nl2.add(n4);
//		nl2.add(n5);

		CyMetaNode m2 = root.addMetaNode();
		CySubNetwork s2 = m2.getSubNetwork();
		s2.addNode(n2);
		s2.addNode(n3);
		s2.addNode(n4);
		s2.addNode(n5);
		s2.addEdge(e2);
		s2.addEdge(e3);
		s2.addEdge(e4);

		assertEquals("s2 num nodes",4,s2.getNodeCount());
		assertEquals("s2 node list size",4,s2.getNodeList().size());
		assertFalse("s2 contains n1",s2.containsNode(n1));
		assertTrue("s2 contains n2",s2.containsNode(n2));
		assertTrue("s2 contains n3",s2.containsNode(n3));
		assertTrue("s2 contains n4",s2.containsNode(n4));
		assertTrue("s2 contains n5",s2.containsNode(n5));

		assertEquals("s2 num edges",3,s2.getEdgeCount());
		assertEquals("s2 edge list size",3,s2.getEdgeList().size());
		assertFalse("s2 contains e1",s2.containsEdge(e1));
		assertTrue("s2 contains e2",s2.containsEdge(e2));
		assertTrue("s2 contains e3",s2.containsEdge(e3));
		assertTrue("s2 contains e4",s2.containsEdge(e4));
		assertFalse("s2 contains e5",s2.containsEdge(e5));
	}


	public void testRemoveSubNetwork() {
		System.out.println("---> testRemoveSubNetwork");
		CyNode n1 = root.addNode();
		CyNode n2 = root.addNode();
		CyNode n3 = root.addNode();
		CyNode n4 = root.addNode();
		CyNode n5 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n2,n3,false);
		CyEdge e3 = root.addEdge(n2,n4,false);
		CyEdge e4 = root.addEdge(n3,n4,false);
		CyEdge e5 = root.addEdge(n1,n4,false);

//		List<CyNode> nl1 = new ArrayList<CyNode>(3);
//		nl1.add(n2);
//		nl1.add(n3);
//		nl1.add(n4);

		CySubNetwork base = root.getBaseNetwork();

		CyMetaNode m1 = root.addMetaNode();
		CySubNetwork s1 = m1.getSubNetwork();
		s1.addNode(n2);
		s1.addNode(n3);
		s1.addNode(n4);

		List<CyMetaNode> metas = root.getMetaNodeList();
		assertNotNull(metas);
		assertEquals("metanode list size",1,metas.size());
		assertTrue("metanode contains s1",metas.contains(m1));
		//assertTrue("subnetwork contains base",metas.contains(base)); // TODO base metanode?

		root.removeMetaNode(m1);

		metas = root.getMetaNodeList();
		assertNotNull(metas);
		assertEquals("metanode list size",0,metas.size());
		assertFalse("metanode contains m1",metas.contains(m1));
		//assertTrue("subnetwork contains base",metas.contains(base)); // TODO

		// try and remove a subnetwork from a different root network
		/* TODO not sure what to do about this - should we have a base metanode?
		CySubNetwork b2 = root2.getBaseNetwork();

		IllegalArgumentException iae = null;

		try {
			root.removeSubNetwork(b2);
		} catch ( IllegalArgumentException e ) {
			iae = e;
		}
		assertNotNull(iae);
		subs = root.getSubNetworkList();
		assertNotNull(subs);
		assertEquals("subnetwork list size",1,subs.size());
		assertFalse("subnetwork contains s1",subs.contains(s1));
		assertTrue("subnetwork contains base",subs.contains(base));

		iae = null;

		// try and remove base subnetwork
		try { 
			root.removeSubNetwork(base);
		} catch ( IllegalArgumentException e ) {
			iae = e;
		}
		assertNotNull(iae);
		subs = root.getSubNetworkList();
		assertNotNull(subs);
		assertEquals("subnetwork list size",1,subs.size());
		assertFalse("subnetwork contains s1",subs.contains(s1));
		assertTrue("subnetwork contains base",subs.contains(base));
		*/
	}

	/**
	 * Read the enhanced sif file: 
	 * A B C
	 * B C A
	 * C A B
	 */
	public void testABCMetaNode() {

		CyMetaNode a = root.addMetaNode();
		CySubNetwork as = a.getSubNetwork();

		CyMetaNode b = root.addMetaNode();
		CySubNetwork bs = b.getSubNetwork();

		CyMetaNode c = root.addMetaNode();
		CySubNetwork cs = c.getSubNetwork();

		as.addNode( b );
		as.addNode( c );
		as.addEdge(b, c, false);

		bs.addNode( c );
		bs.addNode( a );
		bs.addEdge(c, a, false);

		cs.addNode( a );
		cs.addNode( b );
		cs.addEdge(a, b, false);

		assertEquals("num edges in root",3,root.getEdgeCount());
		assertEquals("num nodes in root",3,root.getNodeCount());
	}
}
