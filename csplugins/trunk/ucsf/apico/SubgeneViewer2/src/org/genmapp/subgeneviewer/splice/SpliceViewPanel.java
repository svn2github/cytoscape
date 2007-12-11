package org.genmapp.subgeneviewer.splice;

import giny.view.GraphView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;


/**
 * 
 */
public class SpliceViewPanel extends JPanel {
	
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
		source = Cytoscape.getCyNode("E1.1");
		target = Cytoscape.getCyNode("E2.1");
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
	 * Creates a new NodeFullDetailView object.
	 */
	public void SpliceViewPanel() {

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
		view = new SubgeneNetworkView(dummyNet, "Default Appearence");

		view.setIdentifier(dummyNet.getIdentifier());
		view.setTitle(dummyNet.getTitle());

		view.getNodeView(source).setOffset(0, 0);
		view.getNodeView(target).setOffset(150, 10);
		Cytoscape.getVisualMappingManager().setNetworkView(view);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void clean() {
		Cytoscape.destroyNetwork(dummyNet);
		Cytoscape.getVisualMappingManager().setNetworkView(oldView);
		dummyNet = null;
		canvas = null;
	}

	/**
	 * DOCUMENT ME!
	 */
	protected void updateView() {
		if (view != null) {
			Cytoscape.getVisualMappingManager().setNetworkView(view);
			view.setVisualStyle(Cytoscape.getVisualMappingManager().getVisualStyle().getName());

			final Dimension panelSize = this.getSize();
			((DGraphView) view).getCanvas()
			 .setSize(new Dimension((int) panelSize.getWidth() - PADDING,
			                        (int) panelSize.getHeight() - PADDING));
			view.fitContent();
			canvas = (view.getComponent());

			for (MouseListener listener : canvas.getMouseListeners())
				canvas.removeMouseListener(listener);

			this.removeAll();
			this.add(canvas);

			canvas.setLocation(PADDING / 2, PADDING / 2);
			Cytoscape.getVisualMappingManager().applyAppearances();

			if ((background != null) && (canvas != null)) {
				canvas.setBackground(background);
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphView getView() {
		return view;
	}
}
