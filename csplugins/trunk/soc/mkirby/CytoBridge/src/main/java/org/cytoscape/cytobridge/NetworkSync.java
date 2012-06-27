package org.cytoscape.cytobridge;

import java.util.HashMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class NetworkSync {
	
	private CyNetwork network;
	private HashMap<Integer, CyNode> nodeMap;
	private HashMap<Integer, CyEdge> edgeMap;
	
	public NetworkSync(CyNetwork net, HashMap<Integer, CyNode> nodeMap, HashMap<Integer, CyEdge> edgeMap) {
		this.network = net;
		this.nodeMap = nodeMap;
		this.edgeMap = edgeMap;
	}
	
	public CyNetwork getNetwork() {
		return network;
	}

	public void setEdgeMap(HashMap<Integer, CyEdge> edgeMap) {
		this.edgeMap = edgeMap;
	}

	public HashMap<Integer, CyEdge> getEdgeMap() {
		return edgeMap;
	}

	public void setNodeMap(HashMap<Integer, CyNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public HashMap<Integer, CyNode> getNodeMap() {
		return nodeMap;
	}

}
