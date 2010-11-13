package org.cytoscape.view.vizmap.gui.internal.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * Utilities for setting up VizMap GUI.
 * 
 * @author kono
 * 
 */
public class VizMapperUtil {
	
	private static final Map<Class<? extends CyTableEntry>, VisualProperty<Visualizable>> TARGET_TYPE_MAP;
	static {
		TARGET_TYPE_MAP = new HashMap<Class<? extends CyTableEntry>, VisualProperty<Visualizable>>();
		TARGET_TYPE_MAP.put(CyNode.class, TwoDVisualLexicon.NODE);
		TARGET_TYPE_MAP.put(CyEdge.class, TwoDVisualLexicon.EDGE);
		TARGET_TYPE_MAP.put(CyNetwork.class, TwoDVisualLexicon.NETWORK);
	}
	

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
	public String getStyleName(Component parentComponent, final VisualStyle vs) {
		String suggestedName = null;
		List<String> vsNames = getVisualStyleNames();

		if (vs != null)
			suggestedName = vs.getTitle() + " new";

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(parentComponent,
					"Please enter name for the new Visual Style.",
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
	
	public VisualProperty<Visualizable> getCategory(Class<? extends CyTableEntry> targetDataType) {
		return TARGET_TYPE_MAP.get(targetDataType);
	}
	
	
	

//	public DiscreteMapping<?, ?> getSelectedProperty(VisualStyle style,
//			PropertySheetPanel propertySheetPanel) {
//
//		final int selectedRow = propertySheetPanel.getTable().getSelectedRow();
//
//		if (selectedRow < 0)
//			return null;
//
//		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
//				selectedRow, 0);
//		final VizMapperProperty<?> prop = (VizMapperProperty<?>) item
//				.getProperty();
//		final Object hidden = prop.getHiddenObject();
//
//		if (hidden instanceof VisualProperty) {
//			final VisualProperty<?> type = (VisualProperty<?>) hidden;
//
//			final VisualMappingFunction<?, ?> oMap = style
//					.getVisualMappingFunction(type);
//
//			if ((oMap instanceof DiscreteMapping) == false)
//				return null;
//			else
//				return (DiscreteMapping<?, ?>) oMap;
//		}
//
//		return null;
//	}

//	public VisualProperty<?> getSelectedVisualProperty(
//			PropertySheetPanel propertySheetPanel) {
//
//		final int selectedRow = propertySheetPanel.getTable().getSelectedRow();
//
//		if (selectedRow < 0)
//			return null;
//
//		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
//				selectedRow, 0);
//
//		if (item.getProperty() instanceof VizMapperProperty) {
//			final VizMapperProperty<?> prop = (VizMapperProperty<?>) item
//					.getProperty();
//			final Object hidden = prop.getHiddenObject();
//
//			if (hidden instanceof VisualProperty)
//				return (VisualProperty<?>) hidden;
//		}
//
//		return null;
//	}

	private List<String> getVisualStyleNames() {
		final List<String> vsNames = new ArrayList<String>();

		for (VisualStyle vs : vmm.getAllVisualStyles())
			vsNames.add(vs.getTitle());

		return vsNames;
	}

}
