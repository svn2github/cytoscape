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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.view.CySwingApplication;
import org.cytoscape.session.CyNetworkManager;



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
	public static String NETWORK_DESTROYED = "NETWORK_DESTROYED";

	/**
	 *
	 */
	public static String CYTOSCAPE_INITIALIZED = "CYTOSCAPE_INITIALIZED";

	/**
	 *
	 */
	public static String CYTOSCAPE_EXIT = "CYTOSCAPE_EXIT";


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

	protected static Object pcsO = new Object();
	protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);

	// Test
	protected static Object pcs2 = new Object();
	protected static PropertyChangeSupport newPcs = new PropertyChangeSupport(pcs2);


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
	 * This is a temporary utility method and will eventually be refactored away.
	 */
//	public static void redrawGraph(GraphView view) {
//		if(view == null)
//			return;
//		
//		VMMFactory.getVisualMappingManager().setNetworkView(view);
//		VMMFactory.getVisualMappingManager().applyAppearances();
//		view.updateView();
//	}
}
