package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.NODE_STYLE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.*;


import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.VisualItemRenderer;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.internal.shape.Cube;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;

public class P5NodeRenderer implements VisualItemRenderer<View<CyNode>> {

	private PApplet p;
	
	private final VisualLexicon nodeLexicon;
	
	public P5NodeRenderer(PApplet p) {
		this.p = p;
		nodeLexicon = buildLexicon();
	}
	
	private VisualLexicon buildLexicon() {
		final VisualLexicon sub = new BasicVisualLexicon();
		System.out.println("%%%%%%%%%%%%% Building VP1");
		sub.addVisualProperty(NODE_COLOR);
		
		sub.addVisualProperty(NODE_X_LOCATION);
		sub.addVisualProperty(NODE_Y_LOCATION);
		sub.addVisualProperty(NODE_Z_LOCATION);
		System.out.println("%%%%%%%%%%%%% Building VP2");
		//sub.addVisualProperty(ProcessingVisualLexicon.NODE_STYLE);
		
		sub.addVisualProperty(NODE_X_SIZE);
		sub.addVisualProperty(NODE_Y_SIZE);
		System.out.println("%%%%%%%%%%%%% Building VP3");
		
		return sub;
	}

	public VisualLexicon getVisualLexicon() {
		return nodeLexicon;
	}

	public CyDrawable render(View<CyNode> view) {
		// If Style property is available, use it.
		CyDrawable style = view.getVisualProperty(NODE_STYLE);
		
		// If not available, use the default CyDrawable, which is a cube.
		if(style == null) 
			style = new Cube(p, nodeLexicon);
		
		style.setContext(view);
		
		return style;
	}


}
