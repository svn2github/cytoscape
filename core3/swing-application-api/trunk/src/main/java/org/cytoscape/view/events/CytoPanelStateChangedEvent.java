
package org.cytoscape.view.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.CytoPanel;
import org.cytoscape.view.CytoPanelState;

/**
 * 
 */
public final class CytoPanelStateChangedEvent extends AbstractCyEvent<Object> {

	private final CytoPanel cp;
	private final CytoPanelState newState;

	public CytoPanelStateChangedEvent(final Object source, final CytoPanel cp, final CytoPanelState newState) {
		super(source, CytoPanelStateChangedListener.class);
		this.cp = cp;
		this.newState = newState;
	}

	public CytoPanel getCytoPanel() {
		return cp;
	}

	public CytoPanelState getNewState() {
		return newState;
	}
}
