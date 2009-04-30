package org.cytoscape.view.vizmap.gui;

import org.cytoscape.view.model.CyNetworkView;

public interface DefaultViewPanel {

	/**
	 * Get dummy network view.
	 * Dummy network view is a network displayed on the default view editor.
	 * Usually it is a network with an edge and two nodes.
	 * 
	 * @return DOCUMENT ME!
	 */
	public CyNetworkView getView();

}