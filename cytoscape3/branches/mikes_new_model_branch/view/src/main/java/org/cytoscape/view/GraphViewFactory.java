
package org.cytoscape.view;

import org.cytoscape.CyNetwork;
import org.cytoscape.RootGraphFactory;
import org.cytoscape.view.impl.DGraphView;

public class GraphViewFactory {
	public static GraphView createGraphView(CyNetwork gp) {
		return new DGraphView(gp);
	}

	public static GraphView getNullGraphView() {
		return new DGraphView( RootGraphFactory.getRootGraph().getNullGraphPerspective() );
	}
}
