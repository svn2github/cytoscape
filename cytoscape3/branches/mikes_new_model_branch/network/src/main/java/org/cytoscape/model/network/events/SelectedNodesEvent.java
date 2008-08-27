

package org.cytoscape.model.network.events;

import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public interface SelectedNodesEvent extends CyEvent<CyNetwork> {
	public List<CyNode> getNodeList();
}
