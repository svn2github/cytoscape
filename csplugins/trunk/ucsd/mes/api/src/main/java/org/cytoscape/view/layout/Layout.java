package org.cytoscape.view.layout;

import org.cytoscape.view.CyNetworkView;

public interface Layout {

	public String getName();
	public void doLayout(CyNetworkView networkView);
}

