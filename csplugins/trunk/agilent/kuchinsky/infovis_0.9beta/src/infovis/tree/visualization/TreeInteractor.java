/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import java.awt.event.MouseEvent;

import infovis.Tree;
import infovis.visualization.ItemRenderer;
import infovis.visualization.inter.*;

/**
 * Interactor class for tree visualizations.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 * @infovis.factory RendererInteractorFactory infovis.tree.visualization.TreeVisualSelection
 */
public class TreeInteractor extends VisualSelectionInteractor {
    /**
     * Constructor.
     * @param renderer the VisualSelection 
     */
    public TreeInteractor(ItemRenderer renderer) {
        super(renderer);
    }
    
    /**
     * Return the TreeVisualization.
     * @return the TreeVisualization.
     */
    public TreeVisualization getTreeVisualization() {
        return (TreeVisualization)getVisualization();
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
        TreeVisualization vis = getTreeVisualization();
        super.mouseClicked(e);
        if (e.getClickCount() == 1
            && e.getButton() != MouseEvent.BUTTON1) {
            vis.setVisibleRoot(Tree.ROOT);
        }
        if (e.getClickCount() != 2) {
            return;
        }
        final int x = e.getX();
        final int y = e.getY();
        int r = vis.pickTop(x, y, vis.getBounds());
        if (r != -1)
            vis.setVisibleRoot(r);
    }    

}
