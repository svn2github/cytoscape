
package org.cytoscape.attributes.events;

import org.cytoscape.event.CyEventListener;

/**
 * 
 */
public interface AttributeCreatedListener extends CyEventListener {
	public void handleEvent(AttributeCreatedEvent e);
}
