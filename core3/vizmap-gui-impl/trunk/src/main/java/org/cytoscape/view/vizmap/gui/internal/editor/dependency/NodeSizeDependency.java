package org.cytoscape.view.vizmap.gui.internal.editor.dependency;

import java.util.Set;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.VisualPropertyDependency;

public class NodeSizeDependency implements VisualPropertyDependency {
	
	private static final String NAME = "Lock node width and height";

	@Override
	public String getDisplayName() {
		return NAME;
	}

	@Override
	public VisualProperty<?> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<VisualProperty<?>> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
