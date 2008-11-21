/*$Id: LinkOutPlugin.java 13020 2008-02-11 21:55:44Z mes $*/
package cytoscape.linkout;

import cytoscape.Cytoscape;
import org.cytoscape.model.CyNetwork;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Iterator;
import java.util.Set;


/**
 * LinkOut plugin for customized URL links
 */
public class LinkOutPlugin implements BundleActivator {
	/**
	 * Creates a new LinkOutPlugin object.
	 */
	public void start(BundleContext bc) {
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
				CyNetwork cyNetwork = (CyNetwork) it.next();

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

	public void stop(BundleContext bc) {
	}
}
