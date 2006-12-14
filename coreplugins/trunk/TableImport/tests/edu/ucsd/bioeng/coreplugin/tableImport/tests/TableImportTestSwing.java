package edu.ucsd.bioeng.coreplugin.tableImport.tests;

import java.awt.Robot;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import junit.framework.TestCase;
import swingunit.extensions.ExtendedRobotEventFactory;
import swingunit.framework.EventPlayer;
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.RobotEventFactory;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;
import cytoscape.CyMain;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import edu.ucsd.bioeng.coreplugin.tableImport.TableImportPlugin;

public class TableImportTestSwing extends TestCase {
	private Scenario scenario;
	private RobotEventFactory robotEventFactory = new ExtendedRobotEventFactory();
	private FinderMethodSet methodSet = new FinderMethodSet();
	private Robot robot;

	private CyMain application;
	
	public TableImportTestSwing(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		System.out.println("Running Swing Unite tests for Table Import...");
		
		System.setProperty("TestSetting", "testData/TestSetting.properties");

		// Start application.
		Runnable r = new Runnable() {
			public void run() {
				try {
					String[] args = { "-p", "./TableImport.jar" };
					application = new CyMain(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		SwingUtilities.invokeAndWait(r);

		robot = new Robot();
		TestUtility.waitForCalm();

		// To make sure to load the scenario file.
		// CytoscapeTestSwing.xml is placed on the same package directory.
		
//		System.out.println("Resource = " + TableImportTestSwing.class.getResource(
//		"TableImportSwingUnitOperations.xml"));
		
		String filePath = "tests/TableImportSwingUnitOperations.xml";
		// Create Scenario object and create XML file.
		scenario = new Scenario(robotEventFactory, methodSet);
		scenario.read(filePath);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		application = null;
		scenario = null;
		robot = null;
	}

	public void testTableImportPlugin() throws ExecuteException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		//SwingUtilities.updateComponentTreeUI(application);
		System.out.println("Running Swing Unite tests for Table Import...2");
		// Use keyword substitution.
//		scenario.setTestSetting("IMPORT_NETWORK_TABLE_FILE", "FILE_TO_OPEN",
//				"");
		EventPlayer player = new EventPlayer(scenario);
		player.run(robot, "IMPORT_NETWORK_EXCEL_FILE");
		player.run(robot, "IMPORT_NETWORK_TEXT_TABLE");
		player.run(robot, "IMPORT_EDGE_ATTRIBUTE_TEXT_FILE");
		player.run(robot, "IMPORT_GO_AND_GA");
		player.run(robot, "WRITE_SESSION");
		
		// write assertion code here.
		Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
		Set<String> networkTitles = new TreeSet<String>();
		for(CyNetwork net: networkSet) {
			networkTitles.add(net.getTitle());
		}
		assertTrue(networkTitles.contains("Sub Network of YeastHQ.sif"));
		assertTrue(networkTitles.contains("Gene Ontology Full"));
		assertEquals(4, networkSet.size());
		
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		final String[] edgeAttrNames = edgeAttr.getAttributeNames();
		final Set<String> edgeAttrNameSet = new TreeSet<String>();
		for(String name:edgeAttrNames ) {
			System.out.println("Edge Attr = " + name);
			edgeAttrNameSet.add(name);
		}
		assertTrue(edgeAttrNameSet.contains("List Attr 1"));
		assertEquals(5, edgeAttr.getListAttribute("YCR084C (pp) YCL067C", "List Attr 1").size());
		assertEquals("abcd12364", edgeAttr.getStringAttribute("YCR084C (pp) YCL067C", "String Attr 1"));
		
		assertTrue(edgeAttrNameSet.contains("Boolean Attr 1"));
	}
}
