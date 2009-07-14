package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;
import gestalt.p5.GestaltPlugIn;
import gestalt.shape.AbstractShape;
import gestalt.shape.DrawableFactory;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.P5Renderer;

public class ShapeFactory {

	private final GestaltPlugIn p;

	private final DrawableFactory factory;

	public ShapeFactory(GestaltPlugIn p) {
		this.p = p;
		this.factory = p.drawablefactory();
	}

	public P5Renderer<CyNode> getNodeShape(View<CyNode> view) {

	

		return null;
	}

}
