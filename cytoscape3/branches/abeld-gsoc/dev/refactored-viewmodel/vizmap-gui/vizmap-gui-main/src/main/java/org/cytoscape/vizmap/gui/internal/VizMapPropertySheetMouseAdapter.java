package org.cytoscape.vizmap.gui.internal;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.annotation.Resource;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.mappings.ContinuousMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

public final class VizMapPropertySheetMouseAdapter extends MouseAdapter {

	private VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	private PropertySheetPanel propertySheetPanel;
	private Map<VisualProperty, JDialog> editorWindowManager;

	@Resource
	private VisualMappingManager vmm;
	@Resource
	private EditorFactory editorFactory;
	
	@Resource
	private PropertyEditorRegistry editorReg;

	public VizMapPropertySheetMouseAdapter(
			VizMapPropertySheetBuilder sheetBuilder,
			PropertySheetPanel propertySheetPanel,
			Map<VisualProperty, JDialog> editorWindowManager) {
		this.vizMapPropertySheetBuilder = sheetBuilder;
		this.propertySheetPanel = propertySheetPanel;
		this.editorWindowManager = editorWindowManager;
	}

	public void mouseClicked(MouseEvent e) {

		int selected = propertySheetPanel.getTable().getSelectedRow();
		/*
		 * Adjust height if it's an legend icon.
		 */
		vizMapPropertySheetBuilder.updateTableView();

		if (SwingUtilities.isLeftMouseButton(e) && (0 <= selected)) {
			final Item item = (Item) propertySheetPanel.getTable().getValueAt(
					selected, 0);
			final Property curProp = item.getProperty();

			if (curProp == null)
				return;

			/*
			 * Create new mapping if double-click on unused val.
			 */
			String category = curProp.getCategory();

			if ((e.getClickCount() == 2) && (category != null)
					&& category.equalsIgnoreCase("Unused Properties")) {
				((VizMapperProperty) curProp).setEditable(true);

				VisualProperty type = (VisualProperty) ((VizMapperProperty) curProp)
						.getHiddenObject();
				propertySheetPanel.removeProperty(curProp);

				final VizMapperProperty newProp = new VizMapperProperty();
				final VizMapperProperty mapProp = new VizMapperProperty();

				newProp.setDisplayName(type.getName());
				newProp.setHiddenObject(type);
				newProp.setValue("Please select a value!");

				if (type.isNodeProp()) {
					newProp
							.setCategory(AbstractVizMapperPanel.NODE_VISUAL_MAPPING);
					editorReg
							.registerEditor(newProp, editorFactory
									.getDefaultComboBoxEditor("nodeAttrEditor"));
				} else {
					newProp
							.setCategory(AbstractVizMapperPanel.EDGE_VISUAL_MAPPING);
					editorReg
							.registerEditor(newProp, editorFactory
									.getDefaultComboBoxEditor("edgeAttrEditor"));
				}

				mapProp.setDisplayName("Mapping Type");
				mapProp.setValue("Please select a mapping type!");
				editorReg.registerEditor(mapProp, editorFactory
						.getDefaultComboBoxEditor("mappingTypeEditor"));

				newProp.addSubProperty(mapProp);
				mapProp.setParentProperty(newProp);
				propertySheetPanel.addProperty(0, newProp);

				vizMapPropertySheetBuilder.expandLastSelectedItem(type.getName());

				propertySheetPanel.getTable().scrollRectToVisible(
						new Rectangle(0, 0, 10, 10));
				propertySheetPanel.repaint();

				return;
			} else if ((e.getClickCount() == 1) && (category == null)) {
				/*
				 * Single left-click
				 */
				VisualProperty type = null;

				if ((curProp.getParentProperty() == null)
						&& ((VizMapperProperty) curProp).getHiddenObject() instanceof VisualProperty)
					type = (VisualProperty) ((VizMapperProperty) curProp)
							.getHiddenObject();
				else if (curProp.getParentProperty() != null)
					type = (VisualProperty) ((VizMapperProperty) curProp
							.getParentProperty()).getHiddenObject();
				else

					return;

				final ObjectMapping selectedMapping;
				Calculator calc = null;

				if (type.isNodeProp()) {
					calc = vmm.getVisualStyle().getNodeAppearanceCalculator()
							.getCalculator(type);
				} else {
					calc = vmm.getVisualStyle().getEdgeAppearanceCalculator()
							.getCalculator(type);
				}

				if (calc == null) {
					return;
				}

				selectedMapping = calc.getMapping(0);

				if (selectedMapping instanceof ContinuousMapping) {
					/*
					 * Need to check other windows.
					 */
					if (editorWindowManager.containsKey(type)) {
						// This means editor is already on display.
						editorWindowManager.get(type).requestFocus();

						return;
					} else {
						try {
							((JDialog) editorFactory.showContinuousEditor(
									propertySheetPanel, type))
									.addPropertyChangeListener(propertySheetPanel);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

}
