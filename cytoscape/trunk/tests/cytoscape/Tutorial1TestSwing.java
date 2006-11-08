package cytoscape;

import java.awt.Robot;
import java.util.*;
import javax.swing.*;

import swingunit.extensions.ExtendedRobotEventFactory;
import swingunit.framework.EventPlayer;
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.RobotEventFactory;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;
import junit.framework.TestCase;

import cytoscape.*;
import cytoscape.view.*;
import giny.model.*; 
import giny.view.*; 

public class Tutorial1TestSwing extends TestCase {
	private Scenario scenario;
	private RobotEventFactory robotEventFactory = new ExtendedRobotEventFactory();
	private FinderMethodSet methodSet = new FinderMethodSet();
	private Robot robot;
	
	private CyMain application;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		System.setProperty("TestSetting","testData/TestSetting.properties");

		// Start application.
		Runnable r = new Runnable() {
			public void run() {
				try {
					String[] args = {"-p", "plugins/core"};
					application = new CyMain(args);
				} catch (Exception e) { e.printStackTrace(); }
			}
		};
		SwingUtilities.invokeAndWait(r);

		robot = new Robot();
		TestUtility.waitForCalm();
		
		// To make sure to load the scenario file. 
		// CytoscapeTestSwing.xml is placed on the same package directory.
		String filePath = CytoscapeTestSwing.class.getResource("CytoscapeSwingUnitOperations.xml").getFile();
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

	public void testTutorialOne() throws ExecuteException {
		EventPlayer player = new EventPlayer(scenario);

		// only do this because the attr browser takes up the whole
		// screen in this mode
		player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

		// open net
		scenario.setTestSetting("IMPORT_NETWORK_FILE","FILE_TO_IMPORT","RUAL.subset.sif");
		scenario.setTestSetting("IMPORT_NETWORK_FILE","IMPORT_DIR","testData");
		player.run(robot, "IMPORT_NETWORK_FILE");
		assertEquals("num networks (including ontology root)",2,Cytoscape.getNetworkSet().size());

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		assertNotNull("current network view",view);

		player.run(robot, "APPLY_SPRING_LAYOUT");
		// some test for layout?

		scenario.setTestSetting("SELECT_NODE_BY_NAME","NODE_NAME","7157");
		player.run(robot, "SELECT_NODE_BY_NAME");

		List selNodes = view.getSelectedNodes();
		assertEquals("num selected nodes",1,selNodes.size());
		assertEquals("node id","7157",((NodeView)selNodes.get(0)).getNode().getIdentifier());
		
		player.run(robot, "SELECT_FIRST_NEIGHBORS");
		selNodes = view.getSelectedNodes();
		assertEquals("num selected neighbor nodes",64,selNodes.size());

		player.run(robot, "NEW_NETWORK_FROM_SELECTED_NODES_ALL_EDGES");
		assertEquals("num networks (including ontology root)",3,Cytoscape.getNetworkSet().size());

		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES","FILE_TO_IMPORT","RUAL.na");
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES","IMPORT_DIR","testData");
		player.run(robot, "IMPORT_NODE_ATTRIBUTES");
		assertEquals("node attr","TP53",
			Cytoscape.getNodeAttributes().getStringAttribute("7157","Official HUGO Symbol"));
		assertEquals("node attr","GORASP2",
			Cytoscape.getNodeAttributes().getStringAttribute("26003","Official HUGO Symbol"));
		assertEquals("node attr","RUFY1",
			Cytoscape.getNodeAttributes().getStringAttribute("80230","Official HUGO Symbol"));

		scenario.setTestSetting("PAUSE","DURATION","2000");
		player.run(robot, "PAUSE");

		player.run(robot, "OPEN_VIZMAPPER");
	}
	
}
