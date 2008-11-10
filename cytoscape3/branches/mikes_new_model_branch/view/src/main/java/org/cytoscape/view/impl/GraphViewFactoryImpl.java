
package org.cytoscape.view.impl;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.view.impl.DGraphView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.GraphView;
import org.cytoscape.work.UndoSupport;

public class GraphViewFactoryImpl implements GraphViewFactory {

	private CyDataTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private UndoSupport undo;

	public GraphViewFactoryImpl(CyDataTableFactory dataTableFactory, 
	                            CyRootNetworkFactory rootNetworkFactory,
								UndoSupport undo) {
		this.dataTableFactory = dataTableFactory;
		this.rootNetworkFactory = rootNetworkFactory;
		this.undo = undo;
	}

	public GraphView createGraphView(CyNetwork network) {
		if ( network == null )
			throw new NullPointerException("CyNetwork is null");
		return new DGraphView(network,dataTableFactory,rootNetworkFactory,undo);
	}
}
