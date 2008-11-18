
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor; 
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel; 
import org.cytoscape.vizmap.VisualPropertyType;
import java.beans.PropertyEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class ContinuousContinuous implements EditorDisplayer {

	private final DefaultTableCellRenderer continuousRenderer;
	public ContinuousContinuous() { 
		continuousRenderer = new DefaultTableCellRenderer();	
	}

	public Class<?> getDataType() {
		return Number.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return C2CMappingEditor.showDialog(450, 350,"Continuous Editor for " + type.getName(),null);
	}

    public PropertyEditor getCellEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		continuousRenderer.setIcon(ContinuousMappingEditorPanel.getIcon(width,height, type));
		return continuousRenderer;
    }
}
