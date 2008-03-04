package org.genmapp.subgeneviewer.splice.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.SpliceViewBuilder;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;

/**
 * The splice controller is one of many possible subgene viewer controllers. The
 * splice controller is responsible for listening and responding to mouse
 * events, checking for required data, prompting view construction.
 * 
 */
public class SpliceController extends MouseAdapter implements SubgeneController {

	private static String _nodeId;

	private static String _nodeLabel;

	/**
	 * When user double-clicks on a node, the node's ID and label are retrieved
	 * and passed to the networkViewBuilder.
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() >= 2
				&& (((DGraphView) Cytoscape.getCurrentNetworkView())
						.getPickedNodeView(e.getPoint()) != null)) {

			System.out.println("SGV: double click on node");

			_nodeLabel = ((DGraphView) Cytoscape.getCurrentNetworkView())
					.getPickedNodeView(e.getPoint()).getLabel().getText();

			_nodeId = ((DGraphView) Cytoscape.getCurrentNetworkView())
					.getPickedNodeView(e.getPoint()).getNode().getIdentifier();

			System.out.println("Checking for exon structure data");
			boolean dataReady = exonDataCheck();
			if (dataReady) {
				System.out.println("Adding link out for Affy probesets");
				probesetLinkOut();

				System.out.println("Building splice view");
				buildSpliceView();
			} else {
				// If not; prompt to collect data from server
				System.out
				.println("Insufficient exon structure data for this gene");
				System.out
				.println("Attempting to connect to database...");

				Connection conn = null;

				try {
					System.out.println("check 1");
					String userName = "mysql";
					String password = "fun4mysql";
					String url = "jdbc:mysql://conklinwolf.ucsf.edu/SubgeneViewer";
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					conn = DriverManager.getConnection(url, userName, password);
					System.out.println("Database connection established");
				} catch (Exception ex) {
					System.err.println("Cannot connect to database server: "+ ex.getMessage());
					ex.printStackTrace();
				} finally {
					System.out.println("check 2");
					if (conn != null) {
						System.out.println("check 3");
						try {
							conn.close();
							System.out
									.println("Database connection terminated");
						} catch (Exception ex) { /* ignore close errors */
							
						}
					}
				}

			}
		}
	}

	/**
	 * Verifies integrity of data at server or loaded as node attributes
	 */
	public boolean exonDataCheck() {
		// TODO: do data check
		CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
//		return false;
		if ((nodeAttribs.hasAttribute(_nodeId, "sgv_structure"))
				&& (nodeAttribs.hasAttribute(_nodeId, "sgv_feature"))) {
			if ((nodeAttribs.getListAttribute(_nodeId, "sgv_structure")
					.isEmpty())
					|| (nodeAttribs.getListAttribute(_nodeId, "sgv_feature")
							.isEmpty())) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 
	 */
	public void probesetLinkOut() {
		Properties props = CytoscapeInit.getProperties();
		props
				.setProperty(
						"nodelinkouturl.SubgeneViewer.Affymetrix Human Exon 1_0 Probe Sets",
						"http://genome.ucsc.edu/cgi-bin/hgc?g=affyHuEx1&i=%Affy_probeset%");
		props
				.setProperty(
						"nodelinkouturl.SubgeneViewer.Affymetrix NetAffx Probe Sets (requires login)",
						"https://www.affymetrix.com/analysis/netaffx/exon/probe_set.affx?pk=1:%Affy_probeset%");
		props
				.setProperty(
						"nodelinkouturl.SubgeneViewer.UCSC Genome Browser (link by gene)",
						"http://genome.ucsc.edu/cgi-bin/hgTracks?position=%ID%");
	}

	/**
	 * 
	 */
	public void buildSpliceView() {
		SpliceViewBuilder.createSpliceNetworkView();
		Cytoscape.getDesktop().repaint();

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static String get_nodeId() {
		return _nodeId;
	}

	public void set_nodeId(String id) {
		_nodeId = id;
	}

	/**
	 * @return the _nodeLabel
	 */
	public static String get_nodeLabel() {
		return _nodeLabel;
	}

	/**
	 * @param label
	 *            the _nodeLabel to set
	 */
	public void set_nodeLabel(String label) {
		_nodeLabel = label;
	}

}
