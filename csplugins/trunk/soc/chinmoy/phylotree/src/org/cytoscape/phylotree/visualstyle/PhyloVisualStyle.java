package org.cytoscape.phylotree.visualstyle;


import cytoscape.CyNetwork;
import cytoscape.visual.VisualStyle;



public interface PhyloVisualStyle {
		
	public String getName();
	public VisualStyle createStyle(CyNetwork network);
	
}
