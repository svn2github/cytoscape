
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.PopupLabelPositionChooser;
import cytoscape.visual.ui.editors.discrete.LabelPositionCellRenderer;
import cytoscape.visual.ui.editors.discrete.CyLabelPositionPropertyEditor;
import org.cytoscape.vizmap.LabelPosition;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;


public class DiscreteLabelPosition implements EditorDisplayer {

	private final CytoscapeDesktop desk;
	private final LabelPositionCellRenderer labelPositionRenderer; 
	private final CyLabelPositionPropertyEditor labelPositionEditor;

	public DiscreteLabelPosition(final CytoscapeDesktop desk) {
		this.desk = desk;
		labelPositionRenderer = new LabelPositionCellRenderer();
		labelPositionEditor = new CyLabelPositionPropertyEditor(desk);
	}

	public Class<?> getDataType() {
		return LabelPosition.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return PopupLabelPositionChooser.showDialog(desk,null);
	}

    public PropertyEditor getCellEditor() {
		return labelPositionEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return labelPositionRenderer;
    }
}
