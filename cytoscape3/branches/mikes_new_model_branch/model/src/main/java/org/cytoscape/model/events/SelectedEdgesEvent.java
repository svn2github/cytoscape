

package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public interface SelectedEdgesEvent extends CyEvent<CyNetwork> {
	public List<CyEdge> getEdgeList();
}
