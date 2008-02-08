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
package cytoscape;

import cytoscape.*;

import giny.view.*;

import junit.framework.TestCase;

import swingunit.extensions.ExtendedRobotEventFactory;

import swingunit.framework.EventPlayer;
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.RobotEventFactory;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;

import java.awt.Robot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


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
	private EventPlayer player;
	private CyMain application;
	protected int totalNodesHidden = 0;
	protected int totalEdgesHidden = 0;

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
		String filePath = CytoscapeTestSwing.class.getResource("CytoscapeSwingUnitOperations.xml")
		                                          .getFile();
		// Create Scenario object and create XML file.
		scenario = new Scenario(robotEventFactory, methodSet);
		scenario.read(filePath);

		/*
		 * This is necessary since SwingUnit does not support some Look & Feel.
		 */
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		player = new EventPlayer(scenario);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		application = null;
		scenario = null;
		robot = null;
		player = null;
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
	public void testFileMenu()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException,
	               ClassNotFoundException, InstantiationException, IllegalAccessException,
	               UnsupportedLookAndFeelException {
		scenario.setTestSetting("PAUSE", "DURATION", "5000");
		player.run(robot, "PAUSE");

		player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

		loadNetworks(player);
		loadAttributes(player);
		createNewNetworksFromExistingOnes(player);
		importVizMap(player, "swingTestVisual.props");
		exportNetworks(player, "network_export");
		saveSession(player, "testResult.cys");
		restoreSessionFromFile(player, "testResult.cys");
	}

	/**
	 * Loading all kinds of network files supported in Cytoscape by default.
	 *
	 * This test needs network connection.
	 *
	 * @param player
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 * @throws ExecuteException
	 */
	private void loadNetworks(final EventPlayer player)
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("loadNetworks start");

		/*
		 * Part 1: Load multiple networks from multiple sources.
		 */
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

		// PSI-MI
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "BIOGRID-Mouse.psi25.xml");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// Local BioPAX file
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "bca00030.owl");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// Remote SBML file
		scenario.setTestSetting("IMPORT_REMOTE_NETWORK_FILE", "REMORE_NETWORK_FILE",
		                        "http://www.reactome.org/cgi-bin/sbml_export?DB=gk_current&ID=73894");
		player.run(robot, "IMPORT_REMOTE_NETWORK_FILE");

		// Remote SIF from Bookmark (RUAL.sif)
		scenario.setTestSetting("IMPORT_REMOTE_NETWORK_FILE_FROM_BOOKMARK", "BOOKMARK_INDEX", "3");

		player.run(robot, "IMPORT_REMOTE_NETWORK_FILE_FROM_BOOKMARK");

		System.out.println("loadNetworks stop");
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
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("loadAttributes start");

		/*
		 * Load node attributes
		 */
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NODE_ATTRIBUTES", "FILE_TO_IMPORT", "RUAL.na");
		player.run(robot, "IMPORT_NODE_ATTRIBUTES");

		/*
		 * Load edge attributes
		 */
		scenario.setTestSetting("IMPORT_EDGE_ATTRIBUTES", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_EDGE_ATTRIBUTES", "FILE_TO_IMPORT", "Edge String Attr.ea");
		player.run(robot, "IMPORT_EDGE_ATTRIBUTES");

		/*
		 * Load Expression Matrix
		 */
		scenario.setTestSetting("IMPORT_EXPRESSION_MATRIX", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_EXPRESSION_MATRIX", "MATRIX_FILE_NAME", "galExpData.pvals");
		player.run(robot, "IMPORT_EXPRESSION_MATRIX");

		/*
		 * Load network attributes
		 */

		// Will be implemented near future...
		System.out.println("loadAttributes stop");
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

	private void importVizMap(final EventPlayer player, final String vizmapFileName)
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("importVizMap start");
		scenario.setTestSetting("IMPORT_VIZMAP", "FILE_TO_IMPORT", vizmapFileName);
		scenario.setTestSetting("IMPORT_VIZMAP", "IMPORT_DIR", "testData");
		player.run(robot, "IMPORT_VIZMAP");
		System.out.println("importVizMap stop");
	}

	private void saveSession(final EventPlayer player, final String sessionFileName)
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("saveSession start");
		scenario.setTestSetting("SAVE_SESSION", "SESSION_FILE_NAME", sessionFileName);
		player.run(robot, "SAVE_SESSION");
		System.out.println("saveSession stop");
	}

	private void restoreSessionFromFile(final EventPlayer player, final String sessionFileName)
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("restoreSessionFromFile start");
		scenario.setTestSetting("OPEN_SESSION", "SESSION_FILE_NAME", sessionFileName);
		scenario.setTestSetting("OPEN_SESSION", "SESSION_DIR", "testData");
		player.run(robot, "OPEN_SESSION");
		scenario.setTestSetting("PAUSE", "DURATION", "3000");
		player.run(robot, "PAUSE");
		System.out.println("restoreSessionFromFile stop");
	}

	private void exportNetworks(final EventPlayer player, final String exportFileName)
	    throws IllegalStateException, IllegalArgumentException, ExecuteException {
		System.out.println("export network and attributes start");

		scenario.setTestSetting("EXPORT_AS_XGMML_FILE", "EXPORT_FILE_NAME",
		                        exportFileName + ".xgmml");
		player.run(robot, "EXPORT_AS_XGMML_FILE");

		scenario.setTestSetting("EXPORT_AS_SIF_FILE", "EXPORT_FILE_NAME", exportFileName + ".sif");
		player.run(robot, "EXPORT_AS_SIF_FILE");

		scenario.setTestSetting("EXPORT_AS_GML_FILE", "EXPORT_FILE_NAME", exportFileName + ".gml");
		player.run(robot, "EXPORT_AS_GML_FILE");

		System.out.println("export network and attributes start stop");
	}

	private void createNewNetworksFromExistingOnes(final EventPlayer player)
	    throws ExecuteException {
		System.out.println("create new network from existing one start");
		// Open an existing network
		scenario.setTestSetting("PAUSE", "DURATION", "1000");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "galFiltered.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// select from file
		scenario.setTestSetting("PAUSE", "DURATION", "1000");
		scenario.setTestSetting("SELECT_NODES_FROM_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("SELECT_NODES_FROM_FILE", "FILE_TO_IMPORT",
		                        "galFiltered.select.from.file.txt");
		scenario.setTestSetting("PAUSE", "DURATION", "1000");
		player.run(robot, "SELECT_NODES_FROM_FILE");

		// store current selection
		Set nodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
		HashSet<Edge> edges = new HashSet<Edge>();

		// store all edges between these nodes; use another method than getConnectingEdges, we're testing
		Object[] nodeObj = nodes.toArray();

		for (int i = 0; i < nodeObj.length; i++) {
			Node node1 = (Node) nodeObj[i];

			// store self edges; the current loaded att is string type "interaction" having values pp or pd
			Edge ppSelfEdge = Cytoscape.getCyEdge(node1, node1, "interaction", "pp", false);

			if (ppSelfEdge != null)
				edges.add(ppSelfEdge);

			Edge pdSelfEdge = Cytoscape.getCyEdge(node1, node1, "interaction", "pd", false);

			if (pdSelfEdge != null)
				edges.add(pdSelfEdge);

			// 
			for (int j = i + 1; j < nodeObj.length; j++) {
				Node node2 = (Node) nodeObj[j];
				Edge ppEdge = Cytoscape.getCyEdge(node1, node2, "interaction", "pp", false, true);

				if (ppEdge != null)
					edges.add(ppEdge);

				Edge pdEdge = Cytoscape.getCyEdge(node1, node2, "interaction", "pd", false, true);

				if (pdEdge != null)
					edges.add(pdEdge);

				Edge pp2Edge = Cytoscape.getCyEdge(node2, node1, "interaction", "pp", false, true);

				if (pp2Edge != null)
					edges.add(pp2Edge);

				Edge pd2Edge = Cytoscape.getCyEdge(node2, node1, "interaction", "pd", false, true);

				if (pd2Edge != null)
					edges.add(pd2Edge);
			}
		}

		// Create network from selected nodes and all edges
		scenario.setTestSetting("PAUSE", "DURATION", "3000");
		player.run(robot, "NEW_NETWORK_FROM_SELECTED_NODES_ALL_EDGES");

		// New network will be current
		Iterator nodesIter = Cytoscape.getCurrentNetwork().nodesIterator();

		while (nodesIter.hasNext()) {
			// check whether all nodes present
			assertTrue(nodes.remove((Node) nodesIter.next()));
		}

		// and no more; ie set should be empty
		assertTrue(nodes.isEmpty());

		// and check the edges
		Iterator<Edge> iterOverEdges = edges.iterator();

		while (iterOverEdges.hasNext()) {
			Edge edge = iterOverEdges.next();
			assertNotNull(Cytoscape.getCurrentNetwork().getEdge(edge.getRootGraphIndex()));
		}

		// Create network from selected edges and selected nodes

		// Clone current network
		System.out.println("create new network from existing one stop");
	}

	/**
	 * This test exercises the functionality of the Select menu.

	public void testSelectMenu() throws IllegalStateException,
	        IllegalArgumentException, ExecuteException, ClassNotFoundException,
	        InstantiationException, IllegalAccessException,
	        UnsupportedLookAndFeelException {
	    System.out.println("testSelectMenu start");
	    // player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

	    // Import network
	    scenario.setTestSetting("PAUSE", "DURATION", "1000");
	    scenario
	            .setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
	    scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT",
	            "galFiltered.sif");
	    player.run(robot, "IMPORT_NETWORK_FILE");

	    GraphPerspective curr = Cytoscape.getCurrentNetwork();

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
	    assertEquals("select first neighborts of YPL248C", 11, curr
	            .getSelectedNodes().size());

	    // deselect all nodes
	    player.run(robot, "DESELECT_ALL_NODES");
	    assertEquals("all nodes deselected", 0, curr.getSelectedNodes().size());

	    // select from file
	    scenario.setTestSetting("SELECT_NODES_FROM_FILE", "IMPORT_DIR",
	            "testData");
	    scenario.setTestSetting("SELECT_NODES_FROM_FILE", "FILE_TO_IMPORT",
	            "galFiltered.select.from.file.txt");
	    player.run(robot, "SELECT_NODES_FROM_FILE");
	    assertEquals("select nodes from file galFiltered.select.from.file.txt",
	            10, curr.getSelectedNodes().size());

	    // set up listener to test node and edge hiding graph change events
	    Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(
	            new GraphViewChangeListener() {
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
	    player.run(robot, "PAUSE");
	    assertEquals("num nodes hidden", 10, totalNodesHidden);
	    assertEquals("num edges hidden", 15, totalEdgesHidden);

	    // show all nodes
	    System.out.println("about to show nodes");
	    player.run(robot, "SHOW_ALL_NODES");
	    player.run(robot, "PAUSE");

	    // select from file
	    scenario.setTestSetting("SELECT_NODES_FROM_FILE", "IMPORT_DIR",
	            "testData");
	    scenario.setTestSetting("SELECT_NODES_FROM_FILE", "FILE_TO_IMPORT",
	            "galFiltered.select.from.file.txt");
	    player.run(robot, "SELECT_NODES_FROM_FILE");
	    assertEquals("select nodes from file galFiltered.select.from.file.txt",
	            10, curr.getSelectedNodes().size());

	    // invert selection
	    player.run(robot, "INVERT_SELECTED_NODES");
	    assertEquals("invert selected nodes", 321, curr.getSelectedNodes()
	            .size());

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
	    assertEquals("invert selected edges", 362, curr.getSelectedEdges()
	            .size());

	    // hide selection
	    totalNodesHidden = 0;
	    totalEdgesHidden = 0;
	    System.out.println("about to hide edges");
	    player.run(robot, "HIDE_SELECTED_EDGES");
	    player.run(robot, "PAUSE");
	    assertEquals("num nodes hidden", 0, totalNodesHidden);
	    assertEquals("num edges hidden", 362, totalEdgesHidden);

	    // show all
	    System.out.println("about to show edges");
	    player.run(robot, "SHOW_ALL_EDGES");
	    player.run(robot, "PAUSE");

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

	    System.out.println("testSelectMenu start");
	}*/

	/**
	 * This test exercises the functionality of the Layout menu.
	 */
	public void testlayoutMenu()
	    throws IllegalStateException, IllegalArgumentException, ExecuteException,
	               ClassNotFoundException, InstantiationException, IllegalAccessException,
	               UnsupportedLookAndFeelException {
		System.out.println("testLayoutMenu start");
		//player.run(robot, "SHOW_HIDE_ATTRIBUTE_BROWSER");

		// Import network 
		scenario.setTestSetting("PAUSE", "DURATION", "1000");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "IMPORT_DIR", "testData");
		scenario.setTestSetting("IMPORT_NETWORK_FILE", "FILE_TO_IMPORT", "galFiltered.sif");
		player.run(robot, "IMPORT_NETWORK_FILE");

		// first try yFiles
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "yFiles");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Circular");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "yFiles");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Organic");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "yFiles");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Hierarchic");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "yFiles");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Random");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "yFiles");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "MirrorX");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "yFiles");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "MirrorY");
		player.run(robot, "DO_LAYOUT");

		// JGraph Layouts
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "JGraph Layouts");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Radial");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "JGraph Layouts");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Spring");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "JGraph Layouts");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Sugiyama");
		player.run(robot, "DO_LAYOUT");

		// Cytoscape Layouts
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Hierarchical");
		player.run(robot, "DO_LAYOUT");

		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_ALGORITHM", "Spring Embedded");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_OPTION", "All Nodes");
		player.run(robot, "DO_LAYOUT_WITH_OPTION");

		// To do:  select some nodes first
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_ALGORITHM", "Spring Embedded");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_OPTION", "Selected Nodes Only");
		//player.run(robot, "DO_LAYOUT_WITH_OPTION");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_ALGORITHM",
		                        "Attribute Circle Layout");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_OPTION", "canonicalName");
		player.run(robot, "DO_LAYOUT_WITH_OPTION");

		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		scenario.setTestSetting("DO_LAYOUT", "LAYOUT_ALGORITHM", "Degree Sorted Circle Layout");
		player.run(robot, "DO_LAYOUT");

		// To do: this case will fail, because labelParameter "canonicalName" appeared in previous case
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_ALGORITHM", "Group Attributes Layout");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION", "LAYOUT_OPTION", "canonicalName");
		//player.run(robot, "DO_LAYOUT_WITH_OPTION");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_ALGORITHM", "Edge-weighted");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_OPTION1",
		                        "Edge-weighted Spring Embedded");
		scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_OPTION2", "(unweighted)");
		player.run(robot, "DO_LAYOUT_WITH_OPTION2");

		// To do: this case will fail, because labelParameter "(unweighted)" appeared in previous case		
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_CATEGORY", "Cytoscape Layouts");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_ALGORITHM", "Edge-weighted");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_OPTION1", "bioLayout");
		//scenario.setTestSetting("DO_LAYOUT_WITH_OPTION2", "LAYOUT_OPTION2", "(unweighted)");
		//player.run(robot, "DO_LAYOUT_WITH_OPTION2");
	}
}
