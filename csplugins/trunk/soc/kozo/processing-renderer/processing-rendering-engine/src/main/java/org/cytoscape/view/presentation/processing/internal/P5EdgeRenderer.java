package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.*;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.VisualItemRenderer;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.internal.shape.Cube;
import org.cytoscape.view.presentation.processing.internal.shape.Line;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;

public class P5EdgeRenderer implements VisualItemRenderer<View<CyEdge>>{
	
private PApplet p;
	
	private final VisualLexicon nodeLexicon;
	
	public P5EdgeRenderer(PApplet p) {
		this.p = p;
		nodeLexicon = buildLexicon();
	}
	
	private VisualLexicon buildLexicon() {
		final VisualLexicon sub = new BasicVisualLexicon();
		System.out.println("%%%%%%%%%%%%% Building VP1");
		sub.addVisualProperty(EDGE_COLOR);
		
		sub.addVisualProperty(NODE_X_LOCATION);
		sub.addVisualProperty(NODE_Y_LOCATION);
		sub.addVisualProperty(NODE_Z_LOCATION);
		System.out.println("%%%%%%%%%%%%% Building VP2");
		//sub.addVisualProperty(ProcessingVisualLexicon.NODE_STYLE);
		
//		sub.addVisualProperty(NODE_X_SIZE);
//		sub.addVisualProperty(NODE_Y_SIZE);
//		sub.addVisualProperty(NODE_Z_SIZE);
		System.out.println("%%%%%%%%%%%%% Building VP3");
		
		return sub;
	}

	public VisualLexicon getVisualLexicon() {
		return nodeLexicon;
	}

	public CyDrawable render(View<CyEdge> view) {
		CyDrawable style = view.getVisualProperty(EDGE_STYLE);
		
		if(style == null) 
			style = new Line(p);
		
		view.getSource().getSource();
		view.getSource().getTarget();
		
		
		Cube cube = (Cube) style;
		cube.x = view.getVisualProperty(NODE_X_LOCATION).floatValue();
		cube.y = view.getVisualProperty(NODE_Y_LOCATION).floatValue();
		cube.z = view.getVisualProperty(NODE_Z_LOCATION).floatValue();
		
		return cube;
	}


}
