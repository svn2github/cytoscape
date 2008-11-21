/*$Id: LinkOutNetworkListener.java 9566 2007-02-13 20:13:13Z mes $*/
package cytoscape.linkout;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * LinkOutNetworkListener implements PropertyChangeListener for new Network instances
 * When a new cytoscape network view is created it registers the LinkOutContextMenuListener
 * with the new GraphView.
 */
public class LinkOutNetworkListener implements PropertyChangeListener {
	/**
	 *  DOCUMENT ME!
	 */
	public void LinkOutNetworkListener() {
		//System.out.println("[LinkOutNetworkListener]: constructor called");
	}

	/**
	 * Register a LinkOut[Node/Edge]ContextMenuListener for all new GraphView
	 * objects (ie new network instances).
	 * @param evnt PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent evnt) {
		if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			//System.out.println("[LinkOutNetworkListener]: propertyChange called");

			//Register NodeContext...
			LinkOutNodeContextMenuListener nodeMenuListener = new LinkOutNodeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(nodeMenuListener);

			//Register EdgeContext...
			LinkOutEdgeContextMenuListener edgeMenuListener = new LinkOutEdgeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(edgeMenuListener);
		}
	}
}
