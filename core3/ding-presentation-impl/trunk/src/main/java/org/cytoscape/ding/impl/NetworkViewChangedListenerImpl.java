
package org.cytoscape.ding.impl;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.ding.GraphView;

import java.util.Map;
import java.util.HashMap;

public class NetworkViewChangedListenerImpl implements NetworkViewChangedListener {
	public Map<CyNetworkView, GraphView> viewMap = new HashMap<CyNetworkView, GraphView>();
	
	public void handleEvent(NetworkViewChangedEvent nvce) {
		GraphView gv = viewMap.get(nvce.getNetworkView());
		if ( gv != null )
			gv.updateView();
	}

	void addGraphView( CyNetworkView nv, GraphView gv ) {
		if ( nv != null && gv != null )
			viewMap.put(nv,gv);	
		else
			throw new IllegalArgumentException("trying to add null view");
	}
}
