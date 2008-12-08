
package org.cytoscape.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.internal.editors.continuous.C2DMappingEditor;


public class ContinuousDiscrete implements EditorDisplayer {

	private final EditorFactory ef;
	private final DefaultTableCellRenderer cellRenderer; 

	public ContinuousDiscrete(final EditorFactory ef) { 
		this.ef = ef;
		this.cellRenderer = new DefaultTableCellRenderer();
	}

	public Class<?> getDataType() {
		return Object.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(Component parentComponent, VisualPropertyType type) {
		return C2DMappingEditor.showDialog(450, 300,"Continuous Editor for " + type.getName(),null,parentComponent,ef);
	}

    public PropertyEditor getCellEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		cellRenderer.setIcon(C2DMappingEditor.getIcon(width,height,type));
		return cellRenderer;
    }
}
