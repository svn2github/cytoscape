
package cytoscape.visual.ui.editors.internal;

import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import cytoscape.visual.ui.editors.EditorDisplayer;
import javax.swing.JOptionPane;

public class DiscreteString implements EditorDisplayer {

	private CytoscapeDesktop desk;

	public DiscreteString(CytoscapeDesktop desk) {
		this.desk = desk;
	}

	public Class<?> getDataType() {
		return String.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return JOptionPane.showInputDialog(desk,"Please enter a new numeric value:");
	}
}
