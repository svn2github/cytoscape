package cytoscape;

import giny.view.NodeView;

import java.awt.Robot;
import java.util.List;

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
import cytoscape.view.CyNetworkView;

/**
 * 
 * Swing Unite Tests based on Cytoscape tutorial section 1 and 2
 * 
 * @since Cytoscape 2.4
 * @version 0.5
 * @author kono, pwang
 *
 */
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
		System.setProperty("TestSetting", "testData/TestSetting.properties");

		// Start application.
		Runnable r = new Runnable() {
			public void run() {
				try {
					String[] args = { "-p", "plugins/core" };
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
		String filePath = CytoscapeTestSwing.class.getResource(
				"CytoscapeSwingUnitOperations.xml").getFile();
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

	public void testTutorialOne() throws ExecuteException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		/*
		 * This is necessary since SwingUnit does not support some Look & Feel.
		 */
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		final EventPlayer player = new EventPlayer(scenario);

		/*
		 * only do this because the attr browser takes up the whole screen in
		 * this mode
		 */
		player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

		/*
		 * Load remote file from tutorial page.
		 */
		player.run(robot, "IMPORT_REMOTE_SIF_FILE");

		// Check some values
		assertEquals(1, Cytoscape.getNetworkSet().size());

		final CyNetworkView view = Cytoscape.getCurrentNetworkView();
		assertNotNull(view);

		/*
		 * Do Layout
		 */
		player.run(robot, "APPLY_SPRING_LAYOUT");

		scenario.setTestSetting("SELECT_NODE_BY_NAME", "NODE_NAME", "7157");
		player.run(robot, "SELECT_NODE_BY_NAME");

		List selNodes = view.getSelectedNodes();
		assertEquals("num selected nodes", 1, selNodes.size());
		assertEquals("node id", "7157", ((NodeView) selNodes.get(0)).getNode()
				.getIdentifier());

		player.run(robot, "SELECT_FIRST_NEIGHBORS");
		selNodes = view.getSelectedNodes();
		assertEquals("num selected neighbor nodes", 64, selNodes.size());

		player.run(robot, "NEW_NETWORK_FROM_SELECTED_NODES_ALL_EDGES");
		assertEquals("Number of networks in this session", 2, Cytoscape
				.getNetworkSet().size());

		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "FILE_TO_IMPORT",
				"RUAL.na");
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "IMPORT_DIR",
				"testData");
		player.run(robot, "IMPORT_NODE_ATTRIBUTES");
		assertEquals("node attr", "TP53", Cytoscape.getNodeAttributes()
				.getStringAttribute("7157", "Official HUGO Symbol"));
		assertEquals("node attr", "GORASP2", Cytoscape.getNodeAttributes()
				.getStringAttribute("26003", "Official HUGO Symbol"));
		assertEquals("node attr", "RUFY1", Cytoscape.getNodeAttributes()
				.getStringAttribute("80230", "Official HUGO Symbol"));

		player.run(robot, "SET_VIZMAPPER");

		/*
		 * Do Layout for child network
		 */
		player.run(robot, "APPLY_SPRING_LAYOUT");

		player.run(robot, "ZOOM_IN_AND_OUT");

		player.run(robot, "TEST_FILTER_1");

		assertEquals("Number of Selected Edges", 660, Cytoscape
				.getCurrentNetwork().getSelectedEdges().size());

		player.run(robot, "DELETE_SELECTED_EDGES");

		//player.run(robot, "TEST_FILTER_2");

	}

}
