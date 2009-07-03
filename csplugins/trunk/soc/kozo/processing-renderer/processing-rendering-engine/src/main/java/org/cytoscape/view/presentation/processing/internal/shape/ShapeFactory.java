package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;
import gestalt.p5.GestaltPlugIn;
import gestalt.shape.AbstractShape;
import gestalt.shape.DrawableFactory;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.P5Presentation;

public class ShapeFactory {

	private final GestaltPlugIn p;

	private final DrawableFactory factory;

	public ShapeFactory(GestaltPlugIn p) {
		this.p = p;
		this.factory = p.drawablefactory();
	}

	public P5Presentation<CyNode> getNodeShape(View<CyNode> view) {

		/*
		 * Parse view info
		 */
		final Number x = view.getVisualProperty(NODE_X_LOCATION);
		final Number y = view.getVisualProperty(NODE_Y_LOCATION);
		final Number z = view.getVisualProperty(NODE_Z_LOCATION);

		AbstractShape gNode = factory.cube();
		gNode.position(x.floatValue(), y.floatValue(), z.floatValue());

		return null;
	}

}
