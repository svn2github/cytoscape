
package cytoscape.visual.ui.editors;

import org.cytoscape.vizmap.VisualPropertyType;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;

public interface EditorDisplayer {

	public enum Type {
		CONTINUOUS,
		DISCRETE,
		PASSTHROUGH,
	}

	public Class<?> getDataType();

	public Type getEditorType();

	public Object showEditor(VisualPropertyType type);

	public PropertyEditor getCellEditor();

	public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height);
}
