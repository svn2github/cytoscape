
package cytoscape.visual.ui.editors.internal;

import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.CyDoublePropertyEditor;
import javax.swing.JOptionPane;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;


public class DiscreteNumber implements EditorDisplayer {

	private final CytoscapeDesktop desk;
	private final CyDoublePropertyEditor numberCellEditor; 
	private final DefaultTableCellRenderer numberCellRenderer; 

	public DiscreteNumber(CytoscapeDesktop desk) {
		this.desk = desk;
		// TODO that arg should be a VizMapperMainPanel
		numberCellEditor = new CyDoublePropertyEditor(null);
		numberCellRenderer = new DefaultTableCellRenderer();
	}

	public Class<?> getDataType() {
		return Number.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return JOptionPane.showInputDialog(desk,"Please enter a new numeric value:");
	}

    public PropertyEditor getCellEditor() {
		return numberCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return numberCellRenderer;
    }


}
