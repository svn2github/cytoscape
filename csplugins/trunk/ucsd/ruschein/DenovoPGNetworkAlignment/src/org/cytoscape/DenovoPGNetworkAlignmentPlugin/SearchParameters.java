package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import cytoscape.CyNetwork;

public final class SearchParameters {
	
	CyNetwork network;
	String physicalEdgeAttrName;
	String geneticEdgeAttrName;
	
	boolean randomize = false;
	
	double alpha;
	double alphaMultiplier;
	double physicalNetworkFilterDegree;
	
	public SearchParameters() {
	}
}
