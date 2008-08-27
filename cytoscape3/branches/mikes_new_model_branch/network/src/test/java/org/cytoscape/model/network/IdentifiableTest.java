

package org.cytoscape.model.network;


import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.internal.CyNetworkImpl;
import org.cytoscape.attributes.CyAttributesManager;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.attributes.CyAttributes;

public class IdentifiableTest extends TestCase {

	private CyNetwork net;

	public static Test suite() {
		return new TestSuite(IdentifiableTest.class);
	}

	public void setUp() {
		net = new CyNetworkImpl( new DummyCyEventHelper() );	
	}

	public void tearDown() {
		net = null;
	}

    public void testGetSUID() {

		CyNode n1 = net.addNode();
        assertTrue("suid >= 0",n1.getSUID() >= 0 );

		CyNode n2 = net.addNode();
        assertTrue("suid >= 0",n2.getSUID() >= 0 );

		CyEdge e1 = net.addEdge(n1,n2,true);
        assertTrue("suid >= 0",e1.getSUID() >= 0 );

		CyEdge e2 = net.addEdge(n1,n2,false);
        assertTrue("suid >= 0",e2.getSUID() >= 0 );

        assertTrue("suid >= 0",net.getSUID() >= 0 );
    }

}
