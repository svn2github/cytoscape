package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.lang.reflect.Method;
import java.util.Set;

import networks.SFNetwork;

import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.CytoscapeSessionReader;

public class ConvertCyNetworkToSFNetworksTest extends TestCase {

	private CyNetwork targetNetwork;

	protected void setUp() throws Exception {
		super.setUp();

		invokeReader("testData/testSession.cys");

		// Check all networks are available.
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();

		for (CyNetwork net : networks) {
			if (net.getTitle().equals("Union")) {
				targetNetwork = net;
				break;
			}
		}

		assertNotNull(targetNetwork);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private void invokeReader(String file) throws Exception {
		CytoscapeSessionReader sr;
		Cytoscape.buildOntologyServer();
		sr = new CytoscapeSessionReader(file, null);

		// Run session reader without Desktop using reflection
		Class<?> cls = sr.getClass();
		Method method = cls.getDeclaredMethod("unzipSessionFromURL",
				boolean.class);
		method.setAccessible(true);
		Object ret = method.invoke(sr, false);
	}

	public void testConvertCyNetworkToSFNetworks() throws Exception {

		final ConvertCyNetworkToSFNetworks converter = new ConvertCyNetworkToSFNetworks(
				targetNetwork, "physical edge weight", "genetic edge weight");
		
		final SFNetwork gNet = converter.getGeneticNetwork();
		final SFNetwork pNet = converter.getPhysicalNetwork();
		assertNotNull(gNet);
		assertNotNull(pNet);
		
		assertEquals(pNet.numEdges(), 6);
		assertEquals(pNet.numNodes(), 6);
		
		assertEquals(gNet.numEdges(), 9);
		assertEquals(gNet.numNodes(), 6);
		
	}

}
