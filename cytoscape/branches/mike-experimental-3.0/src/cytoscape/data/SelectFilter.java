/*
 File: SelectFilter.java

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

//---------------------------------------------------------------------------
//  $Revision$ 
//  $Date$
//  $Author$
//---------------------------------------------------------------------------
package cytoscape.data;


//---------------------------------------------------------------------------
import giny.filter.Filter;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


//---------------------------------------------------------------------------
/**
 * This class implements the ability to set the selected state of every node or
 * edge in a GraphPerspective. The state can be either on or off. Methods are
 * provided for inspecting the current state of any graph object, for setting
 * the state, or getting the full set of states for nodes or edges. This
 * functionality is often used to identify a set of interesting nodes or edges
 * in the graph.
 * <P>
 *
 * A non-null GraphPerspective reference is required to construct an instance of
 * this class. This class will listen to the graph to respond to the removal of
 * graph objects. A currently selected object that is removed from the graph
 * will lose its selected state, even if it is later added back to the graph.
 * <P>
 *
 * When the selected state of a node or edge is changed, a event of type
 * SelectEvent is fired. When a group of nodes or edges are changed together in
 * a single operation, one event will be fired for the whole group (but separate
 * events for nodes and edges). Note: a listener should not be removed from this
 * object in response to the firing of an event, as this may cause a
 * ConcurrentModificationException.
 * <P>
 *
 * WARNING: for performance reasons, the set of objects returned by the
 * getSelectedXX methods is the actual data object, not a copy. Users should not
 * directly modify these sets.
 * <P>
 *
 * Performance note: the implementation is a HashSet of selected objects, so
 * most methods are O(1). Operations on groups of nodes are O(N) where N is
 * either the number of selected objects or the number of objects in the graph,
 * as applicable.
 * <P>
 */
public class SelectFilter implements Filter, GraphPerspectiveChangeListener {
	private GraphPerspective graph;
	private final Set<Node> selectedNodes = new HashSet<Node>();
	private final Set<Edge> selectedEdges = new HashSet<Edge>();
	
	private List listeners = new ArrayList();

	/**
	 * Standard Constructor. The argument is the graph that this filter will
	 * apply to; it cannot be null.
	 *
	 * @throws NullPointerException
	 *             if the argument is null.
	 */
	public SelectFilter(final GraphPerspective graph) {
		this.graph = graph;

		// this throws a NullPointerException if the graph is null
		graph.addGraphPerspectiveChangeListener(this);
	}

	/**
	 * Returns the set of all selected nodes in the referenced GraphPespective.
	 * <P>
	 *
	 * WARNING: the returned set is the actual data object, not a copy. Don't
	 * directly modify this set.
	 */
	public Set getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Returns the set of all selected edges in the referenced GraphPespective.
	 * <P>
	 *
	 * WARNING: the returned set is the actual data object, not a copy. Don't
	 * directly modify this set.
	 */
	public Set getSelectedEdges() {
		return selectedEdges;
	}

	/**
	 * Returns true if the argument is a selected Node in the referenced
	 * GraphPerspective, false otherwise.
	 */
	public boolean isSelected(Node node) {
		return selectedNodes.contains(node);
	}

	/**
	 * Returns true if the argument is a selected Edge in the referenced
	 * GraphPerspective, false otherwise.
	 */
	public boolean isSelected(Edge edge) {
		return selectedEdges.contains(edge);
	}

	/**
	 * Implementation of the Filter interface. Returns true if the argument is a
	 * selected Node or Edge in the referenced GraphPerspective, false
	 * otherwise.
	 */
	public boolean passesFilter(Object o) {
		if (selectedNodes.contains(o) || selectedEdges.contains(o)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * If the first argument is a Node in the referenced GraphPerspective, sets
	 * its selected state to the value of the second argument. An event will be
	 * fired iff the new state is different from the old state.
	 *
	 * @return true if an actual change was made, false otherwise
	 */
	public boolean setSelected(final Node node, final boolean newState) {
		boolean setChanged;

		if (newState == true) { // set flag to on
			                    // don't flag the node if it's not in the graph

			if (!graph.containsNode(node)) {
				return false;
			}

			setChanged = selectedNodes.add(node);

			if (setChanged) {
				fireEvent(node, true);
			}
		} else { // set flag to off
			     // a node can't be selected unless it's in the graph
			setChanged = selectedNodes.remove(node);

			if (setChanged) {
				fireEvent(node, false);
			}
		}

		return setChanged;
	}

	/**
	 * If the first argument is an Edge in the referenced GraphPerspective, sets
	 * its selected state to the value of the second argument. An event will be
	 * fired iff the new state is different from the old state.
	 *
	 * @return true if an actual change was made, false otherwise
	 */
	public boolean setSelected(final Edge edge, final boolean newState) {
		boolean setChanged;

		if (newState == true) { // set flag to on
			                    // don'tflagthe edge if it's not in the graph

			if (!graph.containsEdge(edge)) {
				return false;
			}

			setChanged = selectedEdges.add(edge);

			if (setChanged) {
				fireEvent(edge, true);
			}
		} else { // set flag to off
			     // an edge can't be selected unless it's in the graph
			setChanged = selectedEdges.remove(edge);

			if (setChanged) {
				fireEvent(edge, false);
			}
		}

		return setChanged;
	}

	/**
	 * Sets the selected state defined by the second argument for all Nodes
	 * contained in the first argument, which should be a Collection of Node
	 * objects contained in the referenced GraphPerspective. One event will be
	 * fired for the full set of changes. This method does nothing if the first
	 * argument is null.
	 *
	 * @return a Set containing the objects for which the selected state changed
	 * @throws ClassCastException
	 *             if the first argument contains objects other than
	 *             giny.model.Node objects
	 */
	public Set<Node> setSelectedNodes(final Collection<Node> nodesToSet, final boolean newState) {
		final Set<Node> returnSet = new HashSet<Node>();
		if (nodesToSet == null || nodesToSet.size() == 0) {
			return returnSet;
		}
		
		
		if (newState == true) {
			for (Node node : nodesToSet) {
				if (!graph.containsNode(node)) {
					continue;
				}

				if (selectedNodes.add(node)) {
					returnSet.add(node);
				}
			}

			if (returnSet.size() > 0) {
				fireEvent(returnSet, true);
			}
		} else {
			for (Node node : nodesToSet) {
				if (selectedNodes.remove(node)) {
					returnSet.add(node);
				}
			}

			if (returnSet.size() > 0) {
				fireEvent(returnSet, false);
			}
		}
		return returnSet;
	}

	/**
	 * Sets the selected state defined by the second argument for all Edges
	 * contained in the first argument, which should be a Collection of Edge
	 * objects contained in the referenced GraphPerspective. One event will be
	 * fired for the full set of changes. This method does nothing if the first
	 * argument is null.
	 *
	 * @return a Set containing the objects for which the selected state changed
	 * @throws ClassCastException
	 *             if the first argument contains objects other than
	 *             giny.model.Edge objects
	 */
	public Set<Edge> setSelectedEdges(final Collection<Edge> edgesToSet, final boolean newState) {
		
		final Set<Edge> edgeReturnSet = new HashSet<Edge>();
		
		if (edgesToSet == null || edgesToSet.size() == 0) {
			return edgeReturnSet;
		}

		if (newState == true) {
			for (Edge edge : edgesToSet) {
				if (!graph.containsEdge(edge)) {
					continue;
				}

				if (selectedEdges.add(edge)) {
					edgeReturnSet.add(edge);
				}
			}

			if (edgeReturnSet.size() > 0) {
				fireEvent(edgeReturnSet, true);
			}
		} else {
			for (Edge edge : edgesToSet) {
				if (selectedEdges.remove(edge)) {
					edgeReturnSet.add(edge);
				}
			}

			if (edgeReturnSet.size() > 0) {
				fireEvent(edgeReturnSet, false);
			}
		}
		return edgeReturnSet;
	}

	/**
	 * Sets the selected state to true for all Nodes in the GraphPerspective.
	 */
	public Set selectAllNodes() {
		return setSelectedNodes(graph.nodesList(), true);
	}

	/**
	 * Sets the selected state to true for all Edges in the GraphPerspective.
	 */
	public Set selectAllEdges() {
		return setSelectedEdges(graph.edgesList(), true);
	}

	/**
	 * Sets the selected state to false for all Nodes in the GraphPerspective.
	 */
	public Set unselectAllNodes() {
		return setSelectedNodes(graph.nodesList(), false);
	}

	/**
	 * Sets the selected state to false for all Edges in the GraphPerspective.
	 */
	public Set unselectAllEdges() {
		return setSelectedEdges(graph.edgesList(), false);
	}

	/**
	 * Implementation of the GraphPerspectiveChangeListener interface. Responds
	 * to the removal of nodes and edges by removing them from the set of
	 * selected graph objects if needed. Fires an event only if there was an
	 * actual change in the current selected set.
	 */
	public void graphPerspectiveChanged(GraphPerspectiveChangeEvent event) {
		// careful: this event can represent both hidden nodes and hidden edges
		// if a hide node operation implicitly hid its incident edges
		Set nodeChanges = null; // only create the set if we need it

		if (event.isNodesHiddenType()) { // at least one node was hidden

			int[] hiddenNodes = event.getHiddenNodeIndices();
			final int hNodesCount = hiddenNodes.length;
			Node node;
			boolean setChanged;
			for (int index = 0; index <hNodesCount; index++) {
				node = graph.getNode(hiddenNodes[index]);
				setChanged = selectedNodes.remove(node);

				if (setChanged) { // the hidden node was actually selected

					if (nodeChanges == null) {
						nodeChanges = new HashSet();
					}

					nodeChanges.add(node); // save change for the event we'll
					                       // fire
				}
			}
		}

		if ((nodeChanges != null) && (nodeChanges.size() > 0)) {
			fireEvent(nodeChanges, false);
		}

		Set edgeChanges = null; // only create the set if we need it

		if (event.isEdgesHiddenType()) { // at least one edge was hidden
			                             // GINY bug: sometimes we get an event that has valid edge indices
			                             // but the Edge array contains null objects
			                             // for now, get around this by converting indices to edges ourselves

			Object eventSource = event.getSource();
			RootGraph root;

			if (eventSource instanceof RootGraph)
				root = (RootGraph) eventSource;
			else
				root = ((GraphPerspective) eventSource).getRootGraph();

			int[] indices = event.getHiddenEdgeIndices();
			final int eLength = indices.length;
			Edge edge;
			boolean setChanged;
			for (int index = 0; index < eLength; index++) {
				edge = root.getEdge(indices[index]);
				setChanged = selectedEdges.remove(edge);

				if (setChanged) { // the hidden edge was actually selected

					if (edgeChanges == null) {
						edgeChanges = new HashSet();
					}

					edgeChanges.add(edge); // save change for the event we'll
					                       // fire
				}
			}

			/*
			 * this is the code that sometimes doesn't work Edge[] hiddenEdges =
			 * event.getHiddenEdges(); for (int index=0; index<hiddenEdges.length;
			 * index++) { Edge edge = hiddenEdges[index]; boolean setChanged =
			 * selectedEdges.remove(edge); if (setChanged) { if (edgeChanges ==
			 * null) {edgeChanges = new HashSet();} edgeChanges.add(edge); } }
			 */
		}

		if ((edgeChanges != null) && (edgeChanges.size() > 0)) {
			fireEvent(edgeChanges, false);
		}
	}

	/**
	 * If the argument is not already a listener to this object, it is added.
	 * Does nothing if the argument is null.
	 */
	public void addSelectEventListener(SelectEventListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	/**
	 * If the argument is a listener to this object, removes it from the list of
	 * listeners.
	 */
	public void removeSelectEventListener(SelectEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Gets a List of All Registered Listeners.
	 *
	 * @return
	 */
	public List getSelectEventListeners() {
		return listeners;
	}

	/**
	 * Fires an event to all registered listeners that represents the operation
	 * described by the arguments. The first argument should be the graph object
	 * whose selected state changed, or a Set of such objects. The second
	 * argument identifies the change; true for setting a flag and false for
	 * removing it. Creates a suitable event and passes it to all listeners.
	 */
	protected void fireEvent(final Object target, final boolean selectOn) {
		final SelectEvent event = new SelectEvent(this, target, selectOn);

		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			SelectEventListener listener = (SelectEventListener) i.next();
			listener.onSelectEvent(event);
		}
	}
}
