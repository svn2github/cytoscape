/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.performance;

import cytoscape.*;

import cytoscape.performance.track.*;

import cytoscape.performance.ui.*;

import giny.model.*;

import giny.view.*;

import junit.framework.*;

import swingunit.extensions.ExtendedRobotEventFactory;

import swingunit.framework.EventPlayer;
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.RobotEventFactory;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;

import java.awt.Robot;

import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * Swing unit tests used to test performance.
 */
public class RunCytoscape extends TestCase {
	private Scenario scenario;
	private RobotEventFactory robotEventFactory = new ExtendedRobotEventFactory();
	private FinderMethodSet methodSet = new FinderMethodSet();
	private Robot robot;
	private EventPlayer player;
	private CyMain application;
	protected int totalNodesHidden = 0;
	protected int totalEdgesHidden = 0;
	private static String[] args;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		RunCytoscape.args = args;
		junit.textui.TestRunner.run(new TestSuite(RunCytoscape.class));

		FileUI fu = new FileUI(Tracker.getEvents(), System.getProperty("cytoscape.dir"),
		                       System.getProperty("perf.version"));
		fu.dumpResults();
		System.exit(0);
	}

	protected void setUp() throws Exception {
		System.setProperty("TestSetting", "testData/TestSetting.properties");

		// Start application.
		Runnable r = new Runnable() {
			public void run() {
				try {
					application = new CyMain(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		SwingUtilities.invokeAndWait(r);

		robot = new Robot();
		TestUtility.waitForCalm();
		System.out.println("Starting RunCytoscape for version: " + CytoscapeVersion.version
		                   + " and perf.version = " + System.getProperty("perf.version"));

		// To make sure to load the scenario file.
		// CytoscapeTestSwing.xml is placed on the same package directory.
		URL fileURL = getOps();

		// Create Scenario object and create XML file.
		scenario = new Scenario(robotEventFactory, methodSet);
		scenario.read(fileURL.toString());

		// This is necessary since SwingUnit does not support some Look & Feel.
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		player = new EventPlayer(scenario);
	}

	protected void tearDown() throws Exception {
		application = null;
		scenario = null;
		robot = null;
		player = null;
	}

	/**
	 * Run through a (large) variety of tasks so that we can profile the app.
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
	public void testSpeed()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException,
	               ClassNotFoundException, InstantiationException, IllegalAccessException,
	               UnsupportedLookAndFeelException {
		scenario.setTestSetting("PAUSE", "DURATION", "1000");
		player.run(robot, "PAUSE");

		player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

		filterCheck();
		zoomInAndOutCheck();
		loadNetworks();
		loadAttributes();
		loadVizMap();
		exportNetworks();
		saveSession();
		restoreSessionFromFile();
		//selectCheck();
		layoutCheck();
	}

	/**
	 * Loading all kinds of network files supported in Cytoscape by default.
	 *
	 * This test needs network connection.
	 *
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 * @throws ExecuteException
	 */
	private void loadNetworks()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("loadNetworks start");

		// Part 1: Load multiple networks from multiple sources.
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");

		// XGMML
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "galFiltered.xgmml");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// SIF
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "yeastHighQuality.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// GML
		scenario.setTestSetting("IMPORT_GML_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_GML_FILE", "FILE_TO_IMPORT", "galFiltered.gml");
		player.run(robot, "IMPORT_GML_FILE");

		// LARGE SIF
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "BINDyeast.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// CHECK LOADING OVER NETWORK FOR VERSIONS NEWER THAN 2.3
		String version = CytoscapeVersion.version;
		String perfVersion = System.getProperty("perf.version");

		if (!(perfVersion.equalsIgnoreCase("2.3.2") || version.matches("[2.3*]"))) {
			// Remote SBML file
			scenario.setTestSetting("IMPORT_REMOTE_NETWORK_FILE", "REMOTE_NETWORK_FILE",
			                        "http://www.reactome.org/cgi-bin/sbml_export?DB=gk_current&ID=73894");
			player.run(robot, "IMPORT_REMOTE_NETWORK_FILE");
		}

		System.out.println("loadNetworks stop");
	}

	/**
	 * Load multiple attributes for loaded network.
	 *
	 * @throws ExecuteException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void loadAttributes()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("loadAttributes start");

		// Load node attributes
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "FILE_TO_IMPORT", "RUAL.na");
		player.run(robot, "IMPORT_NODE_ATTRIBUTES");

		// Load edge attributes
		scenario.setTestSetting("IMPORT_EDGE_ATTRIBUTES", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_EDGE_ATTRIBUTES", "FILE_TO_IMPORT", "Edge String Attr.ea");
		player.run(robot, "IMPORT_EDGE_ATTRIBUTES");

		// Load Expression Matrix
		scenario.setTestSetting("IMPORT_EXPRESSION_MATRIX", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_EXPRESSION_MATRIX", "MATRIX_FILE_NAME", "galExpData.pvals");
		player.run(robot, "IMPORT_EXPRESSION_MATRIX");

		// Will be implemented near future...
		System.out.println("loadAttributes stop");
	}

	/**
	 * Load a vizmap file
	 *
	 * @param vizmapFileName
	 */
	private void loadVizMap()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("loadVizMap start");

		final String vizmapFileName = "swingTestVisual.props";

		scenario.setTestSetting("IMPORT_VIZMAP", "FILE_TO_IMPORT", vizmapFileName);
		scenario.setTestSetting("IMPORT_VIZMAP", "IMPORT_DIR", "testData");
		player.run(robot, "IMPORT_VIZMAP");

		System.out.println("loadVizMap stop");
	}

	/**
	 * Save a session file.
	 *
	 * @param sessionFileName
	 */
	private void saveSession()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("saveSession start");

		final String sessionFileName = "testResult.cys";

		scenario.setTestSetting("SAVE_SESSION", "SESSION_FILE_NAME", sessionFileName);
		player.run(robot, "SAVE_SESSION");

		System.out.println("saveSession stop");
	}

	/**
	 * Restore a session file.
	 *
	 * @param sessionFileName
	 */
	private void restoreSessionFromFile()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("restoreSessionFromFile start");

		final String sessionFileName = "testResult.cys";

		scenario.setTestSetting("OPEN_SESSION", "SESSION_FILE_NAME", sessionFileName);
		scenario.setTestSetting("OPEN_SESSION", "SESSION_DIR", "testData");

		player.run(robot, "OPEN_SESSION");

		System.out.println("restoreSessionFromFile stop");
	}

	/**
	 * Export networks.
	 *
	 * @param exportFileName
	 */
	private void exportNetworks()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("export network and attributes start");

		final String exportFileName = "network_export";

		// We need a semi-large file with a view as current network so that export
		// will take more than the 100ms needed to achieve popup.
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "yeastHighQuality.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		scenario.setTestSetting("EXPORT_AS_XGMML_FILE", "EXPORT_FILE_NAME",
		                        exportFileName + ".xgmml");
		player.run(robot, "EXPORT_AS_XGMML_FILE");
		/*
		        scenario.setTestSetting("EXPORT_AS_SIF_FILE", "EXPORT_FILE_NAME", exportFileName + ".sif");
		        player.run(robot, "EXPORT_AS_SIF_FILE");
		
		        scenario.setTestSetting("EXPORT_AS_GML_FILE", "EXPORT_FILE_NAME", exportFileName + ".gml");
		        player.run(robot, "EXPORT_AS_GML_FILE");
		*/
		System.out.println("export network and attributes stop");
	}

	/**
	 * Tests filter plugin facilities
	 *
	 * @param player
	 * @throws ExecuteException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void filterCheck()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("filterNetworks start");

		String fileName = "RUAL.subset.sif";
		// load network
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", fileName);
		player.run(robot, "IMPORT_NETWORK_FILE");
		// test first filter
		scenario.setTestSetting("TEST_FILTER_1", "FILE_NAME", fileName);
		player.run(robot, "TEST_FILTER_1");
		System.out.println("filterNetworks stop");
	}

	/**
	 * Tests zooming, panning, fit to screen
	 *
	 * @param player
	 * @throws ExecuteException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void zoomInAndOutCheck()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("zoomInAndOut start");

		String fileName = "galFiltered.sif";
		// load network
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", fileName);
		player.run(robot, "IMPORT_NETWORK_FILE");

		// zoomout=2 zoomin=3 focusselected=4 showall=5
		// click showall
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "FILE_NAME", fileName);
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "BUTTON_NUMBER", "5");
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "NR_OF_CLICKS", "1");
		player.run(robot, "ZOOM_IN_AND_OUT");
		// click zoom in 5 times
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "FILE_NAME", fileName);
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "BUTTON_NUMBER", "3");
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "NR_OF_CLICKS", "5");
		player.run(robot, "ZOOM_IN_AND_OUT");
		// TODO: check # of visible nodes smaller (getZoom doesnt really test this....)
		// click zoom out 5 times
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "FILE_NAME", fileName);
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "BUTTON_NUMBER", "2");
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "NR_OF_CLICKS", "5");
		player.run(robot, "ZOOM_IN_AND_OUT");
		// select nodes and click show selected
		scenario.setTestSetting("SELECT_NODE_BY_NAME", "NODE_NAME", "YJL157C");
		player.run(robot, "SELECT_NODE_BY_NAME");
		// select first neighbors 
		player.run(robot, "SELECT_FIRST_NEIGHBORS");
		// click focusselected
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "FILE_NAME", fileName);
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "BUTTON_NUMBER", "4");
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "NR_OF_CLICKS", "1");
		player.run(robot, "ZOOM_IN_AND_OUT");
		// click show all
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "FILE_NAME", fileName);
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "BUTTON_NUMBER", "5");
		scenario.setTestSetting("ZOOM_IN_AND_OUT", "NR_OF_CLICKS", "1");
		player.run(robot, "ZOOM_IN_AND_OUT");
		System.out.println("zoomInAndOut stop");
	}

	/**
	 * This test exercises the functionality of the Select menu.
	 */
	private void selectCheck()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException,
	               ClassNotFoundException, InstantiationException, IllegalAccessException,
	               UnsupportedLookAndFeelException {
		System.out.println("selectCheck start");

		// Import network
		scenario.setTestSetting("PAUSE", "DURATION", "1000");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "galFiltered.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		CyNetwork curr = Cytoscape.getCurrentNetwork();

		//
		// first do nodes
		//

		// select all nodes
		player.run(robot, "SELECT_ALL_NODES");
		assertEquals("all nodes selected", 331, curr.getSelectedNodes().size());

		// deselect all nodes
		player.run(robot, "DESELECT_ALL_NODES");
		assertEquals("all nodes deselected", 0, curr.getSelectedNodes().size());

		// select by name
		scenario.setTestSetting("SELECT_NODE_BY_NAME", "NODE_NAME", "YPL248C");
		player.run(robot, "SELECT_NODE_BY_NAME");
		assertEquals("select node YPL248C", 1, curr.getSelectedNodes().size());

		// select first neighbors
		player.run(robot, "SELECT_FIRST_NEIGHBORS");
		assertEquals("select first neighborts of YPL248C", 11, curr.getSelectedNodes().size());

		// deselect all nodes
		player.run(robot, "DESELECT_ALL_NODES");
		assertEquals("all nodes deselected", 0, curr.getSelectedNodes().size());

		// select from file
		scenario.setTestSetting("SELECT_NODES_FROM_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("SELECT_NODES_FROM_FILE", "FILE_TO_IMPORT",
		                        "galFiltered.select.from.file.txt");
		player.run(robot, "SELECT_NODES_FROM_FILE");
		assertEquals("select nodes from file galFiltered.select.from.file.txt", 10,
		             curr.getSelectedNodes().size());

		// set up listener to test node and edge hiding graph change events
		Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(new GraphViewChangeListener() {
				public void graphViewChanged(GraphViewChangeEvent event) {
					if (event.isNodesHiddenType()) {
						// System.out.println("node");
						int[] hidden = event.getHiddenNodeIndices();
						// for ( int asdf : hidden )
						// System.out.println(" " + asdf);
						totalNodesHidden += hidden.length;
					}

					if (event.isEdgesHiddenType()) {
						// System.out.println("edge");
						int[] hidden = event.getHiddenEdgeIndices();
						// for ( int asdf : hidden )
						// System.out.println(" " + asdf);
						totalEdgesHidden += hidden.length;
					}
				}
			});

		// hide selection
		totalNodesHidden = 0;
		totalEdgesHidden = 0;
		System.out.println("about to hide nodes");
		player.run(robot, "HIDE_SELECTED_NODES");
		player.run(robot, "PAUSE"); // ???
		assertEquals("num nodes hidden", 10, totalNodesHidden);
		assertEquals("num edges hidden", 15, totalEdgesHidden);

		// show all nodes
		System.out.println("about to show nodes");
		player.run(robot, "SHOW_ALL_NODES");
		player.run(robot, "PAUSE"); // ???

		// select from file
		scenario.setTestSetting("SELECT_NODES_FROM_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("SELECT_NODES_FROM_FILE", "FILE_TO_IMPORT",
		                        "galFiltered.select.from.file.txt");
		player.run(robot, "SELECT_NODES_FROM_FILE");
		assertEquals("select nodes from file galFiltered.select.from.file.txt", 10,
		             curr.getSelectedNodes().size());

		// invert selection
		player.run(robot, "INVERT_SELECTED_NODES");
		assertEquals("invert selected nodes", 321, curr.getSelectedNodes().size());

		// deselect all nodes
		player.run(robot, "DESELECT_ALL_NODES");
		assertEquals("all nodes deselected", 0, curr.getSelectedNodes().size());

		//
		// now do edges
		//

		// select all edges
		player.run(robot, "SELECT_ALL_EDGES");
		assertEquals("all edges selected", 362, curr.getSelectedEdges().size());

		// deselect all edges
		player.run(robot, "DESELECT_ALL_EDGES");
		assertEquals("all edges deselected", 0, curr.getSelectedEdges().size());

		// select all edges
		player.run(robot, "SELECT_ALL_EDGES");
		assertEquals("all edges selected", 362, curr.getSelectedEdges().size());

		// invert selection
		player.run(robot, "INVERT_SELECTED_EDGES");
		assertEquals("invert selected edges", 0, curr.getSelectedEdges().size());

		// invert selection
		player.run(robot, "INVERT_SELECTED_EDGES");
		assertEquals("invert selected edges", 362, curr.getSelectedEdges().size());

		// hide selection
		totalNodesHidden = 0;
		totalEdgesHidden = 0;
		System.out.println("about to hide edges");
		player.run(robot, "HIDE_SELECTED_EDGES");
		player.run(robot, "PAUSE"); // ???
		assertEquals("num nodes hidden", 0, totalNodesHidden);
		assertEquals("num edges hidden", 362, totalEdgesHidden);

		// show all
		System.out.println("about to show edges");
		player.run(robot, "SHOW_ALL_EDGES");
		player.run(robot, "PAUSE"); // ???

		player.run(robot, "DESELECT_ALL_EDGES");
		assertEquals("all edges deselected", 0, curr.getSelectedEdges().size());

		//
		// test de/select all
		//
		player.run(robot, "SELECT_ALL_NODES_AND_EDGES");
		assertEquals("all edges selected", 362, curr.getSelectedEdges().size());
		assertEquals("all nodes selected", 331, curr.getSelectedNodes().size());

		player.run(robot, "DESELECT_ALL_NODES_AND_EDGES");
		assertEquals("all edges deselected", 0, curr.getSelectedEdges().size());
		assertEquals("all nodes deselected", 0, curr.getSelectedNodes().size());

		System.out.println("selectCheck stop");
	}

	/**
	 * This test exercises the functionality of the Layout menu.
	 */
	private void layoutCheck()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException,
	               ClassNotFoundException, InstantiationException, IllegalAccessException,
	               UnsupportedLookAndFeelException {
		System.out.println("layoutCheck start");

		// Import network 
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "galFiltered.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		player.run(robot, "APPLY_CY_HEIRARCHICAL_LAYOUT");
		player.run(robot, "APPLY_CY_SPRING_LAYOUT");

		System.out.println("layoutCheck stop");
	}

	private URL getOps() {
		String version = CytoscapeVersion.version;
		String perfVersion = System.getProperty("perf.version");

		URL ret;

		// temp workaround; version in trunk is 2.4.0....
		if ("current".equalsIgnoreCase(perfVersion)) {
			ret = ClassLoader.getSystemResource("cytoscape/performance/Cyto_current_ops.xml");
			System.out.println("got:  " + version + "; current from svn will be run");
		} else if ("2.3.2".equalsIgnoreCase(perfVersion) || version.matches("2.3")) {
			ret = ClassLoader.getSystemResource("cytoscape/performance/Cyto_2.3_ops.xml");
			System.out.println("got 2.3:  " + version);
		} else if ("2.4.0".equalsIgnoreCase(perfVersion) || version.matches("2.4")) {
			ret = ClassLoader.getSystemResource("cytoscape/performance/Cyto_2.4_ops.xml");
			System.out.println("got 2.4:  " + version);
		} else { // current from svn
			ret = ClassLoader.getSystemResource("cytoscape/performance/Cyto_current_ops.xml");
			System.out.println("got:  " + version + "; current from svn will be run");
		}

		return ret;
	}
}
