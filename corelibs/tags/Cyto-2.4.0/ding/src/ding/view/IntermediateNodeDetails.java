package ding.view;

import cytoscape.render.stateful.NodeDetails;

import java.awt.Font;
import java.awt.Paint;


class IntermediateNodeDetails extends NodeDetails {
    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public byte shape(int node) {
        return DNodeView.DEFAULT_SHAPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint borderPaint(int node) {
        return DNodeView.DEFAULT_BORDER_PAINT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     * @param labelInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String labelText(int node, int labelInx) {
        return DNodeView.DEFAULT_LABEL_TEXT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     * @param labelInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Font labelFont(int node, int labelInx) {
        return DNodeView.DEFAULT_LABEL_FONT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     * @param labelInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint labelPaint(int node, int labelInx) {
        return DNodeView.DEFAULT_LABEL_PAINT;
    }
}
