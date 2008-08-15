
package org.cytoscape.view;

import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.RootGraphFactory;
import org.cytoscape.view.impl.DGraphView;

public class GraphViewFactory {
	public static GraphView createGraphView(CyNetwork gp) {
		return new DGraphView(gp);
	}

	public static GraphView getNullGraphView() {
		return new DGraphView( RootGraphFactory.getRootGraph().getNullGraphPerspective() );
	}
}
