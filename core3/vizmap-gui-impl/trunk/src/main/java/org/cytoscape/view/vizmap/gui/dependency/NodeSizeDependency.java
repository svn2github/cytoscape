package org.cytoscape.view.vizmap.gui.dependency;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.gui.VisualPropertyDependency;

public class NodeSizeDependency implements VisualPropertyDependency {
	
	private static final String NAME = "Lock node width and height";
	
	private Set<VisualProperty<?>> group;
	
	public NodeSizeDependency() {
		group = new HashSet<VisualProperty<?>>();
		
		group.add(TwoDVisualLexicon.NODE_X_SIZE);
		group.add(TwoDVisualLexicon.NODE_Y_SIZE);
	}

	@Override
	public String getDisplayName() {
		return NAME;
	}

	@Override
	public VisualProperty<?> getParent() {
		
		return null;
	}

	@Override
	public Set<VisualProperty<?>> getChildren() {
		return group;
	}

}
