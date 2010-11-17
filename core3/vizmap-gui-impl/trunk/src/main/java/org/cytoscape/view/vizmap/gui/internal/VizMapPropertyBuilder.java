/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.event.CellType;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;


/**
 * Create property for the Property Sheet object.
 */
public class VizMapPropertyBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(VizMapPropertyBuilder.class);

	private DefaultTableCellRenderer emptyBoxRenderer;
	private DefaultTableCellRenderer filledBoxRenderer;

	private EditorManager editorManager;
	
	private CyNetworkManager cyNetworkManager;
	private CyTableManager tableMgr;

	public VizMapPropertyBuilder(CyNetworkManager cyNetworkManager,
			EditorManager editorManager, CyTableManager tableMgr) {
		this.cyNetworkManager = cyNetworkManager;
		this.editorManager = editorManager;
		this.tableMgr = tableMgr;
	}

	/**
	 * Build one property for one visual property.
	 * 
	 * @param <K>
	 *            data type of attribute to be mapped.
	 * @param <V>
	 *            data type of Visual Property.
	 * 
	 */
	public <K, V> VizMapperProperty<VisualProperty<V>, String, VisualMappingFunctionFactory> buildProperty(
			final VisualMappingFunction<K, V> visualMapping,
			final String categoryName,
			final PropertySheetPanel propertySheetPanel, final VisualMappingFunctionFactory factory) {

		logger.debug("\n\n\nbuildProp called: Root VP = " + categoryName);

		// Mapping is empty
		if (visualMapping == null)
			throw new NullPointerException("Mapping is null.");
		if (categoryName == null)
			throw new NullPointerException(
					"Category is null.  It should be one of the following: NODE, EDGE, or NETWORK.");
		if (propertySheetPanel == null)
			throw new NullPointerException("PropertySheet is null.");

		final VisualProperty<V> vp = visualMapping.getVisualProperty();
		final VizMapperProperty<VisualProperty<V>, String, VisualMappingFunctionFactory> topProperty 
			= new VizMapperProperty<VisualProperty<V>, String, VisualMappingFunctionFactory>(CellType.VISUAL_PROPERTY_TYPE, vp, String.class);

		// Build Property object
		topProperty.setCategory(categoryName);
		topProperty.setDisplayName(vp.getDisplayName());
		topProperty.setInternalValue(factory);
		
		final String attrName = visualMapping.getMappingAttributeName();
		final VizMapperProperty<String, VisualMappingFunctionFactory, VisualMappingFunction<K, V>> mappingHeader 
			= new VizMapperProperty<String, VisualMappingFunctionFactory, VisualMappingFunction<K, V>>(CellType.MAPPING_TYPE, "Mapping Type", VisualMappingFunctionFactory.class);
		
		if (attrName == null) {
			topProperty.setValue("Select Attribute");
			((PropertyRendererRegistry) propertySheetPanel.getTable()
					.getRendererFactory()).registerRenderer(topProperty,
					emptyBoxRenderer);
		} else {
			topProperty.setValue(attrName);
			((PropertyRendererRegistry) propertySheetPanel.getTable()
					.getRendererFactory()).registerRenderer(topProperty,
					filledBoxRenderer);
		}

		mappingHeader.setDisplayName("Mapping Type");

		// Set mapping type as string.
		mappingHeader.setValue(factory);
		mappingHeader.setInternalValue(visualMapping);

		// Set parent-child relationship
		mappingHeader.setParentProperty(topProperty);
		topProperty.addSubProperty(mappingHeader);

		// TODO: Should refactor factory.
		((PropertyEditorRegistry) propertySheetPanel.getTable()
				.getEditorFactory()).registerEditor(mappingHeader,
				editorManager.getDefaultComboBoxEditor("mappingTypeEditor"));

		final Set<CyNetwork> networks = cyNetworkManager.getNetworkSet();

		final Set<CyTableEntry> graphObjectSet = new HashSet<CyTableEntry>();
		for (CyNetwork targetNetwork : networks) {
			Iterator<? extends CyTableEntry> it = null;

			((PropertyEditorRegistry) propertySheetPanel.getTable()
					.getEditorFactory()).registerEditor(topProperty,
					editorManager.getDataTableComboBoxEditor((Class<? extends CyTableEntry>) vp.getTargetDataType()));
			if (vp.getTargetDataType().equals(CyNode.class)) {
				it = targetNetwork.getNodeList().iterator();
			} else if (vp.getTargetDataType().equals(CyEdge.class)) {
				it = targetNetwork.getEdgeList().iterator();
			} else if (vp.getTargetDataType().equals(CyNetwork.class)) {
				it = cyNetworkManager.getNetworkSet().iterator();
			} else {
				throw new IllegalArgumentException("Data type not supported: " + vp.getTargetDataType());
			}

			while (it.hasNext())
				graphObjectSet.add(it.next());
		}

		/*
		 * Discrete Mapping
		 */
		if (visualMapping instanceof DiscreteMapping && (attrName != null)) {

			final SortedSet<K> attrSet = new TreeSet<K>();
			for (CyTableEntry go : graphObjectSet) {
				final Class<?> attrClass = go.getCyRow().getDataTable()
						.getColumnTypeMap().get(attrName);

				Object id = go.getCyRow().get(attrName, attrClass);
				if(id != null)
					attrSet.add((K) id);
			}

			// FIXME
			setDiscreteProps(vp, visualMapping, attrSet,
					editorManager.getVisualPropertyEditor(vp),
					topProperty, propertySheetPanel);
		} else if (visualMapping instanceof ContinuousMapping
				&& (attrName != null)) {

			final VizMapperProperty<String, String, VisualMappingFunction<K, V>> graphicalView 
				= new VizMapperProperty<String, String, VisualMappingFunction<K, V>>(CellType.CONTINUOUS, AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW, String.class);
			graphicalView.setValue(visualMapping);
			graphicalView
					.setDisplayName(AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW);
			graphicalView.setParentProperty(topProperty);
			topProperty.addSubProperty(graphicalView);

			// FIXME
			// TableCellRenderer crenderer = editorFactory
			// .getVisualPropertyEditor(vp).getContinuousMappingEditor();
			//
			// ((PropertyRendererRegistry) propertySheetPanel.getTable()
			// .getRendererFactory()).registerRenderer(graphicalView,
			// crenderer);
		} else if (visualMapping instanceof PassthroughMapping
				&& (attrName != null)) {

			String id;
			Object value;
			String stringVal;

			for (CyTableEntry go : graphObjectSet) {
				Class<?> attrClass = go.getCyRow().getDataTable()
						.getColumnTypeMap().get(attrName);

				id = go.getCyRow().get("name", String.class);

				if (attrName.equals("SUID"))
					value = go.getSUID();
				else
					value = go.getCyRow().get(attrName, attrClass);

				if (value != null)
					stringVal = value.toString();
				else
					stringVal = null;

				final VizMapperProperty<String, V, VisualMappingFunction<K, V>> oneProperty 
					= new VizMapperProperty<String, V, VisualMappingFunction<K, V>>(CellType.DISCRETE, id, (Class<V>) value.getClass());
				oneProperty.setInternalValue(visualMapping);
				oneProperty.setValue(stringVal);

				// This prop. should not be editable!
				oneProperty.setEditable(false);

				oneProperty.setParentProperty(topProperty);
				oneProperty.setDisplayName(id);

				topProperty.addSubProperty(oneProperty);
			}

		} else {
			throw new IllegalArgumentException("Unsupported mapping type: " + visualMapping);
		}

		propertySheetPanel.addProperty(0, topProperty);

		return topProperty;
	}

	/*
	 * Set value, title, and renderer for each property in the category. This
	 * list should be created against all available attribute values.
	 */
	private <K, V> void setDiscreteProps(VisualProperty<V> vp, VisualMappingFunction<K, V> mapping,
			SortedSet<K> attrSet,
			VisualPropertyEditor<V> visualPropertyEditor,
			DefaultProperty parent, PropertySheetPanel propertySheetPanel) {
		if (attrSet == null)
			return;

		final Map<K, V> discMapping = ((DiscreteMapping<K, V>) mapping)
		.getAll();
		
		V val = null;
		VizMapperProperty<K, V, VisualMappingFunction<K, V>> valProp;
		String strVal;

		final List<VizMapperProperty<K, V, VisualMappingFunction<K, V>>> children = new ArrayList<VizMapperProperty<K, V, VisualMappingFunction<K, V>>>();
		final PropertySheetTable table = propertySheetPanel.getTable();
		final PropertyRendererRegistry cellRendererFactory = (PropertyRendererRegistry) table.getRendererFactory();
		final PropertyEditorRegistry cellEditorFactory = (PropertyEditorRegistry) table.getEditorFactory();
		
		for (K key : attrSet) {
			
			valProp = new VizMapperProperty<K, V, VisualMappingFunction<K, V>>(CellType.DISCRETE, key, mapping.getVisualProperty().getType());
			strVal = key.toString();
			valProp.setDisplayName(strVal);
			valProp.setParentProperty(parent);

			// Get the mapped value
			val = discMapping.get(key);

			if (val != null)
				valProp.setType(val.getClass());

			children.add(valProp);

			final TableCellRenderer renderer = editorManager.getDiscreteCellRenderer(vp);
			if(renderer != null)
				cellRendererFactory.registerRenderer(valProp, renderer);

			final PropertyEditor cellEditor = editorManager.getDiscreteCellEditor(vp);
			if(cellEditor != null)
				cellEditorFactory.registerEditor(valProp, cellEditor);

			valProp.setValue(val);
			valProp.setInternalValue(mapping);
		}

		// Add all children.
		parent.addSubProperties(children);
	}

}
