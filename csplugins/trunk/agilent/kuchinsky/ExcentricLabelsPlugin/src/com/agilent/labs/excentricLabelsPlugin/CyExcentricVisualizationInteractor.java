package com.agilent.labs.excentricLabelsPlugin;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import infovis.visualization.inter.BasicVisualizationInteractor;
import infovis.visualization.magicLens.ExcentricLabels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

/**
 * Handles user interaction with Excentric labels.
 *
 * @author Allan Kuchinsky, Jean-Daniel Fekete
 * @version $Revision: 0.1 $
 */
public class CyExcentricVisualizationInteractor extends
        BasicVisualizationInteractor {
    protected Timer insideTimer;
    protected int threshold = 50;
    private CyExcentricLabelsWrapper wrapper;
    private int currentX;
    private int currentY;

    public CyExcentricVisualizationInteractor (CyExcentricLabelsWrapper wrapper) {
        super();
        this.wrapper = wrapper;
        wrapper.addMouseListener(this);
        wrapper.addMouseMotionListener(this);
        final ExcentricLabels excentric = wrapper.getExcentric();
        insideTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                System.out.println("Showing Excentric Labels");
                excentric.setVisible(true);
                excentric.setEnabled(true);
            }
        });
        insideTimer.setRepeats(false);
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
     */
    public void mouseMoved (MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        wrapper.getExcentric().setLens(e.getX(), e.getY());
        if (!wrapper.getExcentric().isVisible()) {
            insideTimer.restart();
            redispatchMouseEvent(e);
        }
    }

    public void mouseDragged (MouseEvent e) {
        if (!wrapper.getExcentric().isVisible()) {
            wrapper.getExcentric().setVisible(false);
            insideTimer.restart();
        }
        redispatchMouseEvent(e);
//        else {
//            ExcentricLabels excentric = wrapper.getExcentric();
//            float radius = excentric.getLensRadius();
//            boolean modified = false;
//            if (e.getX() > currentX && e.getY() > currentY) {
//                if (radius <= 100) {
//                    excentric.setLensRadius(radius + 3);
//                    modified = true;
//                }
//            } else if (e.getX() < currentX && e.getY() < currentY) {
//                if (radius >= 20 ) {
//                    excentric.setLensRadius(radius - 3);
//                    modified = true;
//                }
//            }
//            if (modified ) {
//                CyNetworkView view = Cytoscape.getCurrentNetworkView();
//                JComponent foregroundCanvas = ((DGraphView) view).getCanvas
//                        (DGraphView.Canvas.FOREGROUND_CANVAS);
//                foregroundCanvas.repaint();
//            }
//            currentX = e.getX();
//            currentY = e.getY();
//        }
    }

    public void mouseClicked (MouseEvent e) {
        if (wrapper.getExcentric().isVisible()) {
            wrapper.getExcentric().setVisible(false);
            insideTimer.restart();
        }
        redispatchMouseEvent(e);
    }

    public void mouseReleased (MouseEvent e) {
        redispatchMouseEvent(e);
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseEntered(MouseEvent)
     */
    public void mouseEntered (MouseEvent e) {
        insideTimer.restart();
    }

    /**
     * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
     */
    public void mousePressed (MouseEvent e) {
        if (wrapper.getExcentric().isVisible()) {
            wrapper.getExcentric().setVisible(false);
            insideTimer.restart();
        } else {
            redispatchMouseEvent(e);
        }
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseExited(MouseEvent)
     */
    public void mouseExited (MouseEvent e) {
        insideTimer.stop();
        //wrapper.getExcentric().setVisible(false);
    }

    /**
     * Redispatch events to the network canvas.  Otherwise, the glass pane intercepts
     * all these events.
     * @param e MouseEvent.
     */
    private void redispatchMouseEvent (MouseEvent e) {
        DGraphView dGraphView = (DGraphView) Cytoscape.getCurrentNetworkView();
        JComponent networkCanvas = dGraphView.getCanvas(DGraphView.Canvas.NETWORK_CANVAS);
        networkCanvas.dispatchEvent(new MouseEvent(networkCanvas,
                e.getID(),
                e.getWhen(),
                e.getModifiers(),
                e.getX(),
                e.getY(),
                e.getClickCount(),
                e.isPopupTrigger()));
    }
}
