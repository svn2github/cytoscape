package org.cytoscape.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyComboBoxPropertyEditor;

public class DefaultSelectableValues implements EditorDisplayer {
	
	private final CyComboBoxPropertyEditor editor;
	
	public DefaultSelectableValues(String name) {
		this.editor = new CyComboBoxPropertyEditor(name);
	}

	public PropertyEditor getCellEditor() {
		return editor;
	}

	public TableCellRenderer getCellRenderer(VisualPropertyType type,
			int width, int height) {
		return null;
	}

	public Class<?> getDataType() {
		return Object.class;
	}

	public Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualPropertyType type) {
		//  not implemented yet.
		return null;
	}

}
