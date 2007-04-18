package cytoscape.visual.ui.icon;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineTypeDef;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import ding.view.DGraphView;


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

    /*
     * Shapes from rendering engine.
     */
    final static Map<Byte, Shape> nodeShapes;
    final static Map<Byte, Shape> arrowShapes;

    /*
     * Default icon size.
     */
    private static final int DEF_ICON_HEIGHT = 32;

    static {
        nodeShapes = DGraphView.getNodeShapes();
        arrowShapes = DGraphView.getArrowShapes();

        nodeShapeIcons = new HashMap<NodeShape, Icon>();
        arrowShapeIcons = new HashMap<ArrowShape, Icon>();
        lineTypeIcons = new HashMap<LineTypeDef, Icon>();
        buildAllIcons(DEF_ICON_HEIGHT);
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
        return getIconSet(type, DEF_ICON_HEIGHT);
    }

    /**
     * Return Map of icons for the specified visual property type.
     *
     * @param type DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Map getIconSet(final VisualPropertyType type, final int size) {
        switch (type) {
        case NODE_SHAPE:
            buildNodeIcons(size);

            return nodeShapeIcons;

        case EDGE_SRCARROW_SHAPE:
        case EDGE_TGTARROW_SHAPE:
            buildArrowIcons(size);

            return arrowShapeIcons;

        case NODE_LINETYPE:
        case EDGE_LINETYPE:
            buildLineIcons(size);

            return lineTypeIcons;

        default:
            break;
        }

        return null;
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

    private static void buildAllIcons(final int size) {
        /*
         * Build node shape icons
         */
        buildNodeIcons(size);

        /*
         * Build arrow shape icons
         */
        buildArrowIcons(size);

        /*
         * Line icons
         */
        buildLineIcons(size);
    }

    private static void buildNodeIcons(final int size) {
        String name;
        VisualPropertyIcon icon;
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
    }

    private static void buildArrowIcons(final int size) {
        String name;
        VisualPropertyIcon icon;

        //	 First, need to create icon for no arrow head
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
    }

    private static void buildLineIcons(final int size) {
        String name;
        VisualPropertyIcon icon;

        for (LineTypeDef def : LineTypeDef.values()) {
            final BasicStroke lineStroke = (BasicStroke) def.getStroke(5.0f);
            name = def.name();
            icon = new LineTypeIcon(lineStroke, size * 4, size, name);
            lineTypeIcons.put(def, icon);
        }
    }
}
