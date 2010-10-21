

package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import java.util.List;

// TODO remove this event.
public final class UnselectedEdgesEvent extends AbstractEdgeListEvent {
	public UnselectedEdgesEvent(final CyNetwork source, final List<CyEdge> edges) {
		super(source, UnselectedEdgesListener.class, edges);
	}
}
