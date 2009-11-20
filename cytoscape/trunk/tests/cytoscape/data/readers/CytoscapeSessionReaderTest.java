/*
 File: XGMMLWriterTest.java

 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.readers;

import giny.model.Node;

import java.lang.reflect.Method;
import java.util.Set;

import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

/**
 * Tests the CytoscapeSessionReader class.
 * 
 * @author noel.ruddock
 */
public class CytoscapeSessionReaderTest extends TestCase {

	
	private void invokeReader(String file) throws Exception {
		CytoscapeSessionReader sr;
		Cytoscape.buildOntologyServer();
		sr = new CytoscapeSessionReader(file, null);

		// Run session reader without Desktop using reflection
		Class<?> cls = sr.getClass();
		Method method = cls.getDeclaredMethod("unzipSessionFromURL", boolean.class);
		method.setAccessible(true);
		Object ret = method.invoke(sr, false);
	}
	
	
	public void testNestedNetworkReconstruction1() throws Exception {
		invokeReader("testData/NNFData/t3.cys");
		
		//Check all networks are available.
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		
		CyNetwork targetNet = null;
		for (CyNetwork net:networks) {
			if (net.getTitle().equals("Module_Overview")) {
				targetNet = net;
			}
		}
		
		assertNotNull(targetNet);
		assertEquals(4, targetNet.getNodeCount());
		assertEquals(5, targetNet.getEdgeCount());
		
		CyNode m1 = Cytoscape.getCyNode("M1");
		assertNotNull(m1);
		CyNode m1InOverview = (CyNode) targetNet.getNode(m1.getRootGraphIndex());
		assertNotNull(m1InOverview);
		assertTrue(m1InOverview.getIdentifier().equals("M1"));
		assertNotNull(m1InOverview.getNestedNetwork());
		assertTrue(m1InOverview.getNestedNetwork() instanceof CyNetwork);
		assertTrue(((CyNetwork)m1InOverview.getNestedNetwork()).getTitle().equals("M1"));
	}
	
	public void testNestedNetworkReconstruction2() throws Exception {
		invokeReader("testData/NNFData/t4.cys");
		
		//Check all networks are available.
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		
		CyNetwork targetNet = null;
		for (CyNetwork net:networks) {
			if (net.getTitle().equals("Top_Level_Network")) {
				targetNet = net;
			}
		}
		
		assertNotNull(targetNet);
		assertEquals(7, targetNet.getNodeCount());
		assertEquals(5, targetNet.getEdgeCount());
		
		CyNode m3 = Cytoscape.getCyNode("M3");
		assertNotNull(m3);
		Node m3InOverview = targetNet.getNode(m3.getRootGraphIndex());
		assertNotNull(m3InOverview);
		CyNetwork nestedNetwork = (CyNetwork) m3InOverview.getNestedNetwork();
		assertNotNull(nestedNetwork);
		assertTrue(nestedNetwork.getTitle().equals("M3"));
		assertEquals(4, nestedNetwork.getNodeCount());
		assertEquals(3, nestedNetwork.getEdgeCount());
		CyNode m2 = (CyNode) nestedNetwork.getNode(Cytoscape.getCyNode("M2").getRootGraphIndex());
		assertNotNull(m2);
		assertNotNull(m2.getNestedNetwork());
		assertNotNull(m2.getNestedNetwork().getNode(Cytoscape.getCyNode("D").getRootGraphIndex()));

	}

	// These tests work and pass, but are commented out because they disturb
	// one of the PluginManager tests when run using "ant test"
	// All tests function undisturbed when run using "ant test-slow"
	// public void testBug0001929a() throws Exception {
	// CytoscapeSessionReader sr;
	//
	// Cytoscape.buildOntologyServer();
	// sr = new CytoscapeSessionReader("testData/Bug1929TestA.cys", null);
	// sr.read();
	// }
	//
	// public void testBug0001929b() throws Exception {
	// CytoscapeSessionReader sr;
	//
	// //new CytoscapeInit().init(null);
	// Cytoscape.buildOntologyServer();
	// sr = new CytoscapeSessionReader("testData/Bug1929TestB.cys", null);
	// sr.read();
	// }
	//
	// public void testBug0001929c() throws Exception {
	// CytoscapeSessionReader sr;
	//
	// //new CytoscapeInit().init(null);
	// Cytoscape.buildOntologyServer();
	// sr = new CytoscapeSessionReader("testData/Bug1929TestC.cys", null);
	// sr.read();
	// }
	//
	// public void testBug0001929d() throws Exception {
	// CytoscapeSessionReader sr;
	//
	// //new CytoscapeInit().init(null);
	// Cytoscape.buildOntologyServer();
	// sr = new CytoscapeSessionReader("testData/Bug1929TestD.cys", null);
	// sr.read();
	// }
}
