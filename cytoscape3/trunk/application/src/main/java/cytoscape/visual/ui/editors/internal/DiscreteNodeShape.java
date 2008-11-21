
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.ValueSelectDialog;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;
import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.vizmap.VisualPropertyType;
import javax.swing.JOptionPane;
import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.vizmap.icon.NodeIcon;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.Icon;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


public class DiscreteNodeShape implements EditorDisplayer {

	private final ShapeCellRenderer shapeCellRenderer; 
	private final CyComboBoxPropertyEditor shapeCellEditor; 
	private final CytoscapeDesktop desk; 

	public DiscreteNodeShape(final CytoscapeDesktop desk) { 
		this.desk = desk;
		shapeCellRenderer = new ShapeCellRenderer(VisualPropertyType.NODE_SHAPE);
		shapeCellEditor = new CyComboBoxPropertyEditor();
	}

	public Class<?> getDataType() {
		return NodeShape.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(VisualPropertyType type) {
		return ValueSelectDialog.showDialog(VisualPropertyType.NODE_SHAPE,desk);
	}

    public PropertyEditor getCellEditor() {

		// TODO  this is the best we can do?

        final List<Icon> iconList = new ArrayList<Icon>();
        final List<NodeShape> nodeShapes = new ArrayList<NodeShape>();
		
		Map<Object,Icon> nodeShapeIcons = NodeShape.getIconSet();
        for (Object key : nodeShapeIcons.keySet()) {
            NodeShape shape = (NodeShape) key;

            if (shape.isSupported()) {
                iconList.add(nodeShapeIcons.get(key));
                nodeShapes.add(shape);
            }
        }

        Icon[] iconArray = new Icon[iconList.size()];

        for (int i = 0; i < iconArray.length; i++) {
            NodeIcon newIcon = ((NodeIcon) iconList.get(i)).clone();
            newIcon.setIconHeight(16);
            newIcon.setIconWidth(16);
            iconArray[i] = newIcon;
        }

        shapeCellEditor.setAvailableValues(nodeShapes.toArray());
        shapeCellEditor.setAvailableIcons(iconArray);
		
		return shapeCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return shapeCellRenderer;	
    }


}
