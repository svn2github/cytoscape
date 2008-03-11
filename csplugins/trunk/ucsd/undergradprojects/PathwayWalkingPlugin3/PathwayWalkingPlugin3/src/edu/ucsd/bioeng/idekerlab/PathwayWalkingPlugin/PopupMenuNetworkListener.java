/*$Id: LinkOutNetworkListener.java 9566 2007-02-13 20:13:13Z mes $*/
package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import cytoscape.Cytoscape;

import cytoscape.view.CytoscapeDesktop;

import ding.view.*;

import java.beans.*;


/**
 * LinkOutNetworkListener implements PropertyChangeListener for new Network instances
 * When a new cytoscape network view is created it registers the LinkOutContextMenuListener
 * with the new DGraphView.
**/
public class PopupMenuNetworkListener implements PropertyChangeListener {
	/**
	 *  DOCUMENT ME!
	 */
	public void PopupMenuNetworkListener() {
		//System.out.println("[LinkOutNetworkListener]: constructor called");
	}

	
	public void propertyChange(PropertyChangeEvent evnt) {
		if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			//System.out.println("[LinkOutNetworkListener]: propertyChange called");

			//Register NodeContext...
			PopupNodeContextMenuListener nodeMenuListener = new PopupNodeContextMenuListener();
			((DGraphView) Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(nodeMenuListener);

		}
	}
}
