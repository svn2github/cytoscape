package org.cytoscape.view.vizmap.gui.dependency;

import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.gui.AbstractVisualPropertyDependency;

public class NodeSizeDependency extends AbstractVisualPropertyDependency {
	
	private static final String NAME = "Lock node width and height";
		
	public NodeSizeDependency() {
		super(NAME);
		
		group.add(TwoDVisualLexicon.NODE_X_SIZE);
		group.add(TwoDVisualLexicon.NODE_Y_SIZE);
	}
}
