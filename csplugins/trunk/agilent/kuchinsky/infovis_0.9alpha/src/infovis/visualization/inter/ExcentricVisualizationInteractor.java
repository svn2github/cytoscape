/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.visualization.magicLens.ExcentricLabelVisualization;
import infovis.visualization.magicLens.ExcentricLabels;

import java.awt.event.*;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Interactor for excentric labels
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.magicLens.ExcentricLabelVisualization
 */
public class ExcentricVisualizationInteractor extends
        BasicVisualizationInteractor {
    protected ExcentricLabels excentric;
    protected Timer insideTimer;
    protected int threshold = 20;
    protected boolean entered;
  
    public ExcentricVisualizationInteractor(ExcentricLabelVisualization vis) {
        super(vis);
        insideTimer = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excentric.setVisible(true);
            }
        });
        insideTimer.setRepeats(false);
//        install(vis.getComponent());
    }
    
    public ExcentricLabelVisualization getExcentricVisualization() {
        return (ExcentricLabelVisualization)getVisualization();
    }
    
    public void install(JComponent comp) {
        super.install(comp);
        if (comp != null) {
            excentric = getExcentricVisualization().getExcentric();
            comp.addMouseListener(this);
            comp.addMouseMotionListener(this);
            if (entered) {
                restart();
            }
        }
    }
    public void uninstall(JComponent comp) {
        super.uninstall(comp);
        if (comp != null) {
            setVisible(false);
            stop();
            comp.removeMouseListener(this);
            comp.removeMouseMotionListener(this);
        }
    }

    public void restart() {
        if (insideTimer != null)
            insideTimer.restart();
    }
    
    public void stop() {
        if (insideTimer != null)
            insideTimer.stop();
    }
    
    public void setVisible(boolean v) {
        excentric.setVisible(v);
    }
    
    public boolean isVisible() {
        return excentric.isVisible();
    }
    
    public static float dist2(float dx, float dy) {
        return dx * dx + dy * dy;
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        entered = true;
        restart();
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        if (e.getModifiers() != 0) {
            return;
        }
        entered = false;
        stop();
        setVisible(false);
    }

    /**
     * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        setVisible(false);
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        if (isVisible()) {
            if (e.getModifiers() != 0)
                return;
            if (dist2(excentric.getLensX() - e.getX(), excentric.getLensY() - e.getY())
                > threshold * threshold) {
                setVisible(false);
                insideTimer.restart();
            }
        }

        excentric.setLens(e.getX(), e.getY());
    }

    /**
     * Returns the threshold.
     *
     * When the mouse moves a distance larger than this
     * threshold since the last event, excentric labels
     * are disabled.
     *
     * @return int
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Sets the threshold.
     *
     * When the mouse moves a distance larger than the
     * specified threshold since the last event, excentric
     * labels are disabled.
     *
     * @param threshold The threshold to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
