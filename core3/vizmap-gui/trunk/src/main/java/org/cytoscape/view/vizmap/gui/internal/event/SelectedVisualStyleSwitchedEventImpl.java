package org.cytoscape.view.vizmap.gui.internal.event;

import java.awt.Component;

import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedEvent;

public class SelectedVisualStyleSwitchedEventImpl implements
		SelectedVisualStyleSwitchedEvent {

	private VisualStyle newStyle;
	private VisualStyle lastStyle;

	private Component source;
	
	public SelectedVisualStyleSwitchedEventImpl(VisualStyle lastStyle,
			VisualStyle newStyle, Component source) {
		this.newStyle = newStyle;
		this.lastStyle = lastStyle;
		this.source = source;

	}

	public VisualStyle getNewVisualStyle() {
		return newStyle;
	}

	public VisualStyle getLastVisualStyle() {
		return lastStyle;
	}

	public Component getSource() {
		return source;
	}

}
