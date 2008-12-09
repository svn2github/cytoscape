package org.cytoscape.vizmap.gui;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cytoscape.vizmap.VisualPropertyType;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

public class RemoveMappingAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = -6131535468683162915L;

	public void actionPerformed(ActionEvent e) {

		final int selected = vizMapperMainPanel.getPropertySheetPanel()
				.getTable().getSelectedRow();

		if (0 <= selected) {
			Item item = (Item) vizMapperMainPanel.getPropertySheetPanel()
					.getTable().getValueAt(selected, 0);
			Property curProp = item.getProperty();

			if (curProp instanceof VizMapperProperty) {
				final VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) curProp)
						.getHiddenObject();

				if (type == null)
					return;

				String[] message = {
						"The Mapping for " + type.getName()
								+ " will be removed.", "Proceed?" };

				int value = JOptionPane.showConfirmDialog(vizMapperMainPanel,
						message, "Remove Mapping", JOptionPane.YES_NO_OPTION);

				if (value == JOptionPane.YES_OPTION) {
					// If Continuous Mapper is displayed, kill it.
					if (vizMapperMainPanel.getEditorWindowManager().get(type) != null) {
						JDialog editor = vizMapperMainPanel
								.getEditorWindowManager().get(type);
						editor.dispose();
						vizMapperMainPanel.getEditorWindowManager()
								.remove(type);
					}

					if (type.isNodeProp()) {
						vmm.getVisualStyle()
								.getNodeAppearanceCalculator()
								.removeCalculator(type);
					} else {
						vmm.getVisualStyle()
								.getEdgeAppearanceCalculator()
								.removeCalculator(type);
					}

					Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());

					/*
					 * Finally, move the visual property to "unused list"
					 */
					vizMapperMainPanel.getUnusedVisualPropType().add(type);

					VizMapperProperty prop = new VizMapperProperty();
					prop.setCategory(VizMapperMainPanel.CATEGORY_UNUSED);
					prop.setDisplayName(type.getName());
					prop.setHiddenObject(type);
					prop.setValue("Double-Click to create...");
					vizMapperMainPanel.getPropertySheetPanel()
							.addProperty(prop);
					vizMapperMainPanel.getPropertySheetPanel().removeProperty(
							curProp);

					vizMapperMainPanel.removeProperty(curProp);

					vizMapperMainPanel.getPropertyMap().get(
							vmm.getVisualStyle().getName())
							.add(prop);
					vizMapperMainPanel.getPropertySheetPanel().repaint();
				}
			}
		}
	}

}
