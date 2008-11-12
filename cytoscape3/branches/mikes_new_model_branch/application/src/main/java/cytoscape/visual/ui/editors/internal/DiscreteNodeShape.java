
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.ValueSelectDialog;
import org.cytoscape.vizmap.VisualPropertyType;
import javax.swing.JOptionPane;
import org.cytoscape.vizmap.NodeShape;

public class DiscreteNodeShape implements EditorDisplayer {

	public DiscreteNodeShape() { }

	public Class<?> getDataType() {
		return NodeShape.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		// TODO why is the second arg null?
		return ValueSelectDialog.showDialog(VisualPropertyType.NODE_SHAPE,null);
	}
}
