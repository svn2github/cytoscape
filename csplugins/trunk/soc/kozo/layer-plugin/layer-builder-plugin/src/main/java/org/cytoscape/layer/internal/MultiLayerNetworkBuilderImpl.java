package org.cytoscape.layer.internal;

import java.util.List;

import org.cytoscape.layer.MultiLayerNetworkBuilder;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;

import org.cytoscape.session.CyNetworkManager;

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
		for(CyNetwork topLayer : layers){
			for(CyNetwork bottomLayer : layers){
				for(CyNetwork connector : connectors){
					connect(topLayer, bottomLayer, connector);
				}
			}
		}
		
		manager.addNetwork(layeredNetwork);
		return layeredNetwork;
	}

	private void connect(CyNetwork topLayer, CyNetwork connector, CyNetwork bottomLayer) {
		// Connect them
		
		// 1st Phase: add all nodes in the top layer
		for(CyNode cyNode : topLayer.getNodeList()){
			CyNode newNode = layeredNetwork.addNode();
			String name = cyNode.attrs().get("name",String.class);
			newNode.attrs().set("name", name);
		}
		for(CyEdge edge: topLayer.getEdgeList()) {
			CyEdge newEdge = layeredNetwork.addEdge(edge.getSource(), edge.getTarget(), true);
			String name = edge.attrs().get("name",String.class);
			newEdge.attrs().set("name", name);
		}
	
		
	}

}
