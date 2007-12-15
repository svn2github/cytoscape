package org.genmapp.subgeneviewer.splice.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.SpliceViewBuilder;
import org.genmapp.subgeneviewer.splice.SpliceViewPanel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;

/**
 * The splice controller is one of many possible subgene viewer controllers. The
 * splice controller is responsible for listening and responding to mouse
 * events, checking for required data, prompting calculations and views.
 * 
 */
public class SpliceController extends MouseAdapter implements SubgeneController {

	private static String _nodeId;

	private String _nodeLabel;

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
				System.out.println("Building splice view");
				buildSpliceView();
			} else {
				System.out
						.println("Insufficient exon structure data for this gene");
			}
		}
	}

	/**
	 * Verifies integrity of data at server or loaded as node attributes
	 */
	public boolean exonDataCheck() {
		// TODO: do data check
		CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		if (nodeAttribs.hasAttribute(_nodeId, "SubgeneViewer_Regions")) {
			List<String> featureList = nodeAttribs.getListAttribute(_nodeId,
					"SubgeneViewer_Regions");
			if (featureList.isEmpty()) {
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
	public void buildSpliceView() {

		final SpliceViewPanel panel = (SpliceViewPanel) SpliceViewBuilder
				.showDialog(Cytoscape.getDesktop());
		Cytoscape.getDesktop().repaint();

		// SpliceViewBuilder.getSpliceView(visualStyle);

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

}
