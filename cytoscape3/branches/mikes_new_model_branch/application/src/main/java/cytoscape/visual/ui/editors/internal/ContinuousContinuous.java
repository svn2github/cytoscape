
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.continuous.C2CMappingEditor; 
import org.cytoscape.vizmap.VisualPropertyType;

public class ContinuousContinuous implements EditorDisplayer {

	public ContinuousContinuous() { }

	public Class<?> getDataType() {
		return Number.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return C2CMappingEditor.showDialog(450, 350,"Continuous Editor for " + type.getName(),null);
	}
}
