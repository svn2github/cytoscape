
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor; 
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.view.CytoscapeDesktop;
import java.beans.PropertyEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class ContinuousContinuous implements EditorDisplayer {

	private final DefaultTableCellRenderer continuousRenderer;
	private final CytoscapeDesktop desk;
	public ContinuousContinuous(final CytoscapeDesktop desk) { 
		this.desk = desk;
		continuousRenderer = new DefaultTableCellRenderer();	
	}

	public Class<?> getDataType() {
		return Number.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return C2CMappingEditor.showDialog(450, 350,"Continuous Editor for " + type.getName(),type,desk);
	}

    public PropertyEditor getCellEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		continuousRenderer.setIcon(C2CMappingEditor.getIcon(width,height, type, desk));
		return continuousRenderer;
    }
}
