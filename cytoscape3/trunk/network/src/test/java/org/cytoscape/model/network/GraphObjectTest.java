
package org.cytoscape.model.network;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.internal.CyNetworkImpl;
import org.cytoscape.model.CyDataTable;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.model.CyRow;

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
        	n1.getCyAttributes(null);
			fail("didn't throw a NullPointerException for null namespace");
		} catch ( NullPointerException npe ) { return; }
		fail("didn't catch what was thrown" );
	}

	public void testBadNamespace() {
		CyNode n1 = net.addNode();
		try {
        	n1.getCyAttributes("homeradfasdf");
			fail("didn't throw a NullPointerException for null namespace");
		} catch ( NullPointerException npe ) { return; }
		fail("didn't catch what was thrown");
	}

    public void testGetCyAttributes() {
        // As long as the object is not null and is an instance of CyAttributes, we
        // should be satisfied.  Don't test any other properties of CyAttributes.
        // Leave that to the CyAttributes unit tests.

		CyNode n1 = net.addNode();
        assertNotNull("cyattrs exists",n1.attrs());
        assertTrue("cyattrs is CyAttributes",n1.attrs() instanceof CyAttributes);

		CyNode n2 = net.addNode();
        assertNotNull("cyattrs exists",n2.attrs());
        assertTrue("cyattrs is CyAttributes",n2.attrs() instanceof CyAttributes);

		CyEdge e1 = net.addEdge(n1,n2,true);
        assertNotNull("cyattrs exists",e1.attrs());
        assertTrue("cyattrs is CyAttributes",e1.attrs() instanceof CyAttributes);

		CyEdge e2 = net.addEdge(n1,n2,false);
        assertNotNull("cyattrs exists",e2.attrs());
        assertTrue("cyattrs is CyAttributes",e2.attrs() instanceof CyAttributes);

    }
}
