
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import java.awt.Color;
import cytoscape.util.CyColorChooser;
import org.cytoscape.vizmap.VisualPropertyType;

public class DiscreteColor implements EditorDisplayer {

	private CytoscapeDesktop desk;

	public DiscreteColor(CytoscapeDesktop desk) {
		this.desk = desk;
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
}
