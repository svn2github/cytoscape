/*$Id: LinkOutPlugin.java 13020 2008-02-11 21:55:44Z mes $*/
package cytoscape.linkout;

import org.cytoscape.*;
import cytoscape.Cytoscape;

import cytoscape.plugin.*;

import org.cytoscape.view.*;

import java.util.*;



/**
 * LinkOut plugin for customized URL links
 */
public class LinkOutPlugin extends CytoscapePlugin {
	/**
	 * Creates a new LinkOutPlugin object.
	 */
	public LinkOutPlugin() {
		try {
			//Create a Network create event listener
			LinkOutNetworkListener m_listener = new LinkOutNetworkListener();
			Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(m_listener);

			// Create a new ContextMenuListener and register with the pre-loaded networks.
			// Cases where networks are loaded before the plugins. For example, when running Cytoscape
			// from the command line.
			//Todo - To be tested
			Set networkSet = Cytoscape.getNetworkSet();

			for (Iterator it = networkSet.iterator(); it.hasNext();) {
				GraphPerspective cyNetwork = (GraphPerspective) it.next();

				LinkOutNodeContextMenuListener nodeMenuListener = new LinkOutNodeContextMenuListener();
				Cytoscape.getNetworkView(cyNetwork.getIdentifier()).addNodeContextMenuListener(nodeMenuListener);

				LinkOutEdgeContextMenuListener edgeMenuListener = new LinkOutEdgeContextMenuListener();
				Cytoscape.getNetworkView(cyNetwork.getIdentifier()).addEdgeContextMenuListener(edgeMenuListener);
			}

		} catch (ClassCastException e) {
			System.out.println(e.getMessage());

			return;
		}
	}
}
