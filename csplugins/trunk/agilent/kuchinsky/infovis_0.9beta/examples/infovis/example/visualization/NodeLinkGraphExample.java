/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.example.visualization;

import infovis.Graph;
import infovis.Tree;
import infovis.column.BooleanColumn;
import infovis.example.ExampleRunner;
import infovis.graph.Algorithms;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.graph.visualization.layout.FRLayout;
import infovis.io.AbstractReader;
import infovis.visualization.ItemRenderer;
import infovis.visualization.inter.RendererInteractorFactory;
import infovis.visualization.inter.VisualSelectionInteractor;
import infovis.visualization.render.VisualSelection;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Example of graph visualization with an adjacency matrix.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class NodeLinkGraphExample extends VisualSelectionInteractor {
    int startX;
    int startY;
    boolean dragging = false;
    int dragThreshold = 4;
    int draggedNode;
    BooleanColumn fixed;

    public NodeLinkGraphExample(ItemRenderer renderer) {
        super(renderer);
    }
    
    public void install(JComponent comp) {
        super.install(comp);
        fixed = BooleanColumn.findColumn(
                ((NodeLinkGraphVisualization)visualization).getVertexTable(), 
                FRLayout.COLUMN_FIXED);
    }
    
    public void uninstall(JComponent comp) {
        super.uninstall(comp);
        fixed = null;
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
        super.mouseDragged(e);
        if (!dragging
                && (Math.abs(e.getX() - startX) + Math.abs(e.getY()
                        - startY)) > dragThreshold) {
            draggedNode = pickTop(startX, startY, getBounds());
            if (draggedNode != Tree.NIL)
                dragging = true;
            startX = e.getX();
            startY = e.getY();
        } else if (dragging) {
            final int dx = e.getX() - startX;
            final int dy = e.getY() - startY;
            startX = e.getX();
            startY = e.getY();
            moveNodeBy(draggedNode, dx, dy);
            repaint();
        }
    }

    protected void moveNodeBy(int draggedNode, final int dx,
            final int dy) {
        Rectangle2D.Float rect = (Rectangle2D.Float) getShapeAt(draggedNode);
        if (rect == null)
            return;
        rect.x += dx;
        rect.y += dy;
        fixed.addSelectionInterval(draggedNode, draggedNode);
        setShapeAt(draggedNode, rect);
    }

    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            //invalidate();
            dragging = false;
            if ((e.getModifiers()&MouseEvent.SHIFT_MASK)!=0) {
                fixed.removeIndexInterval(draggedNode, draggedNode);
            }
            draggedNode = -1;
            invalidate();
        }
        super.mouseReleased(e);
    }

    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args,
                "NodeLinkGraphExample");
        Graph g;

        if (args.length == 0) {
            //g = Algorithms.getOneCompnentGraph();
            g = Algorithms.getGridGraph(10, 10);
        }
        else {
            g = new DefaultGraph();
            AbstractReader reader = 
            GraphReaderFactory.createGraphReader(
                example.getArg(0),
                g);
            if (reader == null || ! reader.load()) {
                System.err.println("cannot load " + example.getArg(0));
                System.exit(1);
            }
        }
        RendererInteractorFactory.getInstance().add(
                VisualSelection.class.getName(),
                NodeLinkGraphExample.class.getName(), null);
        NodeLinkGraphVisualization visualization =
            new NodeLinkGraphVisualization(g);
        visualization.setLayout(FRLayout.instance);

        example.createFrame(visualization);
    }
}