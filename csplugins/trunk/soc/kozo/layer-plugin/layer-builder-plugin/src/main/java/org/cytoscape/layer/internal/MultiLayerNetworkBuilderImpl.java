package org.cytoscape.layer.internal;

import java.util.List;

import org.cytoscape.layer.MultiLayerNetworkBuilder;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;

import cytoscape.CyNetworkManager;

/**
 * Build actual network here
 * 
 * @author kono
 * 
 */
public class MultiLayerNetworkBuilderImpl implements MultiLayerNetworkBuilder {

	private CyNetworkManager manager;
	private CyNetwork layeredNetwork;
	private CyNetworkFactory factory;

	public MultiLayerNetworkBuilderImpl(CyNetworkManager manager) {
		this.manager = manager;
	}

	public CyNetwork buildLayeredNetwork(List<CyNetwork> layers,
			List<CyNetwork> connectors) {

		layeredNetwork = factory.getInstance();
		layeredNetwork.attrs().set("name", "Layered Network");

		// Connect layers here...
		
		
		manager.addNetwork(layeredNetwork);
		return layeredNetwork;
	}

	private void connect(CyNetwork topLayer, CyNetwork connector, CyNetwork bottomLayer) {
		// Connect them
		
	}

}
