
package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.cellrenderer.ShapeCellRenderer;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.valueeditor.DiscreteValueChooser;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.icon.NodeIcon;


public class DiscreteNodeShape implements VisualPropertyEditor {

	private final ShapeCellRenderer shapeCellRenderer; 
	private final CyComboBoxPropertyEditor shapeCellEditor; 

	public DiscreteNodeShape() { 
		shapeCellRenderer = new ShapeCellRenderer(VisualProperty.NODE_SHAPE);
		shapeCellEditor = new CyComboBoxPropertyEditor();
	}

	public Class<?> getDataType() {
		return NodeShape.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return DiscreteValueChooser.showDialog(parentComponent, VisualProperty.NODE_SHAPE);
	}

    public PropertyEditor getVisualPropertyEditor() {

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

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return shapeCellRenderer;	
    }


}
