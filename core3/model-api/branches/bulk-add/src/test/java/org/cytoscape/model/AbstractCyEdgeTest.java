
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

package org.cytoscape.model;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import java.lang.RuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DOCUMENT ME!
  */
public abstract class AbstractCyEdgeTest extends TestCase {

	protected CyNetwork net;


	private CyEdge eDir;
	private CyEdge eUndir;
	private CyNode n1;
	private CyNode n2;
	private CyNode n3;

	/**
	 *  DOCUMENT ME!
	 */
	private void defaultSetUp() {
		CyTempNode tn1 = net.createNode();
		CyTempNode tn2 = net.createNode();
		CyTempNode tn3 = net.createNode();
		List<CyNode> ln = net.addNodes(tn1,tn2,tn3);

		n1 = ln.get(0);
		n2 = ln.get(1);
		n3 = ln.get(2);

		// TODO should we be instantiating these objects independent of CyNetwork?
		eDir = net.addEdge(n1, n2, true);
		eUndir = net.addEdge(n2, n3, false);
	}


	/**
	 *  DOCUMENT ME!
	 */
	public void testIsDirected() {
		defaultSetUp();
		assertTrue("eDir is directed", eDir.isDirected());
		assertFalse("eUndir is undirected", eUndir.isDirected());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetIndex() {
		defaultSetUp();
		assertTrue("edge index >= 0", eDir.getIndex() >= 0);
		assertTrue("edge index >= 0", eUndir.getIndex() >= 0);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetSource() {
		defaultSetUp();
		assertNotNull("source exists", eDir.getSource());
		assertTrue("source is a CyNode", eDir.getSource() instanceof CyNode);

		assertNotNull("source exists", eUndir.getSource());
		assertTrue("source is a CyNode", eUndir.getSource() instanceof CyNode);

		assertTrue("source for eDir", eDir.getSource() == n1);

		// TODO what should the policy be here?
		// Which node should be returned? Is either legal?
		assertTrue("source for eUndir", ((eUndir.getSource() == n3) || (eUndir.getSource() == n2)));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetTarget() {
		defaultSetUp();
		assertNotNull("target exists", eDir.getTarget());
		assertTrue("target is a CyNode", eDir.getTarget() instanceof CyNode);

		assertNotNull("target exists", eUndir.getTarget());
		assertTrue("target is a CyNode", eUndir.getTarget() instanceof CyNode);

		assertTrue("target for eDir", eDir.getTarget() == n2);

		// TODO what should the policy be here?
		// Which node should be returned? Is either legal?
		assertTrue("target for eUndir", ((eUndir.getTarget() == n3) || (eUndir.getTarget() == n2)));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testToString() {
		defaultSetUp();
		assertNotNull("string is not null", eDir.toString());
		assertNotNull("string is not null", eUndir.toString());
		assertTrue("string has non zero length", eDir.toString().length() > 0);
		assertTrue("string has non zero length", eUndir.toString().length() > 0);
	}

    public void testDefaultAttributes() {
		defaultSetUp();
        CyDataTable def = net.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
        assertNotNull(def);
        assertNotNull(net.getEdgeCyDataTables().get(CyNetwork.HIDDEN_ATTRS));
        assertTrue(def.getColumnTypeMap().containsKey("name"));
        assertTrue(def.getColumnTypeMap().get("name") == String.class );
        assertTrue(def.getColumnTypeMap().containsKey("selected"));
        assertTrue(def.getColumnTypeMap().get("selected") == Boolean.class );
        assertTrue(def.getColumnTypeMap().containsKey("interaction"));
        assertTrue(def.getColumnTypeMap().get("interaction") == String.class );

		CyTempNode tn5 = net.createNode();
		CyTempNode tn6 = net.createNode();
		List<CyNode> ln = net.addNodes(tn5,tn6);
        CyNode n5 = ln.get(0);
        CyNode n6 = ln.get(1);

        CyEdge e5 = net.addEdge(n5,n6,true);

        assertEquals( String.class, e5.attrs().contains("name"));
        assertEquals( Boolean.class, e5.attrs().contains("selected"));
        assertEquals( String.class, e5.attrs().contains("interaction"));
    }

}
