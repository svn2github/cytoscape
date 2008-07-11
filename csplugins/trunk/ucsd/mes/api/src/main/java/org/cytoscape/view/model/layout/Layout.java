package org.cytoscape.view.model.layout;

import org.cytoscape.view.model.CyNetworkView;

/** 
 * The central layout interface that all Layouts must support.  
 * This is meant to be as simple as possible for authors to use.
 * This interface currently omits any notion of a TaskMonitor,
 * something we'll need to sort out.
 */
public interface Layout {

	public String getName();
	public void doLayout(CyNetworkView networkView);
}

