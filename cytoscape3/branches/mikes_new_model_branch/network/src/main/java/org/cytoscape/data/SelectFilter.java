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

package org.cytoscape.data;


import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNode;

import java.util.Collection;
import java.util.List;
import java.util.Set;


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
public interface SelectFilter {

	/**
	 * Returns the set of all selected nodes in the referenced GraphPespective.
	 * <P>
	 *
	 * WARNING: the returned set is the actual data object, not a copy. Don't
	 * directly modify this set.
	 */
	public Set<CyNode> getSelectedNodes();

	/**
	 * Returns the set of all selected edges in the referenced GraphPespective.
	 * <P>
	 *
	 * WARNING: the returned set is the actual data object, not a copy. Don't
	 * directly modify this set.
	 */
	public Set<CyEdge> getSelectedEdges();

	/**
	 * Returns true if the argument is a selected Node in the referenced
	 * GraphPerspective, false otherwise.
	 */
	public boolean isSelected(CyNode node);

	/**
	 * Returns true if the argument is a selected Edge in the referenced
	 * GraphPerspective, false otherwise.
	 */
	public boolean isSelected(CyEdge edge);

	/**
	 * Implementation of the Filter interface. Returns true if the argument is a
	 * selected Node or Edge in the referenced GraphPerspective, false
	 * otherwise.
	 */
	public boolean passesFilter(Object o);

	/**
	 * If the first argument is a Node in the referenced GraphPerspective, sets
	 * its selected state to the value of the second argument. An event will be
	 * fired iff the new state is different from the old state.
	 *
	 * @return true if an actual change was made, false otherwise
	 */
	public boolean setSelected(final CyNode node, final boolean newState);


	/**
	 * If the first argument is an Edge in the referenced GraphPerspective, sets
	 * its selected state to the value of the second argument. An event will be
	 * fired iff the new state is different from the old state.
	 *
	 * @return true if an actual change was made, false otherwise
	 */
	public boolean setSelected(final CyEdge edge, final boolean newState);

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
	 *             cytoscape.Node objects
	 */
	public Set<CyNode> setSelectedNodes(final Collection<CyNode> nodesToSet, final boolean newState);

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
	 *             cytoscape.Edge objects
	 */
	public Set<CyEdge> setSelectedEdges(final Collection<CyEdge> edgesToSet, final boolean newState);

	/**
	 * Sets the selected state to true for all Nodes in the GraphPerspective.
	 */
	public Set selectAllNodes(); 

	/**
	 * Sets the selected state to true for all Edges in the GraphPerspective.
	 */
	public Set selectAllEdges();

	/**
	 * Sets the selected state to false for all Nodes in the GraphPerspective.
	 */
	public Set unselectAllNodes(); 

	/**
	 * Sets the selected state to false for all Edges in the GraphPerspective.
	 */
	public Set unselectAllEdges(); 

	/**
	 * Implementation of the GraphPerspectiveChangeListener interface. Responds
	 * to the removal of nodes and edges by removing them from the set of
	 * selected graph objects if needed. Fires an event only if there was an
	 * actual change in the current selected set.
	 */
	//public void graphPerspectiveChanged(GraphPerspectiveChangeEvent event); 

	/**
	 * If the argument is not already a listener to this object, it is added.
	 * Does nothing if the argument is null.
	 */
	public void addSelectEventListener(SelectEventListener listener); 

	/**
	 * If the argument is a listener to this object, removes it from the list of
	 * listeners.
	 */
	public void removeSelectEventListener(SelectEventListener listener); 

	/**
	 * Gets a List of All Registered Listeners.
	 *
	 * @return List of SelectEventListeners
	 */
	public List<SelectEventListener> getSelectEventListeners();
}
