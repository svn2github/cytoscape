package cytoscape.visual.ui.icon;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineTypeDef;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

import ding.view.DGraphView;

import java.awt.BasicStroke;
import java.awt.Shape;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;


/**
 * Factory class to create icon sets.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 */
public class VisualPropertyIconFactory {
	/*
	 * Maps for storing default size icons
	 */
    private static Map<NodeShape, Icon> nodeShapeIcons;
    private static Map<ArrowShape, Icon> arrowShapeIcons;
    private static Map<LineTypeDef, Icon> lineTypeIcons;
    
    private static Map<NodeShape, Icon> nodeColorIcons;
    
    /*
     * Default icon size.
     */
    private static final int DEF_ICON_HEIGHT = 32;

    static {
        nodeShapeIcons = new HashMap<NodeShape, Icon>();
        arrowShapeIcons = new HashMap<ArrowShape, Icon>();
        lineTypeIcons = new HashMap<LineTypeDef, Icon>();
        buildIcons();
    }

    /**
     * Get set of icons for the given visual property type.
     *
     * @param type
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Map getIconSet(final VisualPropertyType type) {
        switch (type) {
        case NODE_SHAPE:
            return nodeShapeIcons;

        case EDGE_SRCARROW_SHAPE:
        case EDGE_TGTARROW_SHAPE:
            return arrowShapeIcons;

        case NODE_LINETYPE:
        case EDGE_LINETYPE:
            return lineTypeIcons;

        default:
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Map getIconSet(final VisualPropertyType type, final int size) {
        buildIcons(size);

        return getIconSet(type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param shape DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Map<VisualPropertyType, Icon> getDynamicNodeIcons(
        NodeShape shape) {
        final Map<VisualPropertyType, Icon> dynIcons = new HashMap<VisualPropertyType, Icon>();
        VisualPropertyIcon icon;

        icon = ((NodeIcon) nodeShapeIcons.get(shape)).clone();
        dynIcons.put(VisualPropertyType.NODE_SHAPE, icon);

        for (VisualPropertyType type : VisualPropertyType.values()) {
            if (type.getName()
                        .startsWith("Node") &&
                    (type != VisualPropertyType.NODE_SHAPE)) {
                icon = ((NodeIcon) icon).clone();
                dynIcons.put(type, icon);
            }
        }

        return dynIcons;
    }

    private static void buildIcons() {
        buildIcons(DEF_ICON_HEIGHT);
    }

    private static void buildIcons(final int size) {
        final Map<Byte, Shape> nodeShapes = DGraphView.getNodeShapes();
        final Map<Byte, Shape> arrowShapes = DGraphView.getArrowShapes();

        String name;
        VisualPropertyIcon icon;

        /*
         * Build node shape icons
         */
        NodeShape shapeType;

        for (Byte key : nodeShapes.keySet()) {
            shapeType = NodeShape.getNodeShape(key);
            name = shapeType.getShapeName();
            icon = new NodeIcon(
                    nodeShapes.get(key),
                    size,
                    size,
                    name);
            nodeShapeIcons.put(shapeType, icon);
        }

        /*
         * Build arrow shape icons
         */

        // First, need to create icon for no arrow head
        icon = new ArrowIcon(
                null,
                size * 3,
                size,
                ArrowShape.NONE.getName());
        arrowShapeIcons.put(ArrowShape.NONE, icon);

        ArrowShape arrowShapeType;

        for (Byte key : arrowShapes.keySet()) {
            arrowShapeType = ArrowShape.getArrowShape(key);
            name = arrowShapeType.getName();
            icon = new ArrowIcon(
                    arrowShapes.get(key),
                    size * 3,
                    size,
                    name);
            arrowShapeIcons.put(arrowShapeType, icon);
        }

        /*
         * Line icons
         */
        for (LineTypeDef def : LineTypeDef.values()) {
            final BasicStroke lineStroke = (BasicStroke) def.getStroke(5.0f);
            name = def.name();
            icon = new LineTypeIcon(lineStroke, size * 4, size, name);
            lineTypeIcons.put(def, icon);
        }
    }
}
