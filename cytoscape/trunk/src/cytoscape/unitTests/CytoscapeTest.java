package cytoscape.unitTests;


import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.ImportHandler;

public class CytoscapeTest extends TestCase {
	CyNetwork cytoNetwork;
	String title;
	int nodeCount;
	int edgeCount;

	public void setUp() throws Exception {
	}
	
	public void tearDown() throws Exception {
	}

	public void testGetImportHandler() throws Exception {
		ImportHandler importHandler = Cytoscape.getImportHandler();
		assertEquals(importHandler.getClass(), ImportHandler.class);
	}
	
	public void testNullNetwork() throws Exception {	
		cytoNetwork = Cytoscape.getNullNetwork();
		
		title = cytoNetwork.getTitle();
		assertEquals("0", title);
		
		nodeCount = cytoNetwork.getNodeCount();
		assertEquals(0, nodeCount);
		
		edgeCount = cytoNetwork.getEdgeCount();
		assertEquals(0, edgeCount);	
	}
	
	//public void test
	//try getting network attributes
	
	//try creating a network
	public void testCreateNetwork() throws Exception {
		cytoNetwork = Cytoscape.createNetworkFromFile("testNetwork");
		
		/*
		 * Network title is unpredictable! 
		 */
//		title = cytoNetwork.getTitle();
//		assertEquals("20", title);
		
		nodeCount = cytoNetwork.getNodeCount();
		assertEquals(0, nodeCount);
		
		edgeCount = cytoNetwork.getEdgeCount();
		assertEquals(0, edgeCount);
	}
	
	
}