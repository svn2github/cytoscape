
package org.cytoscape.view;

import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.view.impl.DGraphView;

public class GraphViewFactory {
	public static GraphView createGraphView(CyNetwork gp) {
		if ( gp == null )
			throw new NullPointerException("CyNetwork is null");
		return new DGraphView(gp);
	}
}
