

package org.cytoscape.model.events;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;
import java.util.List;

public interface SelectedNodesEvent extends CyEvent<CyNetwork> {
	public List<CyNode> getNodeList();
}
