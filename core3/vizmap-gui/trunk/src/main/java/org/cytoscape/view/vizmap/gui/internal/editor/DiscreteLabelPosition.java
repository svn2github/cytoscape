
package org.cytoscape.view.vizmap.gui.internal.editor;


import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.ding.vizmap.CyLabelPositionPropertyEditor;
import org.cytoscape.ding.vizmap.LabelPositionCellRenderer;
import org.cytoscape.ding.vizmap.LabelPositionChooser;
import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.viewmodel.VisualProperty;


public class DiscreteLabelPosition implements VisualPropertyEditor {

	private final LabelPositionCellRenderer labelPositionRenderer; 
	private final CyLabelPositionPropertyEditor labelPositionEditor;

	public DiscreteLabelPosition() {
		labelPositionRenderer = new LabelPositionCellRenderer();
		labelPositionEditor = new CyLabelPositionPropertyEditor();
	}

	public Class<?> getDataType() {
		return LabelPosition.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		labelPositionEditor.setParentComponent(parentComponent);
		return LabelPositionChooser.showDialog(parentComponent, null);
	}

    public PropertyEditor getVisualPropertyEditor() {
		return labelPositionEditor;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return labelPositionRenderer;
    }
}
