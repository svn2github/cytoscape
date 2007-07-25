/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization.icicletree;
import infovis.Tree;
import infovis.tree.DepthFirst;
import infovis.tree.visualization.IcicleTreeVisualization;
import infovis.visualization.inter.BasicVisualizationInteractor;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import cern.colt.function.IntProcedure;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class EditableIcicleTree extends BasicVisualizationInteractor {
    int startX;
    int startY;
    boolean dragging = false;
    int dragThreshold = 4;
    int draggedNode;
    IcicleTreeVisualization tree;

    /**
     * Constructor for EditableIcicleTree.
     */
    public EditableIcicleTree(IcicleTreeVisualization vis) {
        super(vis);
        this.tree = vis;
    }
    
    public void install(JComponent comp) {
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
    }
    
    public void uninstall(JComponent comp) {
        comp.removeMouseListener(this);
        comp.removeMouseMotionListener(this);
    }

    /**
     * @see infovis.Visualization#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        super.mousePressed(e);
    }

    /**
     * @see infovis.Visualization#mouseDragged(MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
        if (! dragging &&
            (Math.abs(e.getX() - startX)+Math.abs(e.getY() - startY)) > dragThreshold) {
            draggedNode = pickTop(startX, startY, getBounds());
            if (draggedNode != Tree.NIL)
                dragging = true;
                startX = e.getX();
                startY = e.getY();
        }
        else if (dragging) {
            final int dx = e.getX() - startX;
            final int dy = e.getY() - startY;
            startX = e.getX();
            startY = e.getY();
            moveNodeBy(draggedNode, dx, dy);
            repaint();
        }
    }

    protected void moveNodeBy(int draggedNode, final int dx, final int dy) {
        DepthFirst.visitPreorder(
                tree, 
                new IntProcedure() {
            public boolean apply(int node) {
                Rectangle2D.Float rect = (Rectangle2D.Float)getShapeAt(node);
                if (rect == null)
                    return true;
                rect.x += dx;
                rect.y += dy;
                return true;
            }
        }, draggedNode);
    }

    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            // avoid the node picking itelf
            moveNodeBy(draggedNode, -1000, -1000);
            int newParent = pickTop(e.getX(), e.getY(), getBounds());
            if (newParent != Tree.NIL) {
                if (! tree.isAncestor(draggedNode, newParent)) {
                    tree.reparent(draggedNode, newParent);
                    //setVisualColumn(VISUAL_SIZE, AdditiveAggregation.buildDegreeAdditiveWeight(tree));
                }
            }
            invalidate();
            dragging = false;
            draggedNode = Tree.NIL;
        }
        super.mouseReleased(e);
    }

}
