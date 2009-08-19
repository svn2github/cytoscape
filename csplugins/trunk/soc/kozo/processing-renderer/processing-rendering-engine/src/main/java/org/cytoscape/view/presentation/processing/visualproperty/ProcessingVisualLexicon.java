package org.cytoscape.view.presentation.processing.visualproperty;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.processing.internal.drawable.Cube;
import org.cytoscape.view.presentation.processing.internal.drawable.Line;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {

	private final Map<Class<? extends CyDrawable>, VisualLexicon> lexiconMap;

	public static final VisualProperty<P5Shape> NODE_STYLE = new P5ShapeVisualProperty(
			NODE, new P5Shape(Cube.class.getSimpleName(), Cube.class), "NODE_STYLE", "Node Style");
	public static final VisualProperty<P5Shape> EDGE_STYLE = new P5ShapeVisualProperty(
			EDGE, new P5Shape(Line.class.getSimpleName(), Line.class), "EDGE_STYLE", "Edge Style");

	public static final VisualProperty<? extends Image> NETWORK_BACKGROUND_IMAGE = new ImageVisualProperty(
			NETWORK, null, "NETWORK_BACKGROUND_IMAGE",
			"Network Background Image");

	public ProcessingVisualLexicon() {
		super();
		this.visualPropertySet.add(NODE_STYLE);
		this.visualPropertySet.add(EDGE_STYLE);
		this.visualPropertySet.add(NETWORK_BACKGROUND_IMAGE);

		lexiconMap = new HashMap<Class<? extends CyDrawable>, VisualLexicon>();
	}

	public VisualLexicon getSubLexicon(Class<? extends CyDrawable> drawable) {
		return lexiconMap.get(drawable);
	}

	public void registerSubLexicon(Class<? extends CyDrawable> drawable,
			VisualLexicon lexicon) {
		this.lexiconMap.put(drawable, lexicon);
	}

}
