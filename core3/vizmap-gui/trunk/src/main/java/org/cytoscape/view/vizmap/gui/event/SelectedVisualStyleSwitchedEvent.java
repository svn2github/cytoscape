package org.cytoscape.view.vizmap.gui.event;

import java.awt.Component;

import org.cytoscape.event.CyEvent;
import org.cytoscape.view.vizmap.VisualStyle;

public interface SelectedVisualStyleSwitchedEvent extends CyEvent<Component> {
	
	public VisualStyle getLastVisualStyle();

	public VisualStyle getNewVisualStyle();

}
