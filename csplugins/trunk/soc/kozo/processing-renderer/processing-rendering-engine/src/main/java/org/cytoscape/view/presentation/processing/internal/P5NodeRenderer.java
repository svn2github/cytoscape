package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.*;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.internal.shape.Cube;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;

public class P5NodeRenderer extends AbstractRenderer<View<CyNode>> {

	public P5NodeRenderer(PApplet p) {
		super(p);
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
		CyDrawable style = view.getVisualProperty(NODE_STYLE);
		

		// If not available, use the default CyDrawable, which is a cube.
		if (style == null)
			style = new Cube(p, lexicon);

		style.setContext(view);

		return style;
	}

}
