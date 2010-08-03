/*
  File: FingCyNetwork.java

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
package cytoscape.giny;

import cytoscape.*;

import cytoscape.data.*;

import cytoscape.giny.*;

import cytoscape.util.intr.*;

import fing.model.*;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.*;

import javax.swing.event.SwingPropertyChangeSupport;


/**
 *
 * FinhCyNetwork provides an implementation of the CyNetwork interface,
 * as well as the GraphPerspective inteface, and also provides the
 * functionality formally provided by GraphObjAttributes.
 *
 * The Network will notify listeners when nodes/edges are added/removed
 * and when relavant data changes.
 */
public class FingCyNetwork extends FingExtensibleGraphPerspective implements CyNetwork,
                                                                             GraphPerspective {
	private static int uid_counter = 0;
	private String identifier;
	protected String title;

	/**
	 * The Network Listeners Set
	 */

	//TODO: implement the bean accepted way
	protected Set listeners = new HashSet();

	/**
	 * The ClientData map
	 */
	protected Map clientData;

	/**
	 * The default object to set the selected state of nodes and edges
	 */
	protected final SelectFilter selectFilter;

	//TODO: remove
	int activityCount = 0;

	//----------------------------------------//
	// Constructors
	//----------------------------------------//

	/**
	* rootGraphNodeInx need not contain all endpoint nodes corresponding to
	* edges in rootGraphEdgeInx - this is calculated automatically by this
	* constructor.  If any index does not correspond to an existing node or
	* edge, an IllegalArgumentException is thrown.  The indices lists need not
	* be non-repeating - the logic in this constructor handles duplicate
	* filtering.
	**/
	public FingCyNetwork(FingExtensibleRootGraph root, IntIterator rootGraphNodeInx,
	                     IntIterator rootGraphEdgeInx) {
		super(root, rootGraphNodeInx, rootGraphEdgeInx);
		initialize();
		selectFilter = new SelectFilter(this);
	}

	protected void initialize() {
		// TODO: get a better naming system in place
		Integer i = new Integer(uid_counter);
		identifier = i.toString();
		uid_counter++;
		clientData = new HashMap();
	}

	/**
	 * Can Change
	 */
	public String getTitle() {
		if (title == null)
			return identifier;

		return title;
	}

	/**
	 * Can Change
	 * Throws a PropertyChangeEvent if the title has changed with a CyNetworkTitleChange object that contains the network id and the name.
	 */
	public void setTitle(String new_id) {
		if (title == null) {
			title = new_id;
		} else if (!title.equals(new_id)) { // new title is different from the old one
			CyNetworkTitleChange OldTitle = new CyNetworkTitleChange(this.getIdentifier(), title);
			title = new_id;
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_TITLE_MODIFIED, OldTitle, new CyNetworkTitleChange(this.getIdentifier(), new_id) );
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param new_id DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String setIdentifier(String new_id) {
		identifier = new_id;

		return identifier;
	}

	//-------------------------------------//
	// Client Data - Deprecated April 2006
	//-------------------------------------//

	/**
	 * @deprecated
	 *
	 * Networks can support client data.
	 * @param data_name the name of this client data
	 */
	public void putClientData(String data_name, Object data) {
		clientData.put(data_name, data);
	}

	/**
	 * @deprecated
	 *
	 * Get a list of all currently available ClientData objects
	 */
	public Collection getClientDataNames() {
		return clientData.keySet();
	}

	/**
	 * @deprecated
	 *
	 * Get Some client data
	 * @param data_name the data to get
	 */
	public Object getClientData(String data_name) {
		return clientData.get(data_name);
	}

	/**
	 * Appends all of the nodes and edges in teh given Network to
	 * this Network
	 */
	public void appendNetwork(CyNetwork network) {
		int[] nodes = network.getNodeIndicesArray();
		int[] edges = network.getEdgeIndicesArray();
		restoreNodes(nodes);
		restoreEdges(edges);
	}

	/**
	  * Sets the selected state of all nodes in this CyNetwork to true
	  */
	public void selectAllNodes() {
		this.selectFilter.selectAllNodes();
	}

	/**
	 * Sets the selected state of all edges in this CyNetwork to true
	 */
	public void selectAllEdges() {
		this.selectFilter.selectAllEdges();
	}

	/**
	 * Sets the selected state of all nodes in this CyNetwork to false
	 */
	public void unselectAllNodes() {
		this.selectFilter.unselectAllNodes();
	}

	/**
	 * Sets the selected state of all edges in this CyNetwork to false
	 */
	public void unselectAllEdges() {
		this.selectFilter.unselectAllEdges();
	}

	/**
	 * Sets the selected state of a collection of nodes.
	 *
	 * @param nodes a Collection of Nodes
	 * @param selected_state the desired selection state for the nodes
	 */
	public void setSelectedNodeState(Collection nodes, boolean selected_state) {
		this.selectFilter.setSelectedNodes(nodes, selected_state);
	}

	/**
	 * Sets the selected state of a node.
	 *
	 * @param nodes a Node
	 * @param selected_state the desired selection state for the node
	 */
	public void setSelectedNodeState(Node node, boolean selected_state) {
		this.selectFilter.setSelected(node, selected_state);
	}

	/**
	 * Sets the selected state of a collection of edges.
	 *
	 * @param edges a Collection of Edges
	 * @param selected_state the desired selection state for the edges
	 */
	public void setSelectedEdgeState(Collection edges, boolean selected_state) {
		this.selectFilter.setSelectedEdges(edges, selected_state);
	}

	/**
	 * Sets the selected state of an edge.
	 *
	 * @param edges an Edge
	 * @param selected_state the desired selection state for the edge
	 */
	public void setSelectedEdgeState(Edge edge, boolean selected_state) {
		this.selectFilter.setSelected(edge, selected_state);
	}

	/**
	 * Returns the selected state of the given node.
	 *
	 * @param node the node
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected(Node node) {
		return this.selectFilter.isSelected(node);
	}

	/**
	 * Returns the selected state of the given edge.
	 *
	 * @param edge the edge
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected(Edge edge) {
		return this.selectFilter.isSelected(edge);
	}

	/**
	 * Returns the set of selected nodes in this CyNetwork
	 *
	 * @return a Set of selected nodes
	 */
	public Set getSelectedNodes() {
		return this.selectFilter.getSelectedNodes();
	}

	/**
	 * Returns the set of selected edges in this CyNetwork
	 *
	 * @return a Set of selected edges
	 */
	public Set getSelectedEdges() {
		return this.selectFilter.getSelectedEdges();
	}

	/**
	 * Adds a listener for SelectEvents to this CyNetwork
	 *
	 * @param listener
	 */
	public void addSelectEventListener(SelectEventListener listener) {
		this.selectFilter.addSelectEventListener(listener);
	}

	/**
	 * Removes a listener for SelectEvents from this CyNetwork
	 * @param listener
	 */
	public void removeSelectEventListener(SelectEventListener listener) {
		this.selectFilter.removeSelectEventListener(listener);
	}

	/**
	 *
	 * @return SelectFilter
	 */
	public SelectFilter getSelectFilter() {
		return this.selectFilter;
	}

	//----------------------------------------//
	// Data Access Methods
	//----------------------------------------//

	//--------------------//
	// Member Data

	// get

	/**
	 * Return the requested Attribute for the given Node
	 * @param node the given CyNode
	 * @param attribute the name of the requested attribute
	 * @return the value for the give node, for the given attribute
	 */
	public Object getNodeAttributeValue(Node node, String attribute) {
		return getNodeAttributeValue(node.getRootGraphIndex(), attribute);
	}

	/**
	 * Return the requested Attribute for the given Node
	 */
	public Object getNodeAttributeValue(int node, String attribute) {
		final String canonName = getNode(node).getIdentifier();
		final CyAttributes attrs = Cytoscape.getNodeAttributes();
		final byte cyType = attrs.getType(attribute);

		if (cyType == CyAttributes.TYPE_BOOLEAN) {
			return attrs.getBooleanAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_FLOATING) {
			return attrs.getDoubleAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_INTEGER) {
			return attrs.getIntegerAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_STRING) {
			return attrs.getStringAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_LIST) {
			return attrs.getListAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_MAP) {
			return attrs.getMapAttribute(canonName, attribute);
		} else {
			return null;
		}
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public Object getEdgeAttributeValue(Edge edge, String attribute) {
		return getEdgeAttributeValue(edge.getRootGraphIndex(), attribute);
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public Object getEdgeAttributeValue(int edge, String attribute) {
		final String canonName = getEdge(edge).getIdentifier();
		final CyAttributes attrs = Cytoscape.getEdgeAttributes();
		final byte cyType = attrs.getType(attribute);

		if (cyType == CyAttributes.TYPE_BOOLEAN) {
			return attrs.getBooleanAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_FLOATING) {
			return attrs.getDoubleAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_INTEGER) {
			return attrs.getIntegerAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_STRING) {
			return attrs.getStringAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_LIST) {
			return attrs.getListAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_MAP) {
			return attrs.getMapAttribute(canonName, attribute);
		} else {
			return null;
		}
	}

	/**
	 * Return all availble Attributes for the Nodes in this CyNetwork
	 */
	public String[] getNodeAttributesList() {
		return Cytoscape.getNodeAttributes().getAttributeNames();
	}

	/**
	 * Return all available Attributes for the given Nodes
	 */
	public String[] getNodeAttributesList(Node[] nodes) {
		return Cytoscape.getNodeAttributes().getAttributeNames();
	}

	/**
	 * Return all availble Attributes for the Edges in this CyNetwork
	 */
	public String[] getEdgeAttributesList() {
		return Cytoscape.getEdgeAttributes().getAttributeNames();
	}

	/**
	 * Return all available Attributes for the given Edges
	 */
	public String[] getNodeAttributesList(Edge[] edges) {
		return Cytoscape.getEdgeAttributes().getAttributeNames();
	}

	/**
	* Return the requested Attribute for the given Node
	* @param node the given CyNode
	* @param attribute the name of the requested attribute
	* @param value the value to be set
	* @return if it overwrites a previous value
	*/
	public boolean setNodeAttributeValue(Node node, String attribute, Object value) {
		return setNodeAttributeValue(node.getRootGraphIndex(), attribute, value);
	}

	/**
	 * Return the requested Attribute for the given Node
	 */
	public boolean setNodeAttributeValue(int node, String attribute, Object value) {
		final String canonName = getNode(node).getIdentifier();
		final CyAttributes attrs = Cytoscape.getNodeAttributes();

		if (value instanceof Boolean) {
			attrs.setAttribute(canonName, attribute, (Boolean) value);

			return true;
		} else if (value instanceof Integer) {
			attrs.setAttribute(canonName, attribute, (Integer) value);

			return true;
		} else if (value instanceof Double) {
			attrs.setAttribute(canonName, attribute, (Double) value);

			return true;
		} else if (value instanceof String) {
			attrs.setAttribute(canonName, attribute, (String) value);

			return true;
		} else if (value instanceof List) {
			attrs.setListAttribute(canonName, attribute, (List) value);

			return true;
		} else if (value instanceof Map) {
			attrs.setMapAttribute(canonName, attribute, (Map) value);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public boolean setEdgeAttributeValue(Edge edge, String attribute, Object value) {
		return setEdgeAttributeValue(edge.getRootGraphIndex(), attribute, value);
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public boolean setEdgeAttributeValue(int edge, String attribute, Object value) {
		final String canonName = getEdge(edge).getIdentifier();
		final CyAttributes attrs = Cytoscape.getEdgeAttributes();

		if (value instanceof Boolean) {
			attrs.setAttribute(canonName, attribute, (Boolean) value);

			return true;
		} else if (value instanceof Integer) {
			attrs.setAttribute(canonName, attribute, (Integer) value);

			return true;
		} else if (value instanceof Double) {
			attrs.setAttribute(canonName, attribute, (Double) value);

			return true;
		} else if (value instanceof String) {
			attrs.setAttribute(canonName, attribute, (String) value);

			return true;
		} else if (value instanceof List) {
			attrs.setListAttribute(canonName, attribute, (List) value);

			return true;
		} else if (value instanceof Map) {
			attrs.setMapAttribute(canonName, attribute, (Map) value);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes the attribute with the given name from node attributes
	 */
	public void deleteNodeAttribute(String attribute) {
		Cytoscape.getNodeAttributes().deleteAttribute(attribute);
	}

	/**
	 * Deletes the attribute with the given name from edge attributes
	 */
	public void deleteEdgeAttribute(String attribute) {
		Cytoscape.getEdgeAttributes().deleteAttribute(attribute);
	}

	//------------------------------//
	// Listener Methods
	//------------------------------//

	/**
	 * Registers the argument as a listener to this object. Does nothing if
	 * the argument is already a listener.
	 */
	public void addCyNetworkListener(CyNetworkListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the argument from the set of listeners for this object. Returns
	 * true if the argument was a listener before this call, false otherwise.
	 */
	public boolean removeCyNetworkListener(CyNetworkListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Returns the set of listeners registered with this object.
	 */
	public Set getCyNetworkListeners() {
		return new HashSet(listeners);
	}

	//--------------------//
	// Event Firing
	//--------------------//

	/**
	 * Fires an event to all listeners registered with this object. The argument
	 * should be a constant from the CyNetworkEvent class identifying the type
	 * of the event.
	 */
	protected void fireEvent(int type) {
		CyNetworkEvent event = new CyNetworkEvent(this, type);

		for (Iterator i = listeners.iterator(); i.hasNext();) {
			CyNetworkListener listener = (CyNetworkListener) i.next();
			listener.onCyNetworkEvent(event);
		}
	}

	//----------------------------------------//
	// Implements Network
	//----------------------------------------//

	//----------------------------------------//
	// Node and Edge creation/deletion
	//----------------------------------------//

	//--------------------//
	// Nodes

	/**
	 * This method will create a new node.
	 * @return the Cytoscape index of the created node
	 */
	public int createNode() {
		return restoreNode(Cytoscape.getRootGraph().createNode());
	}

	/**
	 * Add a node to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this node
	 */
	public int addNode(int cytoscape_node) {
		return restoreNode(cytoscape_node);
	}

	/**
	 * Add a node to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this node
	 */
	public CyNode addNode(Node cytoscape_node) {
		return (CyNode) restoreNode(cytoscape_node);
	}

	/**
	 * Adds a node to this Network, by looking it up via the
	 * given attribute and value
	 * @return the Network Index of this node
	 */
	public int addNode(String attribute, Object value) {
		return 0;
	}

	/**
	 * This will remove this node from the Network. However,
	 * unless forced, it will remain in Cytoscape to be possibly
	 * resused by another Network in the future.
	 * @param force force this node to be removed from all Networks
	 * @return true if the node is still present in Cytoscape
	 *          ( i.e. in another Network )
	 */
	public boolean removeNode(int node_index, boolean force) {
		hideNode(node_index);

		return true;
	}

	//--------------------//
	// Edges

	/**
	 * This method will create a new edge.
	 * @param source the source node
	 * @param target the target node
	 * @param directed weather the edge should be directed
	 * @return the Cytoscape index of the created edge
	 */
	public int createEdge(int source, int target, boolean directed) {
		return restoreEdge(Cytoscape.getRootGraph().createEdge(source, target, directed));
	}

	/**
	 * Add a edge to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this edge
	 */
	public int addEdge(int cytoscape_edge) {
		return restoreEdge(cytoscape_edge);
	}

	/**
	 * Add a edge to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this edge
	 */
	public CyEdge addEdge(Edge cytoscape_edge) {
		return (CyEdge) restoreEdge(cytoscape_edge);
	}

	/**
	 * Adds a edge to this Network, by looking it up via the
	 * given attribute and value
	 * @return the Network Index of this edge
	 */
	public int addEdge(String attribute, Object value) {
		return 0;
	}

	/**
	 * This will remove this edge from the Network. However,
	 * unless forced, it will remain in Cytoscape to be possibly
	 * resused by another Network in the future.
	 * @param force force this edge to be removed from all Networks
	 * @return true if the edge is still present in Cytoscape
	 *          ( i.e. in another Network )
	 */
	public boolean removeEdge(int edge_index, boolean force) {
		super.hideEdge(edge_index);

		return true;
	}


}
