

package org.cytoscape.model.events;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public final class UnselectedNodesEvent extends AbstractNodeListEvent {
	public UnselectedNodesEvent(final CyNetwork source, List<CyNode> nodes) {
		super(source, UnselectedNodesListener.class, nodes);
	}
}
