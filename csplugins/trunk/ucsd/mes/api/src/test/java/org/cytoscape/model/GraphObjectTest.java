
package org.cytoscape.model;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.internal.CyNetworkImpl;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyRow;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class GraphObjectTest extends TestCase {

	private CyNetwork net;

	public static Test suite() {
		return new TestSuite(GraphObjectTest.class);
	}

	public void setUp() {
		net = new CyNetworkImpl( new DummyCyEventHelper() );	
	}

	public void tearDown() {
		net = null;
	}

    public void testGetNullNamespace() {
		CyNode n1 = net.addNode();
		try {
        	n1.getCyRow(null);
			fail("didn't throw a NullPointerException for null namespace");
		} catch ( NullPointerException npe ) { return; }
		fail("didn't catch what was thrown" );
	}

	public void testBadNamespace() {
		CyNode n1 = net.addNode();
		try {
        	n1.getCyRow("homeradfasdf");
			fail("didn't throw a NullPointerException for null namespace");
		} catch ( NullPointerException npe ) { return; }
		fail("didn't catch what was thrown");
	}

    public void testGetCyRow() {
        // As long as the object is not null and is an instance of CyRow, we
        // should be satisfied.  Don't test any other properties of CyRow.
        // Leave that to the CyRow unit tests.

		CyNode n1 = net.addNode();
        assertNotNull("cyattrs exists",n1.getCyRow("USER"));
        assertTrue("cyattrs is CyRow",n1.getCyRow("USER") instanceof CyRow);

		CyNode n2 = net.addNode();
        assertNotNull("cyattrs exists",n2.getCyRow("USER"));
        assertTrue("cyattrs is CyRow",n2.getCyRow("USER") instanceof CyRow);

		CyEdge e1 = net.addEdge(n1,n2,true);
        assertNotNull("cyattrs exists",e1.getCyRow("USER"));
        assertTrue("cyattrs is CyRow",e1.getCyRow("USER") instanceof CyRow);

		CyEdge e2 = net.addEdge(n1,n2,false);
        assertNotNull("cyattrs exists",e2.getCyRow("USER"));
        assertTrue("cyattrs is CyRow",e2.getCyRow("USER") instanceof CyRow);

    }

	public void testAttrs() {
		CyNode n1 = net.addNode();
        assertNotNull("cyattrs exists",n1.attrs());
        assertTrue("cyattrs is CyRow",n1.attrs() instanceof CyRow);
		assertTrue("attrs equals getCyRow", n1.attrs().equals(n1.getCyRow("USER")));
	}
}
