package org.cytoscape.view.vizmap.gui.internal.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

/**
 * Utilities for setting up VizMap GUI.
 * 
 * @author kono
 * 
 */
public class VizMapperUtil {

	private VisualMappingManager vmm;
	
	public VizMapperUtil(VisualMappingManager vmm) {
		this.vmm = vmm;
	}

	/**
	 * Get a new Visual Style name
	 * 
	 * @param s
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getStyleName(Component parentComponent, VisualStyle vs) {
		String suggestedName = null;
		List<String> vsNames = getVisualStyleNames();

		if (vs != null)
			suggestedName = vs.getTitle() + " new";

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(parentComponent,
					"Please enter new name for the visual style.",
					"Enter Visual Style Name", JOptionPane.QUESTION_MESSAGE,
					null, null, suggestedName);

			if (vsNames.contains(ret) == false)
				return ret;

			JOptionPane.showMessageDialog(parentComponent,
					"Visual style with name " + ret + " already exists!",
					"Duplicate visual style name", JOptionPane.WARNING_MESSAGE,
					null);
		}
	}

	public DiscreteMapping<?, ?> getSelectedProperty(VisualStyle style,
			PropertySheetPanel propertySheetPanel) {

		final int selectedRow = propertySheetPanel.getTable().getSelectedRow();

		if (selectedRow < 0)
			return null;

		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
				selectedRow, 0);
		final VizMapperProperty<?> prop = (VizMapperProperty<?>) item
				.getProperty();
		final Object hidden = prop.getHiddenObject();

		if (hidden instanceof VisualProperty) {
			final VisualProperty<?> type = (VisualProperty<?>) hidden;

			final VisualMappingFunction<?, ?> oMap = style
					.getVisualMappingFunction(type);

			if ((oMap instanceof DiscreteMapping) == false)
				return null;
			else
				return (DiscreteMapping<?, ?>) oMap;
		}

		return null;
	}

	public VisualProperty<?> getSelectedVisualProperty(
			PropertySheetPanel propertySheetPanel) {

		final int selectedRow = propertySheetPanel.getTable().getSelectedRow();

		if (selectedRow < 0)
			return null;

		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
				selectedRow, 0);

		if (item.getProperty() instanceof VizMapperProperty) {
			final VizMapperProperty<?> prop = (VizMapperProperty<?>) item
					.getProperty();
			final Object hidden = prop.getHiddenObject();

			if (hidden instanceof VisualProperty)
				return (VisualProperty<?>) hidden;
		}

		return null;
	}

	private List<String> getVisualStyleNames() {
		final List<String> vsNames = new ArrayList<String>();

		for (VisualStyle vs : vmm.getAllVisualStyles())
			vsNames.add(vs.getTitle());

		return vsNames;
	}

}
