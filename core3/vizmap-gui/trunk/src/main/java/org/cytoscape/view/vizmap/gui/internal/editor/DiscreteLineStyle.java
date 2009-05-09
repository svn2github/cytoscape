
package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.valueeditor.DiscreteValueChooser;


public class DiscreteLineStyle implements VisualPropertyEditor {

    private final ShapeCellRenderer lineCellRenderer; 
    private final CyComboBoxPropertyEditor lineCellEditor; 

	public DiscreteLineStyle() { 
    	lineCellRenderer = new ShapeCellRenderer( VisualProperty.EDGE_LINE_STYLE);
    	lineCellEditor = new CyComboBoxPropertyEditor();
	}

	public Class<?> getDataType() {
		return LineStyle.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return DiscreteValueChooser.showDialog(parentComponent, VisualProperty.EDGE_LINE_STYLE);
	}

    public PropertyEditor getVisualPropertyEditor() {
		// probably better to do this dynamically
        Icon[] iconArray = new Icon[LineStyle.getIconSet().values().size()];
		int i = 0;
        for ( Icon newIcon : LineStyle.getIconSet().values()) {
            ((VisualPropertyIcon)newIcon).setIconHeight(16);
            ((VisualPropertyIcon)newIcon).setIconWidth(16);
            iconArray[i++] = newIcon;
		}

		lineCellEditor.setAvailableValues(LineStyle.getIconSet().keySet().toArray());
        lineCellEditor.setAvailableIcons(iconArray);
		return lineCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return lineCellRenderer;	
    }
}
