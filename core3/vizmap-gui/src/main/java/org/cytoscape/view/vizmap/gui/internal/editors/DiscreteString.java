
package org.cytoscape.view.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyStringPropertyEditor;
import org.cytoscape.view.model.VisualProperty;


public class DiscreteString implements EditorDisplayer {

	private final CyStringPropertyEditor stringCellEditor; 
	private final DefaultTableCellRenderer stringCellRenderer; 

	public DiscreteString() {
		stringCellEditor = new CyStringPropertyEditor();
		stringCellRenderer = new DefaultTableCellRenderer(); 
	}

	public Class<?> getDataType() {
		return String.class;
	}

	public EditorDisplayer.MappingType getEditorType() {
		return EditorDisplayer.MappingType.DISCRETE;
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
