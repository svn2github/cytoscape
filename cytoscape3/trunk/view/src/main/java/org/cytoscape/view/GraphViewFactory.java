
package org.cytoscape.view;

import org.cytoscape.GraphPerspective;
import org.cytoscape.view.impl.DGraphView;

public class GraphViewFactory {
	public static GraphView createGraphView(GraphPerspective gp) {
		return new DGraphView(gp);
	}
}
