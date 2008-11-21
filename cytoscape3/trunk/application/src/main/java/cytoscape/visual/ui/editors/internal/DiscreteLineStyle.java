
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.ValueSelectDialog;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;
import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import javax.swing.JOptionPane;
import org.cytoscape.vizmap.LineStyle;
import org.cytoscape.vizmap.icon.VisualPropertyIcon;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.Icon;


public class DiscreteLineStyle implements EditorDisplayer {

    private final ShapeCellRenderer lineCellRenderer; 
    private final CyComboBoxPropertyEditor lineCellEditor; 
    private final CytoscapeDesktop desk; 

	public DiscreteLineStyle(final CytoscapeDesktop desk) { 
		this.desk = desk;
    	lineCellRenderer = new ShapeCellRenderer( VisualPropertyType.EDGE_LINE_STYLE);
    	lineCellEditor = new CyComboBoxPropertyEditor();
	}

	public Class<?> getDataType() {
		return LineStyle.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return ValueSelectDialog.showDialog(VisualPropertyType.EDGE_LINE_STYLE,desk);
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

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return lineCellRenderer;	
    }
}
