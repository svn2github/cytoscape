package org.cytoscape.view.presentation.processing.internal.shape;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.PNodeView;

import processing.core.PApplet;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.*;

public class ShapeFactory {
	
	private final PApplet p;
	
	public ShapeFactory(PApplet p) {
		this.p = p;
	}
	
	public PNodeView getNodeShape(View<CyNode> view) {
		
		
		
		/*
		 * Parse view info
		 */
		final Number x = view.getVisualProperty(NODE_X_LOCATION);
		final Number y = view.getVisualProperty(NODE_Y_LOCATION);
		final Number z = view.getVisualProperty(NODE_Z_LOCATION);
		
		
		return null;
	}

}
