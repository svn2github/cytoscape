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
import cytoscape.view.CySwingApplication;
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
	protected static CytoscapeDesktop defaultDesktop;

	/**
	 * Used by session writer. If this is null, session writer opens the file
	 * chooser. Otherwise, overwrite the file.
	 *
	 * KONO: 02/23/2006
	 */
	private static String currentSessionFileName;
	private static Bookmarks bookmarks;

	private static CyNetworkManager netmgr;


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
		int networkCount = netmgr.getNetworkSet().size();

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
		for ( CyNetwork net : netmgr.getNetworkSet() )
			allNodes.addAll( net.getNodeList() );

		return allNodes;
	}

	/**
	 * @return all CyEdges that are present in Cytoscape
	 */
	public static List<CyEdge> getCyEdgesList() {
		List<CyEdge> allEdges = new ArrayList<CyEdge>();
		for ( CyNetwork net : netmgr.getNetworkSet() )
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


	/**
	 * Don't use this!.
	 * TODO resolve how the desktop is accessed. 
	 */
	public static void setDesktop(CytoscapeDesktop desk ) {
		if ( desk != null )
			defaultDesktop = desk;
	}

	/**
	 * Don't use this!.
	 */
	static void setNetworkManager(CyNetworkManager nm) {
		if ( nm != null )
			netmgr = nm;	
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

	public static boolean loadExpressionData(String filename, boolean copy_atts) {
		try {
			expressionData = new ExpressionData(filename);
		} catch (Exception e) {
			System.err.println("Unable to Load Expression Data");

			String errString = "Unable to load expression data from " + filename;
			String title = "Load Expression Data";
		}

		if (copy_atts) {
			// TODO where to add the expression attrs
			//expressionData.copyToAttribs(getNodeAttributes(), null);
			firePropertyChange(ATTRIBUTES_CHANGED, null, null);
		}

		// Fire off an EXPRESSION_DATA_LOADED event.
		Cytoscape.firePropertyChange(Cytoscape.EXPRESSION_DATA_LOADED, null, expressionData);

		return true;
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

		for ( GraphView view : netmgr.getNetworkViewSet() ) {
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
				// TODO NEED RENDERER
				view.disableEdgeSelection();
				// TODO NEED RENDERER
				view.enableNodeSelection();

				break;

			case SELECT_EDGES_ONLY:
				// TODO NEED RENDERER
				view.disableNodeSelection();
				// TODO NEED RENDERER
				view.enableEdgeSelection();

				break;

			case SELECT_NODES_AND_EDGES:
				// TODO NEED RENDERER
				view.enableNodeSelection();
				// TODO NEED RENDERER
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
        Set<CyNetwork> netSet = netmgr.getNetworkSet();

        for (CyNetwork net : netSet)
            netmgr.destroyNetwork(net);

        setCurrentSessionFileName(null);
        firePropertyChange(ATTRIBUTES_CHANGED, null, null);
        System.out.println("Cytoscape Session Initialized.");
        System.gc();
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
		if(view == null)
			return;
		
		VMMFactory.getVisualMappingManager().setNetworkView(view);
		VMMFactory.getVisualMappingManager().applyAppearances();
		view.updateView();
	}

	public static VisualMappingManager getVisualMappingManager() {
		return VMMFactory.getVisualMappingManager();
	}
}
