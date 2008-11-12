
package cytoscape.visual.ui.editors.internal;


import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import java.awt.Color;
import cytoscape.visual.ui.editors.continuous.GradientEditorPanel; 
import org.cytoscape.vizmap.VisualPropertyType;

public class ContinuousColor implements EditorDisplayer {

	public ContinuousColor() { }

	public Class<?> getDataType() {
		return Color.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return GradientEditorPanel.showDialog(450, 180,"Gradient Editor",null);
	}
}
