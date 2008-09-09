package ding.view;

import static cytoscape.render.immed.GraphGraphics.ARROW_DELTA;
import static cytoscape.render.immed.GraphGraphics.ARROW_DIAMOND;
import static cytoscape.render.immed.GraphGraphics.ARROW_DISC;
import static cytoscape.render.immed.GraphGraphics.ARROW_NONE;
import static cytoscape.render.immed.GraphGraphics.ARROW_TEE;
import static cytoscape.render.immed.GraphGraphics.SHAPE_DIAMOND;
import static cytoscape.render.immed.GraphGraphics.SHAPE_ELLIPSE;
import static cytoscape.render.immed.GraphGraphics.SHAPE_HEXAGON;
import static cytoscape.render.immed.GraphGraphics.SHAPE_OCTAGON;
import static cytoscape.render.immed.GraphGraphics.SHAPE_PARALLELOGRAM;
import static cytoscape.render.immed.GraphGraphics.SHAPE_RECTANGLE;
import static cytoscape.render.immed.GraphGraphics.SHAPE_ROUNDED_RECTANGLE;
import static cytoscape.render.immed.GraphGraphics.SHAPE_TRIANGLE;
import static cytoscape.render.immed.GraphGraphics.SHAPE_VEE;
import static giny.view.EdgeView.EDGE_COLOR_CIRCLE;
import static giny.view.EdgeView.EDGE_COLOR_DELTA;
import static giny.view.EdgeView.EDGE_COLOR_DIAMOND;
import static giny.view.EdgeView.EDGE_COLOR_T;
import static giny.view.EdgeView.NO_END;
import static giny.view.NodeView.DIAMOND;
import static giny.view.NodeView.ELLIPSE;
import static giny.view.NodeView.HEXAGON;
import static giny.view.NodeView.OCTAGON;
import static giny.view.NodeView.PARALELLOGRAM;
import static giny.view.NodeView.RECTANGLE;
import static giny.view.NodeView.ROUNDED_RECTANGLE;
import static giny.view.NodeView.TRIANGLE;

/**
 *
 * Convert bytes defined in rendering engine into Giny types.
 *
 * @version 0.7
 * @since Cytoscape 2.5
 * @author kono
 *
 */
class GinyUtil {
    static int getGinyNodeType(final byte type) {
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

    static byte getNativeNodeType(final int ginyType) {
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

    static int getGinyArrowType(final byte type) {
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

    static byte getNativeArrowType(final int ginyType) {
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
