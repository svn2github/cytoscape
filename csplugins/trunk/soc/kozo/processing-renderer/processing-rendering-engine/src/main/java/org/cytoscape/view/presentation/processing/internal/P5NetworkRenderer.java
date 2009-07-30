package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.NODE_STYLE;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.internal.shape.Cube;

import processing.core.PApplet;

public class P5NetworkRenderer extends AbstractRenderer<CyNetworkView> {

	public P5NetworkRenderer(PApplet p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected VisualLexicon buildLexicon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CyDrawable render(CyNetworkView view) {
		// If Style property is available, use it.
		CyDrawable style = view.getVisualProperty(NODE_STYLE);
		

		// If not available, use the default CyDrawable, which is a cube.
		if (style == null)
			style = new Cube(p, lexicon);

		style.setContext(view);

		return style;
	}


}
