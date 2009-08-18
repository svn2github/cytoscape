package org.cytoscape.view.presentation.processing.visualproperty;

import static org.cytoscape.model.GraphObject.*;
import static org.cytoscape.model.GraphObject.NODE;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.internal.drawable.Cube;
import org.cytoscape.view.presentation.property.StringVisualProperty;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {

	private final Map<Class<? extends CyDrawable>, VisualLexicon> lexiconMap;

	public static final VisualProperty<Class<?>> NODE_STYLE_CLASS = new ClassTypeVisualProperty(
			NODE, null, "NODE_STYLE", "Node Style");
	public static final VisualProperty<Class<?>> EDGE_STYLE_CLASS = new ClassTypeVisualProperty(
			EDGE, null, "EDGE_STYLE", "Edge Style");

	public static final VisualProperty<? extends Image> NETWORK_BACKGROUND_IMAGE = new ImageVisualProperty(
			NETWORK, null, "NETWORK_BACKGROUND_IMAGE",
			"Network Background Image");

	public ProcessingVisualLexicon() {
		super();
		this.visualPropertySet.add(NODE_STYLE_CLASS);
		this.visualPropertySet.add(EDGE_STYLE_CLASS);
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
