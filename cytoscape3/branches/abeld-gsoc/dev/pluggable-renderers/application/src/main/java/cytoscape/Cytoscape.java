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

import cytoscape.actions.SaveSessionAction;

import cytoscape.bookmarks.Bookmarks;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesFactory;

import cytoscape.data.ExpressionData;
import cytoscape.data.ImportHandler;
import cytoscape.data.Semantics;

import cytoscape.data.readers.BookmarkReader;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.data.readers.GraphReader;

import cytoscape.data.servers.OntologyServer;

import cytoscape.ding.CyGraphLOD;

import org.cytoscape.view.DependentVisualPropertyCallback;
import org.cytoscape.view.DiscreteVisualProperty;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.VisualPropertyCatalog;
import org.cytoscape.view.VisualPropertyIcon;

import cytoscape.init.CyInitParams;

import org.cytoscape.layout.CyLayouts;
import org.cytoscape.layout.CyLayoutAlgorithm;

import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.FileUtil;

import org.cytoscape.view.GraphView;
import org.cytoscape.view.renderers.NodeRenderer;
import org.cytoscape.view.renderers.Renderer;

import cytoscape.view.CytoscapeDesktop;

import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.VMMFactory;
import org.cytoscape.vizmap.icon.ArrowIcon;
import org.cytoscape.vizmap.icon.LineTypeIcon;
import org.cytoscape.vizmap.icon.NodeIcon;

import org.cytoscape.Edge;
import org.cytoscape.Node;
import org.cytoscape.GraphPerspective;
import org.cytoscape.RootGraph;
import org.cytoscape.RootGraphFactory;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;

import javax.xml.bind.JAXBException;


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
	 */
	public static final String ONTOLOGY_ROOT = "ONTOLOGY_ROOT";

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
        * Specifies that the given VisualStyle has been modified. FIXME: this should event be moved to VisualMappingManager
        */
       public static final String VISUALSTYLE_MODIFIED = "VISUALSTYLE_MODIFIED";

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


	/**
	 * New ontology server. This will replace BioDataServer.
	 */
	private static OntologyServer ontologyServer;
	private static String species;

	/**
	 *
	 */
	public static final String READER_CLIENT_KEY = "reader_client_key";

	// global flag to indicate if Squiggle is turned on
//	private static boolean squiggleEnabled = false;

	/**
	 * The shared RootGraph between all Networks
	 */
	protected static RootGraph cytoscapeRootGraph;

	/**
	 * Node CyAttributes.
	 */
	private static CyAttributes nodeAttributes = CyAttributesFactory.getCyAttributes("node"); 

	/**
	 * Edge CyAttributes.
	 */
	private static CyAttributes edgeAttributes = CyAttributesFactory.getCyAttributes("edge"); 

	/**
	 * Network CyAttributes.
	 */
	private static CyAttributes networkAttributes = CyAttributesFactory.getCyAttributes("network"); 

	/**
	 * Ontology Attributes
	 *
	 * Will be used to store annotations for ontology
	 *
	 */
	private static CyAttributes ontologyAttributes = CyAttributesFactory.getCyAttributes("ontology"); 
	protected static ExpressionData expressionData;
	protected static Object pcsO = new Object();
	protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);

	// Test
	protected static Object pcs2 = new Object();
	protected static PropertyChangeSupport newPcs = new PropertyChangeSupport(pcs2);
	protected static Map<String,GraphView> networkViewMap;
	protected static Map<String,GraphPerspective> networkMap;
	protected static CytoscapeDesktop defaultDesktop;
	protected static String currentNetworkID;
	protected static String currentNetworkViewID;
	protected static String ontologyRootID;

	/**
	 * Used by session writer. If this is null, session writer opens the file
	 * chooser. Otherwise, overwrite the file.
	 *
	 * KONO: 02/23/2006
	 */
	private static String currentSessionFileName;
	private static Bookmarks bookmarks;

	/**
	 * A null CyNetwork to give when there is no Current Network
	 */
	protected static GraphPerspective nullNetwork = getRootGraph().createGraphPerspective(new int[] { },new int[] { });
	private static ImportHandler importHandler = new ImportHandler();

	/**
	 * The list analog to the currentNetworkViewID
	 */
	protected static LinkedList<GraphView> selectedNetworkViews = new LinkedList<GraphView>();

	/**
	 * The list analog to the currentNetworkID
	 */
	protected static LinkedList<GraphPerspective> selectedNetworks = new LinkedList<GraphPerspective>();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ImportHandler getImportHandler() {
		return importHandler;
	}

	/**
	 * A null GraphView to give when there is no Current NetworkView
	 */
	protected static GraphView nullNetworkView = GraphViewFactory.createGraphView(nullNetwork);

	/**
	 * @return a nullNetworkView object. This is NOT simply a null object.
	 */
	public static GraphView getNullNetworkView() {
		return nullNetworkView;
	}

	/**
	 * @return the nullNetwork GraphPerspective. This is NOT simply a null object.
	 */
	public static GraphPerspective getNullNetwork() {
		return nullNetwork;
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
					getDesktop().dispose();
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
		int n = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), msg,
		                                     "Save Networks Before Quitting?",
		                                     JOptionPane.YES_NO_OPTION,
		                                     JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (n == JOptionPane.NO_OPTION) {
			return true;
		} else if (n == JOptionPane.YES_OPTION) {
			SaveSessionAction saveAction = new SaveSessionAction();
			saveAction.actionPerformed(null);

			if (Cytoscape.getCurrentSessionFileName() == null) {
				return confirmQuit();
			} else {
				return true;
			}
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
	 * Return the RootGraph
	 */
	public static RootGraph getRootGraph() {
		if (cytoscapeRootGraph == null)
			cytoscapeRootGraph = RootGraphFactory.getRootGraph(); 

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
	public static List<Node> getCyNodesList() {
		return getRootGraph().nodesList();
	}

	/**
	 * @return all CyEdges that are present in Cytoscape
	 */
	public static List<Edge> getCyEdgesList() {
		return getRootGraph().edgesList();
	}

	/**
	 * This method is used to replace direct access to the rootgraph.
	 */
	public static Node getNode(int index) {
		return getRootGraph().getNode(index);
	}

	/**
	 * This method is used to replace direct access to the rootgraph.
	 */
	public static Edge getEdge(int index) {
		return getRootGraph().getEdge(index);
	}

	/**
	 * @param alias an alias of a node
	 * @return will return a node, if one exists for the given alias
	 */
	public static Node getCyNode(String alias) {
		return getCyNode(alias, false);
	}

	/**
	 * @param id the edge identifier 
	 * @return will return an edge, if one exists for the given identifier
	 */
	public static Edge getCyEdge(String id) {
		return getRootGraph().getEdge(id);
	}


	/**
	 * @param nodeID
	 *            an alias of a node
	 * @param create
	 *            will create a node if one does not exist
	 * @return will always return a node, if <code>create</code> is true
	 *
	 */
	public static Node getCyNode(String nodeID, boolean create) {
		Node node = Cytoscape.getRootGraph().getNode(nodeID);

		// If the node is already exists,return it.
		if (node != null) {
			return node;
		}

		// And if we do not have to create new one, just return null
		if (!create) {
			return null;
		}

		// Now, create a new node.
		node = (Node) getRootGraph().getNode(Cytoscape.getRootGraph().createNode());
		node.setIdentifier(nodeID);

		// create the CANONICAL_NAME attribute
		if (getNodeAttributes().getStringAttribute(nodeID, Semantics.CANONICAL_NAME) == null) {
			getNodeAttributes().setAttribute(nodeID, Semantics.CANONICAL_NAME, nodeID);
		}

		return node;
	}

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
	 */
	public static Edge getCyEdge(Node node_1, Node node_2, String attribute,
	                               Object attribute_value, boolean create) {
		if (!create){
			Edge e = getCyEdge(node_1, node_2, attribute, attribute_value, create, true);
			if (e == null){
				return getCyEdge(node_1, node_2, attribute, attribute_value, create, false);
			} else { return e;}
		} else {
			return getCyEdge(node_1, node_2, attribute, attribute_value, create, true);
		}
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
	public static Edge getCyEdge(Node source, Node target, String attribute,
	                               Object attribute_value, boolean create, boolean directed) {
		if (Cytoscape.getRootGraph().getEdgeCount() != 0) {
			int[] n1Edges = Cytoscape.getRootGraph()
			                         .getAdjacentEdgeIndicesArray(source.getRootGraphIndex(), true,
			                                                      true, true);

			for (int i = 0; i < n1Edges.length; i++) {
				Edge edge = (Edge) Cytoscape.getRootGraph().getEdge(n1Edges[i]);
				Object attValue = private_getEdgeAttributeValue(edge, attribute);

				if ((attValue != null) && attValue.equals(attribute_value)) {
					// Despite the fact that we know the source node
					// matches, the case of self edges dictates that
					// we must check the source as well.
					Node edgeTarget = (Node) edge.getTarget();
					Node edgeSource = (Node) edge.getSource();

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
			Edge edge = (Edge) Cytoscape.getRootGraph()
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
	 */
	public static Edge getCyEdge(String source_alias, String edge_name, String target_alias,
            String interaction_type) {
			return getCyEdge(source_alias, edge_name, target_alias, interaction_type, true);
	}
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
	 */	
	public static Edge getCyEdge(String source_alias, String edge_name, String target_alias,
	                               String interaction_type, boolean directed) {

		Edge edge = Cytoscape.getRootGraph().getEdge(edge_name);

		if (edge != null) {
			return edge;
		}

		// edge does not exist, create one
		Node source = getCyNode(source_alias);
		Node target = getCyNode(target_alias);

		return getCyEdge(source, target, Semantics.INTERACTION, interaction_type, true, directed);
	}

	private static Object private_getEdgeAttributeValue(Edge edge, String attribute) {
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


	// --------------------//
	// Network Methods
	// --------------------//

	/**
	 * Return the Network that currently has the Focus. Can be different from
	 * getCurrentNetworkView
	 */
	public static GraphPerspective getCurrentNetwork() {
		if ((currentNetworkID == null) || !(getNetworkMap().containsKey(currentNetworkID)))
			return nullNetwork;

		GraphPerspective network = (GraphPerspective) getNetworkMap().get(currentNetworkID);

		return network;
	}

	/**
	 * Return a List of all available GraphPerspective
	 */
	public static Set<GraphPerspective> getNetworkSet() {
		return new java.util.LinkedHashSet<GraphPerspective>(getNetworkMap().values());
	}

	/**
	 * @return the GraphPerspective that has the given identifier or the nullNetwork
	 *         (see {@link #getNullNetwork()}) if there is no such network.
	 */
	public static GraphPerspective getNetwork(String id) {
		if ((id != null) && getNetworkMap().containsKey(id))
			return (GraphPerspective) getNetworkMap().get(id);

		return nullNetwork;
	}

	/**
	 * @return a GraphView for the given ID, if one exists, otherwise
	 *         returns null
	 */
	public static GraphView getNetworkView(String network_id) {
		if ((network_id == null) || !(getNetworkViewMap().containsKey(network_id)))
			return nullNetworkView;

		GraphView nview = (GraphView) getNetworkViewMap().get(network_id);

		return nview;
	}

	/**
	 * @return if a view exists for a given network id
	 */
	public static boolean viewExists(String network_id) {
		return getNetworkViewMap().containsKey(network_id);
	}

	/**
	 * Return the GraphView that currently has the focus. Can be different
	 * from getCurrentNetwork
	 */
	public static GraphView getCurrentNetworkView() {
		if ((currentNetworkViewID == null)
		    || !(getNetworkViewMap().containsKey(currentNetworkViewID)))
			return nullNetworkView;

		GraphView nview = (GraphView) getNetworkViewMap().get(currentNetworkViewID);

		return nview;
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
    public static void setSelectedNetworkViews(final List<String> viewIDs) {
        selectedNetworkViews.clear();

        if (viewIDs == null)
            return;

        for (String id : viewIDs) {
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
	public static List<GraphPerspective> getSelectedNetworks() {
        GraphPerspective curr = getCurrentNetwork();

        if (!selectedNetworks.contains(curr))
            selectedNetworks.add(curr);

        return new ArrayList<GraphPerspective>(selectedNetworks);
	}

	/**
	 * Sets the list of selected networks.
	 */
	public static void setSelectedNetworks(final List<String> ids) {
        selectedNetworks.clear();

        if (ids == null)
            return;

        for (String id : ids) {
            GraphPerspective n = getNetworkMap().get(id);

            if ((n != null) && (n != nullNetwork)) {
                selectedNetworks.add(n);
            }
        }

        GraphPerspective cn = getCurrentNetwork();

        if (!selectedNetworks.contains(cn)) {
            selectedNetworks.add(cn);
        }
	}

	/**
	 * @return the reference to the One CytoscapeDesktop
	 */
	public static CytoscapeDesktop getDesktop() {
		if (defaultDesktop == null) {
			defaultDesktop = new CytoscapeDesktop();
			// these listeners should be hooked up in the constructor of the given objects, but that would create an infinite loop.
			// so hook them up here instead
			Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(defaultDesktop.getVizMapperUI());
			Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(defaultDesktop.getVizMapperUI().getVPSP());
		}

		return defaultDesktop;
	}

	/**
	 */
    public static void setCurrentNetwork(String id) {
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
    public static boolean setCurrentNetworkView(String id) {
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
	protected static Map<String,GraphPerspective> getNetworkMap() {
		if (networkMap == null) {
			networkMap = new HashMap<String,GraphPerspective>();
		}

		return networkMap;
	}

	/**
	 * This Map has keys that are Strings ( network_ids ) and values that are
	 * networkviews.
	 */
	public static Map<String,GraphView> getNetworkViewMap() {
		if (networkViewMap == null) {
			networkViewMap = new HashMap<String,GraphView>();
		}

		return networkViewMap;
	}

	/**
	 * destroys the given network
	 */
	public static void destroyNetwork(String network_id) {
		destroyNetwork((GraphPerspective) getNetworkMap().get(network_id));
	}

	/**
	 * destroys the given network
	 */
	public static void destroyNetwork(GraphPerspective network) {
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
    public static void destroyNetwork(GraphPerspective network, boolean destroy_unique) {
        if ((network == null) || (network == nullNetwork))
            return;

        getSelectedNetworks().remove(network);

        final String networkId = network.getIdentifier();

        firePropertyChange(NETWORK_DESTROYED, null, networkId);

        network.unselectAllEdges();
        network.unselectAllNodes();

        final Map<String, GraphPerspective> nmap = getNetworkMap();
        nmap.remove(networkId);

        if (networkId.equals(currentNetworkID)) {
            if (nmap.size() <= 0) {
                currentNetworkID = null;
            } else {
                // randomly pick a network to become the current network
                for (String key : nmap.keySet()) {
                    currentNetworkID = key;

                    break;
                }
            }
        }

        if (viewExists(networkId))
            destroyNetworkView(network);

        if (destroy_unique) {
            final List<Node> nodes = new ArrayList<Node>();
            final List<Edge> edges = new ArrayList<Edge>();

            final Collection<GraphPerspective> networks = networkMap.values();

            for (Node node : nodes) {
                boolean add = true;

                for (GraphPerspective net : networks) {
                    if (net.containsNode(node)) {
                        add = false;

                        continue;
                    }
                }

                if (add)
                    nodes.add(node);
            }

            for (Edge edge : edges) {
                boolean add = true;

                for (GraphPerspective net : networks) {
                    if (net.containsEdge(edge)) {
                        add = false;

                        continue;
                    }
                }

                if (add)
                    edges.add(edge);
            }

            for (Node node : nodes) {
                getRootGraph().removeNode(node);
                node = null;
            }

            for (Edge edge : edges) {
                getRootGraph().removeEdge(edge);
                edge = null;
            }
        }

        // theoretically this should not be set to null till after the events
        // firing is done
        network = null;

        // updates the desktop - but only if the view is null
        // if a view exists, then the focus will have already been updated
        // in destroyNetworkView
        if ((currentNetworkID != null) && (currentNetworkViewID == null))
            getDesktop().setFocus(currentNetworkID);
    }




	/**
	 * destroys the networkview, including any layout information
	 */
	public static void destroyNetworkView(GraphView view) {
		if ((view == null) || (view == nullNetworkView))
			return;

		getSelectedNetworkViews().remove(view);

		String viewID = view.getIdentifier();

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

		// so that a network will be selected.
		if (currentNetworkID != null)
			getDesktop().setFocus(currentNetworkID);
	}

	/**
	 * destroys the networkview, including any layout information
	 */
	public static void destroyNetworkView(String network_view_id) {
		destroyNetworkView(getNetworkViewMap().get(network_view_id));
	}

	/**
	 * destroys the networkview, including any layout information
	 */
	public static void destroyNetworkView(GraphPerspective network) {
		destroyNetworkView(getNetworkViewMap().get(network.getIdentifier()));
	}

	protected static void addNetwork(GraphPerspective network, String title, GraphPerspective parent,
	                                 boolean create_view) {
		getNetworkMap().put(network.getIdentifier(), network);
		network.setTitle(title);

		String parentID = null;

		if (parent != null) {
			parentID = parent.getIdentifier();
		}

		firePropertyChange(NETWORK_CREATED, parentID, network.getIdentifier());

		if ((network.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
		                                                            .getProperty("viewThreshold")))
		    && create_view) {
			createNetworkView(network);
		}
	}

	/**
	 * Creates a new, empty Network.
	 *
	 * @param title
	 *            the title of the new network.
	 */
	public static GraphPerspective createNetwork(String title) {
		return createNetwork(new int[] {  }, new int[] {  }, title, null, true);
	}

	/**
	 * Creates a new, empty Network.
	 *
	 * @param title
	 *            the title of the new network.
	 * @param create_view
	 *            if the size of the network is under the node limit, create a
	 *            view
	 */
	public static GraphPerspective createNetwork(String title, boolean create_view) {
		return createNetwork(new int[] {  }, new int[] {  }, title, null, create_view);
	}

	/**
	 * Creates a new, empty Network.
	 *
	 * @param title
	 *            the title of the new network.
	 * @param create_view
	 *            if the size of the network is under the node limit, create a
	 *            view
	 */
	public static GraphPerspective createNetwork(String title, GraphPerspective parent, boolean create_view) {
		return createNetwork(new int[] {  }, new int[] {  }, title, parent, create_view);
	}

	/**
	 * Creates a new Network. A view will be created automatically.
	 *
	 * @param nodes
	 *            the indeces of nodes
	 * @param edges
	 *            the indeces of edges
	 * @param title
	 *            the title of the new network.
	 */
	public static GraphPerspective createNetwork(int[] nodes, int[] edges, String title) {
		return createNetwork(nodes, edges, title, null, true);
	}

	/**
	 * Creates a new Network. A view will be created automatically.
	 *
	 * @param nodes
	 *            a collection of nodes
	 * @param edges
	 *            a collection of edges
	 * @param title
	 *            the title of the new network.
	 */
	public static GraphPerspective createNetwork(Collection<Node> nodes, Collection<Edge> edges, String title) {
		return createNetwork(nodes, edges, title, null, true);
	}

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
	 */
	public static GraphPerspective createNetwork(int[] nodes, int[] edges, String child_title,
	                                      GraphPerspective parent) {
		return createNetwork(nodes, edges, child_title, parent, true);
	}

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
	 */
	public static GraphPerspective createNetwork(int[] nodes, int[] edges, String child_title,
	                                      GraphPerspective parent, boolean create_view) {
		GraphPerspective network = getRootGraph().createGraphPerspective(nodes, edges);
		addNetwork(network, child_title, parent, create_view);

		return network;
	}

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
	 */
	public static GraphPerspective createNetwork(Collection<Node> nodes, Collection<Edge> edges, String child_title,
	                                      GraphPerspective parent) {
		return createNetwork(nodes, edges, child_title, parent, true);
	}

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
	 */
	public static GraphPerspective createNetwork(Collection<Node> nodes, Collection<Edge> edges, String child_title,
	                                      GraphPerspective parent, boolean create_view) {
		GraphPerspective network = getRootGraph().createGraphPerspective(nodes, edges);
		addNetwork(network, child_title, parent, create_view);

		return network;
	}

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
	 */
	public static GraphPerspective createNetworkFromFile(String location) {
		return createNetworkFromFile(location, true);
	}

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
	 */
	public static GraphPerspective createNetworkFromFile(String loc, boolean create_view) {
		return createNetwork(importHandler.getReader(loc), create_view, null);
	}

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
	 */
	public static GraphPerspective createNetworkFromURL(URL url, boolean create_view) {
		return createNetwork(importHandler.getReader(url), create_view, null);
	}

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
	 */
	public static GraphPerspective createNetwork(final GraphReader reader, final boolean create_view,
	                                      final GraphPerspective parent) {
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

		final GraphPerspective network = getRootGraph().createGraphPerspective(nodes, edges);
		addNetwork(network, title, parent, false);

		if (create_view && (network.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
		                                                            .getProperty("viewThreshold")))) {
			createNetworkView(network,title,reader.getLayoutAlgorithm());
		}

		// Execute any necessary post-processing.
		reader.doPostProcessing(network);

		return network;
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
	 * Gets Global Network Attributes.
	 *
	 * @return CyAttributes Object.
	 */
	public static CyAttributes getOntologyAttributes() {
		return ontologyAttributes;
	}

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
	 */
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

	/**
	 * This will replace the bioDataServer.
	 */
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static OntologyServer getOntologyServer() {
		return ontologyServer;
	}

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
	public static GraphView createNetworkView(GraphPerspective network) {
		return createNetworkView(network, network.getTitle(), null, null);
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
	public static GraphView createNetworkView(GraphPerspective network, String title) {
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
	public static GraphView createNetworkView(GraphPerspective network, String title, CyLayoutAlgorithm layout) {
		return createNetworkView(network,title,layout,null);
	}


	/**
	 * Creates a GraphView that is placed placed in a given visual style
	 * and rendered with a given layout algorithm.
	 * The GraphView will become the current view and have focus.
	 *
	 * @param network
	 *            the network to create a view of
	 * @param title
	 *            the title to use for the view
	 * @param layout
	 *            the CyLayoutAlgorithm to use for layout. If null, will
	 *            use the default layout (CyLayouts.getDefaultLayout()).
	 * @param vs the VisualStyle in which to render this new network. If null,
	 *           the default visual style will be used.
	 */
	public static GraphView createNetworkView(GraphPerspective network, String title, CyLayoutAlgorithm layout,
	                                              VisualStyle vs) {
		if (network == nullNetwork) {
			return nullNetworkView;
		}

		if (Cytoscape.viewExists(network.getIdentifier())) {
			return getNetworkView(network.getIdentifier());
		}

		final GraphView view = GraphViewFactory.createGraphView(network);
		view.setGraphLOD(new CyGraphLOD());
		view.setIdentifier(network.getIdentifier());
		getNetworkViewMap().put(network.getIdentifier(), view);

		// TODO:  Evaluate this hack.  It is done to make sure that current network view
		// is set for listeners of NETWORK_VIEW_CREATED.  Apparently in Cytoscape 2.6
		// the NetworkViewManager heard the CREATED event first, and was able to set the
		// value in time for others.  Not so in 3.0.  
		// Ideally we would just get rid of getCurrentNetworkView().
		setCurrentNetworkView(network.getIdentifier());

		setSelectionMode(Cytoscape.getSelectionMode(), view);

		if (vs != null) {
			VMMFactory.getVisualMappingManager().setVisualStyleForView(view,vs);
		}

		if (layout == null) {
			layout = CyLayouts.getDefaultLayout();
		}

		Cytoscape.firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null, view);

		layout.doLayout(view);

		view.fitContent();

		redrawGraph(view);

		return view;
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
		GraphView view;
		String network_id;
		Map networkViewMap = getNetworkViewMap();

		for (Iterator iter = networkViewMap.keySet().iterator(); iter.hasNext();) {
			network_id = (String) iter.next();
			view = (GraphView) networkViewMap.get(network_id);
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
        Set<GraphPerspective> netSet = getNetworkSet();

        for (GraphPerspective net : netSet)
            destroyNetwork(net, true);

        // Clear node attributes
        final String[] nodeAttrNames = nodeAttributes.getAttributeNames();
        for (String name: nodeAttrNames)
            nodeAttributes.deleteAttribute(name);

        // Clear edge attributes
        final String[] edgeAttrNames = edgeAttributes.getAttributeNames();
        for (String name: edgeAttrNames)
            edgeAttributes.deleteAttribute(name);

        // Clear network attributes
        final String[] networkAttrNames = networkAttributes.getAttributeNames();
        for(String name: networkAttrNames)
            networkAttributes.deleteAttribute(name);

        // Reset Ontology Server
        buildOntologyServer();
        setOntologyRootID(null);

        setCurrentSessionFileName(null);
        firePropertyChange(ATTRIBUTES_CHANGED, null, null);
        cytoscapeRootGraph = null;
        cytoscapeRootGraph = RootGraphFactory.getRootGraph(); 
        System.out.println("Cytoscape Session Initialized.");
        System.gc();
    }

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static String getOntologyRootID() {
		return ontologyRootID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param id DOCUMENT ME!
	 */
	public static void setOntologyRootID(String id) {
		ontologyRootID = id;
	}

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
		view.updateView();
	}

	public static VisualMappingManager getVisualMappingManager() {
		return VMMFactory.getVisualMappingManager();
	}


	/** return array of Integer-s containing first,...last with step size increment */
	private static Object[] range(int first, int last, int increment){
		ArrayList result = new ArrayList();
		for (int i = first; i!=last; i+=increment){
			result.add(Integer.valueOf(i));
		}
		return result.toArray();
	}

	public static Map<Object, VisualPropertyIcon> getArrowIconSet(Object [] values, Map<Byte, Shape>shapes) {
		Map<Object, VisualPropertyIcon> arrowShapeIcons = new HashMap<Object, VisualPropertyIcon>();

		for (int i = 0; i < values.length; i++) {
			Integer value = (Integer) values[i];
			Shape shape = shapes.get(new Byte(value.byteValue()));
			ArrowIcon icon = new ArrowIcon(shape, 
			                               VisualPropertyIcon.DEFAULT_ICON_SIZE, 
			                               VisualPropertyIcon.DEFAULT_ICON_SIZE,
			                               "someShape" /* FIXME: user-friendly string! */);
			arrowShapeIcons.put(shape, (VisualPropertyIcon) icon);
		}

		return arrowShapeIcons;
	}

	public static Map<Object, VisualPropertyIcon> getLineStyleIconSet(Object [] values) {
		Map<Object, VisualPropertyIcon> arrowShapeIcons = new HashMap<Object, VisualPropertyIcon>();

		for (int i = 0; i < values.length; i++) {
			Stroke value = (Stroke) values[i];
			arrowShapeIcons.put(value, (VisualPropertyIcon) new LineTypeIcon(value));
		}

		return arrowShapeIcons;
	}

	
	public static Map<Object, VisualPropertyIcon> getRendererIconSet(Object [] values) {
		Map<Object, VisualPropertyIcon> rendererIcons = new HashMap<Object, VisualPropertyIcon>();
		for (int i = 0; i < values.length; i++) {
			NodeRenderer value = (NodeRenderer) values[i];
			
			rendererIcons.put(value, (VisualPropertyIcon) new NodeIcon(value.name())); // FIXME FIXME: do property icons!
		}
		return rendererIcons;
	}


	/** For the duration of pluggable VisualProperties refactor: add legacy VisualProperties */
	public static void defineHardcodedVisualProperties(){
		Object [] range = new Object[]{new TrivialRenderer("trivialrenderer"), new ShapeRenderer("shaperenderer")};
		Map<Object, VisualPropertyIcon> iconSet = getRendererIconSet(range);
		
		VisualPropertyCatalog.addVisualProperty(new DiscreteVisualProperty("NODE_RENDERER", NodeRenderer.class, true, range, iconSet,
			new DependentVisualPropertyCallback(){
				public Set<VisualProperty> changed(Collection<NodeView> nodeviews, Collection<EdgeView> edgeviews, Collection<VisualProperty> current_vps){
					Set <NodeRenderer> renderers = new HashSet<NodeRenderer>();
					for (NodeView nv: nodeviews){
						if (nv.getRenderer() != null){ // FIXME: debug why this can happen, and remove this check
							renderers.add(nv.getRenderer());
						} else {
							System.out.println("fixme: this shouldn't happen: renderer is null!");
						}
							
					}
					Set <VisualProperty> visualProperties = new HashSet<VisualProperty>();
					for (Renderer renderer: renderers){
						visualProperties.addAll(renderer.supportedVisualAttributes());
					}
					Set <VisualProperty> toRemove = new HashSet<VisualProperty>();
					for (VisualProperty vp: current_vps){
						if (vp.getName().startsWith("NODE_")){
							toRemove.add(vp);
						}
					}
					toRemove.removeAll(visualProperties);
					toRemove.remove(VisualPropertyCatalog.getVisualProperty("NODE_RENDERER"));
					return toRemove;
				}
		}));
		for (int i = 0; i< range.length; i++){
			Renderer renderer = (Renderer) range[i];
			VisualPropertyCatalog.addVisualPropertiesOfRenderer(renderer);
		}
		
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_COLOR", Color.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_LABEL", String.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_FONT_FACE", Font.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_FONT_SIZE", Number.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_LABEL_COLOR", Color.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_TOOLTIP", String.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_LINE_WIDTH", Number.class, false));

		range = new Object[]{new BasicStroke(1.0f/*FIXME: width -- is this just a placeholder value? */),
				new BasicStroke(1.0f /*see above */, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f, 4.0f}, 0.0f)  };
		iconSet = getLineStyleIconSet(range);
		VisualPropertyCatalog.addVisualProperty( new DiscreteVisualProperty("EDGE_LINE_STYLE", Stroke.class, false, range, iconSet));
		
		range = range(-1, -5, -1); 
		iconSet = getArrowIconSet(range, GraphGraphics.getArrowShapes()); 
		VisualPropertyCatalog.addVisualProperty( new DiscreteVisualProperty("EDGE_SRCARROW_SHAPE", Integer.class, false, range, iconSet));
		VisualPropertyCatalog.addVisualProperty( new DiscreteVisualProperty("EDGE_TGTARROW_SHAPE", Integer.class, false, range, iconSet));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_SRCARROW_COLOR", Color.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_TGTARROW_COLOR", Color.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_OPACITY", Number.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_LABEL_OPACITY", Number.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_SRCARROW_OPACITY", Number.class, false));
		VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_TGTARROW_OPACITY", Number.class, false));
		
		//VisualPropertyCatalog.addVisualProperty( new LegacyVisualProperty("EDGE_LABEL_POSITION", Number.class, false));
	}
}
