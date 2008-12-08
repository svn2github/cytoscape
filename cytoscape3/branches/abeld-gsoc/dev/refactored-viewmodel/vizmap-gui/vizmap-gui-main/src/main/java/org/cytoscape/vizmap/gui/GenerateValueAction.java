package org.cytoscape.vizmap.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.util.DiscreteValueMapGenerator;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

public class GenerateValueAction extends AbstractVizMapperAction {

	private final static long serialVersionUID = 1213748836986412L;
	
	
	private DiscreteValueMapGenerator<?> generator;
	
	private DiscreteMapping dm;

	/**
	 * User wants to Seed the Discrete Mapper with Random Color Values.
	 */
	public void actionPerformed(ActionEvent e) {
		
		// Check Selected property
		final int selectedRow = vizMapperMainPanel.getPropertySheetPanel().getTable()
				.getSelectedRow();

		if (selectedRow < 0)
			return;

		final Item item = (Item) vizMapperMainPanel.getPropertySheetPanel().getTable()
				.getValueAt(selectedRow, 0);
		final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
		final Object hidden = prop.getHiddenObject();

		if (hidden instanceof VisualPropertyType) {
			
			final GraphView targetNetworkView = Cytoscape.getCurrentNetworkView();
			
			final VisualPropertyType type = (VisualPropertyType) hidden;

			Map valueMap = new HashMap();
			final long seed = System.currentTimeMillis();
			final Random rand = new Random(seed);

			final ObjectMapping oMap;

			final CyDataTable attr;
			final int nOre;

			if (type.isNodeProp()) {
				attr = targetNetworkView.getNetwork().getNodeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				oMap = visualMappingManager.getVisualStyle().getNodeAppearanceCalculator()
						.getCalculator(type).getMapping(0);
				nOre = ObjectMapping.NODE_MAPPING;
			} else {
				attr = targetNetworkView.getNetwork().getEdgeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				oMap = visualMappingManager.getVisualStyle().getEdgeAppearanceCalculator()
						.getCalculator(type).getMapping(0);
				nOre = ObjectMapping.EDGE_MAPPING;
			}

			// This function is for discrete mapping only.
			if ((oMap instanceof DiscreteMapping) == false)
				return;

			dm = (DiscreteMapping) oMap;

			final Set<Object> attrSet = new TreeSet<Object>(attr
					.getColumnValues(oMap.getControllingAttributeName(), attr
							.getColumnTypeMap().get(
									oMap.getControllingAttributeName())));

			// Show error if there is no attribute value.
			if (attrSet.size() == 0) {
				JOptionPane.showMessageDialog(vizMapperMainPanel,
						"No attribute value is available.",
						"Cannot generate values", JOptionPane.ERROR_MESSAGE);
			}

//			/*
//			 * Create random colors
//			 */
//			final float increment = 1f / ((Number) attrSet.size()).floatValue();
//
//			float hue = 0;
//			float sat = 0;
//			float br = 0;
//
//			if (type.getDataType() == Color.class) {
//				int i = 0;
//
//				if (functionType == RAINBOW1) {
//					for (Object key : attrSet) {
//						hue = hue + increment;
//						valueMap.put(key,
//								new Color(Color.HSBtoRGB(hue, 1f, 1f)));
//					}
//				} else if (functionType == RAINBOW2) {
//					for (Object key : attrSet) {
//						hue = hue + increment;
//						sat = (Math.abs(((Number) Math.cos((8 * i)
//								/ (2 * Math.PI))).floatValue()) * 0.7f) + 0.3f;
//						br = (Math.abs(((Number) Math.sin(((i) / (2 * Math.PI))
//								+ (Math.PI / 2))).floatValue()) * 0.7f) + 0.3f;
//						valueMap.put(key, new Color(Color
//								.HSBtoRGB(hue, sat, br)));
//						i++;
//					}
//				} else {
//					for (Object key : attrSet)
//						valueMap.put(key, new Color(
//								((Number) (rand.nextFloat() * MAX_COLOR))
//										.intValue()));
//				}
//			} else if ((type.getDataType() == Number.class)
//					&& (functionType == RANDOM)) {
//				final String range = JOptionPane.showInputDialog(
//						visualPropertySheetPanel,
//						"Please enter the value range (example: 30-100)",
//						"Assign Random Numbers", JOptionPane.PLAIN_MESSAGE);
//
//				String[] rangeVals = range.split("-");
//
//				if (rangeVals.length != 2)
//					return;
//
//				Float min = Float.valueOf(rangeVals[0]);
//				Float max = Float.valueOf(rangeVals[1]);
//				Float valueRange = max - min;
//
//				for (Object key : attrSet)
//					valueMap.put(key, (rand.nextFloat() * valueRange) + min);
//			}

			
			valueMap = generator.generateMap(attrSet);
			
			dm.putAll(valueMap);
			visualMappingManager.setNetworkView(targetNetworkView);
			Cytoscape.redrawGraph(targetNetworkView);

			vizMapperMainPanel.getPropertySheetPanel().removeProperty(prop);

//			final VizMapperProperty newRootProp = new VizMapperProperty();
//
//			if (type.isNodeProp())
//				buildProperty(visualMappingManager.getVisualStyle()
//						.getNodeAppearanceCalculator().getCalculator(type),
//						newRootProp, NODE_VISUAL_MAPPING);
//			else
//				buildProperty(vmm.getVisualStyle()
//						.getEdgeAppearanceCalculator().getCalculator(type),
//						newRootProp, EDGE_VISUAL_MAPPING);
//
//			removeProperty(prop);
//			System.out.println("asdf pre vs name");
//			System.out.println("asdf vs name" + vmm.getVisualStyle().getName());
//			propertyMap.get(vmm.getVisualStyle().getName()).add(newRootProp);
//
//			expandLastSelectedItem(type.getName());
//		} else {
//			System.out.println("Invalid.");
//		}
//
//		return;
		}
	}
}
