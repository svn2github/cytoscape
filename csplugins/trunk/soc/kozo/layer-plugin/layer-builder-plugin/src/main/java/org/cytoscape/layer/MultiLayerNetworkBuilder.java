package org.cytoscape.layer;

import java.util.List;

import org.cytoscape.model.CyNetwork;

/**
 * 
 * A MultiLayerNetworkBuilder is a interface that set multi layer networks and
 * connector network to connect and build one all layer connected network.
 */

public interface MultiLayerNetworkBuilder {

	/**
	 * set layer and connector networks to connect for 3D layer visualization.
	 * 
	 * @param layers
	 *            list of CyNetwork
	 * @param connectors
	 *            list of CyNetwork
	 */
	public void setSourceNetworks(List<CyNetwork> layers,
			List<CyNetwork> connectors);

	/**
	 * build one all layer connected network.
	 * 
	 * @return CyNetwork one network connected all layer networks
	 */
	public CyNetwork buildLayeredNetwork();

}
