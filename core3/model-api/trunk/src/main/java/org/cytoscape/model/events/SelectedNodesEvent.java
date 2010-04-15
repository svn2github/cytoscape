

package org.cytoscape.model.events;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public final class SelectedNodesEvent extends AbstractNodeListEvent {
	public SelectedNodesEvent(final CyNetwork source, List<CyNode> nodes) {
		super(source, SelectedNodesListener.class, nodes);
	}
}
