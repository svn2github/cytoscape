package org.genmapp.golayout;


import giny.model.Edge;
import cytoscape.CyNetwork;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.EdgeAppearanceCalculator;

public class MFEdgeAppearanceCalculator extends EdgeAppearanceCalculator {
	
	public MFEdgeAppearanceCalculator() {
	}
	
	public void calculateNodeAppearance(EdgeAppearance appr, Edge edge,
			CyNetwork network) {
		super.calculateEdgeAppearance(appr, edge, network);	
	}
}
