

package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public final class SelectedEdgesEvent extends AbstractEdgeListEvent {
	public SelectedEdgesEvent(final CyNetwork source, final List<CyEdge> edges) {
		super( source, SelectedEdgesListener.class, edges );
	}
}
