
package org.cytoscape.view;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.impl.DGraphView;

public interface GraphViewFactory {
	GraphView createGraphView(CyNetwork gp);
}
