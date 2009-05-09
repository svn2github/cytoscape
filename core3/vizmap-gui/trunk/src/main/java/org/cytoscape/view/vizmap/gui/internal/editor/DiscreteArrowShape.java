
package org.cytoscape.view.vizmap.gui.internal.editor;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.editor.VisualPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;
import org.cytoscape.view.vizmap.gui.internal.editor.valueeditor.DiscreteValueChooser;


public class DiscreteArrowShape implements VisualPropertyEditor {

    private final CyComboBoxPropertyEditor arrowCellEditor; 
    private final ShapeCellRenderer arrowShapeCellRenderer;

	public DiscreteArrowShape() { 
    	arrowCellEditor = new CyComboBoxPropertyEditor();
    	arrowShapeCellRenderer = new ShapeCellRenderer(VisualProperty.EDGE_TGTARROW_SHAPE);
	}

	public Class<?> getDataType() {
		return ArrowShape.class;
	}

	public VisualPropertyEditor.MappingType getEditorType() {
		return EditorDisplayer.MappingType.VisualPropertyEditor;
	}

	public Object showContinuousMappingEditor(Component parentComponent, VisualProperty type) {
		return DiscreteValueChooser.showDialog(parentComponent, VisualProperty.EDGE_SRCARROW_SHAPE);
	}

    public PropertyEditor getVisualPropertyEditor() {
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

    public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return arrowShapeCellRenderer;	
    }


}
