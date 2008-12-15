package org.cytoscape.vizmap.gui.internal.action;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;

import org.cytoscape.vizmap.VisualStyle;

import com.l2fprod.common.propertysheet.Property;


public class RenameStyleAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = -3823557783901332855L;
	
	public RenameStyleAction() {
		super();
	}

	public void actionPerformed(ActionEvent e) {
		final VisualStyle currentStyle = vmm.getVisualStyle();
		final String oldName = currentStyle.getName();
		final String name = vizMapperUtil.getStyleName(vizMapperMainPanel, currentStyle);

		if (name == null)
			return;

		vizMapperMainPanel.setLastVSName(name);

		final Image img = vizMapperMainPanel.getDefaultImageManager().get(
				oldName);
		vizMapperMainPanel.getDefaultImageManager().put(name, img);
		vizMapperMainPanel.getDefaultImageManager().remove(oldName);

		/*
		 * Update name
		 */
		currentStyle.setName(name);

		vmm.getCalculatorCatalog().removeVisualStyle(oldName);
		vmm.getCalculatorCatalog()
				.addVisualStyle(currentStyle);

		vmm.setVisualStyle(currentStyle);
		vmm.setVisualStyleForView(vmm
				.getNetworkView(), currentStyle);

		/*
		 * Update combo box and
		 */
		vizMapperMainPanel.getVsNameComboBox().addItem(name);
		vizMapperMainPanel.getVsNameComboBox().setSelectedItem(name);
		vizMapperMainPanel.getVsNameComboBox().removeItem(oldName);

		final List<Property> props = vizMapPropertySheetBuilder.getPropertyMap().get(
				oldName);
		vizMapPropertySheetBuilder.getPropertyMap().put(name, props);
		vizMapPropertySheetBuilder.getPropertyMap().remove(oldName);
	}
}
