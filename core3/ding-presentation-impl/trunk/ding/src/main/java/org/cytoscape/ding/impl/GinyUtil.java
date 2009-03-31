package org.cytoscape.ding.impl;

import static cytoscape.render.immed.GraphGraphics.*;
import static org.cytoscape.ding.EdgeView.*;
import static org.cytoscape.ding.NodeView.*;

/**
 *
 * Convert bytes defined in rendering engine into Giny types.
 *
 * @version 0.7
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public class GinyUtil {
    public static int getGinyNodeType(final byte type) {
        switch (type) {
        case SHAPE_RECTANGLE:
            return RECTANGLE;

        case SHAPE_DIAMOND:
            return DIAMOND;

        case SHAPE_ELLIPSE:
            return ELLIPSE;

        case SHAPE_HEXAGON:
            return HEXAGON;

        case SHAPE_OCTAGON:
            return OCTAGON;

        case SHAPE_PARALLELOGRAM:
            return PARALELLOGRAM;

        case SHAPE_ROUNDED_RECTANGLE:
            return ROUNDED_RECTANGLE;

        case SHAPE_TRIANGLE:
            return TRIANGLE;

        case SHAPE_VEE:

            // Not implemented yet.
            return RECTANGLE;

        default:
            return TRIANGLE;
        }
    }

    public static byte getNativeNodeType(final int ginyType) {
        switch (ginyType) {
        case RECTANGLE:
        	return SHAPE_RECTANGLE;

        case DIAMOND:
        	return SHAPE_DIAMOND;

        case ELLIPSE:
        	return SHAPE_ELLIPSE;

        case HEXAGON:
        	return SHAPE_HEXAGON;

        case OCTAGON:
        	return SHAPE_OCTAGON;

        case PARALELLOGRAM:
        	return SHAPE_PARALLELOGRAM;

        case ROUNDED_RECTANGLE:
        	return SHAPE_ROUNDED_RECTANGLE;

        case TRIANGLE:
        	return SHAPE_TRIANGLE;

        default:
        	return -1;
        }
    }

    public static int getGinyArrowType(final byte type) {
        switch (type) {
        case ARROW_NONE:
            return NO_END;

        case ARROW_DELTA:
            return EDGE_COLOR_DELTA;

        case ARROW_DIAMOND:
            return EDGE_COLOR_DIAMOND;

        case ARROW_DISC:
            return EDGE_COLOR_CIRCLE;

        case ARROW_TEE:
            return EDGE_COLOR_T;

        default:
            return NO_END;
        }
    }

    public static byte getNativeArrowType(final int ginyType) {
        switch (ginyType) {
        case NO_END:
        	return ARROW_NONE;

        case EDGE_COLOR_DELTA:
        	return ARROW_DELTA;

        case EDGE_COLOR_DIAMOND:
        	return ARROW_DIAMOND;

        case EDGE_COLOR_CIRCLE:
        	return ARROW_DISC;

        case EDGE_COLOR_T:
        	return ARROW_TEE;

        default:
            return ARROW_NONE;
        }
    }
}
