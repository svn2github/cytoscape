package org.cytoscape.view.vizmap.gui.event;

import java.awt.Component;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.vizmap.VisualStyle;

public final class SelectedVisualStyleSwitchedEvent extends AbstractCyEvent<Component> {
	
	private final VisualStyle lastVS;
	private final VisualStyle newVS;

	public SelectedVisualStyleSwitchedEvent(final Component source, final VisualStyle lastVS, final VisualStyle newVS) {
		super(source, SelectedVisualStyleSwitchedListener.class);
		this.newVS = newVS;
		this.lastVS = lastVS;
	}
	
	public VisualStyle getLastVisualStyle() {
		return lastVS;
	}

	public VisualStyle getNewVisualStyle() {
		return newVS;
	}

}
