 package org.cytoscape.view.vizmap.gui.internal;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.VizMapGUI;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.theme.ColorManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.CyNetworkManager;

/**
 * Maintain property sheet table states.
 * 
 * @author kono
 * 
 */
public class VizMapPropertySheetBuilder {

	private VizMapGUI vizMapGUI;
	
	private PropertySheetPanel propertySheetPanel;

	private VisualMappingManager vmm;

	private PropertyRendererRegistry rendReg;
	private PropertyEditorRegistry editorReg;

	private DefaultTableCellRenderer emptyBoxRenderer;
	private DefaultTableCellRenderer filledBoxRenderer;

	private VizMapPropertyBuilder vizMapPropertyBuilder;

	private EditorManager editorFactory;

	private ColorManager colorMgr;

	private VizMapperMenuManager menuMgr;
	
	private CyNetworkManager cyNetworkManager;

	/*
	 * Keeps Properties in the browser.
	 */
	private Map<VisualStyle, List<Property>> propertyMap;

	private List<VisualProperty<?>> unusedVisualPropType;

	public VizMapPropertySheetBuilder(VizMapGUI vizMapGUI, VisualMappingManager vmm) {
		this.vmm = vmm;
		this.vizMapGUI = vizMapGUI;
		
		propertyMap = new HashMap<VisualStyle, List<Property>>();
	}

	public Map<VisualStyle, List<Property>> getPropertyMap() {
		return this.propertyMap;
	}

	public void setPropertyTable() {
		setPropertySheetAppearence();

		for (Property item : propertySheetPanel.getProperties())
			propertySheetPanel.removeProperty(item);

		editorReg.registerDefaults();

		/*
		 * Add properties to the property sheet.
		 */
		List<Property> propRecord = new ArrayList<Property>();

		setPropertyFromCalculator(
				GraphObject.NODE, propRecord);
		setPropertyFromCalculator(
				GraphObject.EDGE, propRecord);

		// Save it for later use.
		propertyMap.put(vizMapGUI.getSelectedVisualStyle(), propRecord);

		/*
		 * Finally, build unused list
		 */
		setUnused(propRecord);
	}

	private void setPropertySheetAppearence() {
		/*
		 * Set Tooltiptext for the table.
		 */
		propertySheetPanel.setTable(new VizMapPropertySheetTable());
		propertySheetPanel.getTable().getColumnModel().addColumnModelListener(
				new VizMapPropertySheetTableColumnModelListener(this));

		/*
		 * By default, show category.
		 */
		propertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);
		propertySheetPanel.getTable().setComponentPopupMenu(
				menuMgr.getContextMenu());
		propertySheetPanel.getTable().addMouseListener(
				new VizMapPropertySheetMouseAdapter(this, propertySheetPanel,
						null));

		PropertySheetTable table = propertySheetPanel.getTable();
		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCategoryBackground(new Color(10, 10, 50, 20));
		table.setCategoryForeground(Color.black);
		table.setSelectionBackground(Color.white);
		table.setSelectionForeground(Color.blue);

		/*
		 * Set editors
		 */
		emptyBoxRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
		emptyBoxRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		emptyBoxRenderer.setBackground(new Color(0, 200, 255, 20));
		emptyBoxRenderer.setForeground(Color.red);
		emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

		filledBoxRenderer.setBackground(Color.white);
		filledBoxRenderer.setForeground(Color.blue);

		VisualPropertyIcon newIcon;

		List<Icon> iconList = new ArrayList<Icon>();
		final List<NodeShape> nodeShapes = new ArrayList<NodeShape>();

		for (Object key : nodeShapeIcons.keySet()) {
			NodeShape shape = (NodeShape) key;

			if (shape.isSupported()) {
				iconList.add(nodeShapeIcons.get(key));
				nodeShapes.add(shape);
			}
		}

		Icon[] iconArray = new Icon[iconList.size()];
		String[] shapeNames = new String[iconList.size()];

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = ((NodeIcon) iconList.get(i)).clone();
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(16);
			iconArray[i] = newIcon;
			shapeNames[i] = nodeShapes.get(i).getShapeName();
		}

		// shapeCellEditor.setAvailableValues(nodeShapes.toArray());
		// shapeCellEditor.setAvailableIcons(iconArray);
		iconList.clear();
		iconList.addAll(arrowShapeIcons.values());
		iconArray = new Icon[iconList.size()];

		String[] arrowNames = new String[iconList.size()];
		Set arrowShapes = arrowShapeIcons.keySet();

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = ((ArrowIcon) iconList.get(i));
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(40);
			newIcon.setBottomPadding(-9);
			iconArray[i] = newIcon;
			arrowNames[i] = newIcon.getName();
		}

		// arrowCellEditor.setAvailableValues(arrowShapes.toArray());
		// arrowCellEditor.setAvailableIcons(iconArray);
		iconList = new ArrayList();
		iconList.addAll(lineTypeIcons.values());
		iconArray = new Icon[iconList.size()];
		shapeNames = new String[iconList.size()];

		Set lineTypes = lineTypeIcons.keySet();

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = (VisualPropertyIcon) (iconList.get(i));
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(16);
			iconArray[i] = newIcon;
			shapeNames[i] = newIcon.getName();
		}

		// lineCellEditor.setAvailableValues(lineTypes.toArray());
		// lineCellEditor.setAvailableIcons(iconArray);
	}

	private void setPropertyFromCalculator(
			String rootCategory, List<Property> propRecord) {
		
		VisualProperty<?> type = null;

		for (Calculator calc : calcList) {
			final VizMapperProperty calculatorTypeProp = new VizMapperProperty();
			vizMapPropertyBuilder.buildProperty(calc, calculatorTypeProp,
					rootCategory, propertySheetPanel);

			PropertyEditor editor = editorReg.getEditor(calculatorTypeProp);

			if ((editor == null)
					&& (calculatorTypeProp.getCategory().equals(
							"Unused Properties") == false)) {
				type = (VisualProperty) calculatorTypeProp
						.getHiddenObject();

				if (type.getObjectType().equals(VisualProperty.NODE)) {
					editorReg.registerEditor(calculatorTypeProp, editorFactory
							.getDefaultComboBoxEditor("nodeAttrEditor"));
				} else {
					editorReg.registerEditor(calculatorTypeProp, editorFactory
							.getDefaultComboBoxEditor("edgeAttrEditor"));
				}
			}

			propRecord.add(calculatorTypeProp);
		}
	}

	private void setUnused(List<Property> propList) {
		buildList();
		Collections.sort(getUnusedVisualPropType());

		for (VisualProperty type : getUnusedVisualPropType()) {
			VizMapperProperty prop = new VizMapperProperty();
			prop.setCategory(AbstractVizMapperPanel.CATEGORY_UNUSED);
			prop.setDisplayName(type.getName());
			prop.setHiddenObject(type);
			prop.setValue("Double-Click to create...");
			// prop.setEditable(false);
			propertySheetPanel.addProperty(prop);
			propList.add(prop);
		}
	}

	private void buildList() {
		unusedVisualPropType = new ArrayList<VisualProperty>();

		final VisualStyle vs = vmm.getVisualStyle();
		final NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
		final EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();

		VisualMappingFunction mapping = null;

		for (VisualProperty type : VisualProperty.values()) {
			Calculator calc = nac.getCalculator(type);

			if (calc == null) {
				calc = eac.getCalculator(type);

				if (calc != null)
					mapping = calc.getMapping(0);
			} else
				mapping = calc.getMapping(0);

			if (mapping == null)
				getUnusedVisualPropType().add(type);

			mapping = null;
		}
	}

	public void updateTableView() {
		final PropertySheetTable table = propertySheetPanel.getTable();
		Property shownProp = null;
		final DefaultTableCellRenderer empRenderer = new DefaultTableCellRenderer();

		// Number of rows shown now.
		int rowCount = table.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			shownProp = ((Item) table.getValueAt(i, 0)).getProperty();

			if ((shownProp != null)
					&& (shownProp.getParentProperty() != null)
					&& shownProp.getParentProperty().getDisplayName().equals(
							NODE_LABEL_POSITION.getName())) {
				// This is label position cell. Need laeger cell.
				table.setRowHeight(i, 50);
			} else if ((shownProp != null)
					&& shownProp.getDisplayName().equals(
							AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW)) {
				// This is a Continuous Icon cell.
				final Property parent = shownProp.getParentProperty();
				final Object type = ((VizMapperProperty) parent)
						.getHiddenObject();

				if (type instanceof VisualProperty) {
					MappingCalculator mapping;

					if (((VisualProperty) type).getObjectType().equals(VisualProperty.NODE))
						mapping = vmm.getVisualStyle()
								.getNodeAppearanceCalculator().getCalculator(
										((VisualProperty) type))
								.getMapping(0);
					else
						mapping = vmm.getVisualStyle()
								.getEdgeAppearanceCalculator().getCalculator(
										((VisualProperty) type))
								.getMapping(0);

					if (mapping instanceof ContinuousMapping) {
						table.setRowHeight(i, 80);

						int wi = table.getCellRect(0, 1, true).width;
						final TableCellRenderer cRenderer = editorFactory
								.getContinuousCellRenderer(
										(VisualProperty) type, wi, 70);
						rendReg.registerRenderer(shownProp, cRenderer);
					}
				}
			} else if ((shownProp != null)
					&& (shownProp.getCategory() != null)
					&& shownProp.getCategory().equals(
							AbstractVizMapperPanel.CATEGORY_UNUSED)) {
				empRenderer.setForeground(colorMgr.getColor("UNUSED_COLOR"));
				rendReg.registerRenderer(shownProp, empRenderer);
			}
		}
		propertySheetPanel.repaint();
	}

	public void expandLastSelectedItem(String name) {
		final PropertySheetTable table = propertySheetPanel.getTable();
		Item item = null;
		Property curProp;

		for (int i = 0; i < table.getRowCount(); i++) {
			item = (Item) table.getValueAt(i, 0);

			curProp = item.getProperty();

			if ((curProp != null) && (curProp.getDisplayName().equals(name))) {
				table.setRowSelectionInterval(i, i);

				if (item.isVisible() == false) {
					item.toggle();
				}

				return;
			}
		}
	}

	/*
	 * Remove an entry in the browser.
	 */
	public void removeProperty(final Property prop) {
		List<Property> targets = new ArrayList<Property>();

		if (propertyMap.get(vmm.getVisualStyle().getName()) == null) {
			return;
		}

		for (Property p : propertyMap.get(vmm.getVisualStyle().getName())) {
			if (p.getDisplayName().equals(prop.getDisplayName())) {
				targets.add(p);
			}
		}

		for (Property p : targets) {
			System.out.println("Removed: " + p.getDisplayName());
			propertyMap.get(vmm.getVisualStyle().getName()).remove(p);
		}
	}

	//TODO: this should be gone
	public void setAttrComboBox() {
		// Attribute Names
		final List<String> names = new ArrayList<String>();

	
//		CyDataTable attr = /* TODO */getTargetNetwork().getNodeCyDataTables()
//				.get(CyNetwork.DEFAULT_ATTRS);
//
//		// TODO remove the next line too!
//		if (attr == null)
//			return;
//
//		Map<String, Class<?>> cols = attr.getColumnTypeMap();
//		names.addAll(cols.keySet());
//
//		Collections.sort(names);
//
//		// nodeAttrEditor.setAvailableValues(names.toArray());
//		spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "nodeAttrEditor", names
//				.toArray());
//
//		names.clear();
//
//		for (String name : cols.keySet()) {
//			Class<?> dataClass = cols.get(name);
//
//			if ((dataClass == Integer.class) || (dataClass == Double.class))
//				names.add(name);
//		}
//
//		Collections.sort(names);
//		// nodeNumericalAttrEditor.setAvailableValues(names.toArray());
//		spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
//				"nodeNumericalAttrEditor", names.toArray());
//
//		names.clear();
//
//		attr = getTargetNetwork().getEdgeCyDataTables().get(
//				CyNetwork.DEFAULT_ATTRS);
//		cols = attr.getColumnTypeMap();
//		names.addAll(cols.keySet());
//		Collections.sort(names);
//
//		// edgeAttrEditor.setAvailableValues(names.toArray());
//		spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "edgeAttrEditor", names
//				.toArray());
//		names.clear();
//
//		for (String name : cols.keySet()) {
//			Class<?> dataClass = cols.get(name);
//
//			if ((dataClass == Integer.class) || (dataClass == Double.class))
//				names.add(name);
//		}
//
//		Collections.sort(names);
//		// edgeNumericalAttrEditor.setAvailableValues(names.toArray());
//		spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
//				"edgeNumericalAttrEditor", names.toArray());
//		propertySheetPanel.repaint();
	}

	public VizMapPropertyBuilder getPropertyBuilder() {
		return this.vizMapPropertyBuilder;
	}

	public List<VisualProperty<?>> getUnusedVisualPropType() {
		return unusedVisualPropType;
	}
}
