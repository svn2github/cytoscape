
package cytoscape.visual.ui.editors;

import org.cytoscape.vizmap.VisualPropertyType;

public interface EditorDisplayer {

	public enum Type {
		CONTINUOUS,
		DISCRETE,
		PASSTHROUGH,
	}

	public Class<?> getDataType();

	public Type getEditorType();

	public Object showEditor(VisualPropertyType type);
}
