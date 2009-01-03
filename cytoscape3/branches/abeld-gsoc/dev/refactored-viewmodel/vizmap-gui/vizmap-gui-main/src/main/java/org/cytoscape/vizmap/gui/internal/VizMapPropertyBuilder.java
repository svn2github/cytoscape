package org.cytoscape.vizmap.gui.internal;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.GraphObject;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.mappings.ContinuousMapping;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;
import org.cytoscape.vizmap.mappings.PassThroughMapping;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import cytoscape.CyNetworkManager;

public class VizMapPropertyBuilder {

	@Resource
	private PropertyRendererRegistry rendReg;
	@Resource
	private PropertyEditorRegistry editorReg;

	@Resource
	private DefaultTableCellRenderer emptyBoxRenderer;
	@Resource
	private DefaultTableCellRenderer filledBoxRenderer;

	@Resource
	private EditorFactory editorFactory;
	
	@Resource
	private CyNetworkManager cyNetworkManager;

	/*
	 * Build one property for one visual property.
	 */
	public void buildProperty(final Calculator calc,
			final VizMapperProperty calculatorTypeProp, final String rootCategory,
			final PropertySheetPanel propertySheetPanel) {
		
		final VisualPropertyType type = calc.getVisualPropertyType();
		final CyNetwork targetNetwork = cyNetworkManager.getCurrentNetwork();
		/*
		 * Set one calculator
		 */
		calculatorTypeProp.setCategory(rootCategory);
		calculatorTypeProp.setDisplayName(type.getName());
		calculatorTypeProp.setHiddenObject(type);

		/*
		 * Mapping 0 is always currently used mapping.
		 */
		final ObjectMapping firstMap = calc.getMapping(0);
		String attrName;

		if (firstMap != null) {
			final VizMapperProperty mappingHeader = new VizMapperProperty();

			attrName = firstMap.getControllingAttributeName();

			if (attrName == null) {
				calculatorTypeProp.setValue("Select Value");
				rendReg.registerRenderer(calculatorTypeProp, emptyBoxRenderer);
			} else {
				calculatorTypeProp.setValue(attrName);
				rendReg.registerRenderer(calculatorTypeProp, filledBoxRenderer);
			}

			mappingHeader.setDisplayName("Mapping Type");
			mappingHeader.setHiddenObject(firstMap.getClass());

			if (firstMap.getClass() == DiscreteMapping.class)
				mappingHeader.setValue("Discrete Mapping");
			else if (firstMap.getClass() == ContinuousMapping.class)
				mappingHeader.setValue("Continuous Mapping");
			else
				mappingHeader.setValue("Passthrough Mapping");

			mappingHeader.setHiddenObject(firstMap);

			mappingHeader.setParentProperty(calculatorTypeProp);
			calculatorTypeProp.addSubProperty(mappingHeader);
			editorReg.registerEditor(mappingHeader, editorFactory
					.getDefaultComboBoxEditor("mappingTypeEditor"));

			final CyDataTable attr;
			final Iterator it;
			final int nodeOrEdge;

			if (targetNetwork == null)
				return;

			if (calc.getVisualPropertyType().isNodeProp()) {
				attr = targetNetwork.getNodeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				it = targetNetwork.getNodeList().iterator();
				editorReg.registerEditor(calculatorTypeProp, editorFactory
						.getDefaultComboBoxEditor("nodeAttrEditor"));
				nodeOrEdge = ObjectMapping.NODE_MAPPING;
			} else {
				attr = targetNetwork.getEdgeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				it = targetNetwork.getNodeList().iterator();
				editorReg.registerEditor(calculatorTypeProp, editorFactory
						.getDefaultComboBoxEditor("edgeAttrEditor"));
				nodeOrEdge = ObjectMapping.EDGE_MAPPING;
			}

			/*
			 * Discrete Mapping
			 */
			if ((firstMap.getClass() == DiscreteMapping.class)
					&& (attrName != null)) {
				final Map discMapping = ((DiscreteMapping) firstMap).getAll();

				// final Set<Object> attrSet = loadKeys(attrName, attr,
				// firstMap, nodeOrEdge);
				final Set<Object> attrSet = new TreeSet<Object>(
						attr
								.getColumnValues(
										firstMap.getControllingAttributeName(),
										attr
												.getColumnTypeMap()
												.get(
														firstMap
																.getControllingAttributeName())));

				setDiscreteProps(type, discMapping, attrSet, editorFactory
						.getDiscreteCellEditor(type), editorFactory
						.getDiscreteCellRenderer(type), calculatorTypeProp);
			} else if ((firstMap.getClass() == ContinuousMapping.class)
					&& (attrName != null)) {
				int wi = propertySheetPanel.getTable().getCellRect(0, 1,
						true).width;

				VizMapperProperty graphicalView = new VizMapperProperty();
				graphicalView
						.setDisplayName(AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW);
				graphicalView.setName(type.getName());
				graphicalView.setParentProperty(calculatorTypeProp);
				calculatorTypeProp.addSubProperty(graphicalView);

				TableCellRenderer crenderer = editorFactory
						.getContinuousCellRenderer(type, wi, 70);
				rendReg.registerRenderer(graphicalView, crenderer);
			} else if ((firstMap.getClass() == PassThroughMapping.class)
					&& (attrName != null)) {
				// Passthrough
				String id;
				String value;
				VizMapperProperty oneProperty;

				// Accept String only.
				if (attr.getColumnTypeMap().get(attrName) == String.class) {
					while (it.hasNext()) {
						GraphObject go = ((GraphObject) it.next());
						id = go.attrs().get("name", String.class);

						value = go.attrs().get(attrName, String.class);
						oneProperty = new VizMapperProperty();

						if (attrName.equals("ID"))
							oneProperty.setValue(id);
						else
							oneProperty.setValue(value);

						// This prop. should not be editable!
						oneProperty.setEditable(false);

						oneProperty.setParentProperty(calculatorTypeProp);
						oneProperty.setDisplayName(id);
						oneProperty.setType(String.class);

						calculatorTypeProp.addSubProperty(oneProperty);
					}
				}
			}
		}

		propertySheetPanel.addProperty(0, calculatorTypeProp);
		propertySheetPanel.setRendererFactory(rendReg);
		propertySheetPanel.setEditorFactory(editorReg);
	}

	/*
	 * Set value, title, and renderer for each property in the category.
	 */
	private void setDiscreteProps(VisualPropertyType type, Map discMapping,
			Set<Object> attrKeys, PropertyEditor editor,
			TableCellRenderer rend, DefaultProperty parent) {
		if (attrKeys == null)
			return;

		Object val = null;
		VizMapperProperty valProp;
		String strVal;

		final List<VizMapperProperty> children = new ArrayList<VizMapperProperty>();

		for (Object key : attrKeys) {
			valProp = new VizMapperProperty();
			strVal = key.toString();
			valProp.setDisplayName(strVal);
			valProp.setName(strVal + "-" + type.toString());
			valProp.setParentProperty(parent);

			try {
				val = discMapping.get(key);
			} catch (Exception e) {
				System.out.println("------- Map = " + discMapping.getClass()
						+ ", class = " + key.getClass() + ", err = "
						+ e.getMessage());
				System.out.println("------- Key = " + key + ", val = " + val
						+ ", disp = " + strVal);
			}

			if (val != null)
				valProp.setType(val.getClass());

			children.add(valProp);
			rendReg.registerRenderer(valProp, rend);
			editorReg.registerEditor(valProp, editor);

			valProp.setValue(val);
		}

		// Add all children.
		parent.addSubProperties(children);
	}

}
