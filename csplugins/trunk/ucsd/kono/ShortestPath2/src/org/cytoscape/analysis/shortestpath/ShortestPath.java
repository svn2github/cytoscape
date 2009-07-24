package org.cytoscape.analysis.shortestpath;

import giny.model.Node;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
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
	// private boolean debug = true;
	private boolean undirected;
	private boolean incoming;
	private boolean outcoming;
	private String selectedAttribute;
	private boolean attributeIsInt;
	private boolean attributeIsDouble;
	private boolean attributeIsNegLog;

	/**
	 * Get the selected nodes and calculate the shortest path
	 * 
	 * @param directed
	 *            use this parameter to indicate if the network is directed
	 */
	public List<Node> calculate(boolean directed, String attribute) {
		network = Cytoscape.getCurrentNetwork();
		view = Cytoscape.getCurrentNetworkView();
		NodeAttributes = Cytoscape.getNodeAttributes();
		EdgeAttributes = Cytoscape.getEdgeAttributes();
		root = Cytoscape.getRootGraph();
		selectedAttribute = attribute;

		if (!selectedAttribute.equals("Hop Distance")) {
			byte type = EdgeAttributes.getType(selectedAttribute);

			if (type == EdgeAttributes.TYPE_INTEGER)
				attributeIsInt = true;
			else if (type == EdgeAttributes.TYPE_FLOATING)
				attributeIsDouble = true;
			else {
				JOptionPane
						.showMessageDialog(
								view.getComponent(),
								"The Attribute selected is no longer availible. Please update the attribute list");
				return null;
			}
		}

		if (directed) {
			setDirected();
		} else {
			setUndirected();
		}
		int[] selNodes = view.getSelectedNodeIndices();
		if (selNodes.length != 2) {
			JOptionPane
					.showMessageDialog(view.getComponent(),
							"You should select 2 nodes to get the shortest path between them");
		}
		int result = 0;
		int node1 = selNodes[1];
		int node2 = selNodes[0];
		CyNode n1 = (CyNode) network.getNode(node1);
		CyNode n2 = (CyNode) network.getNode(node2);

		if (directed) {
			result = JOptionPane.showConfirmDialog(view.getComponent(),
					"Source node = " + n1.getIdentifier() + "\nTarget node = "
							+ n2.getIdentifier()
							+ "\n \n Do you want to switch them around?");

			if (result == 0) {
				int tmp = node1;
				node1 = node2;
				node2 = tmp;

			}
		}
		if (result == 2)
			return null;

		List<Node> shortestPath = getShortestPath(node1, node2);

		if (shortestPath == null) {
			JOptionPane.showMessageDialog(view.getComponent(),
					"There is no path between the selected nodes");
		} else {
			network.setSelectedNodeState(shortestPath, true);

		}
		return shortestPath;
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
	 * The algorithm is based on the code from dijkstra() function from
	 * NetworkX:
	 * 
	 * http://networkx.sourceforge.net/Reference/NX.paths-module.html#dijkstra
	 * 
	 * but I didn't used the weight to calculate the distance. To do this, just
	 * change the line:
	 * 
	 * <pre>
	 * int vwLength = tmp.intValue() + 1;
	 * </pre>
	 * 
	 * in the source code to:
	 * 
	 * <pre>
	 * int vwLength = tmp.intValue() + getWeight();
	 * </pre>
	 * 
	 * and implement the method
	 * 
	 * <pre>
	 * getWeight()
	 * </pre>
	 * 
	 * .
	 * 
	 * NetworkX website: http://networkx.sourceforge.net/
	 * 
	 * @param node1
	 *            source node
	 * @param node2
	 *            target node
	 * @return list of nodes in the path between node1 and node2. If there is no
	 *         path, returns null
	 */
	public List<Node> getShortestPath(int node1, int node2) {
		boolean foundContradictory = false;
		if (node1 == node2)
			return null;

		selectedAttribute = "Hop Distance";
		setUndirected();
		network = Cytoscape.getCurrentNetwork();
		view = Cytoscape.getCurrentNetworkView();
		NodeAttributes = Cytoscape.getNodeAttributes();
		EdgeAttributes = Cytoscape.getEdgeAttributes();
		root = Cytoscape.getRootGraph();
		Node cynode1 = network.getNode(node1);
		Node cynode2 = network.getNode(node2);

		final Map<Node, Double> dist = new HashMap<Node, Double>();
		final Map<Node, List<Node>> paths = new HashMap<Node, List<Node>>();

		List<Node> tmpList = new ArrayList<Node>();

		tmpList.add(cynode1);
		paths.put(cynode1, tmpList);
		Hashtable<Node, Double> seen = new Hashtable<Node, Double>();
		seen.put(cynode1, 0.0);
		List<Node> fringe = new ArrayList<Node>();
		fringe.add(cynode1);

		Node v;
		while (!fringe.isEmpty()) {
			v = fringe.get(0);
			int nv = root.getIndex(v);
			int vnv = network.getIndex(v);
			fringe.remove(0);
			dist.put(v, seen.get(v));
			if (nv == node2)
				break;

			int[] adjEdges = network.getAdjacentEdgeIndicesArray(vnv,
					undirected, incoming, outcoming);
			int[] adjNodes = new int[adjEdges.length];
			for (int i = 0; i < adjNodes.length; i++) {
				int node = network.getEdgeSourceIndex(adjEdges[i]);
				if (node == nv)
					node = network.getEdgeTargetIndex(adjEdges[i]);
				adjNodes[i] = node;
			}
			if ((adjEdges.length == 0) && (paths.size() == 1))
				return null;
			for (int n = 0; n < adjNodes.length; n++) {
				int nw = adjNodes[n];
				CyNode w = (CyNode) network.getNode(nw);
				Double tmp = (Double) dist.get(v);
				CyEdge edge = (CyEdge) network.getEdge(adjEdges[n]);
				double vwLength = tmp.doubleValue() + getWeight(edge);
				if (dist.containsKey(w)) {
					Double tmp2 = (Double) dist.get(w);
					if (vwLength < tmp2.doubleValue()) {
						if (!foundContradictory) {
							System.err.println("Contraditory paths found");
							foundContradictory = true;
						}
					}
				} else {
					Double tmp3 = (Double) seen.get(w);
					if ((!seen.containsKey(w))
							|| (vwLength < tmp3.doubleValue())) {
						seen.put(w, new Double(vwLength));
						fringe.add(w);
						List tmpList2 = new ArrayList();
						tmpList2.add(w);
						List tmpList3 = (List) paths.get(v);
						List tmpList4 = new ArrayList();
						tmpList4.addAll(tmpList3);
						tmpList4.addAll(tmpList2);
						paths.put(w, tmpList4);
					}
				}
			}
		}

		List<Node> path = paths.get(cynode2);

		Double pathDist = dist.get(cynode2);

		if (attributeIsNegLog && pathDist != 0.0) {
			pathDist = Math.pow(10, -pathDist);
		}
		// JOptionPane.showMessageDialog(view.getComponent(),
		// "Length of Path is "
		// + pathDist);
		return path;
	}

	private double getWeight(CyEdge edge) {

		if (selectedAttribute.equals("Hop Distance"))
			return 1;

		else if (attributeIsInt)
			return EdgeAttributes.getIntegerAttribute(edge.getIdentifier(),
					selectedAttribute).doubleValue();
		else {
			double attr = EdgeAttributes.getDoubleAttribute(
					edge.getIdentifier(), selectedAttribute).doubleValue();
			if (attributeIsNegLog) {
				attr = -Math.log10(attr);
			}
			return attr;
		}
	}

	public void searchAll() {
		CyNetwork net = Cytoscape.getCurrentNetwork();
		Random rnd = new Random(System.currentTimeMillis());
		List<Node> nodes = net.nodesList();
		
		final Set<String> pairs = new HashSet<String>();

		try {
			FileOutputStream fos = new FileOutputStream("path_"
					+ net.getTitle() + ".txt");
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);
			StringBuilder builder = new StringBuilder();
			

			Node node2;
			Node node1;
			int index = 0;
			String pairName;
			String pairNameR;
			
			builder.append("Index\tPath\tPath Length\n");
			while(index < 1000) {
				node1 = nodes.get(rnd.nextInt(nodes.size()));
				node2 = nodes.get(rnd.nextInt(nodes.size()));
				if(node1.getIdentifier().equals(node2.getIdentifier()))
					continue;
				
				pairName = node1.getIdentifier() + ":" + node2.getIdentifier();
				pairNameR = node2.getIdentifier() + ":" + node1.getIdentifier();
				if(pairs.contains(pairName) || pairs.contains(pairNameR))
					continue;
				pairs.add(pairName);
				pairs.add(pairNameR);
				
				builder.append("Pair " + (index+1) + ":\t");
				builder.append(node1.getIdentifier() + " to "
						+ node2.getIdentifier() + "\t");

				List<Node> path = getShortestPath(node1.getRootGraphIndex(),
						node2.getRootGraphIndex());

				
				if (path != null) {
					builder.append(path.size() + "\t");
					
					for (Node n2 : path) {
						builder.append(n2.getIdentifier() + "\t");
					}
					builder.append("\n");
				} else {
					builder.append("0\t");
					builder.append("No Path!!\n");
				}
				index++;
				
			}
			bw.write(builder.toString());
			
			bw.close();
			osw.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Path search done!!");
	}
}
