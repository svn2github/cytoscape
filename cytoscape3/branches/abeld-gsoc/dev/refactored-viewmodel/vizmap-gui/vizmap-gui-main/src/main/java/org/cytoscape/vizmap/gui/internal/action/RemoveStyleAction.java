package org.cytoscape.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.Cytoscape;

public class RemoveStyleAction extends AbstractVizMapperAction {

	public RemoveStyleAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -916650015995966595L;

	public void actionPerformed(ActionEvent e) {
		if (vmm.getVisualStyle().getName().equals(
				VisualMappingManager.DEFAULT_VS_NAME)) {
			JOptionPane.showMessageDialog(vizMapperMainPanel,
					"You cannot delete default style.", "Cannot remove style!",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		// make sure the user really wants to do this
		final String styleName = vmm.getVisualStyle().getName();
		final String checkString = "Are you sure you want to permanently delete"
				+ " the visual style '" + styleName + "'?";
		int ich = JOptionPane.showConfirmDialog(vizMapperMainPanel, checkString,
				"Confirm Delete Style", JOptionPane.YES_NO_OPTION);

		if (ich == JOptionPane.YES_OPTION) {
			final CalculatorCatalog catalog = vmm.getCalculatorCatalog();
			catalog.removeVisualStyle(styleName);

			// try to switch to the default style
			VisualStyle currentStyle = catalog
					.getVisualStyle(VisualMappingManager.DEFAULT_VS_NAME);

			/*
			 * Update Visual Mapping Browser.
			 */
			vizMapperMainPanel.getVsNameComboBox().removeItem(styleName);
			vizMapperMainPanel.getVsNameComboBox().setSelectedItem(
					currentStyle.getName());
			vizMapperMainPanel.switchVS(currentStyle.getName());
			vizMapperMainPanel.getDefaultImageManager().remove(styleName);
			vizMapPropertySheetBuilder.getPropertyMap().remove(styleName);

			vmm.setVisualStyle(currentStyle);
			vmm.setVisualStyleForView(vmm.getNetworkView(), currentStyle);
			Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());
		}
	}

}
