
package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyStringPropertyEditor;


public class DiscreteString implements VisualPropertyEditor {

	private final CyStringPropertyEditor stringCellEditor; 
	private final DefaultTableCellRenderer stringCellRenderer; 

	public DiscreteString() {
		stringCellEditor = new CyStringPropertyEditor();
		stringCellRenderer = new DefaultTableCellRenderer(); 
	}

	public Class<?> getDataType() {
		return String.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return JOptionPane.showInputDialog(parentComponent,"Please enter a new value:");
	}

    public PropertyEditor getVisualPropertyEditor() {
		return stringCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return stringCellRenderer;
    }
}
