
package cytoscape.visual.ui.editors.internal;

import cytoscape.visual.ui.editors.EditorDisplayer;
import cytoscape.visual.ui.editors.discrete.ValueSelectDialog;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;
import javax.swing.JOptionPane;
import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.icon.ArrowIcon;
import java.beans.PropertyEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.Icon;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;


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

	public Object showEditor(VisualPropertyType type) {
		// TODO why is the second arg null?
		return ValueSelectDialog.showDialog(VisualPropertyType.EDGE_SRCARROW_SHAPE,null);
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
