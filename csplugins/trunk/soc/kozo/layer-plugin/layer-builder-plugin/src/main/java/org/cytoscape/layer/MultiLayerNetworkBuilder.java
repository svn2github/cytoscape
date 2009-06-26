package org.cytoscape.layer;

import java.util.List;

import org.cytoscape.model.CyNetwork;

public interface MultiLayerNetworkBuilder {

	public CyNetwork buildLayeredNetwork(List<CyNetwork> layers,
			List<CyNetwork> connectors);

}
