
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import java.awt.Color;
import cytoscape.visual.ui.editors.continuous.GradientEditorPanel; 
import org.cytoscape.vizmap.VisualPropertyType;
import java.beans.PropertyEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class ContinuousColor implements EditorDisplayer {

	private final DefaultTableCellRenderer gradientRenderer; 
	private final CytoscapeDesktop desk; 
	public ContinuousColor(final CytoscapeDesktop desk) { 
		gradientRenderer = new DefaultTableCellRenderer();
		this.desk = desk;
	}

	public Class<?> getDataType() {
		return Color.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return GradientEditorPanel.showDialog(450, 180,"Gradient Editor",type,desk);
	}

    public PropertyEditor getCellEditor() {
		return null;
	}

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		gradientRenderer.setIcon(GradientEditorPanel.getIcon(width,height, type, desk));
		return gradientRenderer;
	}
}
