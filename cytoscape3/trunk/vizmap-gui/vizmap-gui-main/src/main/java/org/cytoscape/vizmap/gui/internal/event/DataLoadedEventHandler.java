package org.cytoscape.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

public class DataLoadedEventHandler extends AbstractVizMapEventHandler {

	@Override
	public void processEvent(PropertyChangeEvent e) {
		final String vsName = vmm.getVisualStyle().getName();

		vizMapperMainPanel.setLastVSName(null);
		vizMapperMainPanel.refreshUI();
		vizMapperMainPanel.switchVS(vsName);
		vizMapperMainPanel.getVsNameComboBox().setSelectedItem(vsName);
		vmm.setVisualStyle(vsName);

	}

}
