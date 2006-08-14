package ding.view;

import cytoscape.render.stateful.EdgeDetails;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;


class IntermediateEdgeDetails extends EdgeDetails {
    // Note: It is extremely important that the methds sourceArrow(int) and
    // targetArrow(int) both return GraphGraphics.ARROW_NONE.  Methods in
    // DEdgeView rely on this.  Right now EdgeDetails does return these values
    // by default.  I could even override those methods here and redundantly
    // return those same values, but I prefer not to.
    public Color colorLowDetail(int edge) {
        return DEdgeView.DEFAULT_EDGE_PAINT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float sourceArrowSize(int edge) {
        return DEdgeView.DEFAULT_ARROW_SIZE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint sourceArrowPaint(int edge) {
        return DEdgeView.DEFAULT_ARROW_PAINT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float targetArrowSize(int edge) {
        return DEdgeView.DEFAULT_ARROW_SIZE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint targetArrowPaint(int edge) {
        return DEdgeView.DEFAULT_ARROW_PAINT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float segmentThickness(int edge) {
        return DEdgeView.DEFAULT_EDGE_THICKNESS;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint segmentPaint(int edge) {
        return DEdgeView.DEFAULT_EDGE_PAINT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     * @param labelInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String labelText(int edge, int labelInx) {
        return DEdgeView.DEFAULT_LABEL_TEXT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     * @param labelInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Font labelFont(int edge, int labelInx) {
        return DEdgeView.DEFAULT_LABEL_FONT;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     * @param labelInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint labelPaint(int edge, int labelInx) {
        return DEdgeView.DEFAULT_LABEL_PAINT;
    }
}
