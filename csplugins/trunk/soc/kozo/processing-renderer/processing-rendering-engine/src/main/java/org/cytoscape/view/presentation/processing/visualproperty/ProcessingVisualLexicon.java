package org.cytoscape.view.presentation.processing.visualproperty;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NODE;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {

	private final Map<CyDrawable, VisualLexicon> lexiconMap;
	
	public static final VisualProperty<? extends CyDrawable> NODE_STYLE = new CyDrawableVisualProperty(
			NODE, null, "NODE_STYLE", "Node Style");
	public static final VisualProperty<? extends CyDrawable> EDGE_STYLE = new CyDrawableVisualProperty(
			EDGE, null, "EDGE_STYLE", "Edge Style");	
	
	public ProcessingVisualLexicon() {
		super();
		this.visualPropertySet.add(NODE_STYLE);
		this.visualPropertySet.add(EDGE_STYLE);
		
		lexiconMap = new HashMap<CyDrawable, VisualLexicon>();
	}


	public VisualLexicon getSubLexicon(CyDrawable drawable) {
		return lexiconMap.get(drawable);
	}

	public void registerSubLexicon(CyDrawable drawable, VisualLexicon lexicon) {
		this.lexiconMap.put(drawable, lexicon);
	}

}
