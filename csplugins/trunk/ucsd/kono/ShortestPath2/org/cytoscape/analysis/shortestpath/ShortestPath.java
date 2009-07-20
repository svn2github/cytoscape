package org.cytoscape.analysis.shortestpath;



import java.util.*;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.data.*;
//import cytoscape.data.Semantics;
import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.view.CyNetworkView;

/**
 * 
 * Class to calculate the shortest path between 2 nodes in the network 
 * considering the network as a directed or undirected graph.
 * 
 * @author mrsva
 *
 */
public class ShortestPath {

	private CyNetwork network;
	private CyNetworkView view;
	private CytoscapeRootGraph root;
	private CyAttributes NodeAttributes;
	private CyAttributes EdgeAttributes;
	//private boolean debug = true;
	private boolean undirected;
	private boolean incoming;
	private boolean outcoming;
	private String selectedAttribute;
	private boolean attributeIsInt;
	private boolean attributeIsDouble;
	private boolean attributeIsNegLog;

	/**
	 * Get the selected nodes and calculate the shortest path
	 * @param directed use this parameter to indicate if the network is directed
	 */
	public void calculate(boolean directed,String attribute) {
		network = Cytoscape.getCurrentNetwork();
		view = Cytoscape.getCurrentNetworkView();
		NodeAttributes = Cytoscape.getNodeAttributes();
		EdgeAttributes = Cytoscape.getEdgeAttributes();
		root = Cytoscape.getRootGraph();
		selectedAttribute = attribute;
		
		if(!selectedAttribute.equals("Hop Distance"))
		{
			byte type = EdgeAttributes.getType(selectedAttribute);
		
			if(type == EdgeAttributes.TYPE_INTEGER)
				attributeIsInt = true;
			else if(type == EdgeAttributes.TYPE_FLOATING)
				attributeIsDouble = true;
			else {
				JOptionPane.showMessageDialog(view.getComponent(), 
				"The Attribute selected is no longer availible. Please update the attribute list");
				return;
			}
		}
		
		if (directed) {
			setDirected();
		} else {
			setUndirected();
		}
		int[] selNodes = view.getSelectedNodeIndices();
		if (selNodes.length != 2) {
			JOptionPane.showMessageDialog(view.getComponent(), 
					"You should select 2 nodes to get the shortest path between them");
		}
		int result = 0;
		int node1 = selNodes[1];
		int node2 = selNodes[0];
		CyNode n1 = (CyNode)network.getNode(node1);
		CyNode n2 = (CyNode)network.getNode(node2);
		
		if (directed) {
			 result = JOptionPane.showConfirmDialog(view.getComponent(),"Source node = " +
					n1.getIdentifier() +
					"\nTarget node = " +
					n2.getIdentifier() +
					"\n \n Do you want to switch them around?");			
			
			if (result == 0) {
				int tmp = node1;
				node1 = node2;
				node2 = tmp;
				
			}
		}
		if (result == 2) return; 
		List shortestPath = getShortestPath(node1,node2);
		
		if (shortestPath == null){
			JOptionPane.showMessageDialog(view.getComponent(),
					"There is no path between the selected nodes");
		}
		else {
			network.setSelectedNodeState(shortestPath, true);
		}
	}

	/**
	 * 
	 */
	public void setUndirected() {
		undirected = true;
		incoming = true;
		outcoming = true;
	}

	/**
	 * 
	 */
	public void setDirected() {
		undirected = false;
		incoming = false;
		outcoming = true;
	}

	/**
	 *
	 */
	public void setNegativeLog(boolean val) {
		this.attributeIsNegLog = val;
	}

	/**
	 *
	 */
	public boolean getNegativeLog() {
		return this.attributeIsNegLog;
	}

	/**
	 * Calculates the shortest path between node1 and node2
	 * 
	 * The algorithm is based on the code from dijkstra() function from NetworkX:
	 * 
	 * http://networkx.sourceforge.net/Reference/NX.paths-module.html#dijkstra
	 * 
	 * but I didn't used the weight to calculate the distance. To do this, just
	 * change the line:
	 * 
	 * <pre>int vwLength = tmp.intValue() + 1;</pre>
	 * 
	 * in the source code to:
	 * 
	 * <pre>int vwLength = tmp.intValue() + getWeight();</pre>
	 * 
	 * and implement the method <pre>getWeight()</pre>.
	 * 
	 * NetworkX website: http://networkx.sourceforge.net/
	 * 
	 * @param node1 source node
	 * @param node2 target node
	 * @return list of nodes in the path between node1 and node2. If there is no path, returns null
	 */
	public List getShortestPath(int node1, int node2) {
		boolean foundContradictory = false;
		if (node1 == node2) return null;
		int nnodes = root.getNodeCount();
		CyNode cynode1 = (CyNode) network.getNode(node1);
		CyNode cynode2 = (CyNode) network.getNode(node2);
		HashMap dist = new HashMap();
		HashMap paths = new HashMap();
		List tmpList = new ArrayList();
		tmpList.add(cynode1);
		paths.put(cynode1, tmpList);
		Hashtable seen = new Hashtable();
		seen.put(cynode1, new Double(0));
		List fringe = new ArrayList();
		fringe.add(cynode1);
		while (!fringe.isEmpty()) {
			CyNode v = (CyNode) fringe.get(0);
			int nv = root.getIndex(v);
			int vnv = network.getIndex(v);
			fringe.remove(0);
			dist.put(v, seen.get(v));
			if (nv == node2) break;
			int[] adjEdges = network.getAdjacentEdgeIndicesArray(vnv,undirected,incoming,outcoming);
			int[] adjNodes = new int[adjEdges.length];
			for (int i = 0; i < adjNodes.length; i++) {
				int node = network.getEdgeSourceIndex(adjEdges[i]);
				if (node == nv) 
					node = network.getEdgeTargetIndex(adjEdges[i]);
				adjNodes[i] = node;
			}
			if ((adjEdges.length == 0) && (paths.size() == 1)) return null;
			for (int n = 0; n < adjNodes.length; n++) {
				int nw = adjNodes[n];
				CyNode w = (CyNode) network.getNode(nw);
				Double tmp = (Double) dist.get(v);
				CyEdge edge = (CyEdge)network.getEdge(adjEdges[n]);
				double vwLength = tmp.doubleValue() + getWeight(edge);
				if (dist.containsKey(w)) {
					Double tmp2 = (Double)dist.get(w);
					if (vwLength < tmp2.doubleValue()){
						if (!foundContradictory) {
							System.err.println("Contraditory paths found");
							foundContradictory=true;
						}
					}
				}
					else {
						Double tmp3 = (Double) seen.get(w);
						if ((!seen.containsKey(w)) || (vwLength < tmp3.doubleValue())) {
							seen.put(w, new Double(vwLength));
							fringe.add(w);
							List tmpList2 = new ArrayList();
							tmpList2.add(w);
							List tmpList3 = (List)paths.get(v);
							List tmpList4 = new ArrayList();
							tmpList4.addAll(tmpList3);
							tmpList4.addAll(tmpList2);
							paths.put(w, tmpList4);
						}
					}
			}
		}
		List path = (List) paths.get(cynode2);
		double pathDist = ((Double)dist.get(cynode2)).doubleValue();
		if (attributeIsNegLog && pathDist != 0.0) {
			pathDist = Math.pow(10,-pathDist);
		}
		JOptionPane.showMessageDialog(view.getComponent(), 
			"Length of Path is " + pathDist);
		return path;
	}

	
	private double getWeight(CyEdge edge) { 
		
		if(selectedAttribute.equals("Hop Distance"))
			return 1;
		
		else if(attributeIsInt)
			return EdgeAttributes.getIntegerAttribute(edge.getIdentifier(),selectedAttribute).doubleValue();
		else {
			double attr = EdgeAttributes.getDoubleAttribute(edge.getIdentifier(),selectedAttribute).doubleValue();
			if (attributeIsNegLog) {
				attr = -Math.log10(attr);
			}
			return attr;	
		}
	}
}

