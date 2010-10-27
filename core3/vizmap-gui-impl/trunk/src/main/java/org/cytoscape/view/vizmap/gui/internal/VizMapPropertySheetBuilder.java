package org.cytoscape.view.vizmap.gui.internal;

import static org.cytoscape.model.CyTableEntry.EDGE;
import static org.cytoscape.model.CyTableEntry.NETWORK;
import static org.cytoscape.model.CyTableEntry.NODE;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.DefaultViewPanel;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.internal.theme.ColorManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

/**
 * Maintain property sheet table states.
 * 
 * @author kono
 * 
 */
public class VizMapPropertySheetBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(VizMapPropertySheetBuilder.class);

	private static final VisualProperty<?>[] CATEGORY = {
			TwoDVisualLexicon.NODE, TwoDVisualLexicon.EDGE,
			TwoDVisualLexicon.NETWORK };

	private PropertySheetPanel propertySheetPanel;

	private DefaultViewPanel defViewPanel;

	private DefaultTableCellRenderer emptyBoxRenderer;
	private DefaultTableCellRenderer filledBoxRenderer;

	private VizMapPropertyBuilder vizMapPropertyBuilder;

	private EditorManager editorManager;

	private ColorManager colorMgr;

	private VizMapperMenuManager menuMgr;

	private CyNetworkManager cyNetworkManager;

	/*
	 * Keeps Properties in the browser.
	 */
	private Map<VisualStyle, List<Property>> propertyMap;

	private List<VisualProperty<?>> unusedVisualPropType;

	public VizMapPropertySheetBuilder(CyNetworkManager cyNetworkManager,
			PropertySheetPanel propertySheetPanel, EditorManager editorManager,
			DefaultViewPanel defViewPanel, CyTableManager tableMgr) {

		this.cyNetworkManager = cyNetworkManager;
		this.propertySheetPanel = propertySheetPanel;

		this.editorManager = editorManager;

		propertyMap = new HashMap<VisualStyle, List<Property>>();

		vizMapPropertyBuilder = new VizMapPropertyBuilder(cyNetworkManager,
				editorManager, tableMgr);
	}

	public Map<VisualStyle, List<Property>> getPropertyMap() {
		return this.propertyMap;
	}

	public void setPropertyTable(final VisualStyle style) {

		setPropertySheetAppearence(style);

		for (Property item : propertySheetPanel.getProperties())
			propertySheetPanel.removeProperty(item);

		/*
		 * Add properties to the property sheet.
		 */
		List<Property> propRecord = setPropertyFromCalculator(style);

		// Save it for later use.
		propertyMap.put(style, propRecord);

		/*
		 * Finally, build unused list
		 */
		setUnused(propRecord, style);
	}

	private void setPropertySheetAppearence(VisualStyle style) {
		/*
		 * Set Tooltiptext for the table.
		 */
		propertySheetPanel.setTable(new VizMapPropertySheetTable());
		propertySheetPanel
				.getTable()
				.getColumnModel()
				.addColumnModelListener(
						new VizMapPropertySheetTableColumnModelListener(this));

		/*
		 * By default, show category.
		 */
		propertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);

		// TODO: fix context menu
		// propertySheetPanel.getTable().setComponentPopupMenu(
		// menuMgr.getContextMenu());

		// TODO: fix listener
		propertySheetPanel.getTable().addMouseListener(
				new VizMapPropertySheetMouseAdapter(this, propertySheetPanel,
						style, editorManager));

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
		// FIXME
		emptyBoxRenderer = new DefaultTableCellRenderer();
		emptyBoxRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
		emptyBoxRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		emptyBoxRenderer.setBackground(new Color(0, 200, 255, 20));
		emptyBoxRenderer.setForeground(Color.red);
		emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

		filledBoxRenderer = new DefaultTableCellRenderer();
		filledBoxRenderer.setBackground(Color.white);
		filledBoxRenderer.setForeground(Color.blue);

		// TODO: fix icon list
		// VisualPropertyIcon newIcon;
		//
		// List<Icon> iconList = new ArrayList<Icon>();
		// final List<NodeShape> nodeShapes = new ArrayList<NodeShape>();
		//
		// for (Object key : nodeShapeIcons.keySet()) {
		// NodeShape shape = (NodeShape) key;
		//
		// if (shape.isSupported()) {
		// iconList.add(nodeShapeIcons.get(key));
		// nodeShapes.add(shape);
		// }
		// }
		//
		// Icon[] iconArray = new Icon[iconList.size()];
		// String[] shapeNames = new String[iconList.size()];
		//
		// for (int i = 0; i < iconArray.length; i++) {
		// newIcon = ((NodeIcon) iconList.get(i)).clone();
		// newIcon.setIconHeight(16);
		// newIcon.setIconWidth(16);
		// iconArray[i] = newIcon;
		// shapeNames[i] = nodeShapes.get(i).getShapeName();
		// }
		//
		// // shapeCellEditor.setAvailableValues(nodeShapes.toArray());
		// // shapeCellEditor.setAvailableIcons(iconArray);
		// iconList.clear();
		// iconList.addAll(arrowShapeIcons.values());
		// iconArray = new Icon[iconList.size()];
		//
		// String[] arrowNames = new String[iconList.size()];
		// Set arrowShapes = arrowShapeIcons.keySet();
		//
		// for (int i = 0; i < iconArray.length; i++) {
		// newIcon = ((ArrowIcon) iconList.get(i));
		// newIcon.setIconHeight(16);
		// newIcon.setIconWidth(40);
		// newIcon.setBottomPadding(-9);
		// iconArray[i] = newIcon;
		// arrowNames[i] = newIcon.getName();
		// }
		//
		// // arrowCellEditor.setAvailableValues(arrowShapes.toArray());
		// // arrowCellEditor.setAvailableIcons(iconArray);
		// iconList = new ArrayList();
		// iconList.addAll(lineTypeIcons.values());
		// iconArray = new Icon[iconList.size()];
		// shapeNames = new String[iconList.size()];
		//
		// Set lineTypes = lineTypeIcons.keySet();
		//
		// for (int i = 0; i < iconArray.length; i++) {
		// newIcon = (VisualPropertyIcon) (iconList.get(i));
		// newIcon.setIconHeight(16);
		// newIcon.setIconWidth(16);
		// iconArray[i] = newIcon;
		// shapeNames[i] = newIcon.getName();
		// }
		// lineCellEditor.setAvailableValues(lineTypes.toArray());
		// lineCellEditor.setAvailableIcons(iconArray);
	}

	private List<Property> setPropertyFromCalculator(final VisualStyle style) {

		final Collection<VisualProperty<?>> nodeVP = style.getVisualLexicon().getAllDescendants(TwoDVisualLexicon.NODE);
		final Collection<VisualProperty<?>> edgeVP = style.getVisualLexicon().getAllDescendants(TwoDVisualLexicon.EDGE);
		//final Collection<VisualProperty<?>> networkVP = style.getVisualLexicon().getVisualLexiconNode(TwoDVisualLexicon.NETWORK).getChildren();
		
		final List<Property> nodeProps = setProps(style, TwoDVisualLexicon.NODE, nodeVP);
		final List<Property> edgeProps = setProps(style, TwoDVisualLexicon.EDGE, edgeVP);
		//final List<Property> networkProps = setProps(style, TwoDVisualLexicon.NETWORK);
		
		final List<Property> result = new ArrayList<Property>();
		result.addAll(nodeProps);
		result.addAll(edgeProps);
		//result.addAll(networkProps);
		
		return result;

	}

	private List<Property> setProps(final VisualStyle style, final VisualProperty<?> cat, final Collection<VisualProperty<?>> vpSet) {

		final List<Property> props = new ArrayList<Property>();
		final Collection<VisualMappingFunction<?, ?>> mappings = style
				.getAllVisualMappingFunctions();

		for (VisualMappingFunction<?, ?> mapping : mappings) {

			final VisualProperty<?> targetVP = mapping.getVisualProperty();
			logger.debug("!!!!!!Checking VP: " + targetVP.getDisplayName() + " for " + cat.getDisplayName());
			// execute the following only if category matches.
			if (vpSet.contains(targetVP) == false || style.getVisualLexicon().getVisualLexiconNode(targetVP).getChildren().size() != 0)
				continue;
			
			logger.debug("This is a leaf VP: " + targetVP.getDisplayName());

			

			final VizMapperProperty<?> calculatorTypeProp = vizMapPropertyBuilder
					.buildProperty(mapping, cat, propertySheetPanel);

			logger.debug("Built new PROP: " + calculatorTypeProp.getDisplayName());
			
			PropertyEditor editor = ((PropertyEditorRegistry) propertySheetPanel
					.getTable().getEditorFactory())
					.getEditor(calculatorTypeProp);
			
			
			
			if ((editor == null)
					&& (calculatorTypeProp.getCategory().equals(
							"Unused Properties") == false)) {
				
				logger.debug("***** Testing category: " + cat.getDisplayName());
				((PropertyEditorRegistry) this.propertySheetPanel
						.getTable().getEditorFactory())
						.registerEditor(calculatorTypeProp, editorManager
								.getDataTableComboBoxEditor(NODE));


//				if (cat.equals(NODE)) {
//
//					((PropertyEditorRegistry) this.propertySheetPanel
//							.getTable().getEditorFactory())
//							.registerEditor(calculatorTypeProp, editorManager
//									.getDataTableComboBoxEditor(NODE));
//				} else if (cat.equals(EDGE)) {
//					((PropertyEditorRegistry) this.propertySheetPanel
//							.getTable().getEditorFactory())
//							.registerEditor(calculatorTypeProp, editorManager
//									.getDataTableComboBoxEditor(EDGE));
//				} else {
//					((PropertyEditorRegistry) this.propertySheetPanel
//							.getTable().getEditorFactory())
//							.registerEditor(calculatorTypeProp, editorManager
//									.getDataTableComboBoxEditor(NETWORK));
//
//				}
			}
			props.add(calculatorTypeProp);
		}

		return props;
	}

	private void setUnused(List<Property> propList, VisualStyle style) {
		buildList(style);

		// TODO: Sort the unused list.
		// Collections.sort(getUnusedVisualPropType());

		for (VisualProperty<?> type : getUnusedVisualPropType()) {
			VizMapperProperty<VisualProperty<?>> prop = new VizMapperProperty<VisualProperty<?>>();
			prop.setCategory(AbstractVizMapperPanel.CATEGORY_UNUSED);
			prop.setDisplayName(type.getDisplayName());
			prop.setHiddenObject(type);
			prop.setValue("Double-Click to create...");
			propertySheetPanel.addProperty(prop);
			propList.add(prop);
		}
	}

	private void buildList(final VisualStyle style) {

		unusedVisualPropType = new ArrayList<VisualProperty<?>>();

		VisualMappingFunction<?, ?> mapping = null;

		final VisualLexicon lex = style.getVisualLexicon();

		for (VisualProperty<?> type : lex.getAllVisualProperties()) {
			mapping = style.getVisualMappingFunction(type);

			if (mapping == null && style.getVisualLexicon().getVisualLexiconNode(type).getChildren().size() == 0)
				unusedVisualPropType.add(type);

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

			if ((shownProp != null)) {
				// FIXME
				// && (shownProp.getParentProperty() != null)
				// && shownProp.getParentProperty().getDisplayName().equals(
				// NODE_LABEL_POSITION.getName())) {
				// // This is label position cell. Need laeger cell.
				// table.setRowHeight(i, 50);
			} else if ((shownProp != null)
					&& shownProp.getDisplayName().equals(
							AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW)) {
				// This is a Continuous Icon cell.
				final Property parent = shownProp.getParentProperty();
				final Object type = ((VizMapperProperty) parent)
						.getHiddenObject();

				if (type instanceof ContinuousMapping) {

					// FIXME!!

					// ContinuousMapping<?> mapping = (ContinuousMapping<?>)
					// type;
					//
					// table.setRowHeight(i, 80);
					//
					// int wi = table.getCellRect(0, 1, true).width;
					// final TableCellRenderer cRenderer =
					// editorManager.getVisualPropertyEditor(vp)
					// .getContinuousCellRenderer((VisualProperty) type,
					// wi, 70);
					// rendReg.registerRenderer(shownProp, cRenderer);

				}
			} else if ((shownProp != null)
					&& (shownProp.getCategory() != null)
					&& shownProp.getCategory().equals(
							AbstractVizMapperPanel.CATEGORY_UNUSED)) {

				// FIXME
				// empRenderer.setForeground(colorMgr.getColor("UNUSED_COLOR"));
				((PropertyRendererRegistry) this.propertySheetPanel.getTable()
						.getRendererFactory()).registerRenderer(shownProp,
						empRenderer);
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
	public void removeProperty(final Property prop, final VisualStyle style) {

		final List<Property> props = propertyMap.get(style);
		if (props == null)
			return;

		final List<Property> targets = new ArrayList<Property>();
		
		for (Property p : props) {
			if(p.getDisplayName() == null)
				continue;
			if (p.getDisplayName().equals(prop.getDisplayName()))
				targets.add(p);
		}

		for (Property p : targets)
			props.remove(p);
	}

	// TODO: this should be gone
	public void setAttrComboBox() {
		// Attribute Names
		final List<String> names = new ArrayList<String>();
		//
		// CyTable attr = /* TODO */getTargetNetwork().getNodeCyDataTables()
		// .get(CyNetwork.DEFAULT_ATTRS);
		//
		// // TODO remove the next line too!
		// if (attr == null)
		// return;
		//
		// Map<String, Class<?>> cols = attr.getColumnTypeMap();
		// names.addAll(cols.keySet());
		//
		// Collections.sort(names);
		//
		// // nodeAttrEditor.setAvailableValues(names.toArray());
		// spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "nodeAttrEditor",
		// names
		// .toArray());
		//
		// names.clear();
		//
		// for (String name : cols.keySet()) {
		// Class<?> dataClass = cols.get(name);
		//
		// if ((dataClass == Integer.class) || (dataClass == Double.class))
		// names.add(name);
		// }
		//
		// Collections.sort(names);
		// // nodeNumericalAttrEditor.setAvailableValues(names.toArray());
		// spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
		// "nodeNumericalAttrEditor", names.toArray());
		//
		// names.clear();
		//
		// attr = getTargetNetwork().getEdgeCyDataTables().get(
		// CyNetwork.DEFAULT_ATTRS);
		// cols = attr.getColumnTypeMap();
		// names.addAll(cols.keySet());
		// Collections.sort(names);
		//
		// // edgeAttrEditor.setAvailableValues(names.toArray());
		// spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "edgeAttrEditor",
		// names
		// .toArray());
		// names.clear();
		//
		// for (String name : cols.keySet()) {
		// Class<?> dataClass = cols.get(name);
		//
		// if ((dataClass == Integer.class) || (dataClass == Double.class))
		// names.add(name);
		// }
		//
		// Collections.sort(names);
		// // edgeNumericalAttrEditor.setAvailableValues(names.toArray());
		// spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
		// "edgeNumericalAttrEditor", names.toArray());
		// propertySheetPanel.repaint();
	}

	public VizMapPropertyBuilder getPropertyBuilder() {
		return this.vizMapPropertyBuilder;
	}

	public List<VisualProperty<?>> getUnusedVisualPropType() {
		return unusedVisualPropType;
	}

}
