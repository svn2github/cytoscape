package org.cytoscape.cytobridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
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
	
	/** Maintains a reference to all networks created by the plugin. */
	private Map<String, NetworkSync> currentNets;
	
	/** Constructs this Network manager with the needed factories/managers.
	 * 
	 * @param netFact
	 * @param netViewFact
	 * @param netMan
	 * @param netViewMan
	 */
	public NetworkManager(CyNetworkFactory netFact, CyNetworkViewFactory netViewFact,
		   					CyNetworkManager netMan, CyNetworkViewManager netViewMan) {
		
		this.netFact = netFact;
		this.netViewFact = netViewFact;
		this.netMan = netMan;
		this.netViewMan = netViewMan;
		
		currentNets = new HashMap<String, NetworkSync>();
	}
	
	/** Handles creation and updating of a network sent through the plugin.
	 * 
	 * @param netName The name of the network (unique, for identification for updating).
	 * @param nodes A Vector of the nodes in the network.
	 * @param edgeFrom A vector of edge sources.
	 * @param edgeTo A vector of edge destinations.
	 */
	public void pushNetwork(String netName, Vector<Integer> nodes, Vector<Double> edgeFrom, Vector<Double> edgeTo) {
		
		if (currentNets.containsKey(netName)) {
			//Update the appropriate CyNetwork
			System.out.println("Updating network "+netName);
			CyNetwork network = currentNets.get(netName).getNetwork();
			CyNode node = network.addNode();
			network.getRow(node).set(CyNetwork.NAME, "55");
			currentNets.get(netName).getNodeMap().put(55, node);
			
		} else {
			//Create the CyNetwork
			System.out.println("Creating network "+netName);
			CyNetwork network = netFact.createNetwork();
			network.getRow(network).set(CyNetwork.NAME,netName);
			
			//Create maps from the 3rd party ID's to CyNode/CyEdge
			HashMap<Integer, CyNode> nodeMap = new HashMap<Integer, CyNode>();
			HashMap<Integer, CyEdge> edgeMap = new HashMap<Integer, CyEdge>();
			
			//Create all of the CyNodes
			for (Integer n : nodes) {
				CyNode node = network.addNode();
				network.getRow(node).set(CyNetwork.NAME, n+"");
				nodeMap.put(n, node);
				System.out.println("Added node "+n);
			}
			System.out.println("Got nodes...");
			
			//Create all of the CyEdges
			for (int e=0; e<edgeFrom.size();e++) {
				CyNode fromNode = nodeMap.get(edgeFrom.get(e).intValue());
				CyNode toNode = nodeMap.get(edgeTo.get(e).intValue());
				CyEdge edge = network.addEdge(fromNode, toNode, false);
				network.getRow(edge).set(CyNetwork.NAME, e+"");
				edgeMap.put(e, edge);
			}
			System.out.println("Got edges...");
			
			//Create a network view for this network
			CyNetworkView networkView = netViewFact.createNetworkView(network);
			
			//Store a reference to this network and its associated maps
			currentNets.put(netName, new NetworkSync(network, networkView, nodeMap, edgeMap));
			
			//Add network and network view to appropriate managers
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
	
	
	public void pushTables(String netName, Vector<String> nheads, Vector<String> ndata, Vector<String> eheads, Vector<String> edata) {
		if (!currentNets.containsKey(netName)) {
			System.out.println("That network doesn't exist!");
			return ;
		}
		
		CyNetwork network = currentNets.get(netName).getNetwork();
		CyTable ntable = network.getDefaultNodeTable();
		
		for (int n=0; n<nheads.size(); n++) {
			ntable.createColumn(nheads.get(n), String.class, true, "");
			for (int i=0; i<ndata.size()/nheads.size();i++) {
				//ntable.getRow(i).set(nheads.get(n), ndata.get(i));
			}
		}
		
		CyTable etable = network.getDefaultEdgeTable();
		
		for (int e=0; e<eheads.size(); e++) {
			etable.createColumn(eheads.get(e), String.class, true, "");
			for (int i=0; i<edata.size()/eheads.size();i++) {
				//etable.getRow(i).set(eheads.get(e), edata.get(i));
			}
		}
		
		System.out.println("Updating tables for network "+netName);
	}

}
