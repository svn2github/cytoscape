package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.VisualItemRenderer;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;

public class P5NetworkRenderer implements VisualItemRenderer<CyNetworkView> {

	public P5NetworkRenderer(PApplet p) {
		
		// TODO Auto-generated constructor stub
	}



	public VisualLexicon getVisualLexicon() {
		final VisualLexicon networkLexicon = new BasicVisualLexicon();
		networkLexicon.addVisualProperty(NETWORK_BACKGROUND_COLOR);
		
		return networkLexicon;
	}
	
	

}
