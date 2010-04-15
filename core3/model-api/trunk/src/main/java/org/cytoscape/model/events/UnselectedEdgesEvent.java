

package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public final class UnselectedEdgesEvent extends AbstractEdgeListEvent {
	public UnselectedEdgesEvent(final CyNetwork source, final List<CyEdge> edges) {
		super(source, UnselectedEdgesListener.class, edges);
	}
}
