package org.genmapp.subgeneviewer.splice;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;

import org.genmapp.subgeneviewer.splice.controller.SpliceController;
import org.genmapp.subgeneviewer.splice.model.SpliceEvent;
import org.genmapp.subgeneviewer.splice.model.SpliceRegion;
import org.genmapp.subgeneviewer.view.SGVNodeAppearanceCalculator;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * 
 */
public abstract class SpliceViewBuilder {

	private static CyNetworkView myView;

	private static CyNetwork dummyNet;

	private static String nodeId;

	private static String nodeLabel;

	private static CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();

	private static CyNode feature;

	private static List<CyNode> features = new ArrayList<CyNode>();

	private static List edges = new ArrayList();

	private static final int NODE_HEIGHT = SGVNodeAppearanceCalculator.FEATURE_NODE_HEIGHT;

	private static final int NODE_WIDTH = SGVNodeAppearanceCalculator.FEATURE_NODE_WIDTH;

	private static final int HGAP = NODE_WIDTH / 2;

	private static final int LABEL_TRACK_HEIGHT = NODE_HEIGHT;

	private static final int TITLE_LEGEND_HEIGHT = NODE_HEIGHT;

	private static int xOffset = HGAP;

	private static int windowHeight = 200;

	private static int windowWidth = 500;

	private static int yOffset = (int) (windowHeight/2 - ((windowHeight) - (NODE_HEIGHT
			+ SpliceRegion.REGION_HEIGHT + SpliceRegion.VGAP + SpliceEvent.spliceHeight)) / 2) - NODE_HEIGHT;

	/**
	 * Processes feature, structure and splice attributes and creates a network
	 */
	public static void createSpliceNetworkView() {

		// Get parent node label
		nodeId = SpliceController.get_nodeId();
		nodeLabel = SpliceController.get_nodeLabel();

		processFeatures();

		processStructure();

		processSplicing();

		applySGVVisualStyle();
	}

	/**
	 * Collect feature attributes from parent node, create CyNodes, set
	 * attributes, and create splice network
	 */
	public static void processFeatures() {
		List<String> featureAttsMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_feature");

		features.clear();

		// Foreach list of feature attributes
		for (String featureAttsList : featureAttsMetaList) {

			// Split list of attributes per feature
			String[] featureAtts = featureAttsList.split(":");

			// Create CyNode: Concatenate feature_label and region_id to
			// generate a unique node identifier.Use feature_label for display.
			String feature_name = featureAtts[0] + "_" + featureAtts[1] + "_"
					+ featureAtts[2];
			feature = Cytoscape.getCyNode(feature_name, true);

			// Set label
			Cytoscape.getNodeAttributes().setAttribute(feature_name, "label",
					featureAtts[0]);
			Cytoscape.getNodeAttributes().setAttributeDescription("label",
					"Feature label");
			Cytoscape.getNodeAttributes().setUserVisible("label", true);
			Cytoscape.getNodeAttributes().setUserEditable("label", false);

			// Set region id
			Cytoscape.getNodeAttributes().setAttribute(feature_name,
					"region_id", featureAtts[1]);
			Cytoscape.getNodeAttributes().setAttributeDescription("region_id",
					"Region identifier");
			Cytoscape.getNodeAttributes().setUserVisible("region_id", false);
			Cytoscape.getNodeAttributes().setUserEditable("region_id", false);

			// Set affy probeset id
			Cytoscape.getNodeAttributes().setAttribute(feature_name,
					"Affy_probeset", featureAtts[2]);
			Cytoscape.getNodeAttributes().setAttributeDescription(
					"Affy_probeset", "Affymetrix probeset identifiers");
			Cytoscape.getNodeAttributes().setUserVisible("Affy_probeset", true);
			Cytoscape.getNodeAttributes().setUserEditable("Affy_probeset",
					false);

			// Add to list of CyNodes
			features.add(feature);
		}

		// creates a CyNetwork and CyNetworkView for feature nodes
		dummyNet = Cytoscape.createNetwork(features, edges, nodeLabel);
		dummyNet.setTitle(nodeLabel);
		myView = Cytoscape.getCurrentNetworkView();

		// Disable node dragging in SGV window
		((DGraphView) myView).getCanvas().disableNodeMovement();

		// Sets size of SGV window. ONLY WORKS WITH Cytoscape v2.6!
		Cytoscape.getDesktop().getNetworkViewManager().getInternalFrame(myView)
				.setBounds(0, 0, windowWidth, windowHeight);

		// layout feature nodes
		xOffset = HGAP;
		for (Object f : features) {
			NodeView nv = myView.getNodeView((CyNode) f);
			nv.setOffset(xOffset + HGAP / 2, 0);
			xOffset += HGAP + NODE_WIDTH;
		}

		myView.fitContent();
		myView.setZoom(1.0);

		xOffset = (features.size() * (NODE_WIDTH + HGAP)) / 2 - windowWidth/2
				+ HGAP;
		for (Object f : features) {
			NodeView nv = myView.getNodeView((CyNode) f);
			Point2D p = nv.getOffset();
			Double newP = p.getX() + xOffset;
			nv.setOffset(newP, yOffset);
		}

	}

	/**
	 * Collect structure attributes from parent node, create Region objects,
	 * pass Region properties
	 */
	public static void processStructure() {
		List<String> structureMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_structure");
		for (String structureList : structureMetaList) {

			// Split list of attributes per region
			String[] structureAtts = structureList.split(":");

			// Transform attribute data types
			String region_name = structureAtts[0] + "_" + structureAtts[1]
					+ "_" + structureAtts[2];
			int units = Integer.parseInt(structureAtts[4]);
			boolean constitutive = false;
			boolean start = false;
			if (structureAtts[5].equalsIgnoreCase("y")) {
				constitutive = true;
			}
			if (structureAtts[6].equalsIgnoreCase("y")) {
				start = true;
			}

			// Create Region with properties
			SpliceRegion region = new SpliceRegion(region_name, myView,
					structureAtts[1], structureAtts[3], units, constitutive,
					start, structureAtts[7]);

			// Add Region to Canvas
			DGraphView dview = (DGraphView) myView;
			DingCanvas aLayer = dview
					.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
			aLayer.add(region);

			// hack
			dview.setZoom(dview.getZoom() * 0.99999999999999999d);
		}

	}

	/**
	 * Collect splicing attributes from parent node, draw splice events between
	 * regions
	 */
	public static void processSplicing() {
		List<String> spliceMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_splice");
		for (String spliceList : spliceMetaList) {

			// Split list of attributes per event
			String[] spliceAtts = spliceList.split(":");

			// Create Splice Events and set properties
			SpliceEvent event = new SpliceEvent(spliceAtts[0], spliceAtts[1],
					myView);

			// Add Splice Events to canvas
			DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
			// DGraphView dview = (DGraphView) view;
			DingCanvas aLayer = dview
					.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
			aLayer.add(event);

			// hack
			dview.setZoom(dview.getZoom() * 0.99999999999999999d);

		}
	}

	/**
	 * creates SGV visual style for feature nodes and applies it to the network
	 */
	public static void applySGVVisualStyle() {
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
			NodeAppearanceCalculator nac = new SGVNodeAppearanceCalculator();
			nac.getDefaultAppearance().setNodeSizeLocked(false);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT,
					SGVNodeAppearanceCalculator.FEATURE_NODE_HEIGHT);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH,
					SGVNodeAppearanceCalculator.FEATURE_NODE_WIDTH);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
					NodeShape.RECT);
			nac.getDefaultAppearance().set(
					VisualPropertyType.NODE_BORDER_COLOR, new Color(0, 0, 0));
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
					new Color(255, 255, 255));

			sgvStyle.setNodeAppearanceCalculator(nac);
			catalog.addVisualStyle(sgvStyle);
		}
		vmm.setNetworkView(myView);
		vmm.setVisualStyle(sgvStyle);
		myView.setVisualStyle("SGV");

		Cytoscape.getVisualMappingManager().setNetworkView(myView);
		Cytoscape.getVisualMappingManager().applyAppearances();

	}

}
