package org.genmapp.subgeneviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.genmapp.subgeneviewer.controller.SubgeneController;
import org.genmapp.subgeneviewer.splice.controller.SpliceController;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;

public class SubgeneViewerPlugin extends CytoscapePlugin implements
		PropertyChangeListener {

	
	private static SpliceController _controller;

	public SubgeneViewerPlugin() {

		_controller = new SpliceController();

		// Listen for Network View Focus
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseListener(_controller);
			System.out.println("SGV: Network View Focused");
		}
	}


}
