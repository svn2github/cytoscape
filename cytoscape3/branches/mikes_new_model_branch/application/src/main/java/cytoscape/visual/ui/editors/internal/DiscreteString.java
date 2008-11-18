
package cytoscape.visual.ui.editors.internal;

import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.CyStringPropertyEditor;
import javax.swing.JOptionPane;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;


public class DiscreteString implements EditorDisplayer {

	private final CytoscapeDesktop desk;
	private final CyStringPropertyEditor stringCellEditor; 
	private final DefaultTableCellRenderer stringCellRenderer; 

	public DiscreteString(final CytoscapeDesktop desk) {
		this.desk = desk;
		stringCellEditor = new CyStringPropertyEditor();
		stringCellRenderer = new DefaultTableCellRenderer(); 
	}

	public Class<?> getDataType() {
		return String.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return JOptionPane.showInputDialog(desk,"Please enter a new value:");
	}

    public PropertyEditor getCellEditor() {
		return stringCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return stringCellRenderer;
    }
}
