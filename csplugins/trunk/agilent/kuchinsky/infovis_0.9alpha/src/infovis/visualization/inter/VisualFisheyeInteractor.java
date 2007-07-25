/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.visualization.ItemRenderer;
import infovis.visualization.magicLens.Fisheye;
import infovis.visualization.render.VisualFisheye;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;

/**
 * Class VisualFisheyeInteractor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * @infovis.factory RendererInteractorFactory infovis.visualization.render.VisualFisheye
 */
public class VisualFisheyeInteractor extends
        BasicVisualizationInteractor {
    protected VisualFisheye renderer;

    public VisualFisheyeInteractor(ItemRenderer renderer) {
        this.renderer = (VisualFisheye)renderer;
    }
    
    public void install(JComponent comp) {
//    	comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.addMouseWheelListener(this);
    }
    
    public void uninstall(JComponent comp) {
//    	comp.removeMouseListener(this);
        comp.removeMouseMotionListener(this);
        comp.removeMouseWheelListener(this);
    }
    
    public void repaint() {
        Fisheye fisheye = renderer.getFisheye();
        if (fisheye != null
            && fisheye.isEnabled()) {
            super.repaint();
        }        
    }

    public void mouseMoved(MouseEvent e) {
        Fisheye fisheye = renderer.getFisheye();
        if (fisheye != null) {
            fisheye.setLens(e.getX(), e.getY());
            repaint();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        Fisheye fisheye = renderer.getFisheye();

        if (fisheye != null) {
            float height = fisheye.getFocusHeight() + e.getWheelRotation();
            fisheye.setFocusHeight(height);
            repaint();
        }
    }    
}
