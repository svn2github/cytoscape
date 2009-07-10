package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.NODE;

import gestalt.render.Drawable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.visualproperty.ShapeVisualProperty;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;

public class ProcessingVisualLexicon extends ThreeDVisualLexicon {
	
	private final Map<Class<? extends Drawable>, VisualLexicon> compatibleVP;
	
	public ProcessingVisualLexicon() {
		super();
		compatibleVP = new HashMap<Class<? extends Drawable>, VisualLexicon>();
	}
	
	
	public static final VisualProperty<? extends CyDrawable> NODE_SHAPE = new ShapeVisualProperty(
			NODE, null, "NODE_SHAPE", "Node Shape");
	
	public void addVisualProperty(VisualProperty<?> vp, Map props) {
		this.visualPropertySet.add(vp);
	}
	
	public VisualLexicon getSubLexicon(Class<? extends Drawable> type) {
		return compatibleVP.get(type);
	}
	
	public void addDrawable(CyDrawable drawable) {
		
	}

}
