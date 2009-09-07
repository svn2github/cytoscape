package org.genmapp.golayout;


import giny.model.Node;
import cytoscape.CyNetwork;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.NodeAppearanceCalculator;

public class MFNodeAppearanceCalculator extends NodeAppearanceCalculator {
	
	public static int FEATURE_NODE_WIDTH = 60;
	public static int FEATURE_NODE_HEIGHT = 30;
	
	public MFNodeAppearanceCalculator() {
	}
	
	public void calculateNodeAppearance(NodeAppearance appr, Node node,
			CyNetwork network) {
		super.calculateNodeAppearance(appr, node, network);	
	}
}
