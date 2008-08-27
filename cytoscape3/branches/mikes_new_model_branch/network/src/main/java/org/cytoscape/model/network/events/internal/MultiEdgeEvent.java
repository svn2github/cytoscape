


package org.cytoscape.model.network.events.internal;

import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.events.SelectedEdgesEvent;
import org.cytoscape.model.network.events.UnselectedEdgesEvent;

import java.util.List;

/**
 * 
 */
public class MultiEdgeEvent extends NetEvent<List<CyEdge>> 
	implements SelectedEdgesEvent, UnselectedEdgesEvent {

	public MultiEdgeEvent(List<CyEdge> e, CyNetwork n) {
		super(e,n);
	}

	public List<CyEdge> getEdgeList() {
		return get();
	}
}
