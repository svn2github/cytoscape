
package org.cytoscape.view.vizmap.events;

import org.cytoscape.event.AbstractCyEvent;

/**
 * 
 */
public final class SaveVizmapPropsEvent extends AbstractCyEvent<Object> {
	public SaveVizmapPropsEvent(final Object source) {
		super(source, SaveVizmapPropsListener.class);
	}
}
