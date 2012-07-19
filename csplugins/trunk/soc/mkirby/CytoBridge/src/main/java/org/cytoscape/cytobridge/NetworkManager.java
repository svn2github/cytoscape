package org.cytoscape.cytobridge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
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
	
	private CyTableFactory tabFact;
	private CyTableManager tabMan;
	
	/** Maintains a reference to all networks created by the plugin. */
	private Map<String, NetworkSync> currentNets;
	
	/** Maintains a reference to all tables created by the plugin. */
	private Map<String, CyTable> currentTabs;
	
	/** Constructs this Network manager with the needed factories/managers.
	 * 
	 * @param netFact
	 * @param netViewFact
	 * @param netMan
	 * @param netViewMan
	 */
	public NetworkManager(CyNetworkFactory netFact, CyNetworkViewFactory netViewFact,
		   					CyNetworkManager netMan, CyNetworkViewManager netViewMan,
		   					CyTableFactory tabFact, CyTableManager tabMan) {
		
		this.netFact = netFact;
		this.netViewFact = netViewFact;
		this.netMan = netMan;
		this.netViewMan = netViewMan;
		
		this.tabFact = tabFact;
		this.tabMan = tabMan;
		
		currentNets = new HashMap<String, NetworkSync>();
		
		currentTabs = new HashMap<String, CyTable>();
	}
	
	/** Handles creation and updating of a network sent through the plugin.
	 * 
	 * @param netName The name of the network (unique, for identification for updating).
	 * @param nodes A Vector of the nodes in the network.
	 * @param edgeFrom A vector of edge sources.
	 * @param edgeTo A vector of edge destinations.
	 */
	public void pushNetwork(String netName, Vector<Integer> nodes, Vector<Integer> edges, Vector<Integer> edgeFrom, Vector<Integer> edgeTo) {
		
		if (currentNets.containsKey(netName)) {
			//Update the appropriate CyNetwork
			System.out.println("Updating network "+netName);
			CyNetwork network = currentNets.get(netName).getNetwork();
			
			//Any nodes to Add?
			for (Integer n : nodes) {
				if (!currentNets.get(netName).getNodeMap().containsKey(n)) {
					System.out.println("Adding node "+n);
					CyNode node = network.addNode();
					network.getRow(node).set(CyNetwork.NAME, n+"");
					currentNets.get(netName).getNodeMap().put(n, node);
				}
			}
			//Any nodes to remove?
			Vector<CyNode> remove = new Vector<CyNode>();
			for (Integer n : currentNets.get(netName).getNodeMap().keySet()) {
				if (!nodes.contains(n)) {
					System.out.println("Removing node "+n);
					remove.add(currentNets.get(netName).getNodeMap().get(n));
				}
			}
			network.removeNodes(remove);
			
			//Any edges to Add?
			int i = 0;
			for (Integer e : edges) {
				if (!currentNets.get(netName).getEdgeMap().containsKey(e)) {
					System.out.println("Adding edge "+e);
					CyNode fromNode = currentNets.get(netName).getNodeMap().get(edgeFrom.get(i).intValue());
					CyNode toNode = currentNets.get(netName).getNodeMap().get(edgeTo.get(i).intValue());
					CyEdge edge = network.addEdge(fromNode, toNode, false);
					network.getRow(edge).set(CyNetwork.NAME, e+"");
					currentNets.get(netName).getEdgeMap().put(e, edge);
				}
				i++;
			}
			//Any edges to remove?
			Vector<CyEdge> removee = new Vector<CyEdge>();
			for (Integer e : currentNets.get(netName).getEdgeMap().keySet()) {
				if (!edges.contains(e)) {
					System.out.println("Removing edge "+e);
					removee.add(currentNets.get(netName).getEdgeMap().get(e));
				}
			}
			network.removeEdges(removee);
			currentNets.get(netName).getNetworkView().updateView();
			
			System.out.println("Done updating!");
			
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
				network.getRow(edge).set(CyNetwork.NAME, edges.get(e)+"");
				edgeMap.put(edges.get(e), edge);
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
	}
	
	public void pushNetTable(String netName, Vector<String> gheads, Vector<String> gdata) {
		if (!currentNets.containsKey(netName)) {
			System.out.println("That network doesn't exist!");
			return ;
		}
		NetworkSync netSyn = currentNets.get(netName);

		try {
			//Network Attributes
			CyTable gtable = netSyn.getNetwork().getDefaultNetworkTable();
			for (int g=0; g<gheads.size()-1; g++) {
				if (gtable.getColumn(gheads.get(g))==null) {
					gtable.createColumn(gheads.get(g), String.class, true, "");
				}
				gtable.getRow(netSyn.getNetwork().getSUID()).set(gheads.get(g), gdata.get(g));
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Updating network table for network "+netName);
	}
	
	public void pushNodeTable(String netName, Vector<String> nheads, Vector<Integer> nrids, Vector<String> ndata) {
		if (!currentNets.containsKey(netName)) {
			System.out.println("That network doesn't exist!");
			return ;
		}
		NetworkSync netSyn = currentNets.get(netName);

		try {
			//Node attributes
			CyTable ntable = netSyn.getNetwork().getDefaultNodeTable();
			for (int n=0; n<nheads.size()-1; n++) {
				if (ntable.getColumn(nheads.get(n))==null) {
					ntable.createColumn(nheads.get(n), String.class, true, "");
				}
				for (int i=0; i<(ndata.size()-1)/(nheads.size()-1);i++) {
					ntable.getRow(netSyn.getNodeMap().get(nrids.get(i)).getSUID()).set(nheads.get(n), ndata.get(i+(n*(ndata.size()-1)/(nheads.size()-1))));
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Updating node table for network "+netName);
	}
	
	public void pushEdgeTable(String netName, Vector<String> eheads, Vector<Integer> erids, Vector<String> edata) {
		if (!currentNets.containsKey(netName)) {
			System.out.println("That network doesn't exist!");
			return ;
		}
		NetworkSync netSyn = currentNets.get(netName);

		try {
			//Edge Attributes
			CyTable etable = netSyn.getNetwork().getDefaultEdgeTable();
			for (int e=0; e<eheads.size()-1; e++) {
				if (etable.getColumn(eheads.get(e))==null) {
					etable.createColumn(eheads.get(e), String.class, true, "");
				}
				for (int i=0; i<(edata.size()-1)/(eheads.size()-1);i++) {
					etable.getRow(netSyn.getEdgeMap().get(erids.get(i)).getSUID()).set(eheads.get(e), edata.get(i+(e*(edata.size()-1)/(eheads.size()-1))));
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Updating edge table for network "+netName);
	}
	
	public void pushTable(String tabName, Vector<String> heads, Vector<String> data) {
		if (currentTabs.containsKey(tabName)) {
			//Update the appropriate CyTable
			System.out.println("Updating table "+tabName);
			CyTable table = currentTabs.get(tabName);
		} else {
			CyTable table= tabFact.createTable(tabName, "cytobid", Integer.class, true, true);
			
			try {
				for (int e=0; e<heads.size(); e++) {
					table.createColumn(heads.get(e), String.class, true, "");
					for (int i=0; i<(data.size()-1)/(heads.size()-1);i++) {
						//table.getRow().set(heads.get(e), data.get(i+(e*(data.size()-1)/(heads.size()-1))));
					}
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			
			//Store a reference to this network and its associated maps
			currentTabs.put(tabName, table);
			
			//Add network and network view to appropriate managers
			tabMan.addTable(table);
		}
	}

}
