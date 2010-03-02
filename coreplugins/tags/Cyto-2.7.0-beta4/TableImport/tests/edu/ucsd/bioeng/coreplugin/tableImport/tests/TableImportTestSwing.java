
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

package edu.ucsd.bioeng.coreplugin.tableImport.tests;

import cytoscape.CyMain;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import edu.ucsd.bioeng.coreplugin.tableImport.TableImportPlugin;

import junit.framework.TestCase;

import swingunit.extensions.ExtendedRobotEventFactory;

import swingunit.framework.EventPlayer;
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.RobotEventFactory;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;

import java.awt.Robot;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 *
 */
public class TableImportTestSwing extends TestCase {
	private Scenario scenario;
	private RobotEventFactory robotEventFactory = new ExtendedRobotEventFactory();
	private FinderMethodSet methodSet = new FinderMethodSet();
	private Robot robot;
	private CyMain application;

	/**
	 * Creates a new TableImportTestSwing object.
	 *
	 * @param name  DOCUMENT ME!
	 */
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws ExecuteException DOCUMENT ME!
	 * @throws ClassNotFoundException DOCUMENT ME!
	 * @throws InstantiationException DOCUMENT ME!
	 * @throws IllegalAccessException DOCUMENT ME!
	 * @throws UnsupportedLookAndFeelException DOCUMENT ME!
	 */
	public void testTableImportPlugin()
	    throws ExecuteException, ClassNotFoundException, InstantiationException,
	               IllegalAccessException, UnsupportedLookAndFeelException {
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

		for (CyNetwork net : networkSet) {
			networkTitles.add(net.getTitle());
		}

		assertTrue(networkTitles.contains("Sub Network of YeastHQ.sif"));
		assertTrue(networkTitles.contains("Gene Ontology Full"));
		assertEquals(4, networkSet.size());

		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		final String[] edgeAttrNames = edgeAttr.getAttributeNames();
		final Set<String> edgeAttrNameSet = new TreeSet<String>();

		for (String name : edgeAttrNames) {
			System.out.println("Edge Attr = " + name);
			edgeAttrNameSet.add(name);
		}

		assertTrue(edgeAttrNameSet.contains("List Attr 1"));
		assertEquals(5, edgeAttr.getListAttribute("YCR084C (pp) YCL067C", "List Attr 1").size());
		assertEquals("abcd12364",
		             edgeAttr.getStringAttribute("YCR084C (pp) YCL067C", "String Attr 1"));

		assertTrue(edgeAttrNameSet.contains("Boolean Attr 1"));
	}
}
