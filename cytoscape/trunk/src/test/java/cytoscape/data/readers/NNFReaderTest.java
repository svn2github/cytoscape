package cytoscape.data.readers;

import giny.model.GraphPerspective;
import giny.model.Node;

import java.io.IOException;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.TestUtil;

import junit.framework.TestCase;


/**
 * Test code for Nested Network Format file reader.
 * 
 * @author kono, ruschein
 * @since Cytoscape 2.7.0
 */
public class NNFReaderTest extends TestCase {
	
	// All test data files are in this directory.
	private static final String FILE_LOCATION = "testData/NNFData/";

	
	public void setUp() throws Exception {
		/* Intentionally empty! */
	}


	public void tearDown() throws Exception {
		/* Intentionally empty! */
	}


	public void testGood1() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good1.nnf");
		reader.read();
		
		assertNotNull(reader.getNetworks().get(0));
		assertEquals("root", reader.getNetworks().get(0).getTitle());
	}
	
	
	public void testGood2() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good2.nnf");
		reader.read();
		
		assertNotNull(reader.getNetworks().get(0));
		assertEquals("root", reader.getNetworks().get(0).getTitle());
	}
	
	
	public void testGood3() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good3.nnf");
		reader.read();
		
		assertNotNull(reader.getNetworks().get(0));
		assertEquals("Module_Overview", reader.getNetworks().get(0).getTitle());
		
		// Check number of graph objects
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		CyNetwork targetNetwork = null;
		for (CyNetwork net:networks) {
			if (net.getTitle().equals("Module_Overview")) {
				targetNetwork = net;
			}
		}

		assertNotNull(targetNetwork);
		assertEquals("Module_Overview", targetNetwork.getTitle());
		assertEquals(4, targetNetwork.getNodeCount());
		assertEquals(5, targetNetwork.getEdgeCount());
	}
	
	
	public void testGood4() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good4.nnf");
		reader.read();
		
		assertNotNull(reader.getNetworks().get(0));
		assertEquals("Top_Level_Network", reader.getNetworks().get(0).getTitle());
		
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		CyNetwork targetNetwork = null;
		for (CyNetwork net : networks) {
			if (net.getTitle().equals("M3")) {
				targetNetwork = net;
			}
		}
		
		assertNotNull(targetNetwork);
		assertEquals("M3", targetNetwork.getTitle());
		assertEquals(4, targetNetwork.getNodeCount());
		assertEquals(3, targetNetwork.getEdgeCount());
		CyNode node = Cytoscape.getCyNode("M2");
		assertNotNull(node);
		Node m2 = targetNetwork.getNode(node.getRootGraphIndex());
		assertNotNull(m2);
		GraphPerspective nestedNetwork = m2.getNestedNetwork();
		assertNotNull(nestedNetwork);
		assertTrue(((CyNetwork)nestedNetwork).getTitle().equals("M2"));
		assertEquals(1, nestedNetwork.getNodeCount());
		assertEquals(0, nestedNetwork.getEdgeCount());
	}
	
	
	public void testGood5() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good5.nnf");
		reader.read();
		
		assertNotNull(reader.getNetworks().get(0));
		assertEquals("TopLevelNetwork", reader.getNetworks().get(0).getTitle());
	}
	
	
	public void testGood6() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good6.nnf");
		reader.read();
		
		assertNotNull(reader.getNetworks().get(0));
		assertNotNull(reader.getNetworks().get(1));
	}
	
	
	public void testBad1() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "bad1.nnf");
		try {
			reader.read();
		} catch (IOException e) {
			e.printStackTrace();
			assertNotNull(reader.getNetworks().get(0));
			return;
		}
		
		//If not caught by the above, something is wrong!
		fail();
	}
	
	
	public void testBad2() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "bad2.nnf");
		try {
			reader.read();
		} catch (IOException e) {
			e.printStackTrace();
			assertNotNull(reader.getNetworks().get(0));
			return;
		}
		
		//If not caught by the above, something is wrong!
		fail();
	}

	public void testMultipleFiles() throws Exception {
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader1 = new NNFReader(FILE_LOCATION + "good3.nnf");
		reader1.read();

		final NNFReader reader2 = new NNFReader(FILE_LOCATION + "good4.nnf");
		reader2.read();
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		CyNetwork targetNetwork = null;
		for (CyNetwork net : networks) {
			if (net.getTitle().equals("M3")) {
				targetNetwork = net;
			}
		}
		
		assertNotNull(targetNetwork);
		assertEquals("M3", targetNetwork.getTitle());
		assertEquals(6, targetNetwork.getNodeCount());
		assertEquals(4, targetNetwork.getEdgeCount());
	}
}
