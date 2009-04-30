/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package org.cytoscape.view.vizmap.gui.internal;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import cytoscape.CyNetworkManager;

import static org.cytoscape.model.GraphObject.*;

/**
 * Create property for the Property Sheet object.
 */
public class VizMapPropertyBuilder {

	private PropertyRendererRegistry rendReg;
	private PropertyEditorRegistry editorReg;

	private DefaultTableCellRenderer emptyBoxRenderer;
	private DefaultTableCellRenderer filledBoxRenderer;

	private EditorManager editorFactory;
	private CyNetworkManager cyNetworkManager;

	/**
	 * Build one property for one visual property.
	 * 
	 * @param <K>
	 *            data type of attribute to be mapped.
	 * @param <V>
	 *            data type of Visual Property.
	 * 
	 */
	public <K, V> void buildProperty(
			final VisualMappingFunction<K, V> visualMapping,
			final VizMapperProperty<VisualProperty<V>> calculatorTypeProp,
			final String rootObjectCategory,
			final PropertySheetPanel propertySheetPanel) {
		// Mapping is empty
		if (visualMapping == null)
			return;

		final VisualProperty<V> vp = visualMapping.getVisualProperty();
		final CyNetwork targetNetwork = cyNetworkManager.getCurrentNetwork();

		/*
		 * Set one calculator
		 */
		calculatorTypeProp.setCategory(rootObjectCategory);
		calculatorTypeProp.setDisplayName(vp.getDisplayName());
		calculatorTypeProp.setHiddenObject(vp);

		final String attrName = visualMapping.getMappingAttributeName();
		final VizMapperProperty<VisualMappingFunction<K, V>> mappingHeader = new VizMapperProperty<VisualMappingFunction<K, V>>();

		if (attrName == null) {
			calculatorTypeProp.setValue("Select Value");
			rendReg.registerRenderer(calculatorTypeProp, emptyBoxRenderer);
		} else {
			calculatorTypeProp.setValue(attrName);
			rendReg.registerRenderer(calculatorTypeProp, filledBoxRenderer);
		}

		// TODO: is this correct?
		mappingHeader.setDisplayName("Mapping Type");
		// mappingHeader.setHiddenObject(visualMapping.getClass());
		// Set mapping type as string.
		mappingHeader.setValue(visualMapping.toString());
		mappingHeader.setHiddenObject(visualMapping);

		// Set parent-child relationship
		mappingHeader.setParentProperty(calculatorTypeProp);
		calculatorTypeProp.addSubProperty(mappingHeader);
		// TODO: Should refactor factory.
		editorReg.registerEditor(mappingHeader, editorFactory
				.getDefaultComboBoxEditor("mappingTypeEditor"));

		CyDataTable attr = null;
		Iterator<? extends GraphObject> it = null;

		if (targetNetwork == null)
			return;

		attr = targetNetwork.getCyDataTables(vp.getObjectType()).get(
				CyNetwork.DEFAULT_ATTRS);
		if (vp.getObjectType().equals(NODE)) {
			it = targetNetwork.getNodeList().iterator();
			editorReg.registerEditor(calculatorTypeProp, editorFactory
					.getDefaultComboBoxEditor("nodeAttrEditor"));
		} else if (vp.getObjectType().equals(EDGE)) {
			it = targetNetwork.getNodeList().iterator();
			editorReg.registerEditor(calculatorTypeProp, editorFactory
					.getDefaultComboBoxEditor("edgeAttrEditor"));
		}

		/*
		 * Discrete Mapping
		 */
		if (visualMapping instanceof DiscreteMapping && (attrName != null)) {
			final Map<K, V> discMapping = ((DiscreteMapping<K, V>) visualMapping)
					.getAll();

			// Extract key attribute values.
			Class<K> attrDataType = null;

			try {
				attrDataType = (Class<K>) attr.getColumnTypeMap().get(attrName);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Attribute is not compatible data type.");
			}

			final SortedSet<K> attrSet = new TreeSet<K>(attr.getColumnValues(
					attrName, attrDataType));

			setDiscreteProps(vp, discMapping, attrSet, editorFactory
					.getDiscreteCellEditor(vp), editorFactory
					.getDiscreteCellRenderer(vp), calculatorTypeProp);
		} else if (visualMapping instanceof ContinuousMapping
				&& (attrName != null)) {
			int wi = propertySheetPanel.getTable().getCellRect(0, 1, true).width;

			VizMapperProperty<?> graphicalView = new VizMapperProperty();

			graphicalView
					.setDisplayName(AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW);
			graphicalView.setName(vp.getDisplayName());
			graphicalView.setParentProperty(calculatorTypeProp);
			calculatorTypeProp.addSubProperty(graphicalView);

			TableCellRenderer crenderer = editorFactory
					.getContinuousCellRenderer(vp, wi, 70);
			rendReg.registerRenderer(graphicalView, crenderer);
		} else if (visualMapping instanceof PassthroughMapping
				&& (attrName != null)) {
			// Passthrough
			String id;
			String value;
			VizMapperProperty<K> oneProperty;

			// Accept String only.
			if (attr.getColumnTypeMap().get(attrName) == String.class) {
				while (it.hasNext()) {
					GraphObject go = it.next();
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

		propertySheetPanel.addProperty(0, calculatorTypeProp);
		propertySheetPanel.setRendererFactory(rendReg);
		propertySheetPanel.setEditorFactory(editorReg);
	}

	/*
	 * Set value, title, and renderer for each property in the category. This
	 * list should be created against all available attribute values.
	 */
	private <K, V> void setDiscreteProps(VisualProperty<V> vp,
			Map<K, V> discMapping, Set<K> attrKeys, PropertyEditor editor,
			TableCellRenderer rend, DefaultProperty parent) {
		if (attrKeys == null)
			return;

		V val = null;
		VizMapperProperty<V> valProp;
		String strVal;

		final List<VizMapperProperty<V>> children = new ArrayList<VizMapperProperty<V>>();

		for (K key : attrKeys) {
			valProp = new VizMapperProperty<V>();
			strVal = key.toString();
			valProp.setDisplayName(strVal);
			valProp.setName(strVal + "-" + vp.toString());
			valProp.setParentProperty(parent);

			// Get the mapped value
			val = discMapping.get(key);

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
