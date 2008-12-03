
package org.cytoscape.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyComboBoxPropertyEditor;
import org.cytoscape.vizmap.gui.internal.editors.discrete.ShapeCellRenderer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.ValueSelectDialog;
import org.cytoscape.vizmap.icon.NodeIcon;


public class DiscreteNodeShape implements EditorDisplayer {

	private final ShapeCellRenderer shapeCellRenderer; 
	private final CyComboBoxPropertyEditor shapeCellEditor; 

	public DiscreteNodeShape() { 
		shapeCellRenderer = new ShapeCellRenderer(VisualPropertyType.NODE_SHAPE);
		shapeCellEditor = new CyComboBoxPropertyEditor();
	}

	public Class<?> getDataType() {
		return NodeShape.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualPropertyType type) {
		return ValueSelectDialog.showDialog(parentComponent, VisualPropertyType.NODE_SHAPE);
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
