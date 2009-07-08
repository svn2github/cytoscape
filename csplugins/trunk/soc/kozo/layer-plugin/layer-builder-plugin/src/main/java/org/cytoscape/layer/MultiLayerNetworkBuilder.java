package org.cytoscape.layer;

import java.util.List;

import org.cytoscape.model.CyNetwork;

public interface MultiLayerNetworkBuilder {
	
	public void setSourceNetworks(List<CyNetwork> layers,
			List<CyNetwork> connectors);

	public CyNetwork buildLayeredNetwork();

}
