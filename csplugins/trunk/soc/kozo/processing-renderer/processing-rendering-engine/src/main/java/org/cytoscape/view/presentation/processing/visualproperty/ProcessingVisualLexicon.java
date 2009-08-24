package org.cytoscape.view.presentation.processing.visualproperty;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.processing.internal.drawable.Cube;
import org.cytoscape.view.presentation.processing.internal.drawable.Line;
import org.cytoscape.view.presentation.property.ColorVisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {

	private final Map<Class<? extends CyDrawable>, VisualLexicon> lexiconMap;

	public static final VisualProperty<P5Shape> NODE_STYLE = new P5ShapeVisualProperty(
			NODE, new P5Shape(Cube.class.getSimpleName(), Cube.class), "NODE_STYLE", "Node Style");
	public static final VisualProperty<P5Shape> EDGE_STYLE = new P5ShapeVisualProperty(
			EDGE, new P5Shape(Line.class.getSimpleName(), Line.class), "EDGE_STYLE", "Edge Style");
	
	public static final VisualProperty<Double> NODE_OPACITY = new DoubleVisualProperty(
			NODE, 255d, "NODE_OPACITY", "Node Opacity");
	public static final VisualProperty<Double> NODE_LABEL_OPACITY = new DoubleVisualProperty(
			NODE, 255d, "NODE_LABEL_OPACITY", "Node Label Opacity");
	
	public static final VisualProperty<? extends Paint> EDGE_SELECTED_COLOR = new ColorVisualProperty(
			EDGE, Color.yellow, "EDGE_SELECTED_COLOR", "Edge Selected Color");
	public static final VisualProperty<Double> EDGE_LABEL_OPACITY = new DoubleVisualProperty(
			EDGE, 255d, "EDGE_LABEL_OPACITY", "Edge Label Opacity");
	public static final VisualProperty<Double> EDGE_OPACITY = new DoubleVisualProperty(
			EDGE, 255d, "EDGE_OPACITY", "Edge Opacity");
	
	public static final VisualProperty<Double> NODE_LABEL_SIZE = new DoubleVisualProperty(
			NODE, 12d, "NODE_LABEL_SIZE", "Node Label Size");
	
	
	public static final VisualProperty<Double> SPHERE_DETAIL = new DoubleVisualProperty(
			NODE, 5d, "SPHERE_DETAIL", "Sphere Detail");

	public static final VisualProperty<? extends Image> NETWORK_BACKGROUND_IMAGE = new ImageVisualProperty(
			NETWORK, null, "NETWORK_BACKGROUND_IMAGE",
			"Network Background Image");

	public ProcessingVisualLexicon() {
		super();
		this.visualPropertySet.add(NODE_STYLE);
		this.visualPropertySet.add(EDGE_STYLE);
		
		visualPropertySet.add(NODE_OPACITY);
		visualPropertySet.add(NODE_LABEL_OPACITY);
		visualPropertySet.add(EDGE_SELECTED_COLOR);
		visualPropertySet.add(EDGE_OPACITY);
		visualPropertySet.add(EDGE_LABEL_OPACITY);
		visualPropertySet.add(NODE_LABEL_SIZE);


		this.visualPropertySet.add(SPHERE_DETAIL);
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
