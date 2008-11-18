
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.CyColorCellRenderer;
import cytoscape.visual.ui.editors.discrete.CyColorPropertyEditor;
import java.awt.Color;
import cytoscape.util.CyColorChooser;
import org.cytoscape.vizmap.VisualPropertyType;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;


public class DiscreteColor implements EditorDisplayer {

	private final CytoscapeDesktop desk;
	private final CyColorCellRenderer colorCellRenderer;
	private final CyColorPropertyEditor colorCellEditor;

	public DiscreteColor(final CytoscapeDesktop desk) {
		this.desk = desk;
		this.colorCellRenderer = new CyColorCellRenderer();
		this.colorCellEditor = new CyColorPropertyEditor();
	}

	public Class<?> getDataType() {
		return Color.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return CyColorChooser.showDialog(desk,"Select Color...",null);
	}

    public PropertyEditor getCellEditor() {
		return colorCellEditor;	
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
   		colorCellRenderer.setForeground(Color.DARK_GRAY);
		colorCellRenderer.setOddBackgroundColor(new Color(150, 150, 150, 20));
		colorCellRenderer.setEvenBackgroundColor(Color.white);
		return colorCellRenderer;
    }
}
