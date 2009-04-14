
package org.cytoscape.view.vizmap.gui.internal.editors;


import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.ding.vizmap.CyLabelPositionPropertyEditor;
import org.cytoscape.ding.vizmap.LabelPositionCellRenderer;
import org.cytoscape.ding.vizmap.LabelPositionChooser;
import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.viewmodel.VisualProperty;


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

	public EditorDisplayer.MappingType getEditorType() {
		return EditorDisplayer.MappingType.DISCRETE;
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
