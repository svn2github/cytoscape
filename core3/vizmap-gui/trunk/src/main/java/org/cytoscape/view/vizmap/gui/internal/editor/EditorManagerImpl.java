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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;

/**
 *
 */
public class EditorManagerImpl implements EditorManager {
	private Set<VisualPropertyEditor> displayers;

	private final Map<String, PropertyEditor> comboBoxEditors;

	/**
	 * Creates a new EditorFactory object.
	 */
	public EditorManagerImpl() {
		displayers = new HashSet<VisualPropertyEditor>();
		comboBoxEditors = new HashMap<String, PropertyEditor>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#addEditorDisplayer(org
	 * .cytoscape.vizmap.gui.editors.EditorDisplayer, java.util.Map)
	 */
	public void addEditorDisplayer(VisualPropertyEditor ed, Map properties) {
		displayers.add(ed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#removeEditorDisplayer(
	 * org.cytoscape.vizmap.gui.editors.EditorDisplayer, java.util.Map)
	 */
	public void removeEditorDisplayer(VisualPropertyEditor ed, Map properties) {
		displayers.remove(ed);
	}

	private VisualPropertyEditor findEditor(VisualProperty type,
			VisualPropertyEditor.MappingType edType) {
		final Class<?> dataType = type.getType();

		for (VisualPropertyEditor disp : displayers)
			if ((dataType == disp.getDataType())
					&& (edType == disp.getEditorType()))
				return disp;

		throw new NullPointerException("no editor displayer found for: "
				+ type.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#showDiscreteEditor(java
	 * .awt.Component, org.cytoscape.viewmodel.VisualProperty)
	 */
	public Object showDiscreteEditor(Component parentComponent,
			VisualProperty type) throws Exception {
		return findEditor(type, EditorDisplayer.MappingType.VisualPropertyEditor).showContinuousMappingEditor(
				parentComponent, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#showContinuousEditor(java
	 * .awt.Component, org.cytoscape.viewmodel.VisualProperty)
	 */
	public Object showContinuousEditor(Component parentComponent,
			VisualProperty type) throws Exception {
		return findEditor(type, EditorDisplayer.MappingType.VisualPropertyEditor).showContinuousMappingEditor(
				parentComponent, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.vizmap.gui.editors.EditorFactory#getCellEditors()
	 */
	public List<PropertyEditor> getCellEditors() {
		List<PropertyEditor> ret = new ArrayList<PropertyEditor>();

		for (VisualPropertyEditor disp : displayers)
			ret.add(disp.getVisualPropertyEditor());

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getDiscreteCellEditor(
	 * org.cytoscape.viewmodel.VisualProperty)
	 */
	public PropertyEditor getDiscreteCellEditor(VisualProperty type) {
		return findEditor(type, EditorDisplayer.MappingType.VisualPropertyEditor).getVisualPropertyEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getDiscreteCellRenderer
	 * (org.cytoscape.viewmodel.VisualProperty)
	 */
	public TableCellRenderer getDiscreteCellRenderer(VisualProperty type) {
		return findEditor(type, EditorDisplayer.MappingType.VisualPropertyEditor).getCellRenderer(
				type, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getContinuousCellEditor
	 * (org.cytoscape.viewmodel.VisualProperty)
	 */
	public PropertyEditor getContinuousCellEditor(VisualProperty type) {
		return findEditor(type, EditorDisplayer.MappingType.VisualPropertyEditor)
				.getVisualPropertyEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.editors.EditorFactory#getContinuousCellRenderer
	 * (org.cytoscape.viewmodel.VisualProperty, int, int)
	 */
	public TableCellRenderer getContinuousCellRenderer(VisualProperty type,
			int w, int h) {
		return findEditor(type, EditorDisplayer.MappingType.VisualPropertyEditor)
				.getCellRenderer(type, w, h);
	}

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
}
