
package org.cytoscape.view.vizmap.gui.internal.editors;


import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.CyColorChooser;
import org.cytoscape.view.vizmap.gui.internal.cellrenderer.CyColorCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyColorPropertyEditor;
import org.cytoscape.viewmodel.VisualProperty;



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

	public EditorDisplayer.MappingType getEditorType() {
		return EditorDisplayer.MappingType.DISCRETE;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return CyColorChooser.showDialog(parentComponent, "Select Color...",null);
	}

    public PropertyEditor getVisualPropertyEditor() {
		return colorCellEditor;	
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
   		colorCellRenderer.setForeground(Color.DARK_GRAY);
		colorCellRenderer.setOddBackgroundColor(new Color(150, 150, 150, 20));
		colorCellRenderer.setEvenBackgroundColor(Color.white);
		return colorCellRenderer;
    }
}
