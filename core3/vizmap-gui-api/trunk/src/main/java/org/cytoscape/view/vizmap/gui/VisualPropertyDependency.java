package org.cytoscape.view.vizmap.gui;

import java.util.Set;

import org.cytoscape.view.model.VisualProperty;

/**
 * Defines the dependency
 * 
 */
public interface VisualPropertyDependency {
	
	String getDisplayName();
	
	VisualProperty<?> getParent();
	
	Set<VisualProperty<?>> getChildren();

}
