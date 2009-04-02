
package org.cytoscape.view.vizmap.gui.internal.editors;


import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.CyFontPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.FontCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.PopupFontChooser;
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

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualProperty type) {
		return PopupFontChooser.showDialog(parentComponent, null);
	}

    public PropertyEditor getCellEditor() {
		return fontCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return fontCellRenderer;
    }


}
