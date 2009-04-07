package org.cytoscape.view.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

import org.cytoscape.view.vizmap.VisualStyle;

import cytoscape.Cytoscape;
import cytoscape.view.NetworkPanel;

public class ViewFocusEventHandler extends AbstractVizMapEventHandler {

	@Override
	public void processEvent(PropertyChangeEvent e) {

		if (e.getSource().getClass() != NetworkPanel.class)
			return;

		final VisualStyle vs = vmm.getVisualStyleForView(vmm.getNetworkView());

		if (vs != null) {
			//vmm.setNetworkView(cyNetworkManager.getCurrentNetworkView());

			if (vs.getName().equals(
					vizMapperMainPanel.getVsNameComboBox().getSelectedItem())) {
				//Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());
			} else {
				vizMapperMainPanel.switchVS(vs.getName(), false);
				vizMapperMainPanel.getVsNameComboBox().setSelectedItem(
						vs.getName());
				vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
						.getDefaultImageManager().get(vs.getName()));
			}
		}

//		vizMapperMainPanel.setTargetNetwork(cyNetworkManager.getNetwork((Long) (e
//				.getNewValue())));
//		vizMapperMainPanel.setTargetView(cyNetworkManager.getNetworkView((Long) (e
//				.getNewValue())));

	}

}
