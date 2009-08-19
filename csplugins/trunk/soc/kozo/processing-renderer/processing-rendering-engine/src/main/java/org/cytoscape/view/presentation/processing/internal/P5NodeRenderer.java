package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.NODE;
import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.NODE_STYLE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_LABEL_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.CyDrawableManager;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;

public class P5NodeRenderer extends AbstractRenderer<View<CyNode>> {
	
	public P5NodeRenderer(PApplet p, CyDrawableManager manager) {
		super(p, manager);
	}

	/**
	 * Build a list of Visual Properties compatible with this renderer.
	 */
	protected VisualLexicon buildLexicon() {
		final VisualLexicon nodeLexicon = new BasicVisualLexicon();
		nodeLexicon.addVisualProperty(NODE_COLOR);
		nodeLexicon.addVisualProperty(NODE_LABEL_COLOR);

		nodeLexicon.addVisualProperty(NODE_X_LOCATION);
		nodeLexicon.addVisualProperty(NODE_Y_LOCATION);
		nodeLexicon.addVisualProperty(NODE_Z_LOCATION);

		nodeLexicon.addVisualProperty(NODE_X_SIZE);
		nodeLexicon.addVisualProperty(NODE_Y_SIZE);
		nodeLexicon.addVisualProperty(NODE_Z_SIZE);
		
		return nodeLexicon;
	}

	public CyDrawable render(View<CyNode> view) {
		// If Style property is available, use it.
		P5Shape shape = view.getVisualProperty(NODE_STYLE);
		CyDrawable style = null;
		if(shape == null)
			style = manager.getDefaultFactory(NODE).getInstance();
		else
			style = manager.getDrawable(shape.getDrawableType());


		style.setContext(view);

		return style;
	}

}
