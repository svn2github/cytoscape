package cytoscape.data.readers;

import java.io.File;

import giny.model.RootGraph;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import cytoscape.Cytoscape;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.AllTests;

public class XGMMLReaderTest extends TestCase {
	
	private static String testDataDir;
	
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out
					.println("Error!  must supply path to test data directory on command line");
			Cytoscape.exit(0);
		}

		testDataDir = args[0];

		junit.textui.TestRunner.run(new TestSuite(XGMMLReaderTest.class));
	}


	public XGMMLReaderTest(String arg0) {
		super(arg0);
		if (AllTests.runAllTests()) {
			testDataDir = "testData";
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testXGMMLGraphRead() throws Exception {
		AllTests.standardOut("testXGMMLGraphRead");
		XGMMLReader reader = new XGMMLReader("testData/galFiltered2.xgmml");
		
		File testfile = new File("testData/galFiltered2.xgmml");
		if(testfile.canRead()) {
			System.out.println("Reading XGMML: " + testfile.getAbsolutePath());
			RootGraph network = reader.getRootGraph();
			network.removeNodes(network.nodesList());
			reader.read();
			
			
			if(network == null) {
				System.out.println("Root Graph is null!");
				return;
			}
			System.out.println("XGMMLReader: Node count = " + network.getNodeCount());
			System.out.println("XGMMLReader: Edge count = " + network.getEdgeCount());
			
			assertTrue("XGMMLReader: Node count, expect 331, got " + network.getNodeCount(),
					network.getNodeCount() == 331);
			assertTrue("XGMMLReader: Edge count, expect 362, got " + network.getEdgeCount(),
					network.getEdgeCount() == 362);
			
		} else {
			System.out.println("No such file");
		}
		

	} // testGraphRead
	
	/* Too large?

	public void testXGMMLHugeGraphRead() throws Exception {
		AllTests.standardOut("testXGMMLHugeGraphRead");
		XGMMLReader reader = new XGMMLReader("testData/BINDyeast.xgmml");
		
		File testfile = new File("testData/BINDyeast.xgmml");
		if(testfile.canRead()) {
			System.out.println("Reading XGMML: " + testfile.getAbsolutePath());
			RootGraph network = reader.getRootGraph();
			network.removeNodes(network.nodesList());
			reader.read();
			
			
			if(network == null) {
				System.out.println("Root Graph is null!");
				return;
			}
			System.out.println("XGMMLReader: Node count = " + network.getNodeCount());
			System.out.println("XGMMLReader: Edge count = " + network.getEdgeCount());
			
			assertTrue("XGMMLReader: Node count, expect 23505, got " + network.getNodeCount(),
					network.getNodeCount() == 23505);
			assertTrue("XGMMLReader: Edge count, expect 60457, got " + network.getEdgeCount(),
					network.getEdgeCount() == 60457);
		} else {
			System.out.println("No such file");
		}
		

	} // testXGMMLHugeGraphRead
	
	*/
}
