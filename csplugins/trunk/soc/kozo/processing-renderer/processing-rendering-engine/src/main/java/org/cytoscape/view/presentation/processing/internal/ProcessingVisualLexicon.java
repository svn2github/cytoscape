package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.NODE;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.processing.visualproperty.ShapeVisualProperty;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {
	
	
	public static final VisualProperty<? extends P5Shape> NODE_SHAPE = new ShapeVisualProperty(
			NODE, null, "NODE_SHAPE", "Node Shape");

}
