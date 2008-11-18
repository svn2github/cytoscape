
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import java.awt.Color;
import cytoscape.visual.ui.editors.continuous.GradientEditorPanel; 
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel; 
import org.cytoscape.vizmap.VisualPropertyType;
import java.beans.PropertyEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class ContinuousColor implements EditorDisplayer {

	private final DefaultTableCellRenderer gradientRenderer; 
	public ContinuousColor() { 
		gradientRenderer = new DefaultTableCellRenderer();
	}

	public Class<?> getDataType() {
		return Color.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return GradientEditorPanel.showDialog(450, 180,"Gradient Editor",type);
	}

    public PropertyEditor getCellEditor() {
		return null;
	}

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		gradientRenderer.setIcon(ContinuousMappingEditorPanel.getIcon(width,height, type));
		return gradientRenderer;
	}
}
