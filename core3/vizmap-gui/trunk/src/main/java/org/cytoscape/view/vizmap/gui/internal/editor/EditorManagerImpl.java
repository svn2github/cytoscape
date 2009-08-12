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
package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.editor.ValueEditor;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.AttributeComboBoxPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;

/**
 *
 */
public class EditorManagerImpl implements EditorManager, ColumnCreatedListener, ColumnDeletedListener {

	private final Map<VisualProperty<?>, VisualPropertyEditor<?>> editors;

	private final Map<String, PropertyEditor> comboBoxEditors;
	
	private final Map<VisualProperty<?>, Component> continuousEditors;
	
	private final Map<Class<?>, ValueEditor<?>> valueEditors;

	/**
	 * Creates a new EditorFactory object.
	 */
	public EditorManagerImpl() {
		continuousEditors = new HashMap<VisualProperty<?>, Component>();
		
		editors = new HashMap<VisualProperty<?>, VisualPropertyEditor<?>>();

		comboBoxEditors = new HashMap<String, PropertyEditor>();
		
		valueEditors = new HashMap<Class<?>, ValueEditor<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#addEditorDisplayer(org
	 * .cytoscape.vizmap.gui.editors.EditorDisplayer, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void addValueEditor(ValueEditor<?> ve, Map properties) {
		System.out.println("************* Got V Editor **************** " + ve.toString());
		
		this.valueEditors.put(ve.getType(), ve);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#removeEditorDisplayer(
	 * org.cytoscape.vizmap.gui.editors.EditorDisplayer, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void removeValueEditor(ValueEditor<?> lexicon, Map properties) {
		System.out.println("************* Remove Lexicon ****************");
		valueEditors.remove(lexicon.getType());
	}

	// private <T> VisualPropertyEditor<T> findEditor(VisualProperty<T> type) {
	// final Class<T> dataType = type.getType();
	//
	// for (VisualPropertyEditor<?> disp : displayers)
	// if ((dataType == disp.getVisualProperty().getType()))
	// return disp;
	//
	// throw new NullPointerException("no editor displayer found for: "
	// + type.toString());
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#showDiscreteEditor(java
	 * .awt.Component, org.cytoscape.viewmodel.VisualProperty)
	 */
	@SuppressWarnings("unchecked")
	public <V> V showVisualPropertyValueEditor(Component parentComponent,
			VisualProperty<V> type, V initial) throws Exception {

		ValueEditor<V> editor = (ValueEditor<V>) valueEditors.get(type.getType());
		
		if(editor == null)
			throw new IllegalStateException("No value editor for " + type.getDisplayName() + "is available.");

		return editor.showEditor(null, initial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#showContinuousEditor(java
	 * .awt.Component, org.cytoscape.viewmodel.VisualProperty)
	 */
	public <V> void showContinuousEditor(Component parentComponent,
			VisualProperty<V> type) throws Exception {
		final VisualPropertyEditor<?> editor = editors.get(type);
		assert editor.getVisualProperty() == type;
		
		//TODO: design dialog state mamagement
//		
//		
//		Component mappingEditor = editor.getContinuousMappingEditor();
//		
//		JDialog editorDialog = new JDialog();
//		editorDialog.setModal(true);
//		editorDialog.setLocationRelativeTo(parentComponent);

	}

	@SuppressWarnings("unchecked")
	public <V> VisualPropertyEditor<V> getVisualPropertyEditor(
			VisualProperty<V> vp) {
		return (VisualPropertyEditor<V>) editors.get(vp);
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.cytoscape.vizmap.gui.editors.EditorFactory#getCellEditors()
//	 */
//	public List<PropertyEditor> getCellEditors() {
//		List<PropertyEditor> ret = new ArrayList<PropertyEditor>();
//
//		for (VisualProperty<?> vp : editors.keySet())
//			ret.add(editors.get(vp).getVisualPropertyEditor());
//
//		return ret;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getDiscreteCellEditor(
//	 * org.cytoscape.viewmodel.VisualProperty)
//	 */
//	public PropertyEditor getDiscreteCellEditor(VisualProperty<?> type) {
//		return 
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getDiscreteCellRenderer
//	 * (org.cytoscape.viewmodel.VisualProperty)
//	 */
//	public TableCellRenderer getDiscreteCellRenderer(VisualProperty type) {
//		return findEditor(type,
//				EditorDisplayer.MappingType.VisualPropertyEditor)
//				.getCellRenderer(type, 0, 0);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getContinuousCellEditor
//	 * (org.cytoscape.viewmodel.VisualProperty)
//	 */
//	public PropertyEditor getContinuousCellEditor(VisualProperty type) {
//		return findEditor(type,
//				EditorDisplayer.MappingType.VisualPropertyEditor)
//				.getVisualPropertyEditor();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getContinuousCellRenderer
//	 * (org.cytoscape.viewmodel.VisualProperty, int, int)
//	 */
//	public TableCellRenderer getContinuousCellRenderer(VisualProperty type,
//			int w, int h) {
//		return findEditor(type,
//				EditorDisplayer.MappingType.VisualPropertyEditor)
//				.getCellRenderer(type, w, h);
//	}
//
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getDefaultComboBoxEditor
	 * (java.lang.String)
	 */
	public PropertyEditor getDefaultComboBoxEditor(String editorName) {
		PropertyEditor editor = comboBoxEditors.get(editorName);
		if (editor == null) {
			editor = new CyComboBoxPropertyEditor();
			comboBoxEditors.put(editorName, editor);
		}
		return editor;
	}

	public <V> ValueEditor<V> getValueEditor(Class<V> dataType) {
		return (ValueEditor<V>) this.valueEditors.get(dataType);
	}

	public PropertyEditor getDataTableComboBoxEditor(CyDataTable table,
			String editorName) {
		PropertyEditor editor = comboBoxEditors.get(editorName);
		if (editor == null) {
			editor = new AttributeComboBoxPropertyEditor(table);
			comboBoxEditors.put(editorName, editor);
		}
		return editor;
	}

	public void handleEvent(ColumnCreatedEvent e) {
		System.out.println("---------------> got column event: " + e.getColumnName());
	}

	public void handleEvent(ColumnDeletedEvent e) {
		// TODO Auto-generated method stub
		
	}

}
