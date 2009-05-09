
package org.cytoscape.view.vizmap.gui.internal.editor;


import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.CyColorChooser;
import org.cytoscape.view.vizmap.gui.internal.cellrenderer.CyColorCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyColorPropertyEditor;



public class DiscreteColor implements VisualPropertyEditor {

	private final CyColorCellRenderer colorCellRenderer;
	private final CyColorPropertyEditor colorCellEditor;

	public DiscreteColor() {
		this.colorCellRenderer = new CyColorCellRenderer();
		this.colorCellEditor = new CyColorPropertyEditor();
	}

	public Class<?> getDataType() {
		return Color.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
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
