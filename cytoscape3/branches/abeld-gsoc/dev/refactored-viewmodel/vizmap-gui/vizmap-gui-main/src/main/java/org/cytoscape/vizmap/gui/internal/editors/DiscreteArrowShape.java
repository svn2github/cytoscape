
package org.cytoscape.vizmap.gui.internal.editors;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyComboBoxPropertyEditor;
import org.cytoscape.vizmap.gui.internal.editors.discrete.ShapeCellRenderer;
import org.cytoscape.vizmap.gui.internal.editors.discrete.ValueSelectDialog;
import org.cytoscape.vizmap.icon.ArrowIcon;


public class DiscreteArrowShape implements EditorDisplayer {

    private final CyComboBoxPropertyEditor arrowCellEditor; 
    private final ShapeCellRenderer arrowShapeCellRenderer;

	public DiscreteArrowShape() { 
    	arrowCellEditor = new CyComboBoxPropertyEditor();
    	arrowShapeCellRenderer = new ShapeCellRenderer(VisualPropertyType.EDGE_TGTARROW_SHAPE);
	}

	public Class<?> getDataType() {
		return ArrowShape.class;
	}

	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	public Object showEditor(Component parentComponent, VisualPropertyType type) {
		return ValueSelectDialog.showDialog(parentComponent, VisualPropertyType.EDGE_SRCARROW_SHAPE);
	}

    public PropertyEditor getCellEditor() {
		List<Icon> iconList = new ArrayList<Icon>();
        iconList.addAll(ArrowShape.getIconSet().values());
        Icon[] iconArray = new Icon[iconList.size()];

        Set arrowShapes = ArrowShape.getIconSet().keySet();

        for (int i = 0; i < iconArray.length; i++) {
            ArrowIcon newIcon = ((ArrowIcon) iconList.get(i));
            newIcon.setIconHeight(16);
            newIcon.setIconWidth(40);
            newIcon.setBottomPadding(-9);
            iconArray[i] = newIcon;
        }

        arrowCellEditor.setAvailableValues(arrowShapes.toArray());
        arrowCellEditor.setAvailableIcons(iconArray);
		
		return arrowCellEditor;
    }

    public TableCellRenderer getCellRenderer(VisualPropertyType type, int width, int height) {
		return arrowShapeCellRenderer;	
    }


}
