
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
public class CyMetaNodeTest extends TestCase {
	private CyRootNetwork root;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(CyMetaNodeTest.class);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
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
        CyNode n1 = root.addNode();
        CyNode n2 = root.addNode();
        CyNode n3 = root.addNode();

		CyEdge e1 = root.addEdge(n1,n2,true);
		CyEdge e2 = root.addEdge(n3,n2,true);
		CyEdge e3 = root.addEdge(n1,n3,false);

        List<CyNode> nl = new ArrayList<CyNode>(2);
        nl.add(n1);
        nl.add(n2);

        CySubNetwork s1 = root.addSubNetwork(nl);

        CyMetaNode m1 = root.addMetaNode(s1);

        assertNotNull("metanode is not null",m1);
	
		CySubNetwork sub = m1.getSubNetwork();	
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
    }

}
