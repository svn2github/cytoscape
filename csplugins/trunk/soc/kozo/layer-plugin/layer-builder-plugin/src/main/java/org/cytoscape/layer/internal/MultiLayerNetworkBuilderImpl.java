package org.cytoscape.layer.internal;

import static org.cytoscape.model.GraphObject.NODE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;

import java.awt.Color;
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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
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

	private static final String VISUAL_STYLE_TITLE = "Layer Style";

	private Map<String, CyDataTable> netAttrMgr;

	private CyNetworkManager manager;
	private CyNetwork layeredNetwork;
	private CyNetworkFactory factory;
	private CyNetworkViewFactory networkViewFactory;
	private CyNetworkView networkView;

	private VisualMappingManager vmm;
	private VisualStyle layerVS;

	private List<CyNetwork> layers;
	private List<CyNetwork> connectors;

	/**
	 * Creates a new MultiLayerNetworkBuilderImpl object.
	 * 
	 * @param manager
	 *            The network manager.
	 * @param factory
	 *            factory class to create CyNetwork.
	 * @param vmm
	 *            manager class to add visual mapping.
	 * @param networkViewFactory
	 *            factory class to create CyNetworkView.
	 */
	public MultiLayerNetworkBuilderImpl(CyNetworkManager manager,
			CyNetworkFactory factory, VisualMappingManager vmm,
			CyNetworkViewFactory networkViewFactory) {
		this.manager = manager;
		this.factory = factory;
		this.vmm = vmm;
		this.networkViewFactory = networkViewFactory;
	}

	/**
	 * Build one network using layer network and connector network. The order of
	 * list are represented as layer z location.
	 * 
	 * @param layers
	 *            list of CyNetwork for layer.
	 * @param connectors
	 *            list of CyNetwork to connect layer networks.
	 * @return CyNetwork one CyNetwork connected all layer and connector
	 *         networks.
	 */
	public CyNetwork buildLayeredNetwork(List<CyNetwork> layers,
			List<CyNetwork> connectors) {

		layeredNetwork = factory.getInstance();
		layeredNetwork.attrs().set(NETWORK_TITLE, "Layered Network");

		netAttrMgr = layeredNetwork.getCyDataTables(NODE);
		netAttrMgr.get(CyNetwork.DEFAULT_ATTRS).createColumn(LAYER_INDEX,
				String.class, false);

		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		Map<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();

		// Connect layers here...

		for (int i = 0; i < layers.size() - 1; i++) {
			CyNetwork topLayer = layers.get(i);
			CyNetwork connector = connectors.get(i);
			CyNetwork bottomLayer = layers.get(i + 1);

			connect(topLayer, connector, bottomLayer, nodeMap, edgeMap, i,
					i + 1);

		}

		nodeMap.clear();
		nodeMap = null;

		manager.addNetwork(layeredNetwork);

		setNetworkView(layeredNetwork);
		manager.addNetworkView(networkView);

		System.out.println("layer index attribute test start!!");

		buildVisualStyle();

		System.out.println("OK!");

		return layeredNetwork;
	}

	/**
	 * Actual connecting fucntion to connect each set of two layer and one
	 * connector network.
	 * 
	 * @param topLayer
	 *            The higher CyNetwork to connect.
	 * @param connector
	 *            The CyNetwork to connect higher CyNetwork and lower CyNetwork.
	 * @param bottomLayer
	 *            The lower CyNetwork to connect.
	 * @param nodeMap
	 *            The Hashmap for preventing to create different nodes with same
	 *            name.
	 * @param edgeMap
	 *            The Hashmap for preventing to create different edges connects
	 *            same name nodes.
	 * @param topLayerIndex
	 *            The index for higher layer to identify which layer order.
	 * @param bottomLayerIndex
	 *            The index for lower layer to identify which layer order
	 */
	private void connect(CyNetwork topLayer, CyNetwork connector,
			CyNetwork bottomLayer, Map<String, CyNode> nodeMap,
			Map<String, CyEdge> edgeMap, int topLayerIndex, int bottomLayerIndex) {

		// 1st Phase: add all nodes in the top layer
		for (CyNode cyNode : topLayer.getNodeList()) {

			String nodeName = cyNode.attrs().get(NODE_TITLE, String.class);

			if (nodeMap.containsKey(nodeName) == false) {
				CyNode newNode = layeredNetwork.addNode();
				newNode.attrs().set(NODE_TITLE, nodeName);

				newNode.attrs().set(LAYER_INDEX,
						Integer.toString(topLayerIndex));

				nodeMap.put(nodeName, newNode);
			}
		}

		for (CyEdge edge : topLayer.getEdgeList()) {

			String edgeName = edge.attrs().get(NETWORK_TITLE, String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs()
					.get(NODE_TITLE, String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs()
					.get(NODE_TITLE, String.class);

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

				newNode.attrs().set(LAYER_INDEX,
						Integer.toString(bottomLayerIndex));

				nodeMap.put(nodeName, newNode);
			}

		}

		for (CyEdge edge : bottomLayer.getEdgeList()) {

			String edgeName = edge.attrs().get(EDGE_TITLE, String.class);
			CyNode sourceNode = edge.getSource();
			String sourceName = sourceNode.attrs()
					.get(NODE_TITLE, String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs()
					.get(NODE_TITLE, String.class);

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
			String sourceName = sourceNode.attrs()
					.get(NODE_TITLE, String.class);
			CyNode targetNode = edge.getTarget();
			String targetName = targetNode.attrs()
					.get(NODE_TITLE, String.class);

			if (edgeMap.containsKey(edgeName) == false) {
				CyEdge newEdge = layeredNetwork.addEdge(
						nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.attrs().set(EDGE_TITLE, edgeName);
				edgeMap.put(edgeName, newEdge);
			}

		}

	}

	public CyNetwork buildLayeredNetwork() {
		return this.buildLayeredNetwork(layers, connectors);
	}

	public void setSourceNetworks(List<CyNetwork> layers,
			List<CyNetwork> connectors) {
		this.layers = layers;
		this.connectors = connectors;
	}

	/**
	 * Build visual style for represent layer order as node Z location.
	 * Currently each layer's Z location separated evenly, and top 3 layer nodes
	 * are color-coded. This part needs to be set interactively by users.
	 * 
	 */
	public void buildVisualStyle() {
		layerVS = vmm.createVisualStyle(VISUAL_STYLE_TITLE);
		final DiscreteMapping<String, Double> index2zLocation = new DiscreteMapping<String, Double>(
				LAYER_INDEX, String.class, NODE_Z_LOCATION);
		final DiscreteMapping<String, Color> index2color = new DiscreteMapping<String, Color>(
				LAYER_INDEX, String.class, (VisualProperty<Color>) NODE_COLOR);

		final List<View<CyNode>> nodeViews = networkView.getNodeViews();

		String indexString;

		for (View<CyNode> nv : nodeViews) {
			System.out.println(nv.getSource().attrs().get(NODE_TITLE,
					String.class));
			indexString = nv.getSource().attrs().get(LAYER_INDEX, String.class);
			System.out.println(indexString);
			index2zLocation.putMapValue(indexString, Integer
					.parseInt(indexString) * 300d);
			if (indexString.equals("0")) {
				index2color.putMapValue(indexString, Color.red);
			} else if (indexString.equals("1")) {
				index2color.putMapValue(indexString, Color.green);
			} else if (indexString.equals("2")) {
				index2color.putMapValue(indexString, Color.blue);
			} else {
				index2color.putMapValue(indexString, Color.orange);
			}
		}

		layerVS.addVisualMappingFunction(index2zLocation);
		layerVS.addVisualMappingFunction(index2color);

		vmm.setVisualStyle(layerVS, networkView);
		layerVS.apply(networkView);

	}

	public void setNetworkView(CyNetwork cyNetwork) {
		this.networkView = networkViewFactory.getNetworkViewFor(cyNetwork);
	}

}
