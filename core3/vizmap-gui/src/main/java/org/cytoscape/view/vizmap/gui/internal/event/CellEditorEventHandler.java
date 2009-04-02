package org.cytoscape.view.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.view.vizmap.gui.internal.AbstractVizMapperPanel;
import org.cytoscape.view.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.calculators.BasicCalculator;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.mappings.ContinuousMapping;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.MappingCalculator;
import org.cytoscape.vizmap.mappings.PassthroughMappingCalculator;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

public class CellEditorEventHandler extends AbstractVizMapEventHandler {

	// Keeps current discrete mappings. NOT PERMANENT
	private final Map<String, Map<Object, Object>> discMapBuffer;


	public CellEditorEventHandler() {
		discMapBuffer = new HashMap<String, Map<Object, Object>>();
	}

	@Override
	public void processEvent(PropertyChangeEvent e) {

		if (e.getPropertyName().equalsIgnoreCase("value") == false)
			return;

		if (e.getNewValue().equals(e.getOldValue()))
			return;

		final PropertySheetTable table = propertySheetPanel.getTable();
		final int selected = table.getSelectedRow();

		/*
		 * Do nothing if not selected.
		 */
		if (selected < 0)
			return;

		Item selectedItem = (Item) propertySheetPanel.getTable().getValueAt(
				selected, 0);
		VizMapperProperty prop = (VizMapperProperty) selectedItem.getProperty();

		VisualProperty type = null;
		String ctrAttrName = null;

		VizMapperProperty typeRootProp = null;

		if ((prop.getParentProperty() == null)
				&& e.getNewValue() instanceof String) {
			/*
			 * This is a controlling attr name change signal.
			 */
			typeRootProp = (VizMapperProperty) prop;
			type = (VisualProperty) ((VizMapperProperty) prop)
					.getHiddenObject();
			ctrAttrName = (String) e.getNewValue();
		} else if ((prop.getParentProperty() == null)
				&& (e.getNewValue() == null)) {
			/*
			 * Empty cell selected. no need to change anything.
			 */
			return;
		} else {
			typeRootProp = (VizMapperProperty) prop.getParentProperty();

			if (prop.getParentProperty() == null)
				return;

			type = (VisualProperty) ((VizMapperProperty) prop
					.getParentProperty()).getHiddenObject();
		}

		/*
		 * Mapping type changed
		 */
		if (prop.getHiddenObject() instanceof MappingCalculator
				|| prop.getDisplayName().equals("Mapping Type")) {
			System.out.println("Mapping type changed: "
					+ prop.getHiddenObject());

			if (e.getNewValue() == null)
				return;

			/*
			 * If invalid data type, ignore.
			 */
			final Object parentValue = prop.getParentProperty().getValue();

			if (parentValue != null) {
				ctrAttrName = parentValue.toString();

				CyDataTable attr;

				if (type.getObjectType().equals(VisualProperty.NODE)) {
					attr = cyNetworkManager.getCurrentNetwork()
							.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
				} else {
					attr = cyNetworkManager.getCurrentNetwork()
							.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
				}

				final Class<?> dataClass = attr.getColumnTypeMap().get(
						ctrAttrName);

				if (e.getNewValue().equals("Continuous Mapper")
						&& ((dataClass != Integer.class) && (dataClass != Double.class))) {
					JOptionPane.showMessageDialog(vizMapperMainPanel,
							"Continuous Mapper can be used with Numbers only.",
							"Incompatible Mapping Type!",
							JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			} else {
				return;
			}

			if (e.getNewValue().toString().endsWith("Mapper") == false)
				return;

			switchMapping(prop, e.getNewValue().toString(), prop
					.getParentProperty().getValue());

			/*
			 * restore expanded props.
			 */
			vizMapPropertySheetBuilder.expandLastSelectedItem(type.getName());
			vizMapPropertySheetBuilder.updateTableView();

			return;
		}

		/*
		 * Extract calculator
		 */
		MappingCalculator mapping;
		final Calculator curCalc;

		if (type.getObjectType().equals(VisualProperty.NODE)) {
			curCalc = vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getCalculator(type);
		} else {
			curCalc = vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.getCalculator(type);
		}

		if (curCalc == null) {
			return;
		}

		mapping = curCalc.getMapping(0);

		/*
		 * Controlling Attribute has been changed.
		 */
		if (ctrAttrName != null) {
			/*
			 * Ignore if not compatible.
			 */
			final CyDataTable attrForTest;

			if (type.getObjectType().equals(VisualProperty.NODE)) {
				attrForTest = cyNetworkManager.getCurrentNetwork()
						.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
			} else {
				attrForTest = cyNetworkManager.getCurrentNetwork()
						.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
			}

			final Class<?> dataType = attrForTest.getColumnTypeMap().get(
					ctrAttrName);

			// This part is for Continuous Mapping.
			if (mapping instanceof ContinuousMapping) {
				if ((dataType == Double.class) || (dataType == Integer.class)) {
					// Do nothing
				} else {
					JOptionPane
							.showMessageDialog(
									vizMapperMainPanel,
									"Continuous Mapper can be used with Numbers only.\nPlease select numerical attributes.",
									"Incompatible Mapping Type!",
									JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}

			// If same, do nothing.
			if (ctrAttrName.equals(mapping.getControllingAttributeName()))
				return;

			// Buffer current discrete mapping
			if (mapping instanceof DiscreteMapping) {
				final String curMappingName = curCalc.toString() + "-"
						+ mapping.getControllingAttributeName();
				final String newMappingName = curCalc.toString() + "-"
						+ ctrAttrName;
				final Map saved = discMapBuffer.get(newMappingName);

				if (saved == null) {
					discMapBuffer.put(curMappingName,
							((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName);
				} else if (saved != null) {
					// Mapping exists
					discMapBuffer.put(curMappingName,
							((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName);
					((DiscreteMapping) mapping).putAll(saved);
				}
			} else {
				mapping.setControllingAttributeName(ctrAttrName);
			}

			propertySheetPanel.removeProperty(typeRootProp);

			final VizMapperProperty newRootProp = new VizMapperProperty();

			if (type.getObjectType().equals(VisualProperty.NODE))
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
						vmm.getVisualStyle().getNodeAppearanceCalculator()
								.getCalculator(type), newRootProp,
						AbstractVizMapperPanel.NODE_VISUAL_MAPPING,
						propertySheetPanel);
			else
				vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
						vmm.getVisualStyle().getEdgeAppearanceCalculator()
								.getCalculator(type), newRootProp,
						AbstractVizMapperPanel.EDGE_VISUAL_MAPPING,
						propertySheetPanel);

			vizMapPropertySheetBuilder.removeProperty(typeRootProp);

			if (vizMapPropertySheetBuilder.getPropertyMap().get(
					vmm.getVisualStyle().getName()) != null)
				vizMapPropertySheetBuilder.getPropertyMap().get(
						vmm.getVisualStyle().getName()).add(newRootProp);

			typeRootProp = null;

			vizMapPropertySheetBuilder.expandLastSelectedItem(type.getName());
			vizMapPropertySheetBuilder.updateTableView();

			// Finally, update graph view and focus.
			//vmm.setNetworkView(cyNetworkManager.getCurrentNetworkView());
			//Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());

			return;
		}

		// Return if not a Discrete Mapping.
		if (mapping instanceof ContinuousMapping
				|| mapping instanceof PassthroughMappingCalculator)
			return;

		Object key = null;

		if ((type.getType() == Number.class)
				|| (type.getType() == String.class)) {
			key = e.getOldValue();

			// TODO WTF?
			// if (type.getDataType() == Number.class) {
			// numberCellEditor = new CyDoublePropertyEditor(this);
			// numberCellEditor.addPropertyChangeListener(this);
			// editorReg.registerEditor(prop, numberCellEditor);
			// }
		} else {
			key = ((Item) propertySheetPanel.getTable().getValueAt(selected, 0))
					.getProperty().getDisplayName();
		}

		/*
		 * Need to convert this string to proper data types.
		 */
		final CyDataTable attr;
		ctrAttrName = mapping.getControllingAttributeName();

		if (type.getObjectType().equals(VisualProperty.NODE)) {
			attr = cyNetworkManager.getCurrentNetwork().getNodeCyDataTables()
					.get(CyNetwork.DEFAULT_ATTRS);
		} else {
			attr = cyNetworkManager.getCurrentNetwork().getEdgeCyDataTables()
					.get(CyNetwork.DEFAULT_ATTRS);
		}

		// Byte attrType = attr.getType(ctrAttrName);
		Class<?> attrType = attr.getColumnTypeMap().get(ctrAttrName);

		if (attrType == Boolean.class)
			key = Boolean.valueOf((String) key);
		else if (attrType == Integer.class)
			key = Integer.valueOf((String) key);
		else if (attrType == Double.class)
			key = Double.valueOf((String) key);

		Object newValue = e.getNewValue();

		if (type.getType() == Number.class) {
			if ((((Number) newValue).doubleValue() == 0)
					|| (newValue instanceof Number
							&& type.toString().endsWith("OPACITY") && (((Number) newValue)
							.doubleValue() > 255))) {
				int shownPropCount = table.getRowCount();
				Property p = null;
				Object val = null;

				for (int i = 0; i < shownPropCount; i++) {
					p = ((Item) table.getValueAt(i, 0)).getProperty();

					if (p != null) {
						val = p.getDisplayName();

						if ((val != null) && val.equals(key.toString())) {
							p.setValue(((DiscreteMapping) mapping)
									.getMapValue(key));

							return;
						}
					}
				}

				return;
			}
		}

		((DiscreteMapping) mapping).putMapValue(key, newValue);

		/*
		 * Update table and current network view.
		 */
		vizMapPropertySheetBuilder.updateTableView();

		propertySheetPanel.repaint();
		//vmm.setNetworkView(cyNetworkManager.getCurrentNetworkView());
		//Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());

	}

	/**
	 * Switching between mapppings. Each calcs has 3 mappings. The first one
	 * (getMapping(0)) is the current mapping used by calculator.
	 * 
	 */
	private void switchMapping(VizMapperProperty prop, String newMapName,
			Object attrName) {
		if (attrName == null) {
			return;
		}

		final VisualProperty type = (VisualProperty) ((VizMapperProperty) prop
				.getParentProperty()).getHiddenObject();
		final String newCalcName = vmm.getVisualStyle().getName() + "-"
				+ type.getName() + "-" + newMapName;

		// Extract target calculator
		Calculator newCalc = vmm.getCalculatorCatalog().getCalculator(type,
				newCalcName);

		Calculator oldCalc = null;

		if (type.getObjectType().equals(VisualProperty.NODE))
			oldCalc = vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getCalculator(type);
		else
			oldCalc = vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.getCalculator(type);

		/*
		 * If not exist, create new one.
		 */
		if (newCalc == null) {
			newCalc = getNewCalculator(type, newMapName, newCalcName);
			newCalc.getMapping(0).setControllingAttributeName((String) attrName);
			vmm.getCalculatorCatalog().addCalculator(newCalc);
		}

		newCalc.getMapping(0).setControllingAttributeName((String) attrName);

		if (type.getObjectType().equals(VisualProperty.NODE)) {
			vmm.getVisualStyle().getNodeAppearanceCalculator().setCalculator(
					newCalc);
		} else
			vmm.getVisualStyle().getEdgeAppearanceCalculator().setCalculator(
					newCalc);

		/*
		 * If old calc is not standard name, rename it.
		 */
		if (oldCalc != null) {
			final String oldMappingTypeName;

			if (oldCalc.getMapping(0) instanceof DiscreteMapping)
				oldMappingTypeName = "Discrete Mapper";
			else if (oldCalc.getMapping(0) instanceof ContinuousMapping)
				oldMappingTypeName = "Continuous Mapper";
			else if (oldCalc.getMapping(0) instanceof PassthroughMappingCalculator)
				oldMappingTypeName = "Passthrough Mapper";
			else
				oldMappingTypeName = null;

			final String oldCalcName = type.getName() + "-"
					+ oldMappingTypeName;

			if (vmm.getCalculatorCatalog().getCalculator(type, oldCalcName) == null) {
				final Calculator newC = getNewCalculator(type,
						oldMappingTypeName, oldCalcName);
				newC.getMapping(0).setControllingAttributeName((String) attrName);
				vmm.getCalculatorCatalog().addCalculator(newC);
			}
		}

		Property parent = prop.getParentProperty();
		propertySheetPanel.removeProperty(parent);

		final VizMapperProperty newRootProp = new VizMapperProperty();

		if (type.getObjectType().equals(VisualProperty.NODE))
			vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
					vmm.getVisualStyle().getNodeAppearanceCalculator()
							.getCalculator(type), newRootProp,
					AbstractVizMapperPanel.NODE_VISUAL_MAPPING,
					propertySheetPanel);
		else
			vizMapPropertySheetBuilder.getPropertyBuilder().buildProperty(
					vmm.getVisualStyle().getEdgeAppearanceCalculator()
							.getCalculator(type), newRootProp,
					AbstractVizMapperPanel.EDGE_VISUAL_MAPPING,
					propertySheetPanel);

		vizMapPropertySheetBuilder.expandLastSelectedItem(type.getName());

		vizMapPropertySheetBuilder.removeProperty(parent);

		if (vizMapPropertySheetBuilder.getPropertyMap().get(
				vmm.getVisualStyle().getName()) != null) {
			vizMapPropertySheetBuilder.getPropertyMap().get(
					vmm.getVisualStyle().getName()).add(newRootProp);
		}

		// vmm.getNetworkView().redrawGraph(false, true);
		//Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());
		parent = null;
	}

	private Calculator getNewCalculator(final VisualProperty type,
			final String newMappingName, final String newCalcName) {
		System.out.println("Mapper = " + newMappingName);

		final CalculatorCatalog catalog = vmm.getCalculatorCatalog();

		Class mapperClass = catalog.getMapping(newMappingName);

		if (mapperClass == null) {
			return null;
		}

		// create the selected mapper
		Class[] conTypes = { Object.class, byte.class };
		Constructor mapperCon;

		try {
			mapperCon = mapperClass.getConstructor(conTypes);
		} catch (NoSuchMethodException exc) {
			// Should not happen...
			System.err.println("Invalid mapper " + mapperClass.getName());

			return null;
		}

		final Object defaultObj = type.getDefault(vmm.getVisualStyle());

		System.out.println("defobj = " + defaultObj.getClass() + ", Type = "
				+ type.getName());

		final Object[] invokeArgs = { defaultObj};
		MappingCalculator mapper = null;

		try {
			mapper = (MappingCalculator) mapperCon.newInstance(invokeArgs);
		} catch (Exception exc) {
			System.err.println("Error creating mapping");

			return null;
		}

		return new BasicCalculator(newCalcName, mapper, type);
	}

}
