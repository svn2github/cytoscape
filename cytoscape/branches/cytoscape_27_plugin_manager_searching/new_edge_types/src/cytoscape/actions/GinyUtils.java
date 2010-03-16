/*
  File: GinyUtils.java

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

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;


//-------------------------------------------------------------------------
import giny.model.*;

import giny.view.*;

import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Utility operations for selection and hiding/unhiding nodes and edges
 * in a Giny GraphView.
 *
 * Only those nodes and edges that were hidden using this class
 * will be unhidden when using the <code>unHide*</code> methods.
 *
 * Because the Giny API does not provide any means to determine
 * if a NodeView or an EdgeView is visible or not, we must
 * internally store all hidden nodes and edges.
 *
 * This class uses <code>GraphPerspective</code>'s
 * <code>hide*</code> * and <code>restore*</code> methods to hide
 * and unhide nodes and edges.
 */
public class GinyUtils {

	/**
	 * Contains necessary details of a node such that when it becomes
	 * unhidden, it is possible to place it correctly as it was in a
	 * GraphView.
	 */
	static class HiddenNode
	{
		/**
		 * The root graph index.
		 */
		public int index;

		/**
		 * The X coordinate of the node before it was hidden.
		 */
		public double x;

		/**
		 * The Y coordinate of the node before it was hidden.
		 */
		public double y;

		public HiddenNode(NodeView nview)
		{
			this.index = nview.getNode().getRootGraphIndex();
			this.x = nview.getXPosition();
			this.y = nview.getYPosition();
		}
	}

	/**
	 * Ensures that when a graph is destroyed,
	 * it is removed from <code>hiddenNodesMap</code> and
	 * <code>hiddenEdgesMap</code>.
	 */
	static class GraphDestroyedListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			GraphPerspective graph = Cytoscape.getNetwork((String) event.getNewValue());
			if (!hiddenNodesMap.containsKey(graph))
				return;
			hiddenNodesMap.remove(graph);
			hiddenEdgesMap.remove(graph);
		}
	}

	/**
	 * Stores all information for hidden nodes.
	 * All relevant details of a hidden node is
	 * stored using <code>HiddenNode</code> objects.
	 *
	 * Using a map is necessary because once a node
	 * is hidden, one cannot determine what GraphPerspective
	 * the node belongs to.
	 */
	static Map<GraphPerspective, Set<HiddenNode>> hiddenNodesMap = new HashMap<GraphPerspective, Set<HiddenNode>>();

	/**
	 * Stores root graph indices of hidden edges.
	 *
	 * Using a map is necessary because once an edge
	 * is hidden, one cannot determine what GraphPerspective
	 * the edge belongs to.
	 */
	static Map<GraphPerspective, Set<Integer>> hiddenEdgesMap = new HashMap<GraphPerspective, Set<Integer>>();

	static GraphDestroyedListener graphDestroyedListener = null;

	/**
	 * Ensures that <code>graph</code> is a valid key in
	 * <code>hiddenNodesMap</code> and <code>hiddenEdgesMap</code>.
	 */
	private static void ensureGraph(GraphPerspective graph)
	{
		if (hiddenNodesMap.containsKey(graph))
			return;
		hiddenNodesMap.put(graph, new HashSet<HiddenNode>());
		hiddenEdgesMap.put(graph, new HashSet<Integer>());

		ensureGraphDestroyedListener();
	}

	/**
	 * Ensures that <code>graphDestroyedListener</code> is
	 * constructed and listening to events.
	 *
	 * Because it may be the case that <code>GinyUtils</code>
	 * <code>hide*</code> methods
	 * may never be used, we initialize and register
	 * <code>graphDestroyedListener</code> only when
	 * we actually do hide a node or an edge.
	 */
	private static void ensureGraphDestroyedListener()
	{
		if (graphDestroyedListener != null)
			return;

		graphDestroyedListener = new GraphDestroyedListener();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED, graphDestroyedListener);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void hideSelectedNodes(GraphView view) {
		//hides nodes and edges between them
		if (view == null) {
			return;
		}

		GraphPerspective graph = view.getGraphPerspective();
		ensureGraph(graph);
		Set<HiddenNode> hiddenNodes = hiddenNodesMap.get(graph);
		Set<Integer> hiddenEdges = hiddenEdgesMap.get(graph);

		for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			HiddenNode hiddenNode = new HiddenNode(nview);
			hiddenNodes.add(hiddenNode);

			// Hide all adjacent edges as well
			int[] edges = graph.getAdjacentEdgeIndicesArray(hiddenNode.index, true, true, true);
			for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++)
			{
				hiddenEdges.add(edges[edgeIndex]);
				graph.hideEdge(edges[edgeIndex]);
			}
			graph.hideNode(hiddenNode.index);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void unHideSelectedNodes(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			view.showGraphObject(nview);

			int[] na = view.getGraphPerspective().neighborsArray(nview.getGraphPerspectiveIndex());

			for (int i2 = 0; i2 < na.length; ++i2) {
				int[] edges = view.getGraphPerspective()
				                  .getEdgeIndicesArray(nview.getGraphPerspectiveIndex(), na[i2],
				                                       true, true);

				for (int j = 0; j < edges.length; ++j) {
					view.showGraphObject(view.getEdgeView(edges[j]));
				}
			}
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void unHideAll(GraphView view) {
		if (view == null) {
			return;
		}

		GraphPerspective graph = view.getGraphPerspective();
		Set<HiddenNode> hiddenNodes = hiddenNodesMap.get(graph);
		if (hiddenNodes == null)
			return;

		for (HiddenNode hiddenNode : hiddenNodes)
		{
			graph.restoreNode(hiddenNode.index);
			NodeView nodeView = view.getNodeView(hiddenNode.index);
			if (nodeView == null)
				nodeView = view.addNodeView(hiddenNode.index);

			// When we restore a node, Fing does not automatically
			// apply the visual style; we have to do it ourselves.
			if (view instanceof cytoscape.view.CyNetworkView)
				((cytoscape.view.CyNetworkView) view).applyVizMap(nodeView);

			// Reposition the node correctly
			nodeView.setXPosition(hiddenNode.x);
			nodeView.setYPosition(hiddenNode.y);
		}
		hiddenNodes.clear();

		Set<Integer> hiddenEdges = hiddenEdgesMap.get(graph);
		for (Integer hiddenEdge : hiddenEdges)
		{
			graph.restoreEdge(hiddenEdge);
			EdgeView edgeView = view.getEdgeView(hiddenEdge);
			if (edgeView == null)
				edgeView = view.addEdgeView(hiddenEdge);

			// When we restore an edge, Fing does not automatically
			// apply the visual style; we have to do it ourselves.
			if (view instanceof cytoscape.view.CyNetworkView)
				((cytoscape.view.CyNetworkView) view).applyVizMap(edgeView);
		}
		hiddenEdges.clear();

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void unHideNodesAndInterconnectingEdges(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			Node n = nview.getNode();

			view.showGraphObject(nview);

			int[] na = view.getGraphPerspective().neighborsArray(nview.getGraphPerspectiveIndex());

			for (int i2 = 0; i2 < na.length; ++i2) {
				int[] edges = view.getGraphPerspective()
				                  .getEdgeIndicesArray(nview.getGraphPerspectiveIndex(), na[i2],
				                                       true);

				if (edges != null)
					for (int j = 0; j < edges.length; ++j) {
						EdgeView ev = view.getEdgeView(edges[j]);
						view.showGraphObject(ev);
					}
			}
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void hideSelectedEdges(GraphView view) {
		if (view == null) {
			return;
		}

		GraphPerspective graph = view.getGraphPerspective();
		ensureGraph(graph);
		Set<Integer> hiddenEdges = hiddenEdgesMap.get(graph);

		for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext();) {
			EdgeView eview = (EdgeView) i.next();
			hiddenEdges.add(eview.getEdge().getRootGraphIndex());
			graph.hideEdge(eview.getEdge());
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void unHideSelectedEdges(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext();) {
			EdgeView eview = (EdgeView) i.next();
			view.showGraphObject(eview);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void invertSelectedNodes(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			nview.setSelected(!nview.isSelected());
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void invertSelectedEdges(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext();) {
			EdgeView eview = (EdgeView) i.next();
			eview.setSelected(!eview.isSelected());
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void selectFirstNeighbors(GraphView view) {
		if (view == null) {
			return;
		}

		GraphPerspective graphPerspective = view.getGraphPerspective();
		Set nodeViewsToSelect = new HashSet();

		for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			Node n = nview.getNode();

			for (Iterator ni = graphPerspective.neighborsList(n).iterator(); ni.hasNext();) {
				Node neib = (Node) ni.next();
				NodeView neibview = view.getNodeView(neib);
				nodeViewsToSelect.add(neibview);
			}
		}

		for (Iterator si = nodeViewsToSelect.iterator(); si.hasNext();) {
			NodeView nview = (NodeView) si.next();
			nview.setSelected(true);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void selectAllNodes(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			nview.setSelected(true);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void deselectAllNodes(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
			NodeView nview = (NodeView) i.next();
			nview.setSelected(false);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void selectAllEdges(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext();) {
			EdgeView eview = (EdgeView) i.next();
			eview.setSelected(true);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void deselectAllEdges(GraphView view) {
		if (view == null) {
			return;
		}

		for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext();) {
			EdgeView eview = (EdgeView) i.next();
			eview.setSelected(false);
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void hideAllEdges(GraphView view) {
		if (view == null) {
			return;
		}

		GraphPerspective graph = view.getGraphPerspective();
		ensureGraph(graph);
		Set<Integer> hiddenEdges = hiddenEdgesMap.get(graph);

		for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext();) {
			EdgeView eview = (EdgeView) i.next();
			hiddenEdges.add(eview.getEdge().getRootGraphIndex());
			graph.hideEdge(eview.getEdge());
		}

		view.updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public static void unHideAllEdges(GraphView view) {
		if (view == null) {
			return;
		}

		GraphPerspective graph = view.getGraphPerspective();
		Set<Integer> hiddenEdges = hiddenEdgesMap.get(graph);
		if (hiddenEdges == null)
			return;

		for (Integer hiddenEdge : hiddenEdges)
		{
			graph.restoreEdge(hiddenEdge);
			EdgeView edgeView = view.getEdgeView(hiddenEdge);
			if (edgeView == null)
				edgeView = view.addEdgeView(hiddenEdge);

			// When we restore an edge, Fing does not automatically
			// apply the visual style; we have to do it ourselves.
			if (view instanceof cytoscape.view.CyNetworkView)
				((cytoscape.view.CyNetworkView) view).applyVizMap(edgeView);
		}
		hiddenEdges.clear();

		view.updateView();
	}

}
