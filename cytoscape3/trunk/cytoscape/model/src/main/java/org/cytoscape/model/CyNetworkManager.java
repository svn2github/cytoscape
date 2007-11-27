
/*
 File: CyNetworkManager.java

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
package org.cytoscape.model;


import giny.model.Edge;
import giny.model.Node;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;

import org.cytoscape.model.attribute.CyAttributes;
import org.cytoscape.model.attribute.CyAttributesImpl;
import org.cytoscape.model.attribute.Semantics;
import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.CytoscapeFingRootGraph;
import org.cytoscape.model.network.CytoscapeRootGraph;


/**
 * This class, Cytoscape is <i>the</i> primary class in the API.
 *
 * All Nodes and Edges must be created using the methods getCyNode and
 * getCyEdge, available only in this class. Once A node or edge is created using
 * these methods it can then be added to a CyNetwork, where it can be used
 * algorithmically.<BR>
 * <BR>
 * The methods get/setNode/EdgeAttributeValue allow you to assocate data with
 * nodes or edges. That data is then carried into all CyNetworks where that
 * Node/Edge is present.
 */
public abstract class CyNetworkManager {
	//
	// Events
	//

	/**
	 * This signals when new attributes have been loaded and a few other
	 * large scale changes to attributes have been made.  There is no
	 * equivalent in the CyAttributes events.
	 */
	public static String ATTRIBUTES_CHANGED = "ATTRIBUTES_CHANGED";

	/**
	 *
	 */
	public static String NETWORK_CREATED = "NETWORK_CREATED";

	/**
	 *
	 */
	public static String DATASERVER_CHANGED = "DATASERVER_CHANGED";

	/**
	 *
	 */
	public static String EXPRESSION_DATA_LOADED = "EXPRESSION_DATA_LOADED";

	/**
	 *
	 */
	public static String NETWORK_DESTROYED = "NETWORK_DESTROYED";

	/**
	 *
	 */
	public static String CYTOSCAPE_INITIALIZED = "CYTOSCAPE_INITIALIZED";

	/**
	 *
	 */
	public static String CYTOSCAPE_EXIT = "CYTOSCAPE_EXIT";

	// KONO: 03/10/2006 For vizmap saving and loading
	/**
	 *
	 */
	public static String SESSION_SAVED = "SESSION_SAVED";

	/**
	 *
	 */
	public static String SESSION_LOADED = "SESSION_LOADED";

	/**
	 *
	 */
	public static String VIZMAP_RESTORED = "VIZMAP_RESTORED";

	/**
	 *
	 */
	public static String SAVE_VIZMAP_PROPS = "SAVE_VIZMAP_PROPS";

	/**
	 *
	 */
	public static String VIZMAP_LOADED = "VIZMAP_LOADED";

	// WANG: 11/14/2006 For plugin to save state
	/**
	 *
	 */
	public static final String SAVE_PLUGIN_STATE = "SAVE_PLUGIN_STATE";

	/**
	 *
	 */
	public static final String RESTORE_PLUGIN_STATE = "RESTORE_PLUGIN_STATE";

	// events for network modification
	/**
	 *
	 */
	public static final String NETWORK_MODIFIED = "NETWORK_MODIFIED";

	public static final String NETWORK_TITLE_MODIFIED = "NETWORK_TITLE_MODIFIED";
	
	/**
	 *
	 */
	public static final String NETWORK_SAVED = "NETWORK_SAVED";

	/**
	 *
	 */
	public static final String NETWORK_LOADED = "NETWORK_LOADED";

	/**
	 *
	 */
	public static final String ONTOLOGY_ROOT = "ONTOLOGY_ROOT";

	// Events for Preference Dialog (properties).
	/**
	 *
	 */
	public static final String PREFERENCE_MODIFIED = "PREFERENCE_MODIFIED";

	/**
	 *
	 */
	public static final String PREFERENCES_UPDATED = "PREFERENCES_UPDATED";

       /**
        * Specifies that the Proxy settings Cytoscape uses to connect to the
        * internet have been changed.
        */
       public static final String PROXY_MODIFIED = "PROXY_MODIFIED";

	/**
	 * When creating a network, use one of the standard suffixes to have it
	 * parsed correctly<BR>
	 * <ul>
	 * <li> sif -- Simple Interaction File</li>
	 * <li> gml -- Graph Markup Languange</li>
	 * <li> sbml -- SBML</li>
	 * <li> xgmml -- XGMML</li>
	 * </ul>
	 */
	public static int FILE_BY_SUFFIX = 0;

	/**
	 *
	 */
	public static int FILE_GML = 1;

	/**
	 *
	 */
	public static int FILE_SIF = 2;

	/**
	 *
	 */
	public static int FILE_SBML = 3;

	/**
	 *
	 */
	public static int FILE_XGMML = 4;

	/**
	 *
	 */
	public static int FILE_BIOPAX = 5;

	/**
	 *
	 */
	public static int FILE_PSI_MI = 6;

	// constants for tracking selection mode globally
	/**
	 *
	 */
	public static final int SELECT_NODES_ONLY = 1;

	/**
	 *
	 */
	public static final int SELECT_EDGES_ONLY = 2;

	/**
	 *
	 */
	public static final int SELECT_NODES_AND_EDGES = 3;

	// global to represent which selection mode is active
	private static int currentSelectionMode = SELECT_NODES_ONLY;

	// Value to manage session state
	/**
	 *
	 */
	public static final int SESSION_NEW = 0;

	/**
	 *
	 */
	public static final int SESSION_OPENED = 1;

	/**
	 *
	 */
	public static final int SESSION_CHANGED = 2;

	/**
	 *
	 */
	public static final int SESSION_CLOSED = 3;
	private static int sessionState = SESSION_NEW;

	/**
	 *
	 */
	public static final String READER_CLIENT_KEY = "reader_client_key";

	// global flag to indicate if Squiggle is turned on
//	private static boolean squiggleEnabled = false;

	/**
	 * The shared RootGraph between all Networks
	 */
	protected static CytoscapeRootGraph cytoscapeRootGraph;

	/**
	 * Node CyAttributes.
	 */
	private static CyAttributes nodeAttributes = new CyAttributesImpl();

	/**
	 * Edge CyAttributes.
	 */
	private static CyAttributes edgeAttributes = new CyAttributesImpl();

	/**
	 * Network CyAttributes.
	 */
	private static CyAttributes networkAttributes = new CyAttributesImpl();

	protected static Object pcsO = new Object();
	protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);

	// Test
	protected static Object pcs2 = new Object();
	protected static PropertyChangeSupport newPcs = new PropertyChangeSupport(pcs2);
	protected static Map networkMap;
	protected static String currentNetworkID;

	/**
	 * Used by session writer. If this is null, session writer opens the file
	 * chooser. Otherwise, overwrite the file.
	 *
	 * KONO: 02/23/2006
	 */
	private static String currentSessionFileName;

	/**
	 * A null CyNetwork to give when there is no Current Network
	 */
	protected static CyNetwork nullNetwork = getRootGraph()
	                                             .createNetwork(new int[] {  }, new int[] {  });




	/**
	 * @return the nullNetwork CyNetwork. This is NOT simply a null object.
	 */
	public static CyNetwork getNullNetwork() {
		return nullNetwork;
	}

	/**
	 * Shuts down Cytoscape, after giving plugins time to react.
	 *
	 * @param returnVal
	 *            The return value. Zero indicates success, non-zero otherwise.
	 */
	public static void exit(int returnVal) {
		firePropertyChange(CYTOSCAPE_EXIT, null, "now");
		System.out.println("Cytoscape Exiting....");
		System.exit(returnVal);
	}

	/**
	 * Bound events are:
	 * <ol>
	 * <li>NETWORK_CREATED
	 * <li>NETWORK_DESTROYED
	 * <li>CYTOSCAPE_EXIT
	 * </ol>
	 */
	public static SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static PropertyChangeSupport getPropertyChangeSupport() {
		return newPcs;
	}

	/**
	 * Return the CytoscapeRootGraph
	 */
	public static CytoscapeRootGraph getRootGraph() {
		if (cytoscapeRootGraph == null)
			cytoscapeRootGraph = new CytoscapeFingRootGraph();

		return cytoscapeRootGraph;
	}

	/**
	 * Ensure the capacity of Cytoscapce. This is to prevent the inefficiency of
	 * adding nodes one at a time.
	 */
	public static void ensureCapacity(int nodes, int edges) {
		// getRootGraph().ensureCapacity( nodes, edges );
	}

	/**
	 * @return all CyNodes that are present in Cytoscape
	 */
	public static List getCyNodesList() {
		return getRootGraph().nodesList();
	}

	/**
	 * @return all CyEdges that are present in Cytoscape
	 */
	public static List getCyEdgesList() {
		return getRootGraph().edgesList();
	}

	/**
	 * @param alias
	 *            an alias of a node
	 * @return will return a node, if one exists for the given alias
	 */
	public static CyNode getCyNode(String alias) {
		return getCyNode(alias, false);
	}

	/**
	 * @param nodeID
	 *            an alias of a node
	 * @param create
	 *            will create a node if one does not exist
	 * @return will always return a node, if <code>create</code> is true
	 *
	 */
	public static CyNode getCyNode(String nodeID, boolean create) {
		CyNode node = getRootGraph().getNode(nodeID);

		// If the node is already exists,return it.
		if (node != null) {
			return node;
		}

		// And if we do not have to create new one, just return null
		if (!create) {
			return null;
		}

		// Now, create a new node.
		node = (CyNode) getRootGraph().getNode(getRootGraph().createNode());
		node.setIdentifier(nodeID);

		// create the CANONICAL_NAME attribute
		if (getNodeAttributes().getStringAttribute(nodeID, Semantics.CANONICAL_NAME) == null) {
			getNodeAttributes().setAttribute(nodeID, Semantics.CANONICAL_NAME, nodeID);
		}

		return node;
	}

	/**
	 * Gets the first CyEdge found between the two nodes (direction does not
	 * matter) that has the given value for the given attribute. If the edge
	 * doesn't exist, then it creates an undirected edge.
	 *
	 * This method MIGHT be deprecated, or even removed, because Cytoscape
	 * shouldn't really be using undirected edges.
	 *
	 * @param node_1
	 *            one end of the edge
	 * @param node_2
	 *            the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            {@link Semantics#INTERACTION }
	 * @param attribute_value
	 *            a value for the attribute, like "pp"
	 * @param create
	 *            will create an edge if one does not exist and if attribute is
	 *            {@link Semantics#INTERACTION}
	 * @return returns an existing CyEdge if present, or creates one if
	 *         <code>create</code> is true and attribute is
	 *         Semantics.INTERACTION, otherwise returns null.
	 */
	public static CyEdge getCyEdge(Node node_1, Node node_2, String attribute,
	                               Object attribute_value, boolean create) {
		return getCyEdge(node_1, node_2, attribute, attribute_value, create, false);
	}

	/**
	 * Gets the first CyEdge found between the two nodes that has the given
	 * value for the given attribute. If direction flag is set, then direction
	 * is taken into account, A->B is NOT equivalent to B->A
	 *
	 * @param source
	 *            one end of the edge
	 * @param target
	 *            the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            {@link Semantics#INTERACTION }
	 * @param attribute_value
	 *            a value for the attribute, like "pp"
	 * @param create
	 *            will create an edge if one does not exist and if attribute is
	 *            {@link Semantics#INTERACTION}
	 * @param directed
	 *            take direction into account, source->target is NOT
	 *            target->source
	 * @return returns an existing CyEdge if present, or creates one if
	 *         <code>create</code> is true and attribute is
	 *         Semantics.INTERACTION, otherwise returns null.
	 */
	public static CyEdge getCyEdge(Node source, Node target, String attribute,
	                               Object attribute_value, boolean create, boolean directed) {
		if (getRootGraph().getEdgeCount() != 0) {
			int[] n1Edges = getRootGraph()
			                         .getAdjacentEdgeIndicesArray(source.getRootGraphIndex(), true,
			                                                      true, true);

			for (int i = 0; i < n1Edges.length; i++) {
				CyEdge edge = (CyEdge) getRootGraph().getEdge(n1Edges[i]);
				Object attValue = private_getEdgeAttributeValue(edge, attribute);

				if ((attValue != null) && attValue.equals(attribute_value)) {
					// Despite the fact that we know the source node
					// matches, the case of self edges dictates that
					// we must check the source as well.
					CyNode edgeTarget = (CyNode) edge.getTarget();
					CyNode edgeSource = (CyNode) edge.getSource();

					if ((edgeTarget.getRootGraphIndex() == target.getRootGraphIndex())
					    && (edgeSource.getRootGraphIndex() == source.getRootGraphIndex())) {
						return edge;
					}

					if (!directed) {
						// note that source and target are switched
						if ((edgeTarget.getRootGraphIndex() == source.getRootGraphIndex())
						    && (edgeSource.getRootGraphIndex() == target.getRootGraphIndex())) {
							return edge;
						}
					}
				}
			} // for i
		}

		if (create && attribute instanceof String && attribute.equals(Semantics.INTERACTION)) {
			// create the edge
			CyEdge edge = (CyEdge) getRootGraph()
			                                .getEdge(getRootGraph()
			                                                  .createEdge(source, target));

			// create the edge id
			String edge_name = CyEdge.createIdentifier(source.getIdentifier(),
			                                           (String) attribute_value,
			                                           target.getIdentifier());
			edge.setIdentifier(edge_name);

			edgeAttributes.setAttribute(edge_name, Semantics.INTERACTION, (String) attribute_value);
			edgeAttributes.setAttribute(edge_name, Semantics.CANONICAL_NAME, edge_name);

			return edge;
		}

		return null;
	}

	/**
	 * Returns and edge if it exists, otherwise creates a directed edge.
	 *
	 * @param source_alias
	 *            an alias of a node
	 * @param edge_name
	 *            the name of the node
	 * @param target_alias
	 *            an alias of a node
	 * @return will always return an edge
	 */
	public static CyEdge getCyEdge(String source_alias, String edge_name, String target_alias,
	                               String interaction_type) {

		CyEdge edge = getRootGraph().getEdge(edge_name);

		if (edge != null) {
			return edge;
		}

		// edge does not exist, create one
		CyNode source = getCyNode(source_alias);
		CyNode target = getCyNode(target_alias);

		return getCyEdge(source, target, Semantics.INTERACTION, interaction_type, true, true);
	}

	private static Object private_getEdgeAttributeValue(Edge edge, String attribute) {
		final CyAttributes edgeAttrs = getEdgeAttributes();
		final String canonName = edge.getIdentifier();
		final byte cyType = edgeAttrs.getType(attribute);

		if (cyType == CyAttributes.TYPE_BOOLEAN) {
			return edgeAttrs.getBooleanAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_FLOATING) {
			return edgeAttrs.getDoubleAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_INTEGER) {
			return edgeAttrs.getIntegerAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_STRING) {
			return edgeAttrs.getStringAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_LIST) {
			return edgeAttrs.getListAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_MAP) {
			return edgeAttrs.getMapAttribute(canonName, attribute);
		} else {
			return null;
		}
	}


	// --------------------//
	// Network Methods
	// --------------------//

	/**
	 * Return the Network that currently has the Focus. Can be different from
	 * getCurrentNetworkView
	 */
	public static CyNetwork getCurrentNetwork() {
		if ((currentNetworkID == null) || !(getNetworkMap().containsKey(currentNetworkID)))
			return nullNetwork;

		CyNetwork network = (CyNetwork) getNetworkMap().get(currentNetworkID);

		return network;
	}

	/**
	 * Return a List of all available CyNetworks
	 */
	public static Set getNetworkSet() {
		return new java.util.LinkedHashSet(((HashMap) getNetworkMap()).values());
	}

	/**
	 * @return the CyNetwork that has the given identifier or the nullNetwork
	 *         (see {@link #getNullNetwork()}) if there is no such network.
	 */
	public static CyNetwork getNetwork(String id) {
		if ((id != null) && getNetworkMap().containsKey(id))
			return (CyNetwork) getNetworkMap().get(id);

		return nullNetwork;
	}


	/**
	 * @deprecated This will be removed Feb 2007.
	 */
	public static void setCurrentNetwork(String id) {
		if (getNetworkMap().containsKey(id))
			currentNetworkID = id;

		// System.out.println( "Currentnetworkid is: "+currentNetworkID+ " set
		// from : "+id );
	}


	/**
	 * This Map has keys that are Strings ( network_ids ) and values that are
	 * networks.
	 */
	protected static Map getNetworkMap() {
		if (networkMap == null) {
			networkMap = new HashMap();
		}

		return networkMap;
	}

	/**
	 * destroys the given network
	 */
	public static void destroyNetwork(String network_id) {
		destroyNetwork((CyNetwork) getNetworkMap().get(network_id));
	}

	/**
	 * destroys the given network
	 */
	public static void destroyNetwork(CyNetwork network) {
		destroyNetwork(network, false);
	}

	/**
	 * destroys the given network
	 *
	 * @param network
	 *            the network to be destroyed
	 * @param destroy_unique
	 *            if this is true, then all Nodes and Edges that are in this
	 *            network, but no other are also destroyed.
	 */
	public static void destroyNetwork(CyNetwork network, boolean destroy_unique) {
		if ((network == null) || (network == nullNetwork))
			return;

		String networkId = network.getIdentifier();

		firePropertyChange(NETWORK_DESTROYED, null, networkId);

		Map nmap = getNetworkMap();
		nmap.remove(networkId);

		if (networkId.equals(currentNetworkID)) {
			if (nmap.size() <= 0) {
				currentNetworkID = null;
			} else {
				// randomly pick a network to become the current network
				for (Iterator it = nmap.keySet().iterator(); it.hasNext();) {
					currentNetworkID = (String) it.next();

					break;
				}
			}
		}

		if (destroy_unique) {
			ArrayList nodes = new ArrayList();
			ArrayList edges = new ArrayList();

			Collection networks = networkMap.values();

			Iterator nodes_i = network.nodesIterator();
			Iterator edges_i = network.edgesIterator();

			while (nodes_i.hasNext()) {
				Node node = (Node) nodes_i.next();
				boolean add = true;

				for (Iterator n_i = networks.iterator(); n_i.hasNext();) {
					CyNetwork net = (CyNetwork) n_i.next();

					if (net.containsNode(node)) {
						add = false;

						continue;
					}
				}

				if (add) {
					nodes.add(node);
				}
			}

			while (edges_i.hasNext()) {
				Edge edge = (Edge) edges_i.next();
				boolean add = true;

				for (Iterator n_i = networks.iterator(); n_i.hasNext();) {
					CyNetwork net = (CyNetwork) n_i.next();

					if (net.containsEdge(edge)) {
						add = false;

						continue;
					}
				}

				if (add) {
					edges.add(edge);
				}
			}

			getRootGraph().removeNodes(nodes);
			getRootGraph().removeEdges(edges);
		}

		// theoretically this should not be set to null till after the events
		// firing is done
		network = null;

	}



	// --------------------//
	// Network Data Methods
	// --------------------//

	/**
	 * Gets Global Node Attributes.
	 *
	 * @return CyAttributes Object.
	 */
	public static CyAttributes getNodeAttributes() {
		return nodeAttributes;
	}

	/**
	 * Gets Global Edge Attributes
	 *
	 * @return CyAttributes Object.
	 */
	public static CyAttributes getEdgeAttributes() {
		return edgeAttributes;
	}

	/**
	 * Gets Global Network Attributes.
	 *
	 * @return CyAttributes Object.
	 */
	public static CyAttributes getNetworkAttributes() {
		return networkAttributes;
	}



	/**
	 *  DOCUMENT ME!
	 *
	 * @param property_type DOCUMENT ME!
	 * @param old_value DOCUMENT ME!
	 * @param new_value DOCUMENT ME!
	 */
	public static void firePropertyChange(String property_type, Object old_value, Object new_value) {
		PropertyChangeEvent e = new PropertyChangeEvent(pcsO, property_type, old_value, new_value);
		// System.out.println("Cytoscape FIRING : " + property_type);
		getSwingPropertyChangeSupport().firePropertyChange(e);
		getPropertyChangeSupport().firePropertyChange(e);
	}

	/**
	 * Get name of the current session file.
	 *
	 * @return current session file name
	 */
	public static String getCurrentSessionFileName() {
		return currentSessionFileName;
	}

	/**
	 * Set the current session name.
	 *
	 * @param newName
	 */
	public static void setCurrentSessionFileName(String newName) {
		currentSessionFileName = newName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param state DOCUMENT ME!
	 */
	public static void setSessionState(int state) {
		sessionState = state;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static int getSessionstate() {
		return sessionState;
	}

	/**
	 * Clear all networks and attributes and start a new session.
	 */
	public static void createNewSession() {
		Set netSet = getNetworkSet();
		Iterator it = netSet.iterator();

		while (it.hasNext()) {
			CyNetwork net = (CyNetwork) it.next();
			destroyNetwork(net);
		}

		// Clear node attributes
		CyAttributes nodeAttributes = getNodeAttributes();
		String[] nodeAttrNames = nodeAttributes.getAttributeNames();

		for (int i = 0; i < nodeAttrNames.length; i++) {
			nodeAttributes.deleteAttribute(nodeAttrNames[i]);
		}

		// Clear edge attributes
		CyAttributes edgeAttributes = getEdgeAttributes();
		String[] edgeAttrNames = edgeAttributes.getAttributeNames();

		for (int i = 0; i < edgeAttrNames.length; i++) {
			edgeAttributes.deleteAttribute(edgeAttrNames[i]);
		}

		// Clear network attributes
		CyAttributes networkAttributes = getNetworkAttributes();
		String[] networkAttrNames = networkAttributes.getAttributeNames();

		for (int i = 0; i < networkAttrNames.length; i++) {
			networkAttributes.deleteAttribute(networkAttrNames[i]);
		}

		setCurrentSessionFileName(null);
	}


}
