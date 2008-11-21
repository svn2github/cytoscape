
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.PopupFontChooser;
import cytoscape.visual.ui.editors.discrete.CyFontPropertyEditor;
import cytoscape.visual.ui.editors.discrete.FontCellRenderer;
import java.awt.Font;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;


public class DiscreteFont implements EditorDisplayer {

	private final CytoscapeDesktop desk;
	private final CyFontPropertyEditor fontCellEditor;
	private final FontCellRenderer fontCellRenderer;


	public DiscreteFont(CytoscapeDesktop desk) {
		this.desk = desk;
		fontCellEditor = new CyFontPropertyEditor(desk);
		fontCellRenderer = new FontCellRenderer();
	}

	public Class<?> getDataType() {
		return Font.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return PopupFontChooser.showDialog(desk,null);
	}

    public PropertyEditor getCellEditor() {
		return fontCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return fontCellRenderer;
    }


}
