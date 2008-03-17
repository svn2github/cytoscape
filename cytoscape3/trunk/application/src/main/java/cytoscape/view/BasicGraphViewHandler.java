/*
  File: BasicGraphViewHandler.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

/**
 * @author Iliana Avila-Campillo <iavila@systemsbiology.org>
 * @version %I%, %G%
 * @since 2.0
 */
package cytoscape.view;


import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.util.*;

import org.cytoscape.Edge;
import org.cytoscape.GraphPerspective;
import org.cytoscape.GraphPerspectiveChangeEvent;
import org.cytoscape.Node;


/**
 * A basic <code>GraphViewHandler</code> that simply reflects <code>GraphPerspective</code>
 * changes on a given <code>GraphView</code>
 */
public class BasicGraphViewHandler implements GraphViewHandler {
	/**
	 * Constructor
	 */
	public BasicGraphViewHandler() {
	} //BasicGraphViewHandler

	/**
	 * Handles the event as desired by updating the given <code>giny.view.GraphView</code>.
	 *
	 * @param event the event to handle
	 * @param graph_view the <code>giny.view.GraphView</code> that views the
	 * <code>cytoscape.GraphPerspective</code> that generated the event and that should
	 * be updated as necessary
	 */
	public void handleGraphPerspectiveEvent(GraphPerspectiveChangeEvent event, GraphView graph_view) {
		int numTypes = 0; // An event may have more than one type

		// Node Events:
		if (event.isNodesHiddenType()) {
			removeGraphViewNodes(graph_view, event.getHiddenNodeIndices());
			numTypes++;
		}

		if (event.isNodesRestoredType()) {
			restoreGraphViewNodes(graph_view, event.getRestoredNodeIndices(), true);
			numTypes++;
		}

		// Edge events:
		if (event.isEdgesHiddenType()) {
			removeGraphViewEdges(graph_view, event.getHiddenEdgeIndices());
			numTypes++;
		}

		if (event.isEdgesRestoredType()) {
			restoreGraphViewEdges(graph_view, event.getRestoredEdgeIndices());
			numTypes++;
		}

		if (numTypes == 0) {
			return;
		}

		graph_view.updateView();

	} //handleGraphPerspectiveEvent

	/**
	 * It removes the views of the edges in the array from the given <code>giny.view.GraphView</code>
	 * object.
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object from which edges will be removed
	 * @param edges the edges whose views will be removed
	 * @return an array of edges that were removed
	 */

	// TESTED: Gets an exception because the edges array has references to null.
	// USE INSTEAD: removeGraphViewEdges(GraphView, int [])
	static public Edge[] removeGraphViewEdges(GraphView graph_view, Edge[] edges) {
		Set<Edge> removedEdges = new HashSet<Edge>();

		for (int i = 0; i < edges.length; i++) {
			EdgeView edgeView = graph_view.removeEdgeView(edges[i]);

			if (edgeView != null) {
				removedEdges.add(edges[i]);
			}
		} 

		return (Edge[]) removedEdges.toArray(new Edge[removedEdges.size()]);
	} 

	/**
	 * It removes the views of the edges in the array from the given <code>giny.view.GraphView</code>
	 * object.
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object from which edges will be removed
	 * @param edge_indices the indices of the edges that will be removed
	 * @return an array of edge indices that were removed
	 */

	// TESTED
	// NOTE: USE THIS INSTEAD OF removeGraphViewEdges (GraphView,Edge[])
	static public int[] removeGraphViewEdges(GraphView graph_view, int[] edge_indices) {
		List<Integer> removedEdges = new ArrayList<Integer>(edge_indices.length);

		for (int i = 0; i < edge_indices.length; i++) {
			EdgeView edgeView = graph_view.removeEdgeView(edge_indices[i]);

			if (edgeView != null) {
				removedEdges.add(edge_indices[i]);
			}
		} //for i

		return getArray(removedEdges);
	} //removeGraphViewEdges

	/**
	 * It restores the views of the edges in the array in the given <code>giny.view.GraphView</code>
	 * object
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be restored
	 * @param edges the edges that will be restored
	 * @return an array of edges that were restored
	 */

	// TESTED
	static public Edge[] restoreGraphViewEdges(GraphView graph_view, Edge[] edges) {
		Set<Edge> restoredEdges = new HashSet<Edge>();

		for (int i = 0; i < edges.length; i++) {
			EdgeView edgeView = graph_view.getEdgeView(edges[i]);
			boolean restored = false;

			if (edgeView == null) {
				// This means that the restored edge had not been viewed before
				// by graph_view
				edgeView = graph_view.addEdgeView(edges[i].getRootGraphIndex());

				if (edgeView != null) {
					restored = true;
				}
			} else {
				// This means that the restored edge had been viewed by the graph_view
				// before, so all we need to do is tell the graph_view to re-show it
				restored = graph_view.showGraphObject(edgeView);
			}

			if (restored) {
				restoredEdges.add(edgeView.getEdge());
			}
		} 

		return (Edge[]) restoredEdges.toArray(new Edge[restoredEdges.size()]);
	} //restoreGraphViewEdges

	/**
	 * It restores the views of the edges with the given indices in the given
	 * <code>giny.view.GraphView</code> object
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which edges' views
	 * will be restored
	 * @param edge_indices the indices of the edges that will be restored
	 * @return an array of indices of edges that were restored
	 */

	// TODO: What if a connected node is not in the graph view or graph perspective?
	static public int[] restoreGraphViewEdges(GraphView graph_view, int[] edge_indices) {
		List<Integer> restoredEdgeIndices = new ArrayList<Integer>(edge_indices.length);

		for (int i = 0; i < edge_indices.length; i++) {
			// The given index can be either RootGraph index or GraphPerspective index
			EdgeView edgeView = graph_view.getEdgeView(edge_indices[i]);
			boolean restored = false;

			if (edgeView == null) {
				// This means that the restored edge had not been viewed before
				// by graph_view
				edgeView = graph_view.addEdgeView(edge_indices[i]);

				if (edgeView != null) {
					restored = true;
				}
			} else {
				// This means that the restored edge had been viewed by the graph_view
				// before, so all we need to do is tell the graph_view to re-show it
				restored = graph_view.showGraphObject(edgeView);
			}

			if (restored) {
				restoredEdgeIndices.add(edge_indices[i]);
			}
		} 

		return getArray(restoredEdgeIndices);
	} 

	/**
	 * It selects the edges in the array in the given <code>giny.view.GraphView</code> object.
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be selected
	 * @param edges the edges in <code>graph_view</code> that will be selected
	 * @return the edges that were selected
	 */
	static public Edge[] selectGraphViewEdges(GraphView graph_view, Edge[] edges) {
		Set<Edge> selectedEdges = new HashSet<Edge>();

		for (int i = 0; i < edges.length; i++) {
			EdgeView edgeView = graph_view.getEdgeView(edges[i]);

			if (edgeView != null) {
				edgeView.setSelected(true);
				selectedEdges.add(edges[i]);
			}
		}

		return (Edge[]) selectedEdges.toArray(new Edge[selectedEdges.size()]);
	}

	/**
	 * It unselects the edges in the array in the given  <code>giny.view.GraphView</code> object
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be unselected
	 * @param edges the edges that will be unselected in <code>graph_view</code>
	 * @return an array of edges that were unselected
	 */
	static public Edge[] unselectGraphViewEdges(GraphView graph_view, Edge[] edges) {
		Set<Edge> unselectedEdges = new HashSet<Edge>();

		for (int i = 0; i < edges.length; i++) {
			EdgeView edgeView = graph_view.getEdgeView(edges[i]);

			if (edgeView != null) {
				edgeView.setSelected(false);
				unselectedEdges.add(edges[i]);
			}
		}

		return (Edge[]) unselectedEdges.toArray(new Edge[unselectedEdges.size()]);
	} //unselectGraphViewEdges

	/**
	 * It removes the nodes in the array from the given <code>giny.view.GraphView</code> object,
	 * it also removes the connected edges to these nodes (an edge without a connecting node makes
	 * no mathematical sense).
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object from which nodes will be removed
	 * @param nodes the nodes whose views will be removed from <code>graph_view</code>
	 * @return an array of nodes that were removed
	 */

	// NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
	// and this hiding fires a hideEdgesEvent, so removeGraphViewEdges will get called on those
	// edges and we don't need to hide them in this method
	// TESTED
	static public Node[] removeGraphViewNodes(GraphView graph_view, Node[] nodes) {
		Set<Node> removedNodes = new HashSet<Node>();

		for (int i = 0; i < nodes.length; i++) {
			NodeView nodeView = graph_view.removeNodeView(nodes[i]);

			if (nodeView != null) {
				removedNodes.add(nodes[i]);
			}
		} 
		
		return (Node[]) removedNodes.toArray(new Node[removedNodes.size()]);
	} 

	/**
	* It removes the views of the nodes with the given indices that are contained in the given
	* <code>giny.view.GraphView</code> object, it also removes the connected edges to
	* these nodes (an edge without a connecting node makes no mathematical sense).
	*
	* @param graph_view the <code>giny.view.GraphView</code> object from which nodes will be removed
	* @param node_indices the indices of the nodes that will be removed
	* @return an array of indices of nodes that were removed
	*/

	// NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
	// and this hiding fires a hideEdgesEvent, so removeGraphViewEdges will get called on those
	// edges and we don't need to remove them in this method
	static public int[] removeGraphViewNodes(GraphView graph_view, int[] node_indices) {
		List<Integer> removedNodesIndices = new ArrayList<Integer>(node_indices.length);

		for (int i = 0; i < node_indices.length; i++) {
			NodeView nodeView = graph_view.removeNodeView(node_indices[i]);

			if (nodeView != null) {
				removedNodesIndices.add(node_indices[i]);
			}
		} //for i

		return getArray(removedNodesIndices);
	} //removeGraphViewNodes

	/**
	 * It restores the views of the nodes in the array in the given
	 * <code>giny.view.GraphView</code> object
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be restored
	 * @param nodes the nodes whose views will be restored in <code>graph_view</code>
	 * @param restore_connected_edges whether or not the connected edges to the restored nodes
	 * should also be restored or not (for now this argument is ignored)
	 * @return an array of nodes that were restored
	 */

	static public Node[] restoreGraphViewNodes(GraphView graph_view, Node[] nodes,
	                                           boolean restore_connected_edges) {
		Set<Node> restoredNodes = new HashSet<Node>();

		for (int i = 0; i < nodes.length; i++) {
			NodeView nodeView = graph_view.getNodeView(nodes[i]);
			boolean restored = false;

			if (nodeView == null) {
				// This means that the nodes that were restored had never been viewed by
				// the graph_view, so we need to create a new NodeView.
				nodeView = graph_view.addNodeView(nodes[i].getRootGraphIndex());

				if (nodeView != null) {
					restored = true;
				}
			} else {
				// This means that the nodes that were restored had been viewed by the graph_view
				// before, so all we need to do is tell the graph_view to re-show them
				restored = graph_view.showGraphObject(nodeView);
			}

			if (restored) {
				positionToBarycenter(nodeView);
				restoredNodes.add(nodeView.getNode());
			}
		} 
		 

		return (Node[]) restoredNodes.toArray(new Node[restoredNodes.size()]);
	} //restoreGraphViewNodes

	/**
	 * It restores the views of the nodes with the given indices in the given
	 * <code>giny.view.GraphView</code> object
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which node views will be restored
	 * @param node_indices the indices of the nodes whose views will be restored
	 * @param restore_connected_edges whether or not the connected edges to the restored nodes
	 * should also be restored or not (for now this argument is ignored)
	 * @return an array of indices of the nodes whose views were restored
	 */

	//TODO: Depending on restore_connected_edges, restore connected edges or not.
	static public int[] restoreGraphViewNodes(GraphView graph_view, int[] node_indices,
	                                          boolean restore_connected_edges) {
		List<Integer> restoredNodeIndices = new ArrayList<Integer>(node_indices.length);

		for (int i = 0; i < node_indices.length; i++) {
			NodeView nodeView = graph_view.getNodeView(node_indices[i]);
			boolean restored = false;

			if (nodeView == null) {
				// This means that the nodes that were restored had never been viewed by
				// the graph_view, so we need to create a new NodeView.
				nodeView = graph_view.addNodeView(node_indices[i]);

				if (nodeView != null) {
					restored = true;
				}
			} else {
				// This means that the nodes that were restored had been viewed by the graph_view
				// before, so all we need to do is tell the graph_view to re-show them
				restored = graph_view.showGraphObject(nodeView);
			}

			if (restored) {
				restoredNodeIndices.add(node_indices[i]);
				positionToBarycenter(nodeView);

			} 
		} //for i

		return getArray(restoredNodeIndices);
	} //restoreGraphViewNodes

	/**
	 * It selects the nodes in the array in the given <code>giny.view.GraphView</code> object.
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be selected
	 * @param nodes the nodes in <code>graph_view</code> that will be selected
	 * @return the nodes that were selected
	 */
	static public Node[] selectGraphViewNodes(GraphView graph_view, Node[] nodes) {
		Set<Node> selectedNodes = new HashSet<Node>();

		for (int i = 0; i < nodes.length; i++) {
			NodeView nodeView = graph_view.getNodeView(nodes[i]);

			if (nodeView != null) {
				nodeView.setSelected(true);
				selectedNodes.add(nodes[i]);
			}
		} 

		return (Node[]) selectedNodes.toArray(new Node[selectedNodes.size()]);
	} //selectGraphViewNodes

	/**
	 * It unselects the nodes in the array in the given  <code>giny.view.GraphView</code> object
	 *
	 * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be unselected
	 * @param nodes the nodes that will be unselected in <code>graph_view</code>
	 * @return an array of nodes that were unselected
	 */
	static public Node[] unselectGraphViewNodes(GraphView graph_view, Node[] nodes) {
		Set<Node> unselectedNodes = new HashSet<Node>();

		for (int i = 0; i < nodes.length; i++) {
			NodeView nodeView = graph_view.getNodeView(nodes[i]);

			if (nodeView != null) {
				nodeView.setSelected(false);
				unselectedNodes.add(nodes[i]);
			}
		} 
		
		return (Node[]) unselectedNodes.toArray(new Node[unselectedNodes.size()]);
	} //unselectGraphViewNodes

	/**
	 * If the node that node_view represents is a meta-node, then it
	 * positions it at the barycenter of its viewable children nodes.
	 *
	 * @param node_view the <code>giny.view.NodeView</code> that will be positioned
	 * to the barycenter of its children
	 */
	static public void positionToBarycenter(NodeView node_view) {
		return;
	} 

	/**
	 * Updates the given graph_view to contain node and edge visual representations
	 * of only nodes and edges that are in its <code>GraphPerspective</code>
	 *
	 * @see GraphViewController#resumeListening()
	 * @see GraphViewController#resumeListening(GraphView)
	 */
	public void updateGraphView(GraphView graph_view) {
		GraphPerspective graphPerspective = graph_view.getGraphPerspective();

		int[] nia = graphPerspective.getNodeIndicesArray();
		List<Integer> gpNodeIndices = new ArrayList<Integer>(nia.length);
		for ( int i : nia)
			gpNodeIndices.add(i);

		int[] eia = graphPerspective.getEdgeIndicesArray();
		List<Integer> gpEdgeIndices = new ArrayList<Integer>(eia.length);
		for ( int i : eia)
			gpEdgeIndices.add(i);


		List<Integer> gvNodeIndices = new ArrayList<Integer>(graph_view.getNodeViewCount());
		List<Integer> gvEdgeIndices = new ArrayList<Integer>(graph_view.getEdgeViewCount());

		// Obtain a list of nodes' root indices that are represented in graph_view
		Iterator it = graph_view.getNodeViewsIterator();

		while (it.hasNext()) {
			NodeView nodeView = (NodeView) it.next();
			Node gvNode = nodeView.getNode();

			if (gvNode == null) {
				System.err.println("Node for nodeView is null (nodeView  = " + nodeView + ")");

				continue;
			}

			int nodeIndex = gvNode.getRootGraphIndex();
			gvNodeIndices.add(nodeIndex);
		} // while there are more graph view nodes

		// Obtain a list of edges that are represented in graph_view,
		// and remove EdgeViews that are no longer in graph_perspective
		it = graph_view.getEdgeViewsIterator();

		while (it.hasNext()) {
			EdgeView edgeView = (EdgeView) it.next();
			Edge gvEdge = edgeView.getEdge();

			if (gvEdge == null) {
				System.err.println("Edge for edgeView is null (edgeView  = " + edgeView + ")");

				continue;
			}

			int edgeIndex = gvEdge.getRootGraphIndex();
			gvEdgeIndices.add(edgeIndex);
		} // while there are more graph view edges

		// Make sure that graph_view represents all nodes that are
		// currently in graphPerspective
		for (int i = 0; i < gpNodeIndices.size(); i++) {
			int nodeIndex = gpNodeIndices.get(i);
			NodeView nodeView = graph_view.getNodeView(nodeIndex);

			if (nodeView == null) {
				graph_view.addNodeView(nodeIndex);
			} else {
				graph_view.showGraphObject(nodeView);
			}
		} // for each graphPerspective node

		// Make sure that graph_view represents all edges that are
		// currently in graphPerspective
		for (int i = 0; i < gpEdgeIndices.size(); i++) {
			int edgeIndex = gpEdgeIndices.get(i);
			EdgeView edgeView = graph_view.getEdgeView(edgeIndex);

			if (edgeView == null) {
				graph_view.addEdgeView(edgeIndex);
			} else {
				graph_view.showGraphObject(edgeView);
			}
		} // for each GraphPerspective edge

		// Remove from graph_view all edge representations that are not in graphPerspective
		gvEdgeIndices.removeAll(gpEdgeIndices);

		for (int i = 0; i < gvEdgeIndices.size(); i++) {
			graph_view.removeEdgeView(gvEdgeIndices.get(i));
		} // for each edge that is in graph_view but that is not in graphPerspective

		// Remove from graph_view all node representations that are not in graphPerspective
		gvNodeIndices.removeAll(gpNodeIndices);

		for (int i = 0; i < gvNodeIndices.size(); i++) {
			graph_view.removeNodeView(gvNodeIndices.get(i));
		} // for each node that is in graph_view but that is not in graphPerspective
	} //updateGraphview

	private static int[] getArray(List<Integer> l) {
		int[] ret = new int[l.size()];
		int i = 0;
		for ( Integer I : l )
			ret[i++] = I.intValue();
		return ret;
	}
} //classs BasicGraphViewHandler
