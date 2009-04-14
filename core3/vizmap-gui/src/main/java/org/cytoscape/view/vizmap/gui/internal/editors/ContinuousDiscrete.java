
package org.cytoscape.view.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.editors.EditorManager;
import org.cytoscape.view.vizmap.gui.internal.editors.continuous.C2DMappingEditor;
import org.cytoscape.viewmodel.VisualProperty;


public class ContinuousDiscrete implements EditorDisplayer {

	private final EditorManager ef;
	private final DefaultTableCellRenderer cellRenderer; 

	public ContinuousDiscrete(final EditorManager ef) { 
		this.ef = ef;
		this.cellRenderer = new DefaultTableCellRenderer();
	}

	public Class<?> getDataType() {
		return Object.class;
	}

	public EditorDisplayer.MappingType getEditorType() {
		return EditorDisplayer.MappingType.CONTINUOUS;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return C2DMappingEditor.showDialog(450, 300,"Continuous Editor for " + type.getName(),null,parentComponent,ef);
	}

    public PropertyEditor getVisualPropertyEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		cellRenderer.setIcon(C2DMappingEditor.getIcon(width,height,type));
		return cellRenderer;
    }
}
