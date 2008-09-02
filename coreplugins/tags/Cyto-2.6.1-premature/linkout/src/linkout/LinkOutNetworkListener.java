/*$Id$*/
package linkout;

import cytoscape.Cytoscape;

import cytoscape.view.CytoscapeDesktop;

import ding.view.*;

import java.beans.*;


/**
 * LinkOutNetworkListener implements PropertyChangeListener for new Network instances
 * When a new cytoscape network view is created it registers the LinkOutContextMenuListener
 * with the new DGraphView.
**/
public class LinkOutNetworkListener implements PropertyChangeListener {
	/**
	 *  DOCUMENT ME!
	 */
	public void LinkOutNetworkListener() {
		//System.out.println("[LinkOutNetworkListener]: constructor called");
	}

	/**
	 * Register a LinkOut[Node/Edge]ContextMenuListener for all new DGraphView
	 * objects (ie new network instances).
	 * @param evnt PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent evnt) {
		if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			//System.out.println("[LinkOutNetworkListener]: propertyChange called");

			//Register NodeContext...
			LinkOutNodeContextMenuListener nodeMenuListener = new LinkOutNodeContextMenuListener();
			((DGraphView) Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(nodeMenuListener);

			//Register EdgeContext...
			LinkOutEdgeContextMenuListener edgeMenuListener = new LinkOutEdgeContextMenuListener();
			((DGraphView) Cytoscape.getCurrentNetworkView()).addEdgeContextMenuListener(edgeMenuListener);
		}
	}
}
/*$Log: LinkOutNetworkListener.java,v $
/*Revision 1.1  2006/06/14 18:12:46  mes
/*updated project to actually compile and work with ant
/*
/*Revision 1.2  2006/06/12 19:27:44  betel
/*Fixes to bug reports 346-links to missing labels, 637-linkout fix for command line mode
/*
/*Revision 1.1  2006/05/19 21:51:29  betel
/*New implementation of LinkOut with network-view listener
/**/
