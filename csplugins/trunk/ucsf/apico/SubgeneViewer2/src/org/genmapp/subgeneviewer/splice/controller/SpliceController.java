package org.genmapp.subgeneviewer.splice.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.view.GraphWalker;
import org.genmapp.subgeneviewer.splice.view.SpliceNetworkView;
import org.genmapp.subgeneviewer.text.Example_Exon_Structure_GenMAPP_CS;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

/**
 * The splice controller is one of many possible subgene viewer controllers. The
 * splice controller is responsible for listening and responding to mouse
 * events, checking for required data, prompting calculations and views.
 * 
 */
public class SpliceController extends MouseAdapter implements SubgeneController {

	private String _nodeId;

	private String _nodeLabel;
	private static final int PADDING = 20;
	private CyNetworkView view;
	private CyNetworkView oldView;
	private static CyNetwork dummyNet;
	private Color background;

	/*
	 * Dummy graph component
	 */
	private static final CyNode source;
	private static final CyNode target;
	private static final CyEdge edge;
	private Component canvas = null;

	static {
		source = Cytoscape.getCyNode("Source");
		target = Cytoscape.getCyNode("Target");
		edge = Cytoscape.getCyEdge(source.getIdentifier(), "Edge", target.getIdentifier(),
		                           "interaction");

		List nodes = new ArrayList();
		List edges = new ArrayList();
		nodes.add(source);
		nodes.add(target);
		edges.add(edge);

		dummyNet = Cytoscape.getRootGraph().createNetwork(nodes, edges);
		dummyNet.setTitle("Default Appearance");
	}

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
				spliceViewBuilder();
			}
			else {
				System.out.println("Insufficient exon structure data for this gene");
			}
		}
	}

	/**
	 * Verifies integrity of data at server or loaded as node attributes
	 */
	public boolean exonDataCheck() {
		//TODO: do data check
		return true;
	}

	/**
	 * 
	 */
	public void spliceViewBuilder() {

		/**
		 * Creates a new NodeFullDetailView object.
		 */
		public DefaultViewPanel() {
			oldView = Cytoscape.getVisualMappingManager().getNetworkView();

			background = Cytoscape.getVisualMappingManager().getVisualStyle()
			                      .getGlobalAppearanceCalculator().getDefaultBackgroundColor();
			this.setBackground(background);
		}

		protected void updateBackgroungColor(final Color newColor) {
			background = newColor;
			this.setBackground(background);
			repaint();
		}

		/**
		 * DOCUMENT ME!
		 *
		 * @return DOCUMENT ME!
		 */
		public Component getCanvas() {
			return canvas;
		}

		/**
		 * Create dummy network
		 */
		protected void createDummyNetworkView() {
			view = new DingNetworkView(dummyNet, "Default Appearence");

			view.setIdentifier(dummyNet.getIdentifier());
			view.setTitle(dummyNet.getTitle());

			view.getNodeView(source).setOffset(0, 0);
			view.getNodeView(target).setOffset(150, 10);
			Cytoscape.getVisualMappingManager().setNetworkView(view);
		}

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

}
