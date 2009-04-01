
package org.cytoscape.vizmap.gui.internal.editors;


import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyColorCellRenderer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyColorPropertyEditor;

import org.cytoscape.vizmap.gui.internal.CyColorChooser;


public class DiscreteColor implements EditorDisplayer {

	private final CyColorCellRenderer colorCellRenderer;
	private final CyColorPropertyEditor colorCellEditor;

	public DiscreteColor() {
		this.colorCellRenderer = new CyColorCellRenderer();
		this.colorCellEditor = new CyColorPropertyEditor();
	}

	public Class<?> getDataType() {
		return Color.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualProperty type) {
		return CyColorChooser.showDialog(parentComponent, "Select Color...",null);
	}

    public PropertyEditor getCellEditor() {
		return colorCellEditor;	
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
   		colorCellRenderer.setForeground(Color.DARK_GRAY);
		colorCellRenderer.setOddBackgroundColor(new Color(150, 150, 150, 20));
		colorCellRenderer.setEvenBackgroundColor(Color.white);
		return colorCellRenderer;
    }
}
