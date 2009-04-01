
package org.cytoscape.vizmap.gui.internal.editors;


import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyLabelPositionPropertyEditor;
import org.cytoscape.vizmap.gui.internal.editors.discrete.LabelPositionCellRenderer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.PopupLabelPositionChooser;


public class DiscreteLabelPosition implements EditorDisplayer {

	private final LabelPositionCellRenderer labelPositionRenderer; 
	private final CyLabelPositionPropertyEditor labelPositionEditor;

	public DiscreteLabelPosition() {
		labelPositionRenderer = new LabelPositionCellRenderer();
		labelPositionEditor = new CyLabelPositionPropertyEditor();
	}

	public Class<?> getDataType() {
		return LabelPosition.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualProperty type) {
		labelPositionEditor.setParentComponent(parentComponent);
		return PopupLabelPositionChooser.showDialog(parentComponent, null);
	}

    public PropertyEditor getCellEditor() {
		return labelPositionEditor;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return labelPositionRenderer;
    }
}
