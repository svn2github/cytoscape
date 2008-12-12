package org.cytoscape.vizmap.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.internal.AbstractVizMapperPanel;
import org.cytoscape.vizmap.gui.internal.VizMapperProperty;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

public class RemoveMappingAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = -6131535468683162915L;
	
	public RemoveMappingAction(){
		super();
	}

	public void actionPerformed(ActionEvent e) {

		final int selected = propertySheetPanel.getTable()
				.getSelectedRow();

		if (0 <= selected) {
			Item item = (Item) propertySheetPanel.getTable()
					.getValueAt(selected, 0);
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
					if (editorWindowManager.isRegistered(type))
						editorWindowManager.removeEditorWindow(type);

					removeMapping(type);
//					if (type.isNodeProp()) {
//						vmm.getVisualStyle().getNodeAppearanceCalculator()
//								.removeCalculator(type);
//					} else {
//						vmm.getVisualStyle().getEdgeAppearanceCalculator()
//								.removeCalculator(type);
//					}
//
//					Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());
//
//					/*
//					 * Finally, move the visual property to "unused list"
//					 */
//					vizMapperMainPanel.unusedVisualPropType.add(type);
//
//					VizMapperProperty prop = new VizMapperProperty();
//					prop.setCategory(VizMapperMainPanel.CATEGORY_UNUSED);
//					prop.setDisplayName(type.getName());
//					prop.setHiddenObject(type);
//					prop.setValue("Double-Click to create...");
//					vizMapperMainPanel.propertySheetPanel.addProperty(prop);
//					vizMapperMainPanel.propertySheetPanel
//							.removeProperty(curProp);
//
//					vizMapperMainPanel.removeProperty(curProp);
//
//					vizMapperMainPanel.propertyMap.get(
//							vmm.getVisualStyle().getName()).add(prop);
//					vizMapperMainPanel.propertySheetPanel.repaint();
				}
			}
		}
	}

	private void removeMapping(final VisualPropertyType type) {
		if (type.isNodeProp()) {
			vmm.getVisualStyle().getNodeAppearanceCalculator()
					.removeCalculator(type);
		} else {
			vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.removeCalculator(type);
		}

		Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());

		final Property[] props = propertySheetPanel
				.getProperties();
		Property toBeRemoved = null;

		for (Property p : props) {
			if (p.getDisplayName().equals(type.getName())) {
				toBeRemoved = p;

				break;
			}
		}

		propertySheetPanel.removeProperty(toBeRemoved);

		vizMapPropertySheetBuilder.removeProperty(toBeRemoved);

		/*
		 * Finally, move the visual property to "unused list"
		 */
		vizMapPropertySheetBuilder.getUnusedVisualPropType().add(type);

		VizMapperProperty prop = new VizMapperProperty();
		prop.setCategory(AbstractVizMapperPanel.CATEGORY_UNUSED);
		prop.setDisplayName(type.getName());
		prop.setHiddenObject(type);
		prop.setValue("Double-Click to create...");
		propertySheetPanel.addProperty(prop);

		if (vizMapPropertySheetBuilder.getPropertyMap().get(vmm.getVisualStyle().getName()) != null)
			vizMapPropertySheetBuilder.getPropertyMap().get(vmm.getVisualStyle().getName())
					.add(prop);

		propertySheetPanel.repaint();
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals("REMOVE_MAPPING") && e.getNewValue() != null) {
			removeMapping((VisualPropertyType) e.getNewValue());
		}
	}

}
