package cytoscape.actions;

import giny.model.Edge;
import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;


public class SelectShortestPathsPlugin extends CytoscapePlugin {

	public SelectShortestPathsPlugin () {
		NewWindowShortestPathsAction mainAction = new NewWindowShortestPathsAction ();
		Cytoscape.getDesktop().getCyMenus().getNewNetworkMenu().add(mainAction);
		
	}

	// ~ Inner Classes
	// //////////////////////////////////////////////////////////

	public class NewWindowShortestPathsAction extends CytoscapeAction {
		/**
		 * Creates a new NewWindowSelectedNodesEdgesAction object.
		 */
		public NewWindowShortestPathsAction() {
			super("From selected nodes, shortest paths between them");
			setPreferredMenu("File.New.Network");
			setAcceleratorCombo(java.awt.event.KeyEvent.VK_R,
			                      ActionEvent.CTRL_MASK);
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @param e DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			// save the vizmapper catalog
			CytoscapeRootGraph root = Cytoscape.getRootGraph();
			CyNetwork current_network = Cytoscape.getCurrentNetwork();

			CyNetworkView current_network_view = Cytoscape.getCurrentNetworkView();

			if ((current_network == null) || (current_network == Cytoscape.getNullNetwork()))
				return;

			Set nodes = current_network.getSelectedNodes();
			if ((nodes == null) || (nodes.size() < 2))
			{
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "You need to select at least two nodes.  Please try again." );
				return;
			}
			Object [] originalSelectedNodes = nodes.toArray();
//			System.out.println ("Calling NewWindowShortestPaths for " + nodes.size() + " selected nodes.");
			
			// add first Neighbors to selected nodes
			// AJK: 09/11/07 don't need first neighbors
//			Set firstNeighbors = new HashSet ();
//			Iterator<Node> node_it = nodes.iterator();
//			while (node_it.hasNext())
//			{
//				addFirstNeighbors (firstNeighbors, node_it.next(), current_network, root);
//				
//			}
//			
//
//			Iterator<Node> node_it2 = firstNeighbors.iterator();
//			while (node_it2.hasNext())
//			{
//				nodes.add(node_it2.next());
//			}
			
			Set edges = new HashSet();
			int [] node_idx = new int [nodes.size()];
			
			// convert nodes into an array of integet indices
			Iterator<Node> it = nodes.iterator();
			int i = 0;
			while (it.hasNext())
			{
				node_idx[i] = root.getIndex(it.next());
				i++;
			}
			
			// now calculate all the shortest paths
			Set shortestPathNodes = new HashSet();
//			System.out.println ("Calculating shortest paths for set of " + nodes.size() + " nodes");
			for (int j = 0; j < nodes.size() - 1; j++)
			{
				for (int k = j + 1; k < nodes.size(); k++)
				{
	//				System.out.println("Getting shortest path for nodes at indices " + j + " and " + k);
		        	int [] new_nodes = getShortestPath (node_idx[j], node_idx[k], root, current_network);
		        	if (new_nodes != null)
		        	{
			        	for (int m = 0; m < new_nodes.length; m++)
			        	{
			        		shortestPathNodes.add(current_network.getNode(m));
			        		if (m > 0)
			        		{
			        			edges.add(findEdgeBetween (new_nodes[m], new_nodes[m - 1], current_network));
			        		}
			        	}	        		
		        	}
	        	
				}
			}
			Iterator<Node> node_it3 = shortestPathNodes.iterator();
			while (node_it3.hasNext())
			{
				Node n = node_it3.next();
				if (n != null)
				{
					nodes.add(node_it3.next());
				}
				
			}

//			System.out.println ("creating network for nodes: " + nodes + " and edges " + edges);
			CyNetwork new_network = Cytoscape.createNetwork(nodes, edges,
			                                                CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
			                                                current_network);

			String title = " selection";
			Cytoscape.createNetworkView(new_network, title);
			
			
			for (int kount = 0; kount < originalSelectedNodes.length; kount++)
			{
				
				Node n = (Node) originalSelectedNodes[kount];
				new_network.setSelectedNodeState(n, true);
			}

			// Set visual style
//			Cytoscape.getNetworkView(new_network.getIdentifier())
//			         .setVisualStyle(Cytoscape.getCurrentNetworkView().getVisualStyle().getName());

			CyNetworkView new_view = Cytoscape.getNetworkView(new_network.getIdentifier());

			if (new_view == Cytoscape.getNullNetworkView()) {
				return;
			}

			if (current_network_view != Cytoscape.getNullNetworkView()) {
				Iterator iter = new_network.nodesIterator();

				while (iter.hasNext()) {
					Node node = (Node) iter.next();
					new_view.getNodeView(node)
					        .setOffset(current_network_view.getNodeView(node).getXPosition(),
					                   current_network_view.getNodeView(node).getYPosition());
				}

				new_view.fitContent();

				// Set visual style
				VisualStyle newVS = current_network_view.getVisualStyle();

				if (newVS != null) {
					new_view.setVisualStyle(newVS.getName());
				} else {
					new_view.setVisualStyle("default");
				}
			} else {
				new_view.setVisualStyle("default");
			}
		}
		
		/**
		 * add first neighbors of selected node to nodes list
		 */
		private void addFirstNeighbors (Set nodes, Node node, CyNetwork network, CytoscapeRootGraph root)
		{
			int n1 = root.getIndex(node);
			int[] adjEdges = network.getAdjacentEdgeIndicesArray(n1,true,true,true);
			
			for (int i = 0; i < adjEdges.length; i++) {
				int source = network.getEdgeSourceIndex(adjEdges[i]);
				int	target = network.getEdgeTargetIndex(adjEdges[i]);
				int n2 = (n1 == source) ? target : source;
//				System.out.println ("Adding node of index " + n2 + " to array of nodes " + nodes + " in RootGraph: " + root);
				nodes.add(root.getNode(n2));

				}			
		}
		
		
		/**
		 * Find the edge that connects the two nodes
		 */
		private Edge findEdgeBetween (int n1, int n2, CyNetwork network)
		{
			int[] adjEdges = network.getAdjacentEdgeIndicesArray(n1,true,true,true);
//			System.out.println ("looking for adjacent edges for node: " + n1);
			
			for (int i = 0; i < adjEdges.length; i++) {
				int source = network.getEdgeSourceIndex(adjEdges[i]);
				int	target = network.getEdgeTargetIndex(adjEdges[i]);
				if (((n1 == source) && (n2 == target)) ||
						((n1 == target) && (n2 == source)))
				{
//					System.out.println("found edge: " + (Cytoscape.getRootGraph().getEdge(adjEdges[i])));
					return (Cytoscape.getRootGraph().getEdge(adjEdges[i]));
				}
			}
			// we have an internal error if we reach this point
//			System.out.println("INTERNAL ERROR: Edge expected but not found for nodes " + Cytoscape.getRootGraph().getNode(n1) 
//					+ ", " + Cytoscape.getRootGraph().getNode(n2));
			return null;
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
		public int[] getShortestPath(int node1, int node2, CytoscapeRootGraph root, CyNetwork network) {
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
			seen.put(cynode1, new Integer(0));
			List fringe = new ArrayList();
			fringe.add(cynode1);
			while (!fringe.isEmpty()) {
				CyNode v = (CyNode) fringe.get(0);
				int nv = root.getIndex(v);
				int vnv = network.getIndex(v);
				fringe.remove(0);
				dist.put(v, seen.get(v));
				if (nv == node2) break;
				int[] adjEdges = network.getAdjacentEdgeIndicesArray(vnv,true,true,true);
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
					Integer tmp = (Integer) dist.get(v);
					int vwLength = tmp.intValue() + 1;
					if (dist.containsKey(w)) {
						Integer tmp2 = (Integer)dist.get(w);
						if (vwLength < tmp2.intValue()){
							System.err.println("Contraditory paths found");
						}
					}
						else {
							Integer tmp3 = (Integer) seen.get(w);
							if ((!seen.containsKey(w)) || (vwLength < tmp3.intValue())) {
								seen.put(w, new Integer(vwLength));
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
			if (path == null) return null;
			int[] pathArray = new int[path.size()];
//			for (int i = 0; i < pathArray.length; i++) {
//				Integer tmp4  = (Integer)path.get(i);
//				pathArray[i] = tmp4.intValue();
//			}
			int i = 0;
			for (Iterator iter = path.iterator(); iter.hasNext();) {
				CyNode node = (CyNode) iter.next();
				pathArray[i] = root.getIndex(node);
				i++;
			}
			return pathArray;
		}


		public void menuSelected(MenuEvent e) {
	        CyNetwork n = Cytoscape.getCurrentNetwork();
	        if ( n == null || n == Cytoscape.getNullNetwork() ) {
	           	setEnabled(false); 
				return;
			}

	        java.util.Set edges = n.getSelectedEdges();
	        java.util.Set nodes = n.getSelectedNodes();

	        if ( ( nodes != null && nodes.size() > 0 ) ||
	             ( edges != null && edges.size() > 0 ) )
	            setEnabled(true);
	        else
	            setEnabled(false);

		}
	}
}