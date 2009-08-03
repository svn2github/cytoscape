package org.cytoscape.layer.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private List<CyNetwork> layers;
	private List<CyNetwork> connectors;

	public MultiLayerNetworkBuilderImpl(CyNetworkManager manager,
			CyNetworkFactory factory) {
		this.manager = manager;
		this.factory = factory;
	}

	public CyNetwork buildLayeredNetwork(List<CyNetwork> layers,
			List<CyNetwork> connectors) {

		layeredNetwork = factory.getInstance();
		layeredNetwork.attrs().set("name", "Layered Network");

		// HashSet cumulatedNodes = new HashSet();
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		Map<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();

		// Connect layers here...
		for (CyNetwork topLayer : layers) {
			for (CyNetwork bottomLayer : layers) {
				for (CyNetwork connector : connectors) {
					connect(topLayer, bottomLayer, connector, nodeMap, edgeMap);
				}
			}
		}

		nodeMap.clear();
		nodeMap = null;

		manager.addNetwork(layeredNetwork);
		return layeredNetwork;
	}

	private void connect(CyNetwork topLayer, CyNetwork connector,
			CyNetwork bottomLayer, Map<String, CyNode> nodeMap,
			Map<String, CyEdge> edgeMap) {
		// Connect them

		// Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		// 1st Phase: add all nodes in the top layer
		for (CyNode cyNode : topLayer.getNodeList()) {

			String nodeName = cyNode.attrs().get("name", String.class);
			Integer layerIndex = topLayer.attrs().get("layerIndex", Integer.class);

			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set("name", nodeName);
				newNode.attrs().set("layerIndex", layerIndex);
				nodeMap.put(nodeName, newNode);
			}
		}

		for (CyEdge edge : topLayer.getEdgeList()) {

			String edgeName = edge.attrs().get("name", String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs().get("name", String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs().get("name", String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set("name", edgeName);
				edgeMap.put(edgeName, newEdge);
			}

		}

		// 2nd Phase: append nodes only included in connector
		for (CyNode cyNode : connector.getNodeList()) {

			String nodeName = cyNode.attrs().get("name", String.class);
			
			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set("name", nodeName);
				nodeMap.put(nodeName, newNode);
			}

		}

		for (CyEdge edge : connector.getEdgeList()) {

			String edgeName = edge.attrs().get("name", String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs().get("name", String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs().get("name", String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set("name", edgeName);
				edgeMap.put(edgeName, newEdge);
			}

		}

		// 3rd Phase: append nodes only included in bottom layer
		for (CyNode cyNode : bottomLayer.getNodeList()) {

			String nodeName = cyNode.attrs().get("name", String.class);
			Integer layerIndex = bottomLayer.attrs().get("layerIndex", Integer.class);
			
			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set("name", nodeName);
				newNode.attrs().set("layerIndex", layerIndex);				
				nodeMap.put(nodeName, newNode);
			}

		}

		for (CyEdge edge : bottomLayer.getEdgeList()) {

			String edgeName = edge.attrs().get("name", String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs().get("name", String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs().get("name", String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set("name", edgeName);
				edgeMap.put(edgeName, newEdge);
			}

		}

		// nodeMap.clear();
		// nodeMap = null;

	}

	public CyNetwork buildLayeredNetwork() {
		// TODO Auto-generated method stub
		return this.buildLayeredNetwork(layers, connectors);
	}

	public void setSourceNetworks(List<CyNetwork> layers,
			List<CyNetwork> connectors) {
		this.layers = layers;
		this.connectors = connectors;
	}
}
