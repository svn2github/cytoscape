
package org.cytoscape.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.continuous.C2CMappingEditor;

import cytoscape.CyOperatingContext;


public class ContinuousContinuous implements EditorDisplayer {

	private final DefaultTableCellRenderer continuousRenderer;
	
	private CyOperatingContext context;
	
	public ContinuousContinuous(CyOperatingContext context) {
		this.context = context;
		continuousRenderer = new DefaultTableCellRenderer();	
	}

	public Class<?> getDataType() {
		return Number.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(Component parentComponent, VisualPropertyType type) {
		return C2CMappingEditor.showDialog(450, 350,"Continuous Editor for " + type.getName(),type, parentComponent, context);
	}

    public PropertyEditor getCellEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		continuousRenderer.setIcon(C2CMappingEditor.getIcon(width,height, type, context));
		return continuousRenderer;
    }
}
