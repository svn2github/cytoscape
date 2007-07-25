/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.Visualization;
import infovis.visualization.VisualizationInteractor;
import infovis.visualization.VisualizationProxy;

import java.awt.event.*;

import javax.swing.JComponent;

/**
 * Base class for VisualizationInteractor.  Also useful as a null interactor.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.StrokingVisualization infovis.Visualization
 */
public class BasicVisualizationInteractor
    extends VisualizationProxy
    implements
    MouseListener, 
    MouseMotionListener,
    MouseWheelListener,
    KeyListener,
    VisualizationInteractor {
    protected JComponent parent;

    public BasicVisualizationInteractor() {
        super(null);
    }
    public BasicVisualizationInteractor(Visualization vis) {
        super(null);
        setVisualization(vis);
    }

    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseDragged(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
    }
    public void mouseWheelMoved(MouseWheelEvent e) {
    }
    public void keyPressed(KeyEvent e) {
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
    public JComponent getJComponent() {
        return parent;
    }
    
    public Visualization getVisualization() {
        return visualization;
    }
    
    public void install(JComponent comp) {
    }
    
    public void setVisualization(Visualization vis) {
        if (this.visualization == vis) return;
        if (visualization != null && visualization.getParent() != null) {
            uninstall(visualization.getParent());
        }
        this.visualization = vis;
        if (visualization != null) {
            //vis.setInteractor(this);
            if (visualization.getParent() != null) {
                install(visualization.getParent());
            }
        }
    }
    public void uninstall(JComponent comp) {
    }
    
}
