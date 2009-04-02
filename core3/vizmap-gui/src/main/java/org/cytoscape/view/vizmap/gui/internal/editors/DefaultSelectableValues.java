package org.cytoscape.view.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.CyComboBoxPropertyEditor;
import org.cytoscape.viewmodel.VisualProperty;

public class DefaultSelectableValues implements EditorDisplayer {
	
	private final CyComboBoxPropertyEditor editor;
	
	public DefaultSelectableValues() {
		this.editor = new CyComboBoxPropertyEditor();
	}

	public PropertyEditor getCellEditor() {
		return editor;
	}

	public TableCellRenderer getCellRenderer(VisualProperty type,
			int width, int height) {
		return null;
	}

	public Class<?> getDataType() {
		return Object.class;
	}

	public MappingType getEditorType() {
		return EditorDisplayer.MappingType.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualProperty type) {
		//  not implemented yet.
		return null;
	}

}
