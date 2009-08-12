package org.cytoscape.layer.internal;

import static org.cytoscape.model.GraphObject.NODE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.layer.MultiLayerNetworkBuilder;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

/**
 * Build actual network here
 * 
 * @author kono
 * 
 */
public class MultiLayerNetworkBuilderImpl implements MultiLayerNetworkBuilder {

	private static final String NETWORK_TITLE = "name";
	private static final String NODE_TITLE = "name";
	private static final String EDGE_TITLE = "name";
	private static final String LAYER_INDEX = "layer index";
		
	private Map<String, CyDataTable> netAttrMgr;
	
	private CyNetworkManager manager;
	private CyNetwork layeredNetwork;
	private CyNetworkFactory factory;
	
	private VisualMappingManager visualMappingManager;
	private VisualStyle visualStyle;
	
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
		layeredNetwork.attrs().set(NETWORK_TITLE, "Layered Network");

		netAttrMgr = layeredNetwork.getCyDataTables(NODE);
		netAttrMgr.get(CyNetwork.DEFAULT_ATTRS).createColumn(LAYER_INDEX, String.class, false);

		// HashSet cumulatedNodes = new HashSet();
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		Map<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();

		// Connect layers here...
				
		for (int i = 0; i < layers.size()-1; i++) {
			CyNetwork topLayer = layers.get(i);
			CyNetwork connector=connectors.get(i);
			CyNetwork bottomLayer = layers.get(i+1);
			
			connect(topLayer, connector, bottomLayer, nodeMap, edgeMap, i, i+1);
			
		}
		
		nodeMap.clear();
		nodeMap = null;

		manager.addNetwork(layeredNetwork);

		System.out.println("layer index attribute test start!!");
		
		for (CyNode cyNode : layeredNetwork.getNodeList()){
			
			System.out.println("NODE_NAME");
			System.out.println(cyNode.attrs().get(NODE_TITLE, String.class));
			System.out.println("NODE_LAYER_INDEX");
			System.out.println(cyNode.attrs().get(LAYER_INDEX, String.class));

		}

		System.out.println("OK!");
		
		return layeredNetwork;
	}

	private void connect(CyNetwork topLayer, CyNetwork connector,
			CyNetwork bottomLayer, Map<String, CyNode> nodeMap,
			Map<String, CyEdge> edgeMap, int topLayerIndex, int bottomLayerIndex) {
		// Connect them

		// Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		// 1st Phase: add all nodes in the top layer
		for (CyNode cyNode : topLayer.getNodeList()) {

			String nodeName = cyNode.attrs().get(NODE_TITLE, String.class);
			
			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set(NODE_TITLE, nodeName);

				newNode.attrs().set(LAYER_INDEX, Integer.toString(topLayerIndex));

				nodeMap.put(nodeName, newNode);
			}
		}

		for (CyEdge edge : topLayer.getEdgeList()) {

			String edgeName = edge.attrs().get(NETWORK_TITLE, String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs().get(NODE_TITLE, String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs().get(NODE_TITLE, String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set(EDGE_TITLE, edgeName);
				edgeMap.put(edgeName, newEdge);
			}

		}

		// 2nd Phase: append nodes only included in bottom layer
		for (CyNode cyNode : bottomLayer.getNodeList()) {

			String nodeName = cyNode.attrs().get(NODE_TITLE, String.class);
			
			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set(NODE_TITLE, nodeName);

				newNode.attrs().set(LAYER_INDEX, Integer.toString(bottomLayerIndex));

				nodeMap.put(nodeName, newNode);
			}

		}

		for (CyEdge edge : bottomLayer.getEdgeList()) {

			String edgeName = edge.attrs().get(EDGE_TITLE, String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs().get(NODE_TITLE, String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs().get(NODE_TITLE, String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set(EDGE_TITLE, edgeName);
				edgeMap.put(edgeName, newEdge);
			}

		}
		
		// 3rd Phase: append nodes only included in connector
		for (CyNode cyNode : connector.getNodeList()) {

			String nodeName = cyNode.attrs().get(NODE_TITLE, String.class);
			
			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set(NODE_TITLE, nodeName);
				nodeMap.put(nodeName, newNode);
			}

		}

		for (CyEdge edge : connector.getEdgeList()) {

			String edgeName = edge.attrs().get(EDGE_TITLE, String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs().get(NODE_TITLE, String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs().get(NODE_TITLE, String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set(EDGE_TITLE, edgeName);
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

	public void setVisualMappingManager(VisualMappingManager visualMappingManager){
		this.visualMappingManager = visualMappingManager;
	}
	
	public void setVisualStyle(){
		this.visualStyle = visualMappingManager.getVisualStyle(manager.getCurrentNetworkView());
	}
	
	public void buildVisualStyle(){
		DiscreteMapping discreteMapping = new DiscreteMapping(LAYER_INDEX, String.class, NODE_Z_LOCATION); 
	}
	
}
