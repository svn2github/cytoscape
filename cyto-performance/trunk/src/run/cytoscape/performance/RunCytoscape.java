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

import cytoscape.performance.track.*;
import cytoscape.performance.ui.*;

import junit.framework.*;

import javax.swing.SwingUtilities;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.CyMain;
import cytoscape.ding.DingNetworkView; 
import cytoscape.actions.CreateNetworkViewAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.visual.CalculatorIO;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.util.URLUtil;

import java.util.Properties;
import java.util.Random;
import java.io.InputStream;
import java.io.IOException;


/**
 * Swing unit tests used to test performance.
 */
public class RunCytoscape extends TestCase {
	private CyMain application;
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
	}

	protected void tearDown() throws Exception {
		application = null;
	}

	/**
	 * Run through a (large) variety of tasks so that we can profile the app.
	 */
	public void testSpeed() {
		loadNetworks();
		zoomInAndOut();
		selectAllNodes();
		applyLayout();
		loadVisualProperties();
		applyVizMap();
		saveSession();
		restoreSessionFromFile();
	}

	private String rualIdentifier;
	private String bindIdentifier;
	private String galIdentifier;

	private void loadNetworks() {
		// this one also loads attrs
		Cytoscape.createNetworkFromURL(getClass().getResource("/galFiltered.xgmml"), true);
		galIdentifier = Cytoscape.getCurrentNetwork().getIdentifier();

		// just to test gml
	//	Cytoscape.createNetworkFromURL(getClass().getResource("/galFiltered.gml"), true);

		// a big network with separate view creation
		Cytoscape.createNetworkFromURL(getClass().getResource("/BINDhuman.sif"), true);
		bindIdentifier = Cytoscape.getCurrentNetwork().getIdentifier();
		CreateNetworkViewAction.createViewFromCurrentNetwork(Cytoscape.getNetwork(bindIdentifier));

		// a moderate network good for testing
		Cytoscape.createNetworkFromURL(getClass().getResource("/RUAL.subset.sif"), true);
		rualIdentifier = Cytoscape.getCurrentNetwork().getIdentifier();
	}

	private void zoomInAndOut() {
		Cytoscape.setCurrentNetworkView( rualIdentifier );
		CyNetwork network = Cytoscape.getNetwork(rualIdentifier);

		// select a random node
		CyNode randomNode = Cytoscape.getCyNode("6612");	
		network.setSelectedNodeState(randomNode,true);

		DingNetworkView view = (DingNetworkView)(Cytoscape.getCurrentNetworkView());

		// zoom out
		view.fitContent();

		// zoom in
		view.fitSelected();

		// zoom out
		view.fitContent();
	}

	private void selectAllNodes() {
		DingNetworkView view = (DingNetworkView)Cytoscape.getNetworkView(bindIdentifier);
		view.fitSelected();

		CyNetwork network = Cytoscape.getNetwork(bindIdentifier);
		network.selectAllNodes();
		view.updateView();
	}

	private void applyLayout() {
		CyNetworkView view = Cytoscape.getNetworkView(rualIdentifier);
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		layout.doLayout(view);
	}

	private void loadVisualProperties() {
		Properties vizmapProps = new Properties();
		try {
		InputStream is = URLUtil.getBasicInputStream(getClass().getResource("/test.vizmap.props"));
		vizmapProps.load(is);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}
		CalculatorCatalog clog = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
		CalculatorIO.loadCalculators(vizmapProps, clog);
	}

	private void applyVizMap() {
		Cytoscape.setCurrentNetwork( galIdentifier );
		CyNetworkView view = Cytoscape.getNetworkView(galIdentifier);
		String styleName = "testStyle";
        Cytoscape.getVisualMappingManager().setVisualStyle(styleName);
		Cytoscape.getVisualMappingManager().setNetworkView(view);
		view.setVisualStyle(styleName);
		view.redrawGraph(true,true);
	}

	String sessionName;

	private void saveSession() {
		sessionName = Integer.toString(Math.abs(new Random().nextInt())) + "-session.cys";
		Cytoscape.getDesktop().setTitle("new session");
		CytoscapeSessionWriter sw = new CytoscapeSessionWriter(sessionName);
        try {
            sw.writeSessionToDisk();
        } catch (Exception e) {
			e.printStackTrace();
		}
		Cytoscape.setCurrentSessionFileName(sessionName);
		Cytoscape.getDesktop().setTitle("temp session");
	}

	private void restoreSessionFromFile() {
		Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
		Cytoscape.createNewSession();
		Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
		try {
		CytoscapeSessionReader sr = new CytoscapeSessionReader(sessionName);
		sr.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Cytoscape.getDesktop().getVizMapperUI().initVizmapperGUI();
		Cytoscape.setCurrentSessionFileName(sessionName);
	}

}
