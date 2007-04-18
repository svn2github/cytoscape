package cytoscape.visual.ui;

import cytoscape.Cytoscape;

import cytoscape.visual.NodeShape;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualPropertyType;
import static cytoscape.visual.VisualPropertyType.*;
import static cytoscape.visual.VisualPropertyType.NODE_FILL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_FACE;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_HEIGHT;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_POSITION;
import static cytoscape.visual.VisualPropertyType.NODE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.NODE_SHAPE;
import static cytoscape.visual.VisualPropertyType.NODE_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_WIDTH;

import cytoscape.visual.ui.icon.NodeFullDetailView;
import cytoscape.visual.ui.icon.NodeIcon;
import cytoscape.visual.ui.icon.VisualPropertyIconFactory;

import java.awt.Frame;
import java.awt.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 *
 * @author kono
 */
public class DefaultNodeAppearenceEditorDialog
    extends DefaultAppearenceEditorDialog {
    private static final VisualPropertyType[] orderedList = {
            NODE_SHAPE, NODE_FILL_COLOR, NODE_WIDTH, NODE_HEIGHT,
            NODE_BORDER_COLOR, NODE_LINE_WIDTH, NODE_LINETYPE, NODE_LABEL_COLOR,
            NODE_FONT_FACE, NODE_FONT_SIZE, NODE_LABEL, NODE_LABEL_POSITION,
            NODE_TOOLTIP
        };
    private Shape objectShape;
    private Map<VisualPropertyType, Object> appearenceMap;

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static JPanel showDialog(Frame parent) {
        final DefaultNodeAppearenceEditorDialog dialog = new DefaultNodeAppearenceEditorDialog(parent);
        dialog.setVisible(true);

        return dialog.getPanel();
    }

    /** Creates new form DefaultAppearenceDialog */
    private DefaultNodeAppearenceEditorDialog(Frame parent) {
        super(orderedList, parent);
        buildList();
    }

    /**
     * DOCUMENT ME!
     */
    public void buildList() {
        /*
         * Get current default node appearence
         */
        appearenceMap = new HashMap<VisualPropertyType, Object>();

        List<Icon> icons = new ArrayList<Icon>();
        DefaultListModel model = new DefaultListModel();
        jXList1.setModel(model);

        byte realizerShape = (Byte) VizUIUtilities.getDefault(
                vmm.getVisualStyle(),
                VisualPropertyType.NODE_SHAPE);

        NodeShape defShape = ShapeNodeRealizer.getNodeShape(realizerShape);

        Map<VisualPropertyType, Icon> dynIcon = VisualPropertyIconFactory.getDynamicNodeIcons(defShape);

        for (VisualPropertyType type : orderedList) {
            final NodeIcon nodeIcon = (NodeIcon) dynIcon.get(type);
            nodeIcon.setLeftPadding(15);
            nodeIcon.setPropertyType(type);
            nodeIcon.setValue(
                VizUIUtilities.getDefault(
                    vmm.getVisualStyle(),
                    type));
            model.addElement(type.getName());
            icons.add(nodeIcon);
            //
            appearenceMap.put(
                type,
                VizUIUtilities.getDefault(
                    vmm.getVisualStyle(),
                    type));
        }

        jXList1.setCellRenderer(new VisualPropCellRenderer(icons));

        objectShape = ((NodeIcon) dynIcon.get(VisualPropertyType.NODE_SHAPE)).getShape();

        jXPanel2.setShape(objectShape);
        jXPanel2.setAppearence(appearenceMap);

        jXPanel2.createView();

        jXPanel2.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static JPanel getDefaultPanel() {
        final NodeShape defShape = (NodeShape) VizUIUtilities.getDefault(
                Cytoscape.getVisualMappingManager().getVisualStyle(),
                VisualPropertyType.NODE_SHAPE);
        final Map<VisualPropertyType, Icon> dynIcon = VisualPropertyIconFactory.getDynamicNodeIcons(defShape);
        final Map<VisualPropertyType, Object> defAppearenceMap = new HashMap<VisualPropertyType, Object>();
        final Shape shape;

        for (VisualPropertyType type : orderedList)
            defAppearenceMap.put(
                type,
                VizUIUtilities.getDefault(
                    Cytoscape.getVisualMappingManager().getVisualStyle(),
                    type));

        shape = ((NodeIcon) dynIcon.get(VisualPropertyType.NODE_SHAPE)).getShape();

        return new NodeFullDetailView(defAppearenceMap, shape);
    }

    private JPanel getPanel() {
        return jXPanel2;
    }
}
