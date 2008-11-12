
package cytoscape.visual.ui.editors.internal;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.EditorFactory;
import cytoscape.visual.ui.editors.continuous.C2DMappingEditor; 
import org.cytoscape.vizmap.VisualPropertyType;

public class ContinuousDiscrete implements EditorDisplayer {

	private CytoscapeDesktop desktop;
	private EditorFactory ef;

	public ContinuousDiscrete(CytoscapeDesktop desktop,EditorFactory ef) { 
		this.desktop = desktop;
		this.ef = ef;
	}

	public Class<?> getDataType() {
		return Object.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.CONTINUOUS;
	}

	public Object showEditor(VisualPropertyType type) {
		return C2DMappingEditor.showDialog(450, 300,"Continuous Editor for " + type.getName(),null,desktop,ef);
	}
}
