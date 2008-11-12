
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.ValueSelectDialog;
import org.cytoscape.vizmap.VisualPropertyType;
import javax.swing.JOptionPane;
import org.cytoscape.vizmap.LineStyle;

public class DiscreteLineStyle implements EditorDisplayer {

	public DiscreteLineStyle() { }

	public Class<?> getDataType() {
		return LineStyle.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		// TODO why is the second arg null?
		return ValueSelectDialog.showDialog(VisualPropertyType.EDGE_LINE_STYLE,null);
	}
}
