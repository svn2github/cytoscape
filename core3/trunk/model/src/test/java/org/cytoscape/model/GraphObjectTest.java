
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

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import java.lang.RuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DOCUMENT ME!
  */
public class GraphObjectTest extends TestCase {
	private CyNetwork net;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(GraphObjectTest.class);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		net = TestCyNetworkFactory.getInstance(); 
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
		net = null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetNullNamespace() {
		CyNode n1 = net.addNode();

		try {
			n1.getCyRow(null);
			fail("didn't throw a NullPointerException for null namespace");
		} catch (NullPointerException npe) {
			return;
		}

		fail("didn't catch what was thrown");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testBadNamespace() {
		CyNode n1 = net.addNode();

		try {
			n1.getCyRow("homeradfasdf");
			fail("didn't throw a NullPointerException for null namespace");
		} catch (NullPointerException npe) {
			return;
		}

		fail("didn't catch what was thrown");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testGetCyRow() {
		// As long as the object is not null and is an instance of CyRow, we
		// should be satisfied.  Don't test any other properties of CyRow.
		// Leave that to the CyRow unit tests.
		CyNode n1 = net.addNode();
		assertNotNull("cyattrs exists", n1.getCyRow("USER"));
		assertTrue("cyattrs is CyRow", n1.getCyRow("USER") instanceof CyRow);

		CyNode n2 = net.addNode();
		assertNotNull("cyattrs exists", n2.getCyRow("USER"));
		assertTrue("cyattrs is CyRow", n2.getCyRow("USER") instanceof CyRow);

		CyEdge e1 = net.addEdge(n1, n2, true);
		assertNotNull("cyattrs exists", e1.getCyRow("USER"));
		assertTrue("cyattrs is CyRow", e1.getCyRow("USER") instanceof CyRow);

		CyEdge e2 = net.addEdge(n1, n2, false);
		assertNotNull("cyattrs exists", e2.getCyRow("USER"));
		assertTrue("cyattrs is CyRow", e2.getCyRow("USER") instanceof CyRow);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testAttrs() {
		CyNode n1 = net.addNode();
		assertNotNull("cyattrs exists", n1.attrs());
		assertTrue("cyattrs is CyRow", n1.attrs() instanceof CyRow);
		assertTrue("attrs equals getCyRow", n1.attrs().equals(n1.getCyRow("USER")));
	}
}
