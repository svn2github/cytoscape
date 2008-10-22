


package org.cytoscape.model.events.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.SelectedEdgesEvent;
import org.cytoscape.model.events.UnselectedEdgesEvent;

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
