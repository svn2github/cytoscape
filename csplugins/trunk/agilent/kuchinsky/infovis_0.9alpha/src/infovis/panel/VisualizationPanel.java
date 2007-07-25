/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.panel;

import infovis.Visualization;
import infovis.visualization.render.VisualLabel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Component managing a visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.24 $
 */
public class VisualizationPanel extends JComponent {
    protected Visualization visualization;
    protected boolean       usingGradient = true;
    public static final String PROPERTY_USING_GRADIENT = "usingGradient";

    public VisualizationPanel(Visualization visualization) {
        setPreferredSize(new Dimension(500, 500));
        setVisualization(visualization);
    }

    public Rectangle2D.Float getFullBounds() {
        return new Rectangle2D.Float(0, 0, getWidth(), getHeight());
    }

    public void paintBackground(Graphics2D graphics) {
        Color backgroundColor = getBackground();
        if (backgroundColor == null)
            return;

        if (usingGradient) {
            GradientPaint paint = new GradientPaint(0, 0, backgroundColor
                    .darker(), getWidth(), getHeight(), backgroundColor);
            graphics.setPaint(paint);
        }
        else {
            graphics.setColor(backgroundColor);
        }
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setColor(backgroundColor);
    }

    /**
     * @see javax.swing.JComponent#printComponent(Graphics)
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        paintBackground(g2);
        if (this.visualization != null) {
            visualization.paint(g2, getFullBounds());
        }
    }

    /**
     * Returns the visualization.
     * 
     * @return Visualization
     */
    public Visualization getVisualization() {
        return visualization;
    }

    /**
     * Sets the visualization.
     * 
     * @param visualization
     *            The visualization to set
     */
    public void setVisualization(Visualization visualization) {
        if (this.visualization != null)
            this.visualization.setParent(null);
        this.visualization = visualization;
        if (this.visualization != null)
            this.visualization.setParent(this);
    }

    /**
     * @see javax.swing.JComponent#getToolTipText(MouseEvent)
     */
    public String getToolTipText(MouseEvent event) {
        int row = visualization.pickTop(
                event.getX(),
                event.getY(),
                getFullBounds());
        if (row == -1)
            return null;
        VisualLabel vl = VisualLabel.get(visualization);
        return vl.getLabelAt(row);
    }

    public Dimension getPreferredSize() {
        Dimension d = visualization.getPreferredSize();
        if (d != null) {
            return d;
        }
        return super.getPreferredSize();
    }

//    public Dimension getMaximumSize() {
//        Dimension d = visualization.getMaximumSize();
//        if (d != null) {
//            return d;
//        }
//        return super.getMaximumSize();
//    }
//
//    public Dimension getMinimumSize() {
//        Dimension d = visualization.getMinimumSize();
//        if (d != null) {
//            return d;
//        }
//        return super.getMinimumSize();
//    }
    public boolean isUsingGradient() {
        return usingGradient;
    }
    
    public void setUsingGradient(boolean usingGradient) {
        if (this.usingGradient == usingGradient) return;
        boolean old = this.usingGradient;
        this.usingGradient = usingGradient;
        repaint();
        firePropertyChange(PROPERTY_USING_GRADIENT, old, usingGradient);
    }
}
