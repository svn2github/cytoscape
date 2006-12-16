package cytoscape;

import java.awt.Robot;

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

/**
 * 
 * Swing unit tests.
 * 
 * @version 0.5
 * @Since Cytoscape 2.3
 * @author mes, kono
 * 
 */
public class CytoscapeTestSwing extends TestCase {
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

	/**
	 * Purpose of this test is try every menu item. May take a very long time to
	 * finish!
	 * 
	 * @throws ExecuteException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * 
	 */
	public void testExtreme() throws IllegalStateException,
			IllegalArgumentException, ExecuteException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

		/*
		 * This is necessary since SwingUnit does not support some Look & Feel.
		 */
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		final EventPlayer player = new EventPlayer(scenario);

		/*
		 * Part 0: Bring up the window (for safety)
		 */

		player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

		loadNetworks(player);
		loadAttributes(player);
		importVizMap(player, "swingTestVisual.props");
		saveSession(player, "testResult.cys");
		restoreSessionFromFile(player, "testResult.cys");

		// scenario.setTestSetting("PAUSE", "DURATION", "3000");
		// player.run(robot, "PAUSE");
		player.run(robot, "QUIT_CYTOSCAPE");

	}

	private void loadNetworks(final EventPlayer player)
			throws IllegalStateException, IllegalArgumentException,
			ExecuteException {
		/*
		 * Part 1: Load multiple networks from multiple sources.
		 */

		// XGMML
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT",
				"galFiltered.xgmml");

		player.run(robot, "IMPORT_NETWORK_FILE");

		// SIF
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT",
				"yeastHighQuality.sif");

		player.run(robot, "IMPORT_NETWORK_FILE");

		// GML
		scenario.setTestSetting("IMPORT_GML_FILE", "FILE_TO_IMPORT",
				"galFiltered.gml");

		player.run(robot, "IMPORT_GML_FILE");

		// PSI-MI
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT",
				"BIOGRID-Mouse.psi25.xml");

		player.run(robot, "IMPORT_NETWORK_FILE");

		// Local BioPAX file
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT",
				"bca00030.owl");

		player.run(robot, "IMPORT_NETWORK_FILE");

		// Remote SBML file
		scenario
				.setTestSetting("IMPORT_REMOTE_NETWORK_FILE",
						"REMORE_NETWORK_FILE",
						"http://www.reactome.org/cgi-bin/sbml_export?DB=gk_current&ID=73894");

		player.run(robot, "IMPORT_REMOTE_NETWORK_FILE");

		// Remote SIF from Bookmark (RUAL.sif)
		scenario.setTestSetting("IMPORT_REMOTE_NETWORK_FILE_FROM_BOOKMARK",
				"BOOKMARK_INDEX", "3");

		player.run(robot, "IMPORT_REMOTE_NETWORK_FILE_FROM_BOOKMARK");

	}

	/**
	 * Load multiple attributes for loaded network.
	 * 
	 * @param player
	 * @throws ExecuteException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void loadAttributes(final EventPlayer player)
			throws IllegalStateException, IllegalArgumentException,
			ExecuteException {

		/*
		 * Load node attributes
		 */
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "FILE_TO_IMPORT",
				"RUAL.na");
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "IMPORT_DIR",
				"testData");

		player.run(robot, "IMPORT_NODE_ATTRIBUTES");

		/*
		 * Load edge attributes
		 */
		scenario.setTestSetting("IMPORT_EDGE_ATTRIBUTES", "FILE_TO_IMPORT",
				"Edge String Attr.ea");
		scenario.setTestSetting("IMPORT_EDGE_ATTRIBUTES", "IMPORT_DIR",
				"testData");

		player.run(robot, "IMPORT_EDGE_ATTRIBUTES");

		/*
		 * Load Expression Matrix
		 */
		scenario.setTestSetting("IMPORT_EXPRESSION_MATRIX", "MATRIX_FILE_NAME",
				"galExpData.pvals");
		scenario.setTestSetting("IMPORT_EXPRESSION_MATRIX", "IMPORT_DIR",
				"testData");

		player.run(robot, "IMPORT_EXPRESSION_MATRIX");

		/*
		 * Load network attributes
		 */
		// Will be implemented in near future...
	}

	/**
	 * Load Ontology (using TableImport plugin.)
	 * 
	 * @param player
	 */
	private void loadOntologyAndAnnotation(final EventPlayer player) {
		// Human

		// Yeast

		// Mouse

	}

	private void importVizMap(final EventPlayer player,
			final String vizmapFileName) throws IllegalStateException,
			IllegalArgumentException, ExecuteException {
		scenario.setTestSetting("IMPORT_VIZMAP", "FILE_TO_IMPORT",
				vizmapFileName);
		scenario.setTestSetting("IMPORT_VIZMAP", "IMPORT_DIR",
				"testData");
		player.run(robot, "IMPORT_VIZMAP");
	}

	private void saveSession(final EventPlayer player,
			final String sessionFileName) throws IllegalStateException,
			IllegalArgumentException, ExecuteException {
		scenario.setTestSetting("SAVE_SESSION", "SESSION_FILE_NAME",
				sessionFileName);

		player.run(robot, "SAVE_SESSION");
	}

	private void restoreSessionFromFile(final EventPlayer player,
			final String sessionFileName) throws IllegalStateException,
			IllegalArgumentException, ExecuteException {
		scenario.setTestSetting("OPEN_SESSION", "SESSION_FILE_NAME",
				sessionFileName);

		scenario.setTestSetting("OPEN_SESSION", "SESSION_DIR", "testData");

		player.run(robot, "OPEN_SESSION");
	}

	private void createNewNetworksFromExistingOnes() {

	}

}
