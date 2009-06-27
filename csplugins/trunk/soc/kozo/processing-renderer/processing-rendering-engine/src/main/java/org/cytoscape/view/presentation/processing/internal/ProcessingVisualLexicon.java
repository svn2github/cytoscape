package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.NODE;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.ObjectShape;
import org.cytoscape.view.presentation.processing.internal.shape.Rectangle;
import org.cytoscape.view.presentation.processing.visualproperty.ShapeVisualProperty;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {
	
	private static final ObjectShape DEFAULT_SHAPE = new Rectangle(0, 0, null);
	
	public static final VisualProperty<? extends ObjectShape> NODE_SHAPE = new ShapeVisualProperty(
			NODE, null, "NODE_SHAPE", "Node Shape");

}
