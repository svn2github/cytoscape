
package org.cytoscape.model.events;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * Just a signal in case anyone wants to 
 * resync after an edge has been removed.
 */
public interface RemovedEdgeEvent extends CyEvent<CyNetwork> {
}
