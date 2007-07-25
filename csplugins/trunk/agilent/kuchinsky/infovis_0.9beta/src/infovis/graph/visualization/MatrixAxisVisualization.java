/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Graph;
import infovis.Visualization;
import infovis.utils.InfovisUtilities;
import infovis.utils.RowIterator;
import infovis.visualization.Layout;
import infovis.visualization.Orientation;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JViewport;

public class MatrixAxisVisualization extends GraphVisualization implements
        Layout {

    public static final String    PROPERTY_PREFERED_SIZE = "PROPERTY_PREFERED_SIZE";
    protected int                 preferedSize           = 100;
    protected MatrixVisualization matrix;
    protected Font                font                   = new Font(
                                                                 "Dialog",
                                                                 Font.BOLD,
                                                                 20);

    public MatrixAxisVisualization(Graph graph, MatrixVisualization matrix) {
        super(graph, graph.getVertexTable());
        this.matrix = matrix;
    }

    public Layout getLayout() {
        return this;
    }

    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        super.paint(graphics, bounds);
        boolean vertical = !Orientation.isVertical(orientation);
        String label = vertical ? "In" : "Out";

        Component parent = getParent();
        parent = (parent == null) ? null : parent.getParent();
        if (parent instanceof JViewport) {
            JViewport vp = (JViewport) parent;
            bounds = vp.getViewRect();
        }
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        if (vertical) {
            InfovisUtilities.drawStringVertical(
                    graphics,
                    label,
                    bounds,
                    0.5f,
                    orientation == ORIENTATION_EAST ? 1 : 0,
                    true);
        }
        else {
            InfovisUtilities.drawString(
                    graphics,
                    label,
                    bounds,
                    0.5f,
                    orientation == ORIENTATION_NORTH ? 0 : 1,
                    true);
        }
    }

    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        assert (this == vis);
        if (Orientation.isVertical(orientation)) {
            double w = bounds.getWidth() / getRowCount();
            int row = 0;
            for (RowIterator iter = iterator(); iter.hasNext(); row++) {
                int v = iter.nextRow();
                Rectangle2D.Float s = findRectAt(v);
                s.setRect(w * row, 0, w, bounds.getHeight());
            }
        }
        else {
            double h = bounds.getHeight() / getRowCount();
            int row = 0;
            for (RowIterator iter = iterator(); iter.hasNext(); row++) {
                int v = iter.nextRow();
                Rectangle2D.Float s = findRectAt(v);
                s.setRect(0, h * row, bounds.getWidth(), h);
            }
        }
    }

    public Dimension getPreferredSize(Visualization vis) {
        Dimension pref = matrix.getPreferredSize();
        if (Orientation.isVertical(orientation)) {
            return new Dimension(pref.width, (int) preferedSize);
        }
        else {
            return new Dimension((int) preferedSize, pref.height);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }

    public int getPreferedSize() {
        return preferedSize;
    }

    public void setPreferedSize(int preferedSize) {
        if (this.preferedSize == preferedSize)
            return;
        int old = this.preferedSize;
        this.preferedSize = preferedSize;
        firePropertyChange(PROPERTY_PREFERED_SIZE, old, this.preferedSize);
        invalidate();
    }

}
