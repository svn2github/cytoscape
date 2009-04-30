package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.internal.AbstractVizMapperPanel;
import org.cytoscape.view.vizmap.gui.internal.VizMapperProperty;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

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
				final VisualProperty<?> type = ((VizMapperProperty<VisualProperty<?>>) curProp)
						.getHiddenObject();

				if (type == null)
					return;

				String[] message = {
						"The Mapping for " + type.getDisplayName()
								+ " will be removed.", "Proceed?" };

				int value = JOptionPane.showConfirmDialog(vizMapperMainPanel,
						message, "Remove Mapping", JOptionPane.YES_NO_OPTION);

				if (value == JOptionPane.YES_OPTION) {
					// If Continuous Mapper is displayed, kill it.
					if (editorWindowManager.isRegistered(type))
						editorWindowManager.removeEditorWindow(type);

					removeMapping(type);
				}
			}
		}
	}

	private <T> void removeMapping(final VisualProperty<T> type) {
		final VisualStyle vs = this.vizMapperMainPanel.getSelectedVisualStyle();
		
		// Remove mapping from the style.
		vs.removeVisualMappingFunction(type);

		// Update GUI
		final Property[] props = propertySheetPanel
				.getProperties();
		Property toBeRemoved = null;

		for (Property p : props) {
			if (p.getDisplayName().equals(type.getDisplayName())) {
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

		VizMapperProperty<VisualProperty<T>> prop = new VizMapperProperty<VisualProperty<T>>();
		prop.setCategory(AbstractVizMapperPanel.CATEGORY_UNUSED);
		prop.setDisplayName(type.getDisplayName());
		prop.setHiddenObject(type);
		prop.setValue("Double-Click to create...");
		propertySheetPanel.addProperty(prop);

		List<Property> target = vizMapPropertySheetBuilder.getPropertyMap().get(vs.getTitle());
		if (target != null)
			target.add(prop);

		propertySheetPanel.repaint();
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals("REMOVE_MAPPING") && e.getNewValue() != null) {
			removeMapping((VisualProperty) e.getNewValue());
		}
	}

}
