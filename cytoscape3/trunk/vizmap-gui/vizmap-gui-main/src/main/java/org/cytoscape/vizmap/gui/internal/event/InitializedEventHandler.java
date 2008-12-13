package org.cytoscape.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

public class InitializedEventHandler extends AbstractVizMapEventHandler {

	@Override
	public void processEvent(PropertyChangeEvent e) {
		String vmName = vmm.getVisualStyle().getName();
		vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
				.getDefaultImageManager().get(vmName));
		vizMapperMainPanel.getVsNameComboBox().setSelectedItem(vmName);
		vmm.setVisualStyle(vmName);
		vizMapPropertySheetBuilder.setPropertyTable();
		vizMapperMainPanel.updateAttributeList();

		propertySheetPanel.setSorting(true);

	}

}
