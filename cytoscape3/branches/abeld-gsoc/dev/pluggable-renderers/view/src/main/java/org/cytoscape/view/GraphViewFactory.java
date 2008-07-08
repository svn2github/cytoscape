
package org.cytoscape.view;

import org.cytoscape.GraphPerspective;
import org.cytoscape.view.impl.DGraphView;
import org.cytoscape.RootGraphFactory;
import org.cytoscape.RootGraph;

public class GraphViewFactory {
	public static GraphView createGraphView(GraphPerspective gp) {
		return new DGraphView(gp);
	}

	public static GraphView getNullGraphView() {
		return new DGraphView( RootGraphFactory.getRootGraph().getNullGraphPerspective() );
	}
}
