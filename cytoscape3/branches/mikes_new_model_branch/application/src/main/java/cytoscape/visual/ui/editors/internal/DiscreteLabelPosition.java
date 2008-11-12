
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.PopupLabelPositionChooser;
import org.cytoscape.vizmap.LabelPosition;

public class DiscreteLabelPosition implements EditorDisplayer {

	private CytoscapeDesktop desk;

	public DiscreteLabelPosition(CytoscapeDesktop desk) {
		this.desk = desk;
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
}
