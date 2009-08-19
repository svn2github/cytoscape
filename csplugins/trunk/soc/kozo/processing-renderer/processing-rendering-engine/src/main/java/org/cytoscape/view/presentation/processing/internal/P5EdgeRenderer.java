package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NODE;
import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.EDGE_STYLE;
import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.NODE_STYLE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.EDGE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.CyDrawableFactory;
import org.cytoscape.view.presentation.processing.CyDrawableManager;
import org.cytoscape.view.presentation.processing.EdgeItem;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;

public class P5EdgeRenderer extends AbstractRenderer<View<CyEdge>> {
	
	
	
	private CyNetworkView networkView;
	
	public P5EdgeRenderer(PApplet p, CyDrawableManager manager, CyNetworkView networkView) {
		super(p, manager);
		this.networkView = networkView;
	}
	
	protected VisualLexicon buildLexicon() {
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
		return null;
	}

	public CyDrawable render(View<CyEdge> view) {
		
		// If Style property is available, use it.
		P5Shape shape = view.getVisualProperty(EDGE_STYLE);
		CyDrawable style = null;
		if(shape == null)
			style = manager.getDefaultFactory(EDGE).getInstance();
		else
			style = manager.getDrawable(shape.getDrawableType());

		
		// Extract source & target views
		final CyNode source = view.getSource().getSource();
		final CyNode target = view.getSource().getTarget();
		View<CyNode> sourceView = networkView.getNodeView(source);
		View<CyNode> targetView = networkView.getNodeView(target);
		
		((EdgeItem)style).setSource(sourceView);
		((EdgeItem)style).setTarget(targetView);
		
		style.setContext(view);
		
		return style;
	}
}
