package org.genmapp.subgeneviewer.splice;


import giny.model.Node;

import java.awt.Color;

import org.genmapp.subgeneviewer.splice.controller.SpliceController;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

public class SGVNodeAppearanceCalculator extends NodeAppearanceCalculator {
	
	public SGVNodeAppearanceCalculator() {

	}
	
	public void calculateNodeAppearance(NodeAppearance appr, Node node,
			CyNetwork network) {
		super.calculateNodeAppearance(appr, node, network);
		
		CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		String feature_id = node.getIdentifier();
		
			
			//Node label
			String label = nodeAttribs.getStringAttribute(feature_id, "label");
			appr.set(VisualPropertyType.NODE_LABEL, label != null ? label : feature_id);
			
			//Node shape
			setNodeSizeLocked(false);
			appr.set(VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT);
			appr.set(VisualPropertyType.NODE_WIDTH, 50);
			appr.set(VisualPropertyType.NODE_HEIGHT, 25);
			
			//Node colors
			Color stroke = new Color(0,0,0);
			if(stroke != null) {
				appr.set(VisualPropertyType.NODE_BORDER_COLOR, stroke);
			}
			Color fill = new Color(255,255,255);
			if(fill != null) {
				appr.set(VisualPropertyType.NODE_FILL_COLOR, fill);
			}
			
		
	}
		
	public String calculateNodeLabel(Node node, CyNetwork network) {
		String feature_id = SpliceController.get_nodeId();
		CyNode feature = Cytoscape.getCyNode(feature_id);
		CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		if(feature != null) {
			String label = nodeAttribs.getStringAttribute(feature_id, "label");
			return label;
		}
		return null;
	}
}
