
package org.cytoscape.network.events;

import org.cytoscape.network.CyNode;
import org.cytoscape.network.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * Just a signal in case anyone wants to 
 * resync after a node has been removed.
 */
public interface RemovedNodeEvent extends CyEvent<CyNetwork> {
}
