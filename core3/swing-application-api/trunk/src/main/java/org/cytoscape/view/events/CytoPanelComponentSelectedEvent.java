
package org.cytoscape.view.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.CytoPanel;
import org.cytoscape.view.CytoPanelState;

/**
 * 
 */
public final class CytoPanelComponentSelectedEvent extends AbstractCyEvent<Object> {

	private final CytoPanel cp;
	private final int index;

	public CytoPanelComponentSelectedEvent(final Object source, final CytoPanel cp, int index) {
		super(source, CytoPanelComponentSelectedListener.class);
		this.cp = cp;
		this.index = index;
	}

	public CytoPanel getCytoPanel() {
		return cp;
	}

	public int getSelectedIndex() {
		return index;
	}
}
