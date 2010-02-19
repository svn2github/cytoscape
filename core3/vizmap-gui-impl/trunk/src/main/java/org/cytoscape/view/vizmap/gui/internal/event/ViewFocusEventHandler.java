package org.cytoscape.view.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

import org.cytoscape.view.vizmap.VisualStyle;

//import cytoscape.internal.view.NetworkPanel;

public class ViewFocusEventHandler extends AbstractVizMapEventHandler {

	@Override
	public void processEvent(PropertyChangeEvent e) {

//		if (e.getSource().getClass() != NetworkPanel.class)
//			return;

		// Get visual style for the selected netwrok view.
		final VisualStyle vs = vmm.getVisualStyle(cyNetworkManager
				.getCurrentNetworkView());

		if (vs != null) {

			if (vs.equals(vizMapperMainPanel.getSelectedVisualStyle()) == false) {

				vizMapperMainPanel.switchVS(vs);

				vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
						.getDefaultImageManager().get(vs));
			}
		}
	}

}
