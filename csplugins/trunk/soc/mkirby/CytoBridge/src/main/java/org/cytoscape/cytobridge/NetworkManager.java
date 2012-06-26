package org.cytoscape.cytobridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;

/** Handles creating and updating Networks for CytoBridge.
 * @author Michael Kirby
 */
public class NetworkManager {
	
	private CyNetworkFactory netFact;
	private CyNetworkViewFactory netViewFact;
	private CyNetworkManager netMan;
	private CyNetworkViewManager netViewMan;
	
	private Map<String, NetworkSync> currentNets;
	
	public NetworkManager(CyNetworkFactory netFact, CyNetworkViewFactory netViewFact,
		   					CyNetworkManager netMan, CyNetworkViewManager netViewMan) {
		
		this.netFact = netFact;
		this.netViewFact = netViewFact;
		this.netMan = netMan;
		this.netViewMan = netViewMan;
		
		currentNets = new HashMap<String, NetworkSync>();
	}
	
	public void pushNetwork(String netName, Vector<Integer> nodes, Vector<Double> edgeFrom, Vector<Double> edgeTo) {
		
		if (currentNets.containsKey(netName)) {
			System.out.println("Updating network "+netName);
			CyNetwork network = currentNets.get(netName).getNetwork();
			CyNode node = network.addNode();
			network.getRow(node).set(CyNetwork.NAME, "55");
		} else {
			System.out.println("Creating network "+netName);
			CyNetwork network = netFact.createNetwork();
			network.getRow(network).set(CyNetwork.NAME,netName);
			
			currentNets.put(netName, new NetworkSync(network));
			
			Map<String, CyNode> nMap = new HashMap<String, CyNode>();
			
			for (Integer n : nodes) {
				CyNode node = network.addNode();
				network.getRow(node).set(CyNetwork.NAME, n+"");
				nMap.put(n+"", node);
				System.out.println("Added node "+n);
			}
			System.out.println("Got nodes...");
			for (int e=0; e<edgeFrom.size();e++) {
				CyNode fromNode = nMap.get(edgeFrom.get(e).intValue()+"");
				CyNode toNode = nMap.get(edgeTo.get(e).intValue()+"");
				CyEdge edge = network.addEdge(fromNode, toNode, false);
				network.getRow(edge).set(CyNetwork.NAME, e+"");
			}
			System.out.println("Got edges...");
			
			CyNetworkView networkView = netViewFact.createNetworkView(network);
			netMan.addNetwork(network);
			netViewMan.addNetworkView(networkView);
		}
		
		   //create network, record network id
		   //create map from r-id to suid or Cynode
		   //create similar ,map for edges
		   //create all nodes, update maps
		   //create all edges, update maps
		   //create view
		   //register network
		   //register view
	}

}
