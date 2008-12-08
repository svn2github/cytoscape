/*
 File: Cytoscape.java

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
package cytoscape;

//import cytoscape.actions.SaveSessionAction;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.bind.JAXBException;

import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VMMFactory;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.bookmarks.Bookmarks;
import cytoscape.data.ExpressionData;
import cytoscape.data.readers.BookmarkReader;
import cytoscape.init.CyInitParams;
import cytoscape.view.CytoscapeDesktop;


/**
 * This class, Cytoscape is <i>the</i> primary class in the API.
 *
 * All Nodes and Edges must be created using the methods getCyNode and
 * getCyEdge, available only in this class. Once A node or edge is created using
 * these methods it can then be added to a GraphPerspective, where it can be used
 * algorithmically.<BR>
 * <BR>
 * The methods get/setNode/EdgeAttributeValue allow you to assocate data with
 * nodes or edges. That data is then carried into all CyNetworks where that
 * Node/Edge is present.
 */
public abstract class Cytoscape {
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

	// Root ontology network in the network panel
	/**
	 *
	public static final String ONTOLOGY_ROOT = "ONTOLOGY_ROOT";
	 */

	// Events for Preference Dialog (properties).
	/**
	 *
	 */
	public static final String PREFERENCE_MODIFIED = "PREFERENCE_MODIFIED";

	// Signals that CytoscapeInit properties have been updated.
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
	public static final Integer SESSION_NEW = 0;

	/**
	 *
	 */
	public static final Integer SESSION_OPENED = 1;

	/**
	 *
	 */
	public static final Integer SESSION_CHANGED = 2;

	/**
	 *
	 */
	public static final Integer SESSION_CLOSED = 3;

	private static Integer sessionState = SESSION_NEW;


	public static final String READER_CLIENT_KEY = "reader_client_key";

	protected static ExpressionData expressionData;
	protected static Object pcsO = new Object();
	protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);

	// Test
	protected static Object pcs2 = new Object();
	protected static PropertyChangeSupport newPcs = new PropertyChangeSupport(pcs2);
	protected static Map<Long,GraphView> networkViewMap;
	protected static Map<Long, CyNetwork> networkMap;
	protected static CytoscapeDesktop defaultDesktop;
	protected static Long currentNetworkID;
	protected static Long currentNetworkViewID;

	/**
	 * Used by session writer. If this is null, session writer opens the file
	 * chooser. Otherwise, overwrite the file.
	 *
	 * KONO: 02/23/2006
	 */
	private static String currentSessionFileName;
	private static Bookmarks bookmarks;

//	private static ImportHandler importHandler = new ImportHandler();

	/**
	 * The list analog to the currentNetworkViewID
	 */
	protected static LinkedList<GraphView> selectedNetworkViews = new LinkedList<GraphView>();

	/**
	 * The list analog to the currentNetworkID
	 */
	protected static LinkedList<CyNetwork> selectedNetworks = new LinkedList<CyNetwork>();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	public static ImportHandler getImportHandler() {
		return importHandler;
	}
	 */

	/**
	 * @return a null object. 
	 */
	public static GraphView getNullNetworkView() {
		return null;
	}

	/**
	 * @return a null object 
	 */
	public static CyNetwork getNullNetwork() {
		return null;
	}

	/**
	 * Shuts down Cytoscape, after giving plugins time to react.
	 *
	 * @param returnVal
	 *            The return value. Zero indicates success, non-zero otherwise.
	 */
	public static void exit(int returnVal) {
		int mode = CytoscapeInit.getCyInitParams().getMode();

		if ((mode == CyInitParams.EMBEDDED_WINDOW) || (mode == CyInitParams.GUI)) {
			// prompt the user about saving modified files before quitting
			if (confirmQuit()) {
				try {
					firePropertyChange(CYTOSCAPE_EXIT, null, "now");
				} catch (Exception e) {
					System.out.println("Errors on close, closed anyways.");
				}

				System.out.println("Cytoscape Exiting....");

				if (mode == CyInitParams.EMBEDDED_WINDOW) {
					// don't system exit since we are running as part
					// of a bigger application. Instead, dispose of the
					// desktop.
					defaultDesktop.dispose();
				} else {
					System.exit(returnVal);
				}
			} else {
				return;
			}
		} else {
			System.out.println("Cytoscape Exiting....");
			System.exit(returnVal);
		}
	}

	/**
	 * Prompt the user about saving modified files before quitting.
	 */
	private static boolean confirmQuit() {
		final String msg = "Do you want to save your session?";
		int networkCount = Cytoscape.getNetworkSet().size();

		// If there is no network, just quit.
		if (networkCount == 0) {
			return true;
		}

		//
		// Confirm user to save current session or not.
		//
		Object[] options = { "Yes, save and quit", "No, just quit", "Cancel" };
		int n = JOptionPane.showOptionDialog(defaultDesktop, msg,
		                                     "Save Networks Before Quitting?",
		                                     JOptionPane.YES_NO_OPTION,
		                                     JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (n == JOptionPane.NO_OPTION) {
			return true;
		} else if (n == JOptionPane.YES_OPTION) {
			// TODO 
			System.out.println("NOT implemented");
			return true;
		/*
			SaveSessionAction saveAction = new SaveSessionAction();
			saveAction.actionPerformed(null);

			if (Cytoscape.getCurrentSessionFileName() == null) {
				return confirmQuit();
			} else {
				return true;
			}
			*/
		} else {
			return false; // default if dialog box is closed
		}
	}

	// --------------------//
	// Root Graph Methods
	// --------------------//

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
	 * @return all CyNodes that are present in Cytoscape
	 */
	public static List<CyNode> getCyNodesList() {
		List<CyNode> allNodes = new ArrayList<CyNode>();
		for ( CyNetwork net : getNetworkSet() )
			allNodes.addAll( net.getNodeList() );

		return allNodes;
	}

	/**
	 * @return all CyEdges that are present in Cytoscape
	 */
	public static List<CyEdge> getCyEdgesList() {
		List<CyEdge> allEdges = new ArrayList<CyEdge>();
		for ( CyNetwork net : getNetworkSet() )
			allEdges.addAll( net.getEdgeList() );

		return allEdges;
	}

	/**
	 * This method is used to replace direct access to the rootgraph.
	public static CyNode getNode(int index) {
		return getRootGraph().getNode(index);
	}
	 */

	/**
	 * This method is used to replace direct access to the rootgraph.
	public static CyEdge getEdge(int index) {
		return getRootGraph().getEdge(index);
	}
	 */

	/**
	 * @param alias an alias of a node
	 * @return will return a node, if one exists for the given alias
	public static CyNode getCyNode(String alias) {
		return getCyNode(alias, false);
	}
	 */

	/**
	 * @param id the edge identifier 
	 * @return will return an edge, if one exists for the given identifier
	public static CyEdge getCyEdge(String id) {
		return getRootGraph().getEdge(id);
	}
	 */


	/**
	 * @param nodeID
	 *            an alias of a node
	 * @param create
	 *            will create a node if one does not exist
	 * @return will always return a node, if <code>create</code> is true
	 *
	public static CyNode getCyNode(String nodeID, boolean create) {
		CyNode node = Cytoscape.getRootGraph().getNode(nodeID);

		// If the node is already exists,return it.
		if (node != null) {
			return node;
		}

		// And if we do not have to create new one, just return null
		if (!create) {
			return null;
		}

		// Now, create a new node.
		node = (CyNode) getRootGraph().getNode(Cytoscape.getRootGraph().createNode());
		node.setIdentifier(nodeID);

		// create the CANONICAL_NAME attribute
		if (getNodeAttributes().getStringAttribute(nodeID, Semantics.CANONICAL_NAME) == null) {
			getNodeAttributes().setAttribute(nodeID, Semantics.CANONICAL_NAME, nodeID);
		}

		return node;
	}
	 */

	/**
	 * Gets the first CyEdge found between the two nodes (direction does not
	 * matter, but tries directed edge first) that has the given value for the
	 * given attribute. If the edge doesn't exist, then it creates a directed
	 * edge.
	 * 
	 * Thus, if create is true, this method will allways return a directed edge.
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
	public static CyEdge getCyEdge(CyNode node_1, CyNode node_2, String attribute,
	                               Object attribute_value, boolean create) {
		if (!create){
			CyEdge e = getCyEdge(node_1, node_2, attribute, attribute_value, create, true);
			if (e == null){
				return getCyEdge(node_1, node_2, attribute, attribute_value, create, false);
			} else { return e;}
		} else {
			return getCyEdge(node_1, node_2, attribute, attribute_value, create, true);
		}
	}
	 */

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
	public static CyEdge getCyEdge(CyNode source, CyNode target, String attribute,
	                               Object attribute_value, boolean create, boolean directed) {
		if (Cytoscape.getRootGraph().getEdgeCount() != 0) {
			int[] n1Edges = Cytoscape.getRootGraph()
			                         .getAdjacentEdgeIndicesArray(source.getRootGraphIndex(), true,
			                                                      true, true);

			for (int i = 0; i < n1Edges.length; i++) {
				CyEdge edge = (CyEdge) Cytoscape.getRootGraph().getEdge(n1Edges[i]);
				Object attValue = private_getEdgeAttributeValue(edge, attribute);

				if ((attValue != null) && attValue.equals(attribute_value)) {
					// Despite the fact that we know the source node
					// matches, the case of self edges dictates that
					// we must check the source as well.
					CyNode edgeTarget = (CyNode) edge.getTarget();
					CyNode edgeSource = (CyNode) edge.getSource();

					if ((edgeTarget.getRootGraphIndex() == target.getRootGraphIndex())
					    && (edgeSource.getRootGraphIndex() == source.getRootGraphIndex())
					    && (edge.isDirected() == directed)) {
						return edge;
					}

					if (!directed) {
						// note that source and target are switched
						if ((edgeTarget.getRootGraphIndex() == source.getRootGraphIndex())
						    && (edgeSource.getRootGraphIndex() == target.getRootGraphIndex())
						    && (edge.isDirected() == directed)) {
							return edge;
						}
					}
				}
			} // for i
		}

		if (create && attribute instanceof String && attribute.equals(Semantics.INTERACTION)) {
			// create the edge
			CyEdge edge = (CyEdge) Cytoscape.getRootGraph()
			                                .getEdge(Cytoscape.getRootGraph()
			                                                  .createEdge(source, target, directed));

			// create the edge id
			String edge_name = Cytoscape.createEdgeIdentifier(source.getIdentifier(),
			                                           (String) attribute_value,
			                                           target.getIdentifier());
			edge.setIdentifier(edge_name);

			edgeAttributes.setAttribute(edge_name, Semantics.INTERACTION, (String) attribute_value);
			edgeAttributes.setAttribute(edge_name, Semantics.IS_DIRECTED, new Boolean(directed));
			edgeAttributes.setAttribute(edge_name, Semantics.CANONICAL_NAME, edge_name);

			return edge;
		}

		return null;
	}
	 */

	/**
	 * Returns a directed edge if it exists, otherwise creates a directed edge.
	 *
	 * @param source_alias
	 *            an alias of a node
	 * @param edge_name
	 *            the name of the node
	 * @param target_alias
	 *            an alias of a node
	 * @return will always return an edge
	public static CyEdge getCyEdge(String source_alias, String edge_name, String target_alias,
            String interaction_type) {
			return getCyEdge(source_alias, edge_name, target_alias, interaction_type, true);
	}
	 */

	/**
	 * Returns an edge if it exists, otherwise creates an edge with given directionality.
	 *
	 * @param source_alias
	 *            an alias of a node
	 * @param edge_name
	 *            the name of the node
	 * @param target_alias
	 *            an alias of a node
	 * @param directed
	 * 			directedness of edge
	 * @return will always return an edge
	public static CyEdge getCyEdge(String source_alias, String edge_name, String target_alias,
	                               String interaction_type, boolean directed) {

		CyEdge edge = Cytoscape.getRootGraph().getEdge(edge_name);

		if (edge != null) {
			return edge;
		}

		// edge does not exist, create one
		CyNode source = getCyNode(source_alias);
		CyNode target = getCyNode(target_alias);

		return getCyEdge(source, target, Semantics.INTERACTION, interaction_type, true, directed);
	}

	private static Object private_getEdgeAttributeValue(CyEdge edge, String attribute) {
		final CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
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
	 */	


	// --------------------//
	// Network Methods
	// --------------------//

	/**
	 * Return the Network that currently has the Focus. Can be different from
	 * getCurrentNetworkView
	 */
	public static CyNetwork getCurrentNetwork() {
		if ((currentNetworkID == null) || !(getNetworkMap().containsKey(currentNetworkID)))
			return null;

		CyNetwork network = getNetworkMap().get(currentNetworkID);

		return network;
	}

	/**
	 * Return a List of all available GraphPerspective
	 */
	public static Set<CyNetwork> getNetworkSet() {
		return new java.util.LinkedHashSet<CyNetwork>(getNetworkMap().values());
	}

	/**
	 * @return the GraphPerspective that has the given identifier or null 
	 *         (see {@link #getNullNetwork()}) if there is no such network.
	 */
	public static CyNetwork getNetwork(long id) {
		return getNetworkMap().get(id);
	}

	/**
	 * @return a GraphView for the given ID, if one exists, otherwise
	 *         returns null
	 */
	public static GraphView getNetworkView(long network_id) {
		return getNetworkViewMap().get(network_id);
	}

	/**
	 * @return if a view exists for a given network id
	 */
	public static boolean viewExists(long network_id) {
		return getNetworkViewMap().containsKey(network_id);
	}

	/**
	 * Return the GraphView that currently has the focus. Can be different
	 * from getCurrentNetwork
	 */
	public static GraphView getCurrentNetworkView() {
		if (currentNetworkViewID == null) 
			return null;
		return getNetworkViewMap().get(currentNetworkViewID);
	}

	/**
	 * Returns the list of currently selected networks.
	 */
    public static List<GraphView> getSelectedNetworkViews() {
        GraphView view = getCurrentNetworkView();

        if (!selectedNetworkViews.contains(view))
            selectedNetworkViews.add(view);

        return new ArrayList<GraphView>(selectedNetworkViews);
    }

    /**
     * Sets the selected network views.
     */
    public static void setSelectedNetworkViews(final List<Long> viewIDs) {
        selectedNetworkViews.clear();

        if (viewIDs == null)
            return;

        for (Long id : viewIDs) {
            GraphView nview = getNetworkViewMap().get(id);

            if (nview != null) {
                selectedNetworkViews.add(nview);
            }
        }

        GraphView cv = getCurrentNetworkView();

        if (!selectedNetworkViews.contains(cv)) {
            selectedNetworkViews.add(cv);
        }
    }

	/**
	 * Returns the list of selected networks.
	 */
	public static List<CyNetwork> getSelectedNetworks() {
        CyNetwork curr = getCurrentNetwork();

        if (!selectedNetworks.contains(curr))
            selectedNetworks.add(curr);

        return new ArrayList<CyNetwork>(selectedNetworks);
	}

	/**
	 * Sets the list of selected networks.
	 */
	public static void setSelectedNetworks(final List<Long> ids) {
        selectedNetworks.clear();

        if (ids == null)
            return;

        for (Long id : ids) {
            CyNetwork n = getNetworkMap().get(id);

            if (n != null) {
                selectedNetworks.add(n);
            }
        }

        CyNetwork cn = getCurrentNetwork();

        if (!selectedNetworks.contains(cn)) {
            selectedNetworks.add(cn);
        }
	}

	/**
	 * Don't use this!.
	 * TODO resolve how the desktop is accessed. 
	 */
	public static void setDesktop(CytoscapeDesktop desk ) {
		if ( desk != null )
			defaultDesktop = desk;
	}

	/**
	 */
    public static void setCurrentNetwork(Long id) {
        //System.out.println("- TRY setting current network" + id);
        if (getNetworkMap().containsKey(id)) {
            //System.out.println("- SUCCEED setting current network " + id);
            currentNetworkID = id;

            // reset selected networks
            selectedNetworks.clear();
            selectedNetworks.add(getNetworkMap().get(id));
        }
    }

    /**
     * @return true if there is network view, false if not
     */
    public static boolean setCurrentNetworkView(Long id) {
        //System.out.println("= TRY setting current network VIEW " + id);
        if (getNetworkViewMap().containsKey(id)) {
            //System.out.println("= SUCCEED setting current network VIEW " + id);
            currentNetworkViewID = id;

            // reset selected network views
            selectedNetworkViews.clear();
            selectedNetworkViews.add(getNetworkViewMap().get(id));

            return true;
        }

        return false;
    }

	/**
	 * This Map has keys that are Strings ( network_ids ) and values that are
	 * networks.
	 */
	protected static Map<Long, CyNetwork> getNetworkMap() {
		if (networkMap == null) {
			networkMap = new HashMap<Long, CyNetwork>();
		}

		return networkMap;
	}

	/**
	 * This Map has keys that are Strings ( network_ids ) and values that are
	 * networkviews.
	 */
	public static Map<Long,GraphView> getNetworkViewMap() {
		if (networkViewMap == null) {
			networkViewMap = new HashMap<Long,GraphView>();
		}

		return networkViewMap;
	}

	/**
	 * destroys the given network
	 */
	public static void destroyNetwork(Long network_id) {
		destroyNetwork(getNetworkMap().get(network_id));
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
        if (network == null) 
            return;

        getSelectedNetworks().remove(network);

        final long networkId = network.getSUID();

        firePropertyChange(NETWORK_DESTROYED, null, networkId);

		for ( CyNode n : network.getNodeList() )
			n.attrs().set("selected",false);
		for ( CyEdge e : network.getEdgeList() )
			e.attrs().set("selected",false);

        final Map<Long, CyNetwork> nmap = getNetworkMap();
        nmap.remove(networkId);

        if (networkId == currentNetworkID) {
            if (nmap.size() <= 0) {
                currentNetworkID = null;
            } else {
                // randomly pick a network to become the current network
                for (Long key : nmap.keySet()) {
                    currentNetworkID = key;

                    break;
                }
            }
        }

        if (viewExists(networkId))
            destroyNetworkView(network);

        if (destroy_unique) {
            final List<CyNode> nodes = new ArrayList<CyNode>();
            final List<CyEdge> edges = new ArrayList<CyEdge>();

            final Collection<CyNetwork> networks = networkMap.values();

            for (CyNode node : nodes) {
                boolean add = true;

                for (CyNetwork net : networks) {
                    if (net.containsNode(node)) {
                        add = false;

                        continue;
                    }
                }

                if (add)
                    nodes.add(node);
            }

            for (CyEdge edge : edges) {
                boolean add = true;

                for (CyNetwork net : networks) {
                    if (net.containsEdge(edge)) {
                        add = false;

                        continue;
                    }
                }

                if (add)
                    edges.add(edge);
            }

            for (CyNode node : nodes) {
                node = null;
            }

            for (CyEdge edge : edges) {
                edge = null;
            }
        }

        // theoretically this should not be set to null till after the events
        // firing is done
        network = null;

    }




	/**
	 * destroys the networkview, including any layout information
	 */
	public static void destroyNetworkView(GraphView view) {
		if (view == null)
			return;

		getSelectedNetworkViews().remove(view);

		Long viewID = view.getNetwork().getSUID();

		if (viewID.equals(currentNetworkViewID)) {
			if (getNetworkViewMap().size() <= 0)
				currentNetworkViewID = null;
			else {
				// depending on which randomly chosen currentNetworkID we get, 
				// we may or may not have a view for it.
				GraphView newCurr = (GraphView) (getNetworkViewMap().get(currentNetworkID));

				if (newCurr != null)
					currentNetworkViewID = newCurr.getIdentifier();
				else
					currentNetworkViewID = null;
			}
		}

		firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, null, view);
		// theoretically this should not be set to null till after the events
		// firing is done
		getNetworkViewMap().remove(viewID);
		view = null;
	}

	/**
	 * destroys the networkview, including any layout information
	 */
	public static void destroyNetworkView(long network_view_id) {
		destroyNetworkView(getNetworkViewMap().get(network_view_id));
	}

	/**
	 * destroys the networkview, including any layout information
	 */
	public static void destroyNetworkView(CyNetwork network) {
		destroyNetworkView(getNetworkViewMap().get(network.getSUID()));
	}

	public static void addNetwork(CyNetwork network, GraphView view, CyLayouts cyLayouts) {
		getNetworkMap().put(network.getSUID(), network);

		if ( view != null ) {
			getNetworkViewMap().put(network.getSUID(), view);

			setCurrentNetworkView(network.getSUID());

			setSelectionMode(Cytoscape.getSelectionMode(), view);
	
			if ( cyLayouts != null )
				cyLayouts.getDefaultLayout().doLayout(view);

			view.fitContent();

			redrawGraph(view);
		}

		firePropertyChange(NETWORK_CREATED, null, network.getSUID());
		firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null, view);
	}

	/**
	 * Creates a new, empty Network.
	 *
	 * @param title
	 *            the title of the new network.
	public static CyNetwork createNetwork(String title) {
		return createNetwork(new int[] {  }, new int[] {  }, title, null, true);
	}
	 */

	/**
	 * Creates a new, empty Network.
	 *
	 * @param title
	 *            the title of the new network.
	 * @param create_view
	 *            if the size of the network is under the node limit, create a
	 *            view
	public static CyNetwork createNetwork(String title, boolean create_view) {
		return createNetwork(new int[] {  }, new int[] {  }, title, null, create_view);
	}
	 */

	/**
	 * Creates a new, empty Network.
	 *
	 * @param title
	 *            the title of the new network.
	 * @param create_view
	 *            if the size of the network is under the node limit, create a
	 *            view
	public static CyNetwork createNetwork(String title, CyNetwork parent, boolean create_view) {
		return createNetwork(new int[] {  }, new int[] {  }, title, parent, create_view);
	}
	 */

	/**
	 * Creates a new Network. A view will be created automatically.
	 *
	 * @param nodes
	 *            the indeces of nodes
	 * @param edges
	 *            the indeces of edges
	 * @param title
	 *            the title of the new network.
	public static CyNetwork createNetwork(int[] nodes, int[] edges, String title) {
		return createNetwork(nodes, edges, title, null, true);
	}
	 */

	/**
	 * Creates a new Network. A view will be created automatically.
	 *
	 * @param nodes
	 *            a collection of nodes
	 * @param edges
	 *            a collection of edges
	 * @param title
	 *            the title of the new network.
	public static CyNetwork createNetwork(Collection<CyNode> nodes, Collection<CyEdge> edges, String title) {
		return createNetwork(nodes, edges, title, null, true);
	}
	 */

	/**
	 * Creates a new Network, that inherits from the given ParentNetwork. A view
	 * will be created automatically.
	 *
	 * @param nodes
	 *            the indeces of nodes
	 * @param edges
	 *            the indeces of edges
	 * @param child_title
	 *            the title of the new network.
	 * @param parent
	 *            the parent of the this Network
	public static CyNetwork createNetwork(int[] nodes, int[] edges, String child_title,
	                                      CyNetwork parent) {
		return createNetwork(nodes, edges, child_title, parent, true);
	}
	 */

	/**
	 * Creates a new Network, that inherits from the given ParentNetwork
	 *
	 * @param nodes
	 *            the indeces of nodes
	 * @param edges
	 *            the indeces of edges
	 * @param child_title
	 *            the title of the new network.
	 * @param parent
	 *            the parent of the this Network
	 * @param create_view
	 *            whether or not a view will be created
	public static CyNetwork createNetwork(int[] nodes, int[] edges, String child_title,
	                                      CyNetwork parent, boolean create_view) {
		CyNetwork network = getRootGraph().createGraphPerspective(nodes, edges);
		addNetwork(network, child_title, parent, create_view);

		return network;
	}
	 */

	/**
	 * Creates a new Network, that inherits from the given ParentNetwork. A view
	 * will be created automatically.
	 *
	 * @param nodes
	 *            the indeces of nodes
	 * @param edges
	 *            the indeces of edges
	 * @param parent
	 *            the parent of the this Network
	public static CyNetwork createNetwork(Collection<CyNode> nodes, Collection<CyEdge> edges, String child_title,
	                                      CyNetwork parent) {
		return createNetwork(nodes, edges, child_title, parent, true);
	}
	 */

	/**
	 * Creates a new Network, that inherits from the given ParentNetwork.
	 *
	 * @param nodes
	 *            the indeces of nodes
	 * @param edges
	 *            the indeces of edges
	 * @param parent
	 *            the parent of the this Network
	 * @param create_view
	 *            whether or not a view will be created
	public static CyNetwork createNetwork(Collection<CyNode> nodes, Collection<CyEdge> edges, String child_title,
	                                      CyNetwork parent, boolean create_view) {
		CyNetwork network = getRootGraph().createGraphPerspective(nodes, edges);
		addNetwork(network, child_title, parent, create_view);

		return network;
	}
	 */

	/**
	 * Creates a GraphPerspective from a file. The file type is determined by the
	 * suffix of the file.* Uses the new ImportHandler and thus the passed in
	 * location should be a file of a recognized "Graph Nature". The "Nature" of
	 * a file is a new way to tell what a file is beyond it's filetype e.g.
	 * galFiltered.sif is, in addition to being a .sif file, the file is also of
	 * Graph "Nature". Other files of Graph Nature include GML and XGMML. Beyond
	 * Graph Nature there are Node, Edge, and Properties Nature.
	 *
	 * A view will be created automatically.
	 *
	 * @param location
	 *            the location of the file
	public static CyNetwork createNetworkFromFile(String location) {
		return createNetworkFromFile(location, true);
	}
	 */

	/**
	 * Creates a GraphPerspective from a file. The file type is determined by the
	 * suffix of the file.* Uses the new ImportHandler and thus the passed in
	 * location should be a file of a recognized "Graph Nature". The "Nature" of
	 * a file is a new way to tell what a file is beyond it's filetype e.g.
	 * galFiltered.sif is, in addition to being a .sif file, the file is also of
	 * Graph "Nature". Other files of Graph Nature include GML and XGMML. Beyond
	 * Graph Nature there are Node, Edge, and Properties Nature.
	 *
	 * @param loc
	 *            location of importable file
	 * @param create_view
	 *            whether or not a view will be created
	 * @return a network based on the specified file or null if the file type is
	 *         supported but the file is not of Graph Nature.
	public static CyNetwork createNetworkFromFile(String loc, boolean create_view) {
		return createNetwork(importHandler.getReader(loc), create_view, null);
	}
	 */

	/**
	 * Creates a GraphPerspective from a URL. The file type is determined by the
	 * suffix of the file or, if one does't exist, the contentType of the data.
	 * Uses the new ImportHandler and thus the passed in
	 * location should be a file of a recognized "Graph Nature". The "Nature" of
	 * a file is a new way to tell what a file is beyond it's filetype e.g.
	 * galFiltered.sif is, in addition to being a .sif file, the file is also of
	 * Graph "Nature". Other files of Graph Nature include GML and XGMML. Beyond
	 * Graph Nature there are Node, Edge, and Properties Nature.
	 *
	 * @param url
	 *            url of importable file
	 * @param create_view
	 *            whether or not a view will be created
	 * @return a network based on the specified file or null if the file type is
	 *         supported but the file is not of Graph Nature.
	public static CyNetwork createNetworkFromURL(URL url, boolean create_view) {
		return createNetwork(importHandler.getReader(url), create_view, null);
	}
	 */

	/**
	 * Creates a cytoscape.data.GraphPerspective from a reader. Neccesary with
	 * cesssions.
	 * <p>
	 * This operation may take a long time to complete. It is a good idea NOT to
	 * call this method from the AWT event handling thread. This operation
	 * assumes the reader is of type .xgmml since this should only be called by
	 * the cessions reader which opens .xgmml files from the zipped cytoscape
	 * session.
	 *
	 * @param reader
	 *            the graphreader that will read in the network
	 * @param create_view
	 *            whether or not a view will be created
	public static CyNetwork createNetwork(final GraphReader reader, final boolean create_view,
	                                      final CyNetwork parent) {
		if (reader == null) {
			throw new RuntimeException("Couldn't read specified file.");
		}

		// have the GraphReader read the given file

		// Explanation for code below: the code below recasts an IOException
		// into a RuntimeException, so that the exception can still be thrown
		// without having to change the method signature. This is less than
		// ideal, but the only sure way to ensure API stability for plugins.
		try {
			reader.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final String title = reader.getNetworkName();

		// get the RootGraph indices of the nodes and
		// edges that were just created
		final int[] nodes = reader.getNodeIndicesArray();
		final int[] edges = reader.getEdgeIndicesArray();

		if (nodes == null) {
			System.err.println("reader returned null nodes");
		}

		if (edges == null) {
			System.err.println("reader returned null edges");
		}

		final CyNetwork network = getRootGraph().createGraphPerspective(nodes, edges);
		addNetwork(network, title, parent, false);

		if (create_view && (network.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
		                                                            .getProperty("viewThreshold")))) {
			createNetworkView(network,title,reader.getLayoutAlgorithm());
		}

		// Execute any necessary post-processing.
		reader.doPostProcessing(network);

		return network;
	}
	 */

	// --------------------//
	// Network Data Methods
	// --------------------//

	/**
	 * Gets Global Node Attributes.
	 *
	 * @return CyDataTable Object.
	 */
	public static CyDataTable getNodeAttributes() {
		CyNetwork n = getCurrentNetwork();
		if ( n == null )
			return null;
		else
			return n.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
	}

	/**
	 * Gets Global Edge Attributes
	 *
	 * @return CyDataTable Object.
	 */
	public static CyDataTable getEdgeAttributes() {
		CyNetwork n = getCurrentNetwork();
		if ( n == null )
			return null;
		else
			return n.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
	}

	/**
	 * Gets Global Network Attributes.
	 *
	 * @return CyDataTable Object.
	 */
	public static CyDataTable getNetworkAttributes() {
		CyNetwork n = getCurrentNetwork();
		if ( n == null )
			return null;
		else
			return n.getNetworkCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
	}

	/**
	 * Gets Global Network Attributes.
	 *
	 * @return CyDataTable Object.
	public static CyDataTable getOntologyAttributes() {
		return ontologyAttributes;
	}
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ExpressionData getExpressionData() {
		return expressionData;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param expData DOCUMENT ME!
	 */
	public static void setExpressionData(ExpressionData expData) {
		expressionData = expData;
	}

	/**
	 * Load Expression Data
	 */

	// TODO: remove the JOption Pane stuff
	public static boolean loadExpressionData(String filename, boolean copy_atts) {
		try {
			expressionData = new ExpressionData(filename);
		} catch (Exception e) {
			System.err.println("Unable to Load Expression Data");

			String errString = "Unable to load expression data from " + filename;
			String title = "Load Expression Data";
		}

		if (copy_atts) {
			expressionData.copyToAttribs(getNodeAttributes(), null);
			firePropertyChange(ATTRIBUTES_CHANGED, null, null);
		}

		// Fire off an EXPRESSION_DATA_LOADED event.
		Cytoscape.firePropertyChange(Cytoscape.EXPRESSION_DATA_LOADED, null, expressionData);

		return true;
	}

	/**
	 * Loads Node and Edge attribute data into Cytoscape from the given file
	 * locations. Currently, the only supported attribute types are of the type
	 * "name = value".
	 *
	 * @param nodeAttrLocations
	 *            an array of node attribute file locations. May be null.
	 * @param edgeAttrLocations
	 *            an array of edge attribute file locations. May be null.
	public static void loadAttributes(String[] nodeAttrLocations, String[] edgeAttrLocations) {
		// check to see if there are Node Attributes passed
		if (nodeAttrLocations != null) {
			for (int i = 0; i < nodeAttrLocations.length; ++i) {
				try {
					InputStreamReader reader = new InputStreamReader(FileUtil.getInputStream(nodeAttrLocations[i]));
					CyAttributesReader.loadAttributes(nodeAttributes, reader);
					firePropertyChange(ATTRIBUTES_CHANGED, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Failure loading node attribute data: "
					                                   + nodeAttrLocations[i] + "  because of:"
					                                   + e.getMessage());
				}
			}
		}

		// Check to see if there are Edge Attributes Passed
		if (edgeAttrLocations != null) {
			for (int j = 0; j < edgeAttrLocations.length; ++j) {
				try {
					InputStreamReader reader = new InputStreamReader(FileUtil.getInputStream(edgeAttrLocations[j]));
					CyAttributesReader.loadAttributes(edgeAttributes, reader);
					firePropertyChange(ATTRIBUTES_CHANGED, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Failure loading edge attribute data: "
					                                   + edgeAttrLocations[j] + "  because of:"
					                                   + e.getMessage());
				}
			}
		}
	}
	 */

	/**
	 * This will replace the bioDataServer.
	public static OntologyServer buildOntologyServer() {
		try {
			ontologyServer = new OntologyServer();
		} catch (Exception e) {
			System.err.println("Could not build OntologyServer.");
			e.printStackTrace();

			return null;
		}

		return ontologyServer;
	}
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	public static OntologyServer getOntologyServer() {
		return ontologyServer;
	}
	 */

	// ------------------------------//
	// GraphView Creation Methods
	// ------------------------------//

	/**
	 * Creates a GraphView, but doesn't do anything with it. Ifnn's you want
	 * to use it
	 *
	 * @link {CytoscapeDesktop}
	 * @param network
	 *            the network to create a view of
	 */
	public static GraphView createNetworkView(CyNetwork network) {
		return createNetworkView(network, network.attrs().get("name",String.class), null, null);
	}

	/**
	 * Creates a GraphView, but doesn't do anything with it. Ifnn's you want
	 * to use it
	 *
	 * @link {CytoscapeDesktop}
	 * @param network
	 *            the network to create a view of
	 * @param title
	 *            the title to use for the view
	 */
	public static GraphView createNetworkView(CyNetwork network, String title) {
		return createNetworkView(network, title, null, null);
	}

	/**
	 * Creates a GraphView, but doesn't do anything with it. Ifnn's you want
	 * to use it
	 *
	 * @link {CytoscapeDesktop}
	 * @param network
	 *            the network to create a view of
	 * @param title
	 *            the title to use for the view
	 * @param layout
	 *            the CyLayoutAlgorithm to use to lay this out by default
	 */
	public static GraphView createNetworkView(CyNetwork network, String title, CyLayoutAlgorithm layout) {
		return createNetworkView(network,title,layout,null);
	}


	public static GraphView createNetworkView(CyNetwork network, String title, CyLayoutAlgorithm layout,
	                                              VisualStyle vs) {
		if (network == null) {
			return null;
		}

		if (Cytoscape.viewExists(network.getSUID())) {
			return getNetworkView(network.getSUID());
		}

		/* TODONOW
		final GraphView view = GraphViewFactory.createGraphView(network);
		view.setGraphLOD(new CyGraphLOD());
		view.setIdentifier(network.getSUID());
		getNetworkViewMap().put(network.getSUID(), view);

		// TODO:  Evaluate this hack.  It is done to make sure that current network view
		// is set for listeners of NETWORK_VIEW_CREATED.  Apparently in Cytoscape 2.6
		// the NetworkViewManager heard the CREATED event first, and was able to set the
		// value in time for others.  Not so in 3.0.  
		// Ideally we would just get rid of getCurrentNetworkView().
		setCurrentNetworkView(network.getSUID());

		setSelectionMode(Cytoscape.getSelectionMode(), view);

		if (vs != null) {
			VMMFactory.getVisualMappingManager().setVisualStyleForView(view,vs);
			VMMFactory.getVisualMappingManager().setVisualStyle(vs);
			VMMFactory.getVisualMappingManager().setNetworkView(view);
		}

		if (layout == null) {
			layout = CyLayouts.getDefaultLayout();
		}

		Cytoscape.firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null, view);

		layout.doLayout(view);

		view.fitContent();

		redrawGraph(view);

		return view;
		*/
		return null;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @param property_type DOCUMENT ME!
	 * @param old_value DOCUMENT ME!
	 * @param new_value DOCUMENT ME!
	 */
	public static void firePropertyChange(String property_type, Object old_value, Object new_value) {
		System.out.println("firing property change: " + property_type);
		PropertyChangeEvent e = new PropertyChangeEvent(pcsO, property_type, old_value, new_value);
		getSwingPropertyChangeSupport().firePropertyChange(e);
		getPropertyChangeSupport().firePropertyChange(e);
	}

	/**
	 * Gets the selection mode value.
	 */
	public static int getSelectionMode() {
		return currentSelectionMode;
	}

	/**
	 * Sets the specified selection mode on all views.
	 *
	 * @param selectionMode
	 *            SELECT_NODES_ONLY, SELECT_EDGES_ONLY, or
	 *            SELECT_NODES_AND_EDGES.
	 */
	public static void setSelectionMode(int selectionMode) {
		// set the selection mode on all the views
		String network_id;
		Map<Long,GraphView> networkViewMap = getNetworkViewMap();

		for ( GraphView view : networkViewMap.values() ) {
			setSelectionMode(selectionMode, view);
		}

		// update the global indicating the selection mode
		currentSelectionMode = selectionMode;
	}

	/**
	 * Utility method to set the selection mode on the specified GraphView.
	 *
	 * @param selectionMode
	 *            SELECT_NODES_ONLY, SELECT_EDGES_ONLY, or
	 *            SELECT_NODES_AND_EDGES.
	 * @param view
	 *            the GraphView to set the selection mode on.
	 */
	public static void setSelectionMode(int selectionMode, GraphView view) {

		// then, based on selection mode, enable node and/or edge selection
		switch (selectionMode) {
			case SELECT_NODES_ONLY:
				view.disableEdgeSelection();
				view.enableNodeSelection();

				break;

			case SELECT_EDGES_ONLY:
				view.disableNodeSelection();
				view.enableEdgeSelection();

				break;

			case SELECT_NODES_AND_EDGES:
				view.enableNodeSelection();
				view.enableEdgeSelection();

				break;
		}
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
        // Destroy all networks
        Set<CyNetwork> netSet = getNetworkSet();

        for (CyNetwork net : netSet)
            destroyNetwork(net, true);

        setCurrentSessionFileName(null);
        firePropertyChange(ATTRIBUTES_CHANGED, null, null);
        System.out.println("Cytoscape Session Initialized.");
        System.gc();
    }

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	public static String getOntologyRootID() {
		return ontologyRootID;
	}
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @param id DOCUMENT ME!
	public static void setOntologyRootID(String id) {
		ontologyRootID = id;
	}
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws JAXBException DOCUMENT ME!
	 * @throws IOException DOCUMENT ME!
	 */
	public static Bookmarks getBookmarks() throws JAXBException, IOException {
		if (bookmarks == null) {
			BookmarkReader reader = new BookmarkReader();
			reader.readBookmarks();
			bookmarks = reader.getBookmarks();
		}

		return bookmarks;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarks DOCUMENT ME!
	 */
	public static void setBookmarks(Bookmarks pBookmarks) {
		bookmarks = pBookmarks;
	}

    /**
     * A static method used to create edge identifiers.
     */
    public static String createEdgeIdentifier(String source, String attribute_value, String target) {
        return source + " (" + attribute_value + ") " + target;
    }

	/**
	 * This is a temporary utility method and will eventually be refactored away.
	 */
	public static void redrawGraph(GraphView view) {
		VMMFactory.getVisualMappingManager().setNetworkView(view);
		VMMFactory.getVisualMappingManager().applyAppearances();
		view.updateView();
	}

	public static VisualMappingManager getVisualMappingManager() {
		return VMMFactory.getVisualMappingManager();
	}
}
