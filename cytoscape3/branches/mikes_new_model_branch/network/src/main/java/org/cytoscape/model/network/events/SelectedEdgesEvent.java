

package org.cytoscape.model.network.events;

import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public interface SelectedEdgesEvent extends CyEvent<CyNetwork> {
	public List<CyEdge> getEdgeList();
}
