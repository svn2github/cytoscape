package cytoscape.data.readers.unitTests;

//--------------------------------------------------------------------------------------
import cytoscape.view.NetworkViewManager;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import cytoscape.unitTests.AllTests;
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
		assertEquals(11, nodeCount);
		
		edgeCount = network.getEdgeCount();
		assertEquals(10, edgeCount);
		
		//test visual style builder
		/*vsd = new VisualStyleBuilderDialog(
				network.getTitle(), 
				(GraphReader)network.getClientData(Cytoscape.READER_CLIENT_KEY), 
				Cytoscape.getDesktop(),
				true);
		
		assertEquals("Visual Style Builder", vsd.getTitle());*/

	}
	
	public void testXGMMLImport() throws Exception {
		location = "testData/galFiltered2.xgmml";
		network = Cytoscape.createNetworkFromFile(location, false);
		
		title = network.getTitle();
		assertEquals("GAL Filtered (Yeast)", title);
		
		/*title = Cytoscape.getDesktop().getNetworkPanel().getTreeTableModel()
				.getValueAt(Cytoscape.getDesktop().getNetworkPanel().getTreeTable().getTree()
				.getSelectionPath().getLastPathComponent(), 0).toString();
		assertEquals("GAL Filtered (Yeast)", title);*/
		
		nodeCount = network.getNodeCount();
		assertEquals(331, nodeCount);
		
		edgeCount = network.getEdgeCount();
		assertEquals(362, edgeCount);
	

	}
	
	public void testSIFImport() throws Exception {
		location = "testData/galFiltered.sif";
		network = Cytoscape.createNetworkFromFile(location, false);
		
		title = network.getTitle();
		assertEquals("galFiltered.sif", title);
		
		nodeCount = network.getNodeCount();
		assertEquals(331, nodeCount);
		
		edgeCount = network.getEdgeCount();
		assertEquals(362, edgeCount);
	
	}
	
	//test sessions importer ?
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(FileImporterTest.class);
	}
	
}
	
