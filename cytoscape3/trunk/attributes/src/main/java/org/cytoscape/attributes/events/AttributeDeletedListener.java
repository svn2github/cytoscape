
package org.cytoscape.attributes.events;

import org.cytoscape.event.CyEventListener;

/**
 * 
 */
public interface AttributeDeletedListener extends CyEventListener {
	public void handleEvent(AttributeDeletedEvent e);
}
