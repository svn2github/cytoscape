
package org.cytoscape.view.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.LineStyle;
import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.CyComboBoxPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.ShapeCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.ValueSelectDialog;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.icon.VisualPropertyIcon;


public class DiscreteLineStyle implements EditorDisplayer {

    private final ShapeCellRenderer lineCellRenderer; 
    private final CyComboBoxPropertyEditor lineCellEditor; 

	public DiscreteLineStyle() { 
    	lineCellRenderer = new ShapeCellRenderer( VisualProperty.EDGE_LINE_STYLE);
    	lineCellEditor = new CyComboBoxPropertyEditor();
	}

	public Class<?> getDataType() {
		return LineStyle.class;
	}

	public EditorDisplayer.MappingType getEditorType() {
		return EditorDisplayer.MappingType.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualProperty type) {
		return ValueSelectDialog.showDialog(parentComponent, VisualProperty.EDGE_LINE_STYLE);
	}

    public PropertyEditor getCellEditor() {
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
