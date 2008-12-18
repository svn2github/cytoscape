package org.cytoscape.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

import org.cytoscape.vizmap.VisualStyle;

import cytoscape.Cytoscape;
import cytoscape.view.NetworkPanel;

public class ViewFocusEventHandler extends AbstractVizMapEventHandler {

	@Override
	public void processEvent(PropertyChangeEvent e) {

		if (e.getSource().getClass() != NetworkPanel.class)
			return;

		final VisualStyle vs = vmm.getVisualStyleForView(vmm.getNetworkView());

		if (vs != null) {
			vmm.setNetworkView(vizMapperMainPanel.getTargetView());

			if (vs.getName().equals(
					vizMapperMainPanel.getVsNameComboBox().getSelectedItem())) {
				Cytoscape.redrawGraph(vizMapperMainPanel.getTargetView());
			} else {
				vizMapperMainPanel.switchVS(vs.getName(), false);
				vizMapperMainPanel.getVsNameComboBox().setSelectedItem(
						vs.getName());
				vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
						.getDefaultImageManager().get(vs.getName()));
			}
		}

		vizMapperMainPanel.setTargetNetwork(cyNetworkManager.getNetwork((Long) (e
				.getNewValue())));
		vizMapperMainPanel.setTargetView(cyNetworkManager.getNetworkView((Long) (e
				.getNewValue())));

	}

}
