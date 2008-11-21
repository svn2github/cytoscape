package cytoscape.visual.ui;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.Cytoscape;

public class RemoveStyleAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = -916650015995966595L;

	public void actionPerformed(ActionEvent e) {
		if (visualMappingManager.getVisualStyle().getName().equals(
				VizMapperMainPanel.DEFAULT_VS_NAME)) {
			JOptionPane.showMessageDialog(cytoscapeDesktop,
					"You cannot delete default style.", "Cannot remove style!",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		// make sure the user really wants to do this
		final String styleName = visualMappingManager.getVisualStyle().getName();
		final String checkString = "Are you sure you want to permanently delete"
				+ " the visual style '" + styleName + "'?";
		int ich = JOptionPane.showConfirmDialog(cytoscapeDesktop, checkString,
				"Confirm Delete Style", JOptionPane.YES_NO_OPTION);

		if (ich == JOptionPane.YES_OPTION) {
			final CalculatorCatalog catalog = visualMappingManager.getCalculatorCatalog();
			catalog.removeVisualStyle(styleName);

			// try to switch to the default style
			VisualStyle currentStyle = catalog
					.getVisualStyle(VizMapperMainPanel.DEFAULT_VS_NAME);

			/*
			 * Update Visual Mapping Browser.
			 */
			vizMapperMainPanel.getVsNameComboBox().removeItem(styleName);
			vizMapperMainPanel.getVsNameComboBox().setSelectedItem(
					currentStyle.getName());
			vizMapperMainPanel.switchVS(currentStyle.getName());
			vizMapperMainPanel.getDefaultImageManager().remove(styleName);
			vizMapperMainPanel.getPropertyMap().remove(styleName);

			visualMappingManager.setVisualStyle(currentStyle);
			visualMappingManager.setVisualStyleForView(visualMappingManager.getNetworkView(), currentStyle);
			Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());
		}
	}

}
