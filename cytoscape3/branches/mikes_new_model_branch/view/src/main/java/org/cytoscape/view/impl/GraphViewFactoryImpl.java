
package org.cytoscape.view.impl;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.impl.DGraphView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.GraphView;

public class GraphViewFactoryImpl implements GraphViewFactory {

	public GraphViewFactoryImpl() {
	}

	public GraphView createGraphView(CyNetwork gp) {
		if ( gp == null )
			throw new NullPointerException("CyNetwork is null");
				// TODO make the null a CyDataTableFactory
		return new DGraphView(gp,null,null);
	}
}
