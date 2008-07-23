package org.cytoscape.model.network;


import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.impl.CyNetworkImpl;
import org.cytoscape.model.attrs.CyAttributesManager;
import org.cytoscape.model.attrs.InitialCyAttributesManager;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.model.attrs.CyAttributes;

public class CyEdgeTest extends TestCase {

	private CyNetwork net;
	private CyEdge eDir;
	private CyEdge eUndir;
	private CyNode n1; 
	private CyNode n2;
	private CyNode n3;

	public void setUp() {
		
		Map<String,CyAttributesManager> netm = new HashMap<String,CyAttributesManager>();
		netm.put("USER",InitialCyAttributesManager.getInstance());

		Map<String,CyAttributesManager> nodem = new HashMap<String,CyAttributesManager>();
		nodem.put("USER",InitialCyAttributesManager.getInstance());

		Map<String,CyAttributesManager> edgem = new HashMap<String,CyAttributesManager>();
		edgem.put("USER",InitialCyAttributesManager.getInstance());

		net = new CyNetworkImpl(netm,nodem,edgem);

		n1 = net.addNode();
		n2 = net.addNode();
		n3 = net.addNode();

		// TODO should we be instantiating these objects independent of CyNetwork?
		eDir = net.addEdge(n1,n2,true);
		eUndir = net.addEdge(n2,n3,false);
	}

	public void tearDown() {
		net = null;
		n1 = null;
		n2 = null;
		n3 = null;
		eDir = null;
		eUndir = null;
	}

	public void testIsDirected() {
		assertTrue("eDir is directed", eDir.isDirected());
		assertFalse("eUndir is undirected", eUndir.isDirected());
	}

	public void testGetIndex() {
		assertTrue("edge index >= 0", eDir.getIndex() >= 0);	
		assertTrue("edge index >= 0", eUndir.getIndex() >= 0);	
	}

	public void testGetSource() {
		assertNotNull("source exists", eDir.getSource());
		assertTrue("source is a CyNode", eDir.getSource() instanceof CyNode);

		assertNotNull("source exists", eUndir.getSource());
		assertTrue("source is a CyNode", eUndir.getSource() instanceof CyNode);

		assertTrue("source for eDir", eDir.getSource() == n1);

		// TODO what should the policy be here?
		// Which node should be returned? Is either legal?
		assertTrue("source for eUndir", (eUndir.getSource() == n3 || eUndir.getSource() == n2));
	}

	public void testGetTarget() {
		assertNotNull("target exists", eDir.getTarget());
		assertTrue("target is a CyNode", eDir.getTarget() instanceof CyNode);

		assertNotNull("target exists", eUndir.getTarget());
		assertTrue("target is a CyNode", eUndir.getTarget() instanceof CyNode);

		assertTrue("target for eDir", eDir.getTarget() == n2);

		// TODO what should the policy be here?
		// Which node should be returned? Is either legal?
		assertTrue("target for eUndir", (eUndir.getTarget() == n3 || eUndir.getTarget() == n2));
	}

	public void testToString() {
		assertNotNull("string is not null", eDir.toString());
		assertNotNull("string is not null", eUndir.toString());
		assertTrue("string has non zero length", eDir.toString().length() > 0);
		assertTrue("string has non zero length", eUndir.toString().length() > 0);
	}

}
