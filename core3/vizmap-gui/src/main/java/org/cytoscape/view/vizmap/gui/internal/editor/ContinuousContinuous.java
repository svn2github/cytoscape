
package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor.C2CMappingEditor;
import org.cytoscape.view.model.VisualProperty;


public class ContinuousContinuous implements VisualPropertyEditor {

	private final DefaultTableCellRenderer continuousRenderer;
	public ContinuousContinuous() { 
		continuousRenderer = new DefaultTableCellRenderer();	
	}

	public Class<?> getDataType() {
		return Number.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty<?> type) {
		return C2CMappingEditor.showDialog(450, 350,"Continuous Editor for " + type.getName(),type, parentComponent);
	}

    public PropertyEditor getVisualPropertyEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		continuousRenderer.setIcon(C2CMappingEditor.getIcon(width,height, type));
		return continuousRenderer;
    }
}
