
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.PopupFontChooser;
import java.awt.Font;

public class DiscreteFont implements EditorDisplayer {

	private CytoscapeDesktop desk;

	public DiscreteFont(CytoscapeDesktop desk) {
		this.desk = desk;
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
}
