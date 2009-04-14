
package org.cytoscape.view.vizmap.gui.internal.editors;


import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.cellrenderer.FontCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyFontPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.valueeditor.FontEditor;
import org.cytoscape.viewmodel.VisualProperty;


public class DiscreteFont implements EditorDisplayer {

	private final CyFontPropertyEditor fontCellEditor;
	private final FontCellRenderer fontCellRenderer;


	public DiscreteFont() {
		fontCellEditor = new CyFontPropertyEditor();
		fontCellRenderer = new FontCellRenderer();
	}

	public Class<?> getDataType() {
		return Font.class;
	}

	public EditorDisplayer.MappingType getEditorType() {
		return EditorDisplayer.MappingType.DISCRETE;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return FontEditor.showDialog(parentComponent, null);
	}

    public PropertyEditor getVisualPropertyEditor() {
		return fontCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return fontCellRenderer;
    }


}
