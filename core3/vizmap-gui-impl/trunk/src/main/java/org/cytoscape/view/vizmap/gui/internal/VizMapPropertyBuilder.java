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

import static org.cytoscape.model.CyTableEntry.EDGE;
import static org.cytoscape.model.CyTableEntry.NODE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.table.DefaultTableCellRenderer;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkAddedEvent;
import org.cytoscape.session.events.NetworkAddedListener;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

/**
 * Create property for the Property Sheet object.
 */
public class VizMapPropertyBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(VizMapPropertyBuilder.class);

	private DefaultTableCellRenderer emptyBoxRenderer;
	private DefaultTableCellRenderer filledBoxRenderer;

	private EditorManager editorFactory;
	private CyNetworkManager cyNetworkManager;
	private CyTableManager tableMgr;

	public VizMapPropertyBuilder(CyNetworkManager cyNetworkManager,
			EditorManager editorFactory, CyTableManager tableMgr) {
		this.cyNetworkManager = cyNetworkManager;
		this.editorFactory = editorFactory;
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
	public <K, V> VizMapperProperty<VisualProperty<V>> buildProperty(
			final VisualMappingFunction<K, V> visualMapping,
			final VisualProperty<?> rootObjectCategory,
			final PropertySheetPanel propertySheetPanel) {

		logger.debug("\n\n\nbuildProp called!");

		// Mapping is empty
		if (visualMapping == null)
			throw new NullPointerException("Mapping is null.");
		if (rootObjectCategory == null)
			throw new NullPointerException(
					"Category is null.  It should be one of the following: NODE, EDGE, or NETWORK.");
		if (propertySheetPanel == null)
			throw new NullPointerException("PropertySheet is null.");

		final VisualProperty<V> vp = visualMapping.getVisualProperty();
		final VizMapperProperty<VisualProperty<V>> topProperty = new VizMapperProperty<VisualProperty<V>>();

		// Build Property object

		topProperty.setCategory(rootObjectCategory.getDisplayName());
		topProperty.setDisplayName(vp.getDisplayName());
		topProperty.setHiddenObject(vp);
		topProperty.setName(vp.getIdString());

		final String attrName = visualMapping.getMappingAttributeName();
		final VizMapperProperty<VisualMappingFunction<K, V>> mappingHeader = new VizMapperProperty<VisualMappingFunction<K, V>>();

		if (attrName == null) {
			topProperty.setValue("Select Value");
			((PropertyRendererRegistry) propertySheetPanel.getTable()
					.getRendererFactory()).registerRenderer(topProperty,
					emptyBoxRenderer);
		} else {
			topProperty.setValue(attrName);
			((PropertyRendererRegistry) propertySheetPanel.getTable()
					.getRendererFactory()).registerRenderer(topProperty,
					filledBoxRenderer);
		}

		// TODO: is this correct?
		mappingHeader.setDisplayName("Mapping Type");
		mappingHeader.setName("Mapping Type");

		// Set mapping type as string.
		mappingHeader.setValue(visualMapping.toString());
		mappingHeader.setHiddenObject(visualMapping);

		// Set parent-child relationship
		mappingHeader.setParentProperty(topProperty);
		topProperty.addSubProperty(mappingHeader);

		// TODO: Should refactor factory.
		((PropertyEditorRegistry) propertySheetPanel.getTable()
				.getEditorFactory()).registerEditor(mappingHeader,
				editorFactory.getDefaultComboBoxEditor("mappingTypeEditor"));

		final Set<CyNetwork> networks = cyNetworkManager.getNetworkSet();

		final Set<CyTableEntry> graphObjectSet = new HashSet<CyTableEntry>();
		for (CyNetwork targetNetwork : networks) {
			Iterator<? extends CyTableEntry> it = null;

			if (rootObjectCategory.getIdString().equals(NODE)) {
				it = targetNetwork.getNodeList().iterator();
				((PropertyEditorRegistry) propertySheetPanel.getTable()
						.getEditorFactory()).registerEditor(topProperty,
						editorFactory.getDataTableComboBoxEditor(NODE));
			} else if (rootObjectCategory.getIdString().equals(EDGE)) {
				it = targetNetwork.getEdgeList().iterator();
				((PropertyEditorRegistry) propertySheetPanel.getTable()
						.getEditorFactory()).registerEditor(topProperty,
						editorFactory.getDataTableComboBoxEditor(EDGE));
			} else {
				it = cyNetworkManager.getNetworkSet().iterator();
				((PropertyEditorRegistry) propertySheetPanel.getTable()
						.getEditorFactory())
						.registerEditor(
								topProperty,
								editorFactory
										.getDataTableComboBoxEditor(CyTableEntry.NETWORK));
			}

			while (it.hasNext())
				graphObjectSet.add(it.next());
		}

		/*
		 * Discrete Mapping
		 */
		if (visualMapping instanceof DiscreteMapping && (attrName != null)) {
			final Map<K, V> discMapping = ((DiscreteMapping<K, V>) visualMapping)
					.getAll();

			final SortedSet<K> attrSet = new TreeSet<K>();

			for (CyTableEntry go : graphObjectSet) {
				final Class<?> attrClass = go.getCyRow().getDataTable()
						.getColumnTypeMap().get(attrName);

				Object id = go.getCyRow().get(attrName, attrClass);
				attrSet.add((K) id);
			}

			// FIXME
			setDiscreteProps(vp, discMapping, attrSet,
					editorFactory.getVisualPropertyEditor(vp),
					topProperty, propertySheetPanel);
		} else if (visualMapping instanceof ContinuousMapping
				&& (attrName != null)) {
			int wi = propertySheetPanel.getTable().getCellRect(0, 1, true).width;

			VizMapperProperty<?> graphicalView = new VizMapperProperty();

			graphicalView
					.setDisplayName(AbstractVizMapperPanel.GRAPHICAL_MAP_VIEW);
			graphicalView.setName(vp.getDisplayName());
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
			// Passthrough

			Object id;
			Object value;
			String stringVal;

			VizMapperProperty<K> oneProperty;

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

				oneProperty = new VizMapperProperty<K>();

				oneProperty.setValue(stringVal);
				oneProperty.setName(id.toString());

				// This prop. should not be editable!
				oneProperty.setEditable(false);

				oneProperty.setParentProperty(topProperty);
				oneProperty.setDisplayName(id.toString());
				oneProperty.setType(String.class);

				topProperty.addSubProperty(oneProperty);
			}

		}

		propertySheetPanel.addProperty(0, topProperty);
		propertySheetPanel
				.setRendererFactory(((PropertyRendererRegistry) propertySheetPanel
						.getTable().getRendererFactory()));
		propertySheetPanel
				.setEditorFactory(((PropertyEditorRegistry) propertySheetPanel
						.getTable().getEditorFactory()));

		return topProperty;
	}

	/*
	 * Set value, title, and renderer for each property in the category. This
	 * list should be created against all available attribute values.
	 */
	private <K, V> void setDiscreteProps(VisualProperty<V> vp,
			Map<K, V> discMapping, SortedSet<K> attrSet,
			VisualPropertyEditor<V> visualPropertyEditor,
			DefaultProperty parent, PropertySheetPanel propertySheetPanel) {
		if (attrSet == null)
			return;

		V val = null;
		VizMapperProperty<V> valProp;
		String strVal;

		final List<VizMapperProperty<V>> children = new ArrayList<VizMapperProperty<V>>();

		for (K key : attrSet) {
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

//			// FIXME!
//			((PropertyRendererRegistry) propertySheetPanel.getTable()
//					.getRendererFactory()).registerRenderer(valProp, vp.getType());

			// FIXME!!
			((PropertyEditorRegistry) propertySheetPanel.getTable()
					.getEditorFactory()).registerEditor(valProp, editorFactory.getVisualPropertyEditor(vp).getVisualPropertyEditor());

			valProp.setValue(val);
		}

		// Add all children.
		parent.addSubProperties(children);
	}

}
