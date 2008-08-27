
package org.cytoscape.model.network.events;

import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * Just a signal in case anyone wants to 
 * resync after a node has been removed.
 */
public interface RemovedNodeEvent extends CyEvent<CyNetwork> {
}
