package org.genmapp.subgeneviewer.splice;

import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.genmapp.subgeneviewer.splice.controller.SpliceController;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
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
	private static CyNode feature;

	private static List<CyNode> features = new ArrayList<CyNode>();

	private static HashMap featureAttsMap = new HashMap();

	private static List edges = new ArrayList();

	private Component canvas = null;

	/**
	 * Creates a new NodeFullDetailView object.
	 */
	public SpliceViewPanel() {
		String nodeId = SpliceController.get_nodeId();
		CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		List<String> featureAttsMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_structure");

		features.clear();
		featureAttsMap.clear();

		// Foreach list of feature attributes
		for (String featureAttsList : featureAttsMetaList) {

			// Split list of attributes per feature
			String[] featureAtts = featureAttsList.split(":");

			// Transform Attributes to lists and booleans where needed
			ArrayList<String> featureStr = new ArrayList<String>();
			featureStr.add(featureAtts[3]);
			featureStr.add(featureAtts[4]);
			featureStr.add(featureAtts[5]);

			boolean constitutive = true;
			if (featureAtts[6].equalsIgnoreCase("n")) {
				constitutive = false;
			}
			boolean start = true;
			if (featureAtts[7].equalsIgnoreCase("n")) {
				start = false;
			}

			// Create CyNode:
			// Concatenate feature_label and region_id to generate a unique node
			// identifier.
			// Use feature_label for display.
			String feature_id = featureAtts[0] + "_" + featureAtts[2];
			feature = Cytoscape.getCyNode(feature_id, true);

			// Set attributes
			Cytoscape.getNodeAttributes().setAttribute(feature_id, "label",
					featureAtts[0]);
			Cytoscape.getNodeAttributes().setAttributeDescription("label",
					"Feature label");
			Cytoscape.getNodeAttributes().setUserVisible("label", true);
			Cytoscape.getNodeAttributes().setUserEditable("label", false);
			Cytoscape.getNodeAttributes().setAttribute(feature_id,
					"Affy_probeset", featureAtts[1]);
			Cytoscape.getNodeAttributes().setAttributeDescription(
					"Affy_probeset", "Affymetrix probeset identifiers");
			Cytoscape.getNodeAttributes()
					.setUserVisible("Affy_probeset", false);
			Cytoscape.getNodeAttributes().setUserEditable("Affy_probeset",
					false);
			Cytoscape.getNodeAttributes().setAttribute(feature_id, "region_id",
					featureAtts[2]);
			Cytoscape.getNodeAttributes().setAttributeDescription("region_id",
					"Region identifier");
			Cytoscape.getNodeAttributes().setUserVisible("region_id", false);
			Cytoscape.getNodeAttributes().setUserEditable("region_id", false);
			Cytoscape.getNodeAttributes().setListAttribute(feature_id,
					"structure", featureStr);
			Cytoscape.getNodeAttributes().setAttributeDescription("structure",
					"type, block, region");
			Cytoscape.getNodeAttributes().setUserVisible("structure", false);
			Cytoscape.getNodeAttributes().setUserEditable("structure", false);
			Cytoscape.getNodeAttributes().setAttribute(feature_id,
					"constitutive", constitutive);
			Cytoscape.getNodeAttributes().setAttributeDescription(
					"constitutive", "Constitutive region?");
			Cytoscape.getNodeAttributes().setUserVisible("constitutive", false);
			Cytoscape.getNodeAttributes()
					.setUserEditable("constitutive", false);
			Cytoscape.getNodeAttributes().setAttribute(feature_id, "start",
					start);
			Cytoscape.getNodeAttributes().setAttributeDescription("start",
					"Potential start site?");
			Cytoscape.getNodeAttributes().setUserVisible("start", false);
			Cytoscape.getNodeAttributes().setUserEditable("start", false);
			Cytoscape.getNodeAttributes().setAttribute(feature_id,
					"annotation", featureAtts[8]);
			Cytoscape.getNodeAttributes().setAttributeDescription("annotation",
					"Region annotation");
			Cytoscape.getNodeAttributes().setUserVisible("annotation", false);
			Cytoscape.getNodeAttributes().setUserEditable("annotation", false);

			// Add to list of CyNodes
			features.add(feature);
		}

		dummyNet = Cytoscape.getRootGraph().createNetwork(features, edges);
		dummyNet.setTitle(nodeId);

		oldView = Cytoscape.getVisualMappingManager().getNetworkView();

//		background = Cytoscape.getVisualMappingManager().getVisualStyle()
//		.getGlobalAppearanceCalculator().getDefaultBackgroundColor();

//		this.setBackground(background);

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Component getCanvas() {
		return canvas;
	}

	private static final int NODE_HEIGHT = 20;

	private static final int NODE_WIDTH = 40;

	private static final int VGAP = NODE_HEIGHT / 2;

	private static final int HGAP = NODE_WIDTH / 2;

	int xOffset = HGAP;

	/**
	 * Create dummy network
	 */
	protected void createDummyNetworkView() {
		view = new SubgeneNetworkView(dummyNet, dummyNet.getTitle());

		view.setIdentifier(dummyNet.getIdentifier());
		// view.setTitle(dummyNet.getTitle());

		for (Object f : features) {
			NodeView nv = view.getNodeView((CyNode) f);
			nv.setOffset(xOffset + HGAP / 2 + 1.0, VGAP + 1.0);
			xOffset += HGAP + NODE_WIDTH;
		}
		
		applySGVVisualStyle();
	}

	public void applySGVVisualStyle() {
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		VisualStyle sgvStyle = catalog.getVisualStyle("SGV");
		if (sgvStyle == null) { // Create the SGV visual style
			try {
				sgvStyle = (VisualStyle) vmm.getVisualStyle().clone();
			} catch (CloneNotSupportedException e) {
				sgvStyle = new VisualStyle("SGV");
			}
			sgvStyle.setName("SGV");
			sgvStyle.setNodeAppearanceCalculator(new SGVNodeAppearanceCalculator());
			catalog.addVisualStyle(sgvStyle);
		}
		
		background = new Color(250,240,160);
		this.setBackground(background);

//		vmm.setNetworkView(view);
		vmm.setVisualStyle(sgvStyle);
//		view.setVisualStyle("SGV");
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

			final Dimension panelSize = this.getSize();
			((DGraphView) view).getCanvas().setSize(
					new Dimension((int) panelSize.getWidth() - PADDING,
							(int) panelSize.getHeight() - PADDING));
			view.fitContent();
			canvas = (view.getComponent());

			// for (MouseListener listener : canvas.getMouseListeners())
			// canvas.removeMouseListener(listener);

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
