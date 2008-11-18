
package cytoscape.visual.ui.editors.internal;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.EditorFactory;
import cytoscape.visual.ui.editors.continuous.C2DMappingEditor; 
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel; 
import org.cytoscape.vizmap.VisualPropertyType;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;


public class ContinuousDiscrete implements EditorDisplayer {

	private final CytoscapeDesktop desktop;
	private final EditorFactory ef;
	private final DefaultTableCellRenderer cellRenderer; 

	public ContinuousDiscrete(final CytoscapeDesktop desktop,final EditorFactory ef) { 
		this.desktop = desktop;
		this.ef = ef;
		this.cellRenderer = new DefaultTableCellRenderer();
	}

	public Class<?> getDataType() {
		return Object.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return C2DMappingEditor.showDialog(450, 300,"Continuous Editor for " + type.getName(),null,desktop,ef);
	}

    public PropertyEditor getCellEditor() {
		return null;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		cellRenderer.setIcon(ContinuousMappingEditorPanel.getIcon(width,height,type));
		return cellRenderer;
    }
}
