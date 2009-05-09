package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;

public class DefaultSelectableValues implements VisualPropertyEditor {
	
	private final CyComboBoxPropertyEditor editor;
	
	public DefaultSelectableValues() {
		this.editor = new CyComboBoxPropertyEditor();
	}

	public PropertyEditor getVisualPropertyEditor() {
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
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		//  not implemented yet.
		return null;
	}

}
