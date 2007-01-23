package cytoscape.data.readers;

//--------------------------------------------------------------------------------------
import cytoscape.view.NetworkViewManager;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import cytoscape.AllTests;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.dialogs.*;
import cytoscape.data.readers.GraphReader;
import cytoscape.view.NetworkPanel;

//-----------------------------------------------------------------------------------------
public class FileImporterTest extends TestCase {
	String location;
	String title;
	int nodeCount;
	int edgeCount;
	CyNetwork network;
//	VisualStyleBuilderDialog vsd;
	
	// ------------------------------------------------------------------------------
	public void setUp() throws Exception {
	}

	// ------------------------------------------------------------------------------
	public void tearDown() throws Exception {
	}

	// ------------------------------------------------------------------------------
	public void testGMLImport() throws Exception {
		location = "testData/gal.gml";
		network = Cytoscape.createNetworkFromFile(location);
		
		title = network.getTitle();
		assertEquals("gal.gml", title);
		
		nodeCount = network.getNodeCount();
		assertEquals("number of nodes", 11, nodeCount);
		
		edgeCount = network.getEdgeCount();
		assertEquals("number of edges", 10, edgeCount);
		
	}
	
	public void testXGMMLImport() throws Exception {
		location = "testData/galFiltered2.xgmml";
		network = Cytoscape.createNetworkFromFile(location, false);
		
		title = network.getTitle();
		assertEquals("GAL Filtered (Yeast)", title);
		
		nodeCount = network.getNodeCount();
		assertEquals("num nodes", 331, nodeCount);
		
		edgeCount = network.getEdgeCount();
		assertEquals("num edges", 362, edgeCount);
	

	}
	
	public void testSIFImport() throws Exception {
		location = "testData/galFiltered.sif";
		network = Cytoscape.createNetworkFromFile(location, false);
		
		title = network.getTitle();
		assertEquals("galFiltered.sif", title);
		
		nodeCount = network.getNodeCount();
		assertEquals("num nodes", 331, nodeCount);
		
		edgeCount = network.getEdgeCount();
		assertEquals("num edge",362, edgeCount);
	
	}
	
	//test sessions importer ?
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(FileImporterTest.class);
	}
	
}
	
