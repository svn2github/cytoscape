
package org.cytoscape.view.vizmap.gui.internal.editor;


import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.cellrenderer.FontCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor.C2DMappingEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyFontPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.valueeditor.FontEditor;


public class FontVisualPropertyEditor extends AbstractVisualPropertyEditor<Font> {

	public FontVisualPropertyEditor(VisualProperty<Font> vp) {
		super(vp);
		this.propertyEditor= new CyFontPropertyEditor();
		this.tableCellRenderer = new FontCellRenderer();
		this.continuousEditor = new C2DMappingEditor(this.vp);
	}

	public Class<?> getDataType() {
		return Font.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
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
