/*$Id: LinkOutPlugin.java 9566 2007-02-13 20:13:13Z mes $*/
package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import cytoscape.*;

import cytoscape.plugin.*;

import ding.view.*;

import java.util.*;

public class PopupMenuPlugin extends CytoscapePlugin {
	/**
	 * Creates a new LinkOutPlugin object.
	 */
	public PopupMenuPlugin() {
		try {
			//Create a Network create event listener
			PopupMenuNetworkListener m_listener = new PopupMenuNetworkListener();
			Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(m_listener);

			// Create a new ContextMenuListener and register with the pre-loaded networks.
			// Cases where networks are loaded before the plugins. For example, when running Cytoscape
			// from the command line.
			//Todo - To be tested
			Set networkSet = Cytoscape.getNetworkSet();

			for (Iterator it = networkSet.iterator(); it.hasNext();) {
				CyNetwork cyNetwork = (CyNetwork) it.next();

				PopupNodeContextMenuListener nodeMenuListener = new PopupNodeContextMenuListener();
				((DGraphView) Cytoscape.getNetworkView(cyNetwork.getIdentifier()))
				                                                              .addNodeContextMenuListener(nodeMenuListener);

			}

		} catch (ClassCastException e) {
			System.out.println(e.getMessage());

			return;
		}
	}
}
