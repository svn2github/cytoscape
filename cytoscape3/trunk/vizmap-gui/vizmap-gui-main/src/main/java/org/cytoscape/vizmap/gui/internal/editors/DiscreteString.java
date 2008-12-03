
package org.cytoscape.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyStringPropertyEditor;


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

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualPropertyType type) {
		return JOptionPane.showInputDialog(parentComponent,"Please enter a new value:");
	}

    public PropertyEditor getCellEditor() {
		return stringCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return stringCellRenderer;
    }
}
