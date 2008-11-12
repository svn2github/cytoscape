
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.ValueSelectDialog;
import javax.swing.JOptionPane;
import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.VisualPropertyType;

public class DiscreteArrowShape implements EditorDisplayer {


	public DiscreteArrowShape() { }

	public Class<?> getDataType() {
		return ArrowShape.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		// TODO why is the second arg null?
		return ValueSelectDialog.showDialog(VisualPropertyType.EDGE_SRCARROW_SHAPE,null);
	}
}
