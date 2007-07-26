package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.inter.BasicVisualizationInteractor;
import infovis.visualization.magicLens.ExcentricLabels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Handles user interaction with Excentric labels.
 *
 * @author Allan Kuchinsky, Jean-Daniel Fekete
 * @version $Revision: 0.1 $
*/
public class CyExcentricVisualizationInteractor extends
        BasicVisualizationInteractor {
    protected ExcentricLabels excentric;

    protected Timer insideTimer;

    protected int threshold = 20;

    protected boolean entered;

    private CyExcentricLabelsWrapper wrapper;

    public CyExcentricVisualizationInteractor (CyExcentricLabelsWrapper wrapper) {
        super();
        this.excentric = wrapper.getExcentric();
        this.wrapper = wrapper;
        insideTimer = new Timer(50000, new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                System.out.println("Ping!");
                excentric.setVisible(true);

            }
        });
        insideTimer.setRepeats(false);
        setVisualization(wrapper);
    }

    public void setVisualization (CyExcentricLabelsWrapper wrapper) {
        if (this.wrapper == wrapper) {
            return;
        }
        if (wrapper != null && wrapper.getParent() != null) {
            uninstall(wrapper);
        }
        this.wrapper = wrapper;
        if (wrapper != null) {
            if (wrapper.parent != null) {
                install(wrapper);
            }
        }
    }

    public void install (JComponent comp) {
        if (comp != null) {
            comp.addMouseListener(this);
            comp.addMouseMotionListener(this);
            restart();
        }
    }

    public void uninstall (JComponent comp) {
        super.uninstall(comp);
        if (comp != null) {
            setVisible(false);
            stop();
            comp.removeMouseListener(this);
            comp.removeMouseMotionListener(this);
        }
    }

    public void restart () {
        if (insideTimer != null) {
            insideTimer.restart();
        }
    }

    public void stop () {
        if (insideTimer != null) {
            insideTimer.stop();
        }
    }

    public void setVisible (boolean v) {
        excentric.setVisible(v);
    }

    public boolean isVisible () {
        return excentric.isVisible();
    }

    public static float dist2 (float dx, float dy) {
        return dx * dx + dy * dy;
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseEntered(MouseEvent)
     */
    public void mouseEntered (MouseEvent e) {
        entered = true;
        restart();
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseExited(MouseEvent)
     */
    public void mouseExited (MouseEvent e) {
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
    public void mousePressed (MouseEvent e) {
        setVisible(false);
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
     */
    public void mouseMoved (MouseEvent e) {
		System.out.println("mouse event at: " + e.getWhen());
        if (isVisible()) {
			System.out.println("Visible mouse event: " + e);
            if (e.getModifiers() != 0) {
                return;
            }
            if (dist2(excentric.getLensX() - e.getX(), excentric.getLensY()
                    - e.getY()) > threshold * threshold) {
                setVisible(false);
                insideTimer.restart();
            }
        }
        excentric.setLens(e.getX(), e.getY());
    }

    /**
     * Returns the threshold.
     * <p/>
     * When the mouse moves a distance larger than this threshold since the last
     * event, excentric labels are disabled.
     *
     * @return int
     */
    public int getThreshold () {
        return threshold;
    }

    /**
     * Sets the threshold.
     * <p/>
     * When the mouse moves a distance larger than the specified threshold since
     * the last event, excentric labels are disabled.
     *
     * @param threshold The threshold to set
     */
    public void setThreshold (int threshold) {
        this.threshold = threshold;
    }
}